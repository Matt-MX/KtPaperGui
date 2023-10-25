package com.mattmx.ktgui.components.button.dynamic

import com.mattmx.ktgui.GuiManager
import com.mattmx.ktgui.components.button.GuiButton
import org.bukkit.Bukkit
import org.bukkit.inventory.ItemStack

open class DynamicButton(val build: GuiButton.() -> Unit) : GuiButton(), VariableListener {
    private val depends = arrayListOf<Listenable<*>>()

    override fun onChange(value: Any) {
        // Rebuild
        build(this)
        // Send changes to player
        parent?.let {
            GuiManager.getPlayers(it).forEach { uniqueId ->
                val player = Bukkit.getPlayer(uniqueId) ?: return@forEach
                update(player)
            }
        }
    }

    override fun getItemStack(): ItemStack? {
        build(this)
        return super.getItemStack()
    }

    infix fun dependOn(depend: Listenable<*>): DynamicButton {
        depends.add(depend)
        depend.addListener(this)
        return this
    }

    fun getDependencies() = depends

    override fun destroy() {
        depends.forEach { it.removeListener(this) }
    }

}