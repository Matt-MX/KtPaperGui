package com.mattmx.ktgui.examples

import co.pvphub.procotollib.DummyPacketAdapter
import co.pvphub.procotollib.dsl.packetReceiving
import co.pvphub.procotollib.extensions.unregister
import com.comphenix.protocol.PacketType
import com.comphenix.protocol.events.PacketAdapter
import com.mattmx.ktgui.KotlinBukkitGui
import com.mattmx.ktgui.components.button.GuiButton
import com.mattmx.ktgui.components.screen.GuiScreen
import com.mattmx.ktgui.dsl.button
import com.mattmx.ktgui.dsl.gui
import com.mattmx.ktgui.extensions.color
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryType
import java.lang.Exception

object AnvilInputGuiExample {

    /**
     * Each gui needs to be per-player
     *
     * @param player we're making the gui for
     */
    val gui: (player: Player) -> GuiScreen = { player ->
        // ProtocolLib MUST be installed for this.
        if (!KotlinBukkitGui.protocollib) {
            throw ClassNotFoundException("ProtocolLib is not installed! This example requires it.")
        }

        gui {
            title = "Rename GUI"
            type = InventoryType.ANVIL
            var currentName = ""

            button<GuiButton> {
                lore {
                    this += "Using the anvil rename feature".color()
                }
            } material Material.PAPER named "&aRename this item" slot 0 childOf this

            button<GuiButton> {
                click {
                    generic = { e ->
                        forceClose(e.whoClicked as Player)
                        e.whoClicked.sendMessage(currentName.color())
                    }
                }
                lore {
                    this += "&7Click to finish!"
                }
            } childOf this slot 2 named "&aDone" material Material.PAPER

            /**
             * We want to make sure that the listener is dynamically made.
             * We're currently using PvPHub's ProtocolLibDsl to make it look nicer.
             */
            var listener: PacketAdapter? = null
            open {
                listener = packetReceiving(KotlinBukkitGui.plugin!!, type = arrayOf(PacketType.Play.Client.ITEM_NAME)) {
                    // Only change the name if the packet's player is the player we want
                    if (this.player == player)
                        currentName = packet.strings.read(0)
                }
            }

            // Make sure to unregister the listener after we're done.
            close {
                listener?.unregister()
            }
        }
    }
}