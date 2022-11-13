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

    val gui: () -> GuiScreen = {
        if (!KotlinBukkitGui.protocollib) {
            throw ClassNotFoundException("ProtocolLib is not installed! This example requires it.")
        }

        gui {
            title = "Rename GUI"
            type = InventoryType.ANVIL
            var currentName = ""

            button {
                lore {
                    this += "Using the anvil rename feature".color()
                }
            } material Material.PAPER named "&aRename this item" slot 0 childOf this

            button {
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

            var listener: PacketAdapter? = null
            open {
                listener =
                    packetReceiving(KotlinBukkitGui.plugin!!, type = arrayOf(PacketType.Play.Client.ITEM_NAME)) {
                        currentName = packet.strings.read(0)
                    }
            }

            close {
                listener?.unregister()
            }
        }
    }
}