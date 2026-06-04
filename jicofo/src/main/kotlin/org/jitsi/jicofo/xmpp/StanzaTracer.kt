/*
 * Jicofo, the Jitsi Conference Focus.
 *
 * Copyright @ 2018 - present 8x8, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jitsi.jicofo.xmpp

import org.jitsi.jicofo.Telemetry
import org.jitsi.utils.logging2.createLogger
import org.jivesoftware.smack.StanzaListener
import org.jivesoftware.smack.packet.IQ
import org.jivesoftware.smack.packet.Message
import org.jivesoftware.smack.packet.Presence
import org.jivesoftware.smack.packet.Stanza

/**
 * Traces all incoming spans.
 *
 * It is hard to link this span to the business logic span that is created in the other handlers.
 * This is probably not the right approach, but it is useful for learning.
 *
 * Being XMPP such a generic protocol, I could not find a way to capture useful information in a
 * readable and organized way.
 *
 * Another approach is to only create spans for specific stanzas directly in their handlers.
 * This would allow to choose in each case what attributes to keep and what attributes to discard,
 * as well as linking the reception with the business logic.
 */
class StanzaTracer : StanzaListener {
    private val tracer = Telemetry.otel.getTracer("org.jitsi.jicofo")
    private val logger = createLogger()

    override fun processStanza(stanza: Stanza) {
        val span = when (stanza) {
            is Message -> {
                tracer.spanBuilder(
                    "xmpp.msg.${stanza.type}"
                )
            }

            is Presence -> {
                tracer.spanBuilder(
                    "xmpp.presence.${stanza.type}"
                )
            }

            is IQ -> {
                tracer.spanBuilder(
                    "xmpp.iq.${stanza.type}.${stanza.childElementName}"
                )
            }

            else -> {
                throw AssertionError("received stanza of unexpected type: " + stanza.javaClass)
            }
        }.startSpan()

        span
            .setAttribute("id", stanza.stanzaId)
            .setAttribute("to", stanza.to?.toString() ?: "")
            .setAttribute("from", stanza.from?.toString() ?: "")
            .setAttribute("lang", stanza.language)

        when (stanza) {
            is Message -> {
                span.setAttribute("subject", stanza.subject ?: "")
                span.setAttribute("body", stanza.body ?: "")
                span.setAttribute("thread", stanza.thread ?: "")
            }

            is Presence -> {
                span.setAttribute("mode", stanza.mode?.name ?: "")
                span.setAttribute("status", stanza.status ?: "")
                span.setAttribute("priority", stanza.priority.toLong())
            }

            is IQ -> {
                span.setAttribute("namespace", stanza.childElementNamespace)
            }
        }

        stanza.error?.let { err ->
            span.setAttribute("error.type", err.type?.toString() ?: "")
            span.setAttribute("error.condition", err.condition?.toString() ?: "")
            span.setAttribute("error.text", err.descriptiveText)
            span.setAttribute("error.generator", err.errorGenerator)
        }

        // Probably not a good idea to do this, as the XML can be very large.
        stanza.extensions.forEach { ext ->
            span.setAttribute(
                "extension.${ext.elementName}",
                ext.toXML().toString()
            )
        }
        span.setAttribute(
            "xml",
            stanza.toXML().toString()
        )

        span.end()
    }
}
