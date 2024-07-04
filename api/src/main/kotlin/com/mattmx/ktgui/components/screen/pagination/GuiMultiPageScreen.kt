package com.mattmx.ktgui.components.screen.pagination

import com.mattmx.ktgui.components.screen.GuiScreen
import com.mattmx.ktgui.dsl.button
import com.mattmx.ktgui.event.EventCallback
import com.mattmx.ktgui.utils.not
import net.kyori.adventure.text.Component
import org.bukkit.Material
import org.bukkit.entity.Player
import java.util.*
import kotlin.math.max
import kotlin.math.min

open class GuiMultiPageScreen(
    title: Component,
    rows: Int = 6
) : GuiScreen(title, rows) {
    var currentPage = 0
        set(value) {
            field = value
            refresh()
            pageChange.invoke(value)
        }
    val pageChange = EventCallback<Int>()
    val pages = Collections.synchronizedList(arrayListOf<GuiScreen>())

    override fun open(player: Player) {
        TODO()
    }

    infix fun page(block: GuiScreen.() -> Unit) = page(null, block)
    open fun page(index: Int? = null, block: GuiScreen.() -> Unit) = apply {
        val sub = GuiScreen(Component.empty(), rows).apply(block)
        if (index == null) {
            pages.add(sub)
        } else {
            pages[index] = sub
        }
    }

    open fun navigatePreviousPage() {
        currentPage = max(0, currentPage - 1)
    }

    open fun navigateNextPage() {
        currentPage = min(pages.size, currentPage + 1)
    }
}

fun multiPageGui(title: Component, rows: Int = 6, block: GuiMultiPageScreen.() -> Unit) =
    GuiMultiPageScreen(title, rows).apply(block)