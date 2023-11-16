package com.mattmx.ktgui.examples

import com.mattmx.ktgui.GuiManager
import com.mattmx.ktgui.components.button.GuiButton
import com.mattmx.ktgui.components.screen.GuiInfiniteScreen
import com.mattmx.ktgui.dsl.button
import com.mattmx.ktgui.utils.not
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.event.inventory.ClickType

class InfiniteGuiExample : GuiInfiniteScreen(!"Infinite Screen", 6) {

    init {
        button(Material.RED_STAINED_GLASS_PANE) {
            named(!"&7Left")
            click {
                ClickType.LEFT {
                    x--
                    title = !"($x, $y)"
                    updateAll()
                }
            }
        } childOf this slots listOf(18, 27)
        button(Material.RED_STAINED_GLASS_PANE) {
            named(!"&7Right")
            click {
                ClickType.LEFT {
                    x++
                    title = !"($x, $y)"
                    updateAll()
                }
            }
        } childOf this slots listOf(26, 35)
        button(Material.RED_STAINED_GLASS_PANE) {
            named(!"&7Up")
            click {
                ClickType.LEFT {
                    y--
                    title = !"($x, $y)"
                    updateAll()
                }
            }
        } childOf this slot 4
        button(Material.RED_STAINED_GLASS_PANE) {
            named(!"&7Down")
            click {
                ClickType.LEFT {
                    y++
                    title = !"($x, $y)"
                    updateAll()
                }
            }
        } childOf this slot 49
        repeat(300) {
            val x = (-100..100).random()
            val y = (-100..100).random()
            this[x, y] = button(Material.values().random()) {
                named(!"Random item $it")
            }
        }
    }

    fun updateAll() {
        GuiManager.getPlayers(this)
            .forEach { player -> open(player) }
    }

    companion object {
        lateinit var instance: InfiniteGuiExample
    }

}