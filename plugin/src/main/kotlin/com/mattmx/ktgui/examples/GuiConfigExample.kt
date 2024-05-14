package com.mattmx.ktgui.examples

import com.mattmx.ktgui.components.signal.signal
import com.mattmx.ktgui.dsl.effect
import com.mattmx.ktgui.guiconfig.configButton
import com.mattmx.ktgui.guiconfig.configGui
import com.mattmx.ktgui.utils.legacy
import com.mattmx.ktgui.utils.not
import org.bukkit.entity.Player

class GuiConfigExample : Example {
    val gui = configGui("gui.example") {
        parseButtons()

        gui {
            val initialTitle = title.legacy()
            var value by signal(0)

            effect {
                title = !("$initialTitle&7 ($value)")
            }

            configButton("gui.example.close") {
                leftClick {
                    forceClose()
                }
            } childOf this slot last()

            configButton("gui.example.dec") {
                leftClick {
                    value--
                }
            } childOf this slot 0

            configButton("gui.example.add") {
                leftClick {
                    value++
                }
            } childOf this slot 1

            findButton("slot-specified") {
                leftClick {
                    value = 0
                }
            }
        }
    }

    override fun run(player: Player) {
        gui.gui.open(player)
    }
}