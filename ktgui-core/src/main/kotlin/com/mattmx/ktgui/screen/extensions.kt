package com.mattmx.ktgui.screen

import com.mattmx.ktgui.RefreshBlock
import com.mattmx.ktgui.UpdateBlock
import kotlin.time.Duration

fun <T : GuiScreen<*, *>> T.refresh(duration: Duration, block: () -> Unit) =
    RefreshBlock(block, duration, this)

infix fun <T : GuiScreen<*, *>> T.updatable(block: UpdateBlock.() -> Unit) =
    UpdateBlock(block, this)