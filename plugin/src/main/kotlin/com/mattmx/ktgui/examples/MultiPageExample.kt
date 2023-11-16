package com.mattmx.ktgui.examples

import com.mattmx.ktgui.components.screen.GuiMultiPageScreen
import com.mattmx.ktgui.dsl.button
import com.mattmx.ktgui.utils.not
import org.bukkit.Material
import org.bukkit.event.inventory.ClickType


class MultiPageExample : GuiMultiPageScreen(!"Multi-page Example", 6) {
    init {
        Material.values().forEach { material ->
            this += button(material) {
                click {
                    any { player.sendMessage(!"&bYou clicked item &3&l${material.name}") }
                }
                named(!"&b&l${material.name}")
            }
        }
        button(Material.GRAY_STAINED_GLASS_PANE) {
            named(!" ") slots (0..8).toList() + (45..53).toList()
        }
        val currentPage =
            button(Material.COMPASS) { named(!"&9&lPage ${getCurrentPage()}") slot 49 }
        button(Material.ARROW) {
            click {
                ClickType.LEFT {
                    gotoNextPage(player)
                    currentPage named !"&9&lPage ${getCurrentPage()}"
                    currentPage.update(player)
                }
            }
            named(!"&bNext Page") slot 53
        }
        button(Material.ARROW) {
            click {
                ClickType.LEFT {
                    gotoPrevPage(player)
                    currentPage named !"&9&lPage ${getCurrentPage()}"
                    currentPage.update(player)
                }
            }
            named(!"&bPrev Page") slot 45
        }
    }
}