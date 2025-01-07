package com.mattmx.ktgui.impl

import com.github.retrooper.packetevents.util.Vector3d
import com.mattmx.ktgui.listener.PlayerLocationTracker
import org.bukkit.entity.Player

class DummyPlayerLocationTracker : PlayerLocationTracker {
    override fun getLocation(player: Any): PlayerLocationTracker.CachedLocation? {
        if (player !is Player) error("Player is not instance of a Bukkit Player")

        return player.location.let { loc ->
            PlayerLocationTracker.CachedLocation(
                Vector3d(loc.x, loc.y, loc.z),
                loc.yaw,
                loc.pitch
            )
        }
    }

    override fun remove(player: Any): PlayerLocationTracker.CachedLocation? {
        return null
    }
}