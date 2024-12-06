package com.mattmx.ktgui.screen

import com.mattmx.ktgui.RefreshBlock
import kotlin.time.Duration

fun <T : GuiScreen<*, *>> T.refresh(duration: Duration, block: () -> Unit) = apply {
    RefreshBlock(block, duration, this)
}