package com.mattmx.ktgui.examples

import com.mattmx.ktgui.components.button.GuiCycleButton
import com.mattmx.ktgui.components.button.GuiToggleButton
import com.mattmx.ktgui.components.button.LoreCycleButton
import com.mattmx.ktgui.components.button.NumberWidgetButton
import com.mattmx.ktgui.components.screen.GuiScreen
import com.mattmx.ktgui.item.ItemBuilder
import com.mattmx.ktgui.utils.Chat
import org.bukkit.Material

class ConfigScreenExample : GuiScreen("Example Config", 3) {
    init {
        val enabled = ItemBuilder(Material.LIME_STAINED_GLASS_PANE).name("&aEnabled")
        val disabled = ItemBuilder(Material.RED_STAINED_GLASS_PANE).name("&cDisabled")
        var slot = 12
        repeat(2) {
            GuiToggleButton(
                enabled.copy().lore("&8This is item $it").make(),
                disabled.copy().lore("&8This is item $it").make(),
                false
            ) { _, e, s ->
                e?.whoClicked?.sendMessage(Chat.color("&cChanged button $it! ($s)"))
            } childOf this slot slot
            slot += 2
        }
        GuiCycleButton(
            map = mutableMapOf(
                "dirt" to ItemBuilder(Material.DIRT).name("&6Dirt").lore("&8Click to cycle").make(),
                "grass_block" to ItemBuilder(Material.GRASS_BLOCK).lore("&8Click to cycle").name("&6Grass Block")
                    .make(),
                "diamond" to ItemBuilder(Material.DIAMOND_BLOCK).lore("&8Click to cycle").name("&bDiamond Block").make()
            )
        ) childOf this slot 10
        NumberWidgetButton()
            .lore {
                it.add("&8Right click to increase")
                it.add("&8Left click to decrease")
            } material Material.BLUE_STAINED_GLASS_PANE named "&9Amount widget" childOf this slot 16
        LoreCycleButton()
            .specialLore {
                it.add { l ->
                    l.id = "1"
                    l.line = "&8 ⤷ Option one"
                    l.lineSelected = "&7 ⤷ &aOption one"
                }.add { l ->
                    l.id = "2"
                    l.line = "&8 ⤷ Option two"
                    l.lineSelected = "&7 ⤷ &aOption two"
                }.add { l ->
                    l.line = "&8&oThis line is not selectable."
                }.add { l ->
                    l.id = "3"
                    l.line = " &8⤷ Option three"
                    l.lineSelected = "&7 ⤷ &aOption three"
                }.add { l ->
                    l.id = "3"
                    l.line = "  &8 ⤷ Description of option blah blah"
                    l.lineSelected = "  &7 ⤷ Description of option blah blah"
                }
            } material Material.PAPER named "&d&lLore Cycle option" childOf this slot 13
    }
}

private fun MutableList<LoreCycleButton.LoreOption>.add(cb: (LoreCycleButton.LoreOption) -> Unit) : MutableList<LoreCycleButton.LoreOption> {
    val l = LoreCycleButton.LoreOption(null, "", "")
    cb.invoke(l)
    this.add(l)
    return this
}

fun MutableList<LoreCycleButton.LoreOption>.add(line: String, id: String, lineSelected: String) {
    this.add(LoreCycleButton.LoreOption(id, line, lineSelected))
}
