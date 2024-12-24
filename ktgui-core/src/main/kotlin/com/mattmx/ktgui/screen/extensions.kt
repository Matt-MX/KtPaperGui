package com.mattmx.ktgui.screen

import com.mattmx.ktgui.RefreshBlock
import com.mattmx.ktgui.UpdateBlock
import kotlin.time.Duration

fun <T : GuiScreen<*, *>> T.refreshBlock(duration: Duration, block: () -> Unit) =
    refresh(duration) { block() }

fun <T : GuiScreen<*, *>> T.refresh(duration: Duration, block: (RefreshBlock) -> Unit) =
    RefreshBlock(block, duration, this).also { trait ->
        this.traits.register(trait)
        trait.onEnable()
    }

infix fun <T : GuiScreen<*, *>> T.updatable(block: UpdateBlock.() -> Unit) =
    UpdateBlock(block, this)

fun <T : GuiScreen<*, *>, E> T.stateful(initial: E, block: StatefulGui<T, E>.() -> Unit) =
    StatefulGui(this, initial).apply(block)