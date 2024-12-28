package foundation.esoteric.fireworkwarslobby.npc.connection

import io.netty.channel.*
import java.net.SocketAddress

class EmptyChannel(parent: Channel?) : AbstractChannel(parent) {
    private val config: ChannelConfig = DefaultChannelConfig(this)

    override fun config(): ChannelConfig {
        config.setAutoRead(true)
        return config
    }

    override fun doBeginRead() {}

    override fun doBind(arg0: SocketAddress) {}

    override fun doClose() {}

    override fun doDisconnect() {}

    override fun doWrite(arg0: ChannelOutboundBuffer) {}

    override fun isActive(): Boolean {
        return false
    }

    override fun isCompatible(arg0: EventLoop): Boolean {
        return false
    }

    override fun isOpen(): Boolean {
        return false
    }

    override fun localAddress0(): SocketAddress? {
        return null
    }

    override fun metadata(): ChannelMetadata {
        return ChannelMetadata(true)
    }

    override fun newUnsafe(): AbstractUnsafe? {
        return null
    }

    override fun remoteAddress0(): SocketAddress? {
        return null
    }
}