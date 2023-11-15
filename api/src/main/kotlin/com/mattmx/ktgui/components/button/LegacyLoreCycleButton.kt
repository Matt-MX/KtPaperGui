package com.mattmx.ktgui.components.button

import com.mattmx.ktgui.components.screen.IGuiScreen
import com.mattmx.ktgui.extensions.format
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

// fixme: sometimes doesn't change on first click (after this gui has been opened already)
class LegacyLoreCycleButton(
    material: Material = Material.STONE,
    item: ItemStack? = null,
    var selected: Int = 0,
    private var changed: ((LegacyLoreCycleButton, ButtonClickedEvent?) -> Unit)? = null,
    val lores: MutableList<LoreEntry> = mutableListOf()
) : GuiButton(material, item) {
    var selectableLores = mutableListOf<Int>()

    init {
        click {
            right = {
                nextItem(player)
                changed?.invoke(this@LegacyLoreCycleButton, this)
            }
            left = {
                prevItem(player)
                changed?.invoke(this@LegacyLoreCycleButton, this)
            }
        }
    }

    fun nextItem(player: Player) {
        selected++
        if (selected >= selectableLores.size)
            selected = 0
        update(player)
    }

    fun prevItem(player: Player) {
        selected--
        if (selected < 0)
            selected = selectableLores.size - 1
        update(player)
    }

    fun changed(cb: (LegacyLoreCycleButton, ButtonClickedEvent?) -> Unit): LegacyLoreCycleButton {
        changed = cb
        return this
    }

    /**
     * Can either provide:
     * <null, String> (Denoting a normal Lore line)
     * <String, String> (ID pointing to lore value)
     */
    inline fun specialLore(lore: MutableList<LoreEntry>.() -> Unit): GuiButton {
        lore.invoke(lores)
        // get all selectable lores
        selectableLores.clear()
        val ids = arrayListOf<String>()
        lores.forEachIndexed { index, loreOption ->
            // remove any duplicates
            if (loreOption.id != null && !ids.contains(loreOption.id)) {
                ids.add(loreOption.id!!)
                selectableLores.add(index)
            }
        }
        return this
    }

    fun clearSpecialLore() : GuiButton {
        lores.clear()
        selectableLores.clear()
        selected = 0
        return this
    }

    fun getSelectedId() : String? {
        return lores().getOrNull(getSelectedNum())?.id
    }

    fun getSelectedNum() : Int {
        return selectableLores.getOrNull(selected) ?: 0
    }

    fun lores() : MutableList<LoreEntry> {
        return lores.toMutableList()
    }

    fun optionalLores() : MutableList<LoreEntry> {
        return selectableLores.map { lores[it] }.toMutableList()
    }

    override fun formatIntoItemStack(player: Player?) : ItemStack? {
        // here we need to apply and format lores
        val loreToApply = mutableListOf<String>()
        val selected = getSelectedId()
        val i = item?.clone()
        lores.forEach {lo ->
            if (lo.id != null) {
                loreToApply.add(if (lo.id == selected) lo.lineSelected else lo.line)
            } else loreToApply.add(lo.line)
        }
        i?.itemMeta?.let {
            it.lore = loreToApply
            i.itemMeta = it
        }
        i?.format(player, format)
        return i
    }

    data class LoreEntry(
        var id: String?,
        var line: String,
        var lineSelected: String)

    override fun copy(parent: IGuiScreen): GuiButton {
        val copy = LegacyLoreCycleButton(lores = lores, item = item)
        copy.selectableLores = selectableLores.toMutableList()
        copy.changed = changed
        copy.parent = parent
        copy.clickCallback = clickCallback
        copy.close = close
        return copy
    }
}

inline fun MutableList<LegacyLoreCycleButton.LoreEntry>.addLore(cb: LegacyLoreCycleButton.LoreEntry.() -> Unit) : MutableList<LegacyLoreCycleButton.LoreEntry> {
    val l = LegacyLoreCycleButton.LoreEntry(null, "", "")
    cb.invoke(l)
    this.add(l)
    return this
}

fun MutableList<LegacyLoreCycleButton.LoreEntry>.addLore(line: String, id: String, lineSelected: String) {
    this.add(LegacyLoreCycleButton.LoreEntry(id, line, lineSelected))
}