package org.jitsi.jicofo.xmpp
import org.jitsi.xmpp.extensions.AbstractPacketExtension

class TracePacketExtension(trace: String, parent: String) : AbstractPacketExtension(NAMESPACE, ELEMENT) {
    init {
        setTrace(trace)
        setParent(parent)
    }

    fun getParent(): String = getAttributeAsString(PARENT_ATTR_NAME)
    fun setParent(parent: String) {
        setAttribute(PARENT_ATTR_NAME, parent)
    }
    fun getTrace(): String = getAttributeAsString(TRACE_ATTR_NAME)
    fun setTrace(parent: String) {
        setAttribute(TRACE_ATTR_NAME, parent)
    }

    companion object {
        const val ELEMENT = "trace"
        const val NAMESPACE = "opentelemetry"
        const val PARENT_ATTR_NAME = "parent"
        const val TRACE_ATTR_NAME = "trace"
    }
}
