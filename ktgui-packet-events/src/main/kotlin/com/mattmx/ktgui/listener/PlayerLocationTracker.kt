package com.mattmx.ktgui.listener

import com.github.retrooper.packetevents.event.PacketListenerAbstract
import com.github.retrooper.packetevents.event.PacketSendEvent
import com.github.retrooper.packetevents.protocol.packettype.PacketType
import com.github.retrooper.packetevents.util.Vector3d
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerPlayerPositionAndLook
import java.util.*

interface PlayerLocationTracker {
    fun getLocation(player: Any): CachedLocation?

    fun remove(player: Any) : CachedLocation?

    class CachedLocation(
        var pos: Vector3d = Vector3d.zero(),
        var yaw: Float = 0f,
        var pitch: Float = 0f
    )
}

class PlayerLocationTrackerImpl : PacketListenerAbstract(), PlayerLocationTracker {
    private val locations = Collections.synchronizedMap(hashMapOf<Any, PlayerLocationTracker.CachedLocation>())

    override fun onPacketSend(event: PacketSendEvent) {
        val player = event.getPlayer<Any>()
        when (event.packetType) {
            PacketType.Play.Server.PLAYER_POSITION_AND_LOOK -> {
                val packet = WrapperPlayServerPlayerPositionAndLook(event)

                val cache = locations.getOrPut(player) { PlayerLocationTracker.CachedLocation() }
                cache.pos = packet.position
                cache.yaw = packet.yaw
                cache.pitch = packet.pitch
            }
        }
    }

    override fun remove(player: Any): PlayerLocationTracker.CachedLocation? {
        return locations.remove(player)
    }

    override fun getLocation(player: Any): PlayerLocationTracker.CachedLocation? {
        return locations[player]
    }

}