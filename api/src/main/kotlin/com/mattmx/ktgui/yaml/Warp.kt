package com.mattmx.ktgui.yaml

import com.mattmx.ktgui.commands.declarative.runs
import com.mattmx.ktgui.utils.not
import org.bukkit.Location
import org.bukkit.entity.Player

class Warp(
    val id: String,
    var displayName: String,
    var location: Location,
    var directCommandWarp: Boolean = true
) {

    fun registerDirectWarpCommand() {
        (id).runs<Player> {
            teleport(sender)
        }
    }

    fun teleport(player: Player) {
        player.sendMessage(!WarpsPlugin.get().msg_teleporting)
        player.teleportAsync(location)
    }

}