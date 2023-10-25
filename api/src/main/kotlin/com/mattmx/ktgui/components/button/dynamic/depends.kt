package com.mattmx.ktgui.components.button.dynamic

import com.mattmx.ktgui.GuiManager
import com.mattmx.ktgui.components.screen.GuiScreen
import com.mattmx.ktgui.components.screen.IGuiScreen
import org.bukkit.Bukkit

class DependentCode(
    val block: GuiScreen.() -> Unit,
    val gui: GuiScreen,
    private val depends: ArrayList<Listenable<*>>
) : VariableListener {
    init {
        depends.forEach { it.addListener(this) }
    }

    override fun onChange(value: Any) {
        block(gui)
        // Update for the player(s)
        GuiManager.getPlayers(gui).forEach { uniqueId ->
            val player = Bukkit.getPlayer(uniqueId) ?: return@forEach
            // todo update slots
        }
    }

    fun destroy() {
        depends.forEach { dep -> dep.removeListener(this) }
        depends.clear()
    }

}

fun GuiScreen.depends(vararg depends: Listenable<*>, block: GuiScreen.() -> Unit): DependentCode {
    // Initially run it
    block(this)
    return DependentCode(block, this, arrayListOf(*depends))
}