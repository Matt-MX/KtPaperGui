package com.mattmx.ktgui.examples

import com.mattmx.ktgui.GuiManager
import com.mattmx.ktgui.components.button.GuiButton
import com.mattmx.ktgui.components.screen.GuiInfiniteScreen
import com.mattmx.ktgui.dsl.button
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.event.inventory.ClickType

class InfiniteGuiExample : GuiInfiniteScreen("Infinite Screen", 6) {

    init {
        button<GuiButton> {
            named("&7Left")
            material(Material.RED_STAINED_GLASS_PANE)
            click {
                ClickType.LEFT {
                    x--
                    title = "($x, $y)"
                    updateAll()
                }
            }
        } childOf this slots listOf(18, 27)
        button<GuiButton> {
            named("&7Right")
            material(Material.RED_STAINED_GLASS_PANE)
            click {
                ClickType.LEFT {
                    x++
                    title = "($x, $y)"
                    updateAll()
                }
            }
        } childOf this slots listOf(26, 35)
        button<GuiButton> {
            named("&7Up")
            material(Material.RED_STAINED_GLASS_PANE)
            click {
                ClickType.LEFT {
                    y--
                    title = "($x, $y)"
                    updateAll()
                }
            }
        } childOf this slot 4
        button<GuiButton> {
            named("&7Down")
            material(Material.RED_STAINED_GLASS_PANE)
            click {
                ClickType.LEFT {
                    y++
                    title = "($x, $y)"
                    updateAll()
                }
            }
        } childOf this slot 49
        repeat(300) {
            val x = (-100..100).random()
            val y = (-100..100).random()
            this[x, y] = button {
                named("Random item $it")
                material(Material.values().random())
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