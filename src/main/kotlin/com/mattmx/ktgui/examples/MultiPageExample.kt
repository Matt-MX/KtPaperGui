package com.mattmx.ktgui.examples

import com.mattmx.ktgui.components.button.GuiButton
import com.mattmx.ktgui.components.screen.GuiMultiPageScreen
import com.mattmx.ktgui.extensions.color
import org.bukkit.Material
import org.bukkit.entity.Player

// fixme: even if copied page seems to save?
class MultiPageExample : GuiMultiPageScreen("Multi-page Example", 6) {
    init {
        Material.values().forEach { material ->
            this += GuiButton()
                .click {
                    generic = { player.sendMessage("&bYou clicked item &3&l${material.name}".color()) }
                } named "&b&l${material.name}" material material
        }
        GuiButton() material Material.GRAY_STAINED_GLASS_PANE named " " slots (0..8).toList() + (45..53).toList() childOf this
        val currentPage =
            GuiButton() named "&9&lPage ${getCurrentPage()}" slot 49 childOf this material Material.COMPASS
        GuiButton()
            .click {
                left = {
                    gotoNextPage(player)
                    currentPage named "&9&lPage ${getCurrentPage()}"
                    currentPage.update(player)
                }
            } named "&bNext Page" slot 53 childOf this materialOf "arrow"
        GuiButton()
            .click {
                left = {
                    gotoPrevPage(player)
                    currentPage named "&9&lPage ${getCurrentPage()}"
                    currentPage.update(player)
                }
            } named "&bPrev Page" slot 45 childOf this materialOf "arrow"
    }
}