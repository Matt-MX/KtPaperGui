package com.mattmx.ktgui.components

import com.mattmx.ktgui.components.button.GuiButton
import com.mattmx.ktgui.components.button.IGuiButton
import com.mattmx.ktgui.components.screen.GuiScreen
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

class GuiPattern(
    private var pattern: String
) {
    private val items = hashMapOf<Char, IGuiButton>()
    var blankSpaceChar = '-'

    fun apply(guiScreen: GuiScreen) {
        // Can't effectively apply the Gui Pattern to a non-row gui
        if (guiScreen.type != null) return
        val map = build(guiScreen.rows)
        map.entries.forEach {
            it.key childOf guiScreen slots it.value
        }
    }

    fun build(rows: Int) : Map<IGuiButton, ArrayList<Int>> {
        val map = mutableMapOf<IGuiButton, ArrayList<Int>>()
        val maxSlot = rows * 9
        trimPattern()
        val charArr = pattern.toCharArray()
        repeat(maxSlot) { slot ->
            val index = maxSlot - slot - 1
            val char = charArr.getOrNull(index) ?: return map
            if (char != blankSpaceChar) {
                val item = items[char] ?: GuiButton(Material.AIR)
                // Add the item to the list
                map[item]?.also { it.add(index) }
                    ?: run { map[item] = arrayListOf(index) }
            }
        }
        return map
    }

    fun setPattern(pattern: String) {
        this.pattern = pattern
    }

    fun getPattern() = pattern

    fun trimPattern() {
        pattern = pattern.replace("\n", "")
            .replace("\r\n", "")
    }

    operator fun set(char: Char, item: ItemStack) = items.put(char, GuiButton(item = item))
    operator fun set(char: Char, button: IGuiButton) = items.put(char, button)
    operator fun get(char: Char) : IGuiButton? = items[char]
}

fun GuiScreen.applyPattern(pattern: GuiPattern) {
    pattern.apply(this)
}