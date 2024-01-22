package com.mattmx.ktgui.examples

import co.pvphub.protocollib.dsl.packetReceiving
import co.pvphub.protocollib.extensions.unregister
import com.comphenix.protocol.PacketType
import com.comphenix.protocol.events.PacketAdapter
import com.mattmx.ktgui.KotlinGui
import com.mattmx.ktgui.components.screen.GuiScreen
import com.mattmx.ktgui.dsl.button
import com.mattmx.ktgui.dsl.gui
import com.mattmx.ktgui.scheduling.not
import com.mattmx.ktgui.utils.not
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryType

class AnvilInputGuiExample : Example {

    /**
     * Each gui needs to be per-player
     *
     * @param player we're making the gui for
     */
    val gui: (player: Player) -> GuiScreen = { player ->
        // ProtocolLib MUST be installed for this.
        if (!KotlinGui.protocolLib) {
            throw ClassNotFoundException("ProtocolLib is not installed! This example requires it.")
        }

        gui(!"Rename GUI", InventoryType.ANVIL) {
            var currentName = ""

            button(Material.PAPER) {
                lore {
                    this += !"Using the anvil rename feature"
                }
            } named !"&aRename this item" slot 0 childOf this

            button(Material.PAPER) {
                click {
                    any {
                        forceClose(player)
                        player.sendMessage(!currentName)
                    }
                }
                lore {
                    this += !"&7Click to finish!"
                }
            } named !"&aDone" childOf this slot 2

            /**
             * We want to make sure that the listener is dynamically made.
             * We're currently using PvPHub's ProtocolLibDsl to make it look nicer.
             */
            var listener: PacketAdapter? = null
            open {
                listener = packetReceiving(KotlinGui.plugin!!, type = arrayOf(PacketType.Play.Client.ITEM_NAME)) {
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

    override fun run(player: Player) = gui(player).open(player)
}