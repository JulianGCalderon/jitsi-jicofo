/*
 * Jicofo, the Jitsi Conference Focus.
 *
 * Copyright @ 2020 - present 8x8, Inc
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

package org.jitsi.jicofo

import io.opentelemetry.api.trace.SpanBuilder
import org.jitsi.jicofo.xmpp.muc.ChatRoomMember
import java.util.*

class TracingHelper {
    companion object {
        fun setMemberSpan(builder: SpanBuilder, member: ChatRoomMember): SpanBuilder = builder
            .setAttribute("member.name", member.name)
            .setAttribute("member.jid.local", Objects.toString(member.jid?.localpartOrNull))
            .setAttribute("member.jid.resource", Objects.toString(member.jid?.resourceOrNull))
            .setAttribute("member.role", member.role.toString())
            .setAttribute("member.region", Objects.toString(member.region))
            .setAttribute("member.isAudioMuted", member.isAudioMuted)
            .setAttribute("member.isVideoMuted", member.isVideoMuted)
            .setAttribute("member.isJibri", member.isJibri)
            .setAttribute("member.isJigasi", member.isJigasi)
            .setAttribute("member.isTranscriber", member.isTranscriber)
    }
}
