package com.mattmx.ktgui.examples

import com.mattmx.ktgui.KotlinGui
import com.mattmx.ktgui.components.screen.GuiScreen
import com.mattmx.ktgui.components.signal.signal
import com.mattmx.ktgui.dsl.button
import com.mattmx.ktgui.dsl.event
import com.mattmx.ktgui.dsl.gui
import com.mattmx.ktgui.dsl.signalButton
import com.mattmx.ktgui.event.PreGuiBuildEvent
import com.mattmx.ktgui.utils.not
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType

class GuiHookExample : Example {
    val gui = gui(!"Title", 3) {
        id = "kgui.example.gui-hook"

        button(Material.LIME_DYE) {
            named(!"&a&lA button")
            lore {
                add(!"&fThis button was added regularly.")
            }
        } slots listOf(11, 13)
    }

    override fun run(player: Player) = gui.open(player)

    companion object {
        fun registerListener(plugin: KotlinGui) {
            event<PreGuiBuildEvent>(plugin) {
                if (gui !is GuiScreen) return@event
                if ((gui as GuiScreen).id != "kgui.example.gui-hook") return@event

                var signalExampleVar by (gui as GuiScreen).signal(0)
                gui.signalButton(Material.PURPLE_DYE) {
                    named(!"&d&lA button")
                    lore {
                        add(!"&fThis button was added after the gui was built.")
                        add(!"&fWe can even add our own signals here and whatnot: $signalExampleVar")
                        add(!"&a&l[CLICK]")
                    }
                    click {
                        ClickType.LEFT {
                            signalExampleVar++
                        }
                        ClickType.RIGHT {
                            signalExampleVar--
                        }
                    }
                } slot 15
            }
        }
    }
}