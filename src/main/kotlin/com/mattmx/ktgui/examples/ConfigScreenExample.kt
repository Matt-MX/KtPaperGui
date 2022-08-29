package com.mattmx.ktgui.examples

import com.mattmx.ktgui.components.button.GuiToggleButton
import com.mattmx.ktgui.components.screen.GuiScreen
import com.mattmx.ktgui.item.ItemBuilder
import com.mattmx.ktgui.utils.Chat
import org.bukkit.Material

class ConfigScreenExample : GuiScreen("Example Config", 3) {
    init {
        val enabled = ItemBuilder(Material.LIME_STAINED_GLASS_PANE).name("&aEnabled")
        val disabled = ItemBuilder(Material.RED_STAINED_GLASS_PANE).name("&cDisabled")
        var slot = 10
        repeat(4) {
            GuiToggleButton(enabled.copy().lore("&8This is item $it").make(),
                disabled.copy().lore("&8This is item $it").make(),
                false
            ) { _, e, s ->
                e?.whoClicked?.sendMessage(Chat.color("&cChanged button $it! ($s)"))
            } childOf this slot slot
            slot += 2
        }
    }
}