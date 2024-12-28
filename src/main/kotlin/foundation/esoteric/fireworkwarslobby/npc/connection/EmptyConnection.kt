package foundation.esoteric.fireworkwarslobby.npc.connection

import net.minecraft.network.Connection
import net.minecraft.network.PacketSendListener
import net.minecraft.network.protocol.Packet
import net.minecraft.network.protocol.PacketFlow
import java.io.Serial
import java.net.SocketAddress

class EmptyConnection(flag: PacketFlow?) : Connection(flag!!) {
    init {
        this.channel = EmptyChannel(null)
        this.address = object : SocketAddress() {
            @Serial
            private val serialVersionUID = 8207338859896320185L
        }
    }

    override fun flushChannel() {}

    override fun isConnected(): Boolean { return true }

    override fun send(packet: Packet<*>) {}

    override fun send(packet: Packet<*>, listener: PacketSendListener?) {}

    override fun send(packet: Packet<*>, listener: PacketSendListener?, flag: Boolean) {}
}