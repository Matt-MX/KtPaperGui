package com.mattmx.ktgui.yaml

import com.mattmx.ktgui.GuiManager
import com.mattmx.ktgui.commands.declarative.arg.impl.booleanArgument
import com.mattmx.ktgui.commands.declarative.arg.impl.multiChoiceArgument
import com.mattmx.ktgui.commands.declarative.arg.impl.stringArgument
import com.mattmx.ktgui.commands.declarative.div
import com.mattmx.ktgui.commands.declarative.invoke
import com.mattmx.ktgui.commands.declarative.runs
import com.mattmx.ktgui.utils.not
import com.mattmx.ktgui.utils.placeholders
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin

class WarpsPlugin : JavaPlugin() {
    val warps by yaml {
        getKeys(false)
            .mapNotNull { k -> getObject(k, Warp::class.java)?.let { k to it } }
            .toMap(HashMap())
    }

    val lang by yaml()
    val msg_set_warp: String by lang("&aSet warp %warp_id% to location (%x%, %y%, %z%, %pitch%, %yaw%) in world '%world%'")
    val msg_del_warp: String by lang("&aDeleted warp %warp_id%")
    val msg_invalid_warp_id: String by lang("&cInvalid warp ID")
    val msg_teleporting: String by lang("&aTeleporting...")
    val msg_modify_warp_display: String by lang("&aSet display of %warp_id% to %warp_display%")
    val msg_modify_warp_location: String by lang("&aMoved display of %warp_id% to your position")
    val msg_modify_warp_directCommandWarp: String by lang("&aSet direct command of %warp_id% to %warp_directCommandWarp")

    override fun onEnable() {
        instance = this
        GuiManager.init(this)

        val newWarpId by stringArgument()
        newWarpId matches "[0-9a-z_]{3,16}".toRegex(RegexOption.IGNORE_CASE)
        newWarpId invalid { reply(!msg_invalid_warp_id) }

        ("set-warp" / newWarpId).runs<Player> {
            val warp = Warp(
                newWarpId(),
                newWarpId(),
                sender.location.clone()
            )

            warps[warp.id] = warp

            reply(msg_set_warp placeholders sender)
        } register this

        val existingWarp by multiChoiceArgument(warps)
        existingWarp invalid { reply(!msg_invalid_warp_id) }

        ("modify-warp" / existingWarp) {
            val display by stringArgument()
            ("display" / display).runs<Player> {
                existingWarp().displayName = display()
                reply(!msg_modify_warp_display)
            }

            ("move-here").runs<Player> {
                existingWarp().location = sender.location.clone()
                reply(!msg_modify_warp_location)
            }

            val enabled by booleanArgument()
            ("direct-command" / enabled).runs<Player> {
                val newState = enabled()
                existingWarp().directCommandWarp = newState
                reply(!msg_modify_warp_directCommandWarp)
            } description "Should this warp have a direct command? e.g /warp spawn -> /spawn"
        }

        ("warp" / existingWarp).runs<Player> {
            existingWarp().teleport(sender)
        } register this

        ("del-warp" / existingWarp).runs<Player> {
            reply(msg_del_warp placeholders sender)
        } register this

        warps.values
            .filter(Warp::directCommandWarp)
            .forEach(Warp::registerDirectWarpCommand)
    }

    companion object {
        private lateinit var instance: WarpsPlugin
        fun get() = instance
    }
}