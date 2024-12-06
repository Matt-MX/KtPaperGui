package com.mattmx.ktgui

import com.mattmx.ktgui.screen.GuiScreen
import kotlin.time.Duration
import kotlin.time.toJavaDuration

class RefreshBlock(
    val block: () -> Unit,
    val duration: Duration,
    val owner: GuiScreen<*, *>
) {
    var task: TaskWrapper? = null

    init {
        owner.open {
            if (task == null) {
                task = GuiManager.getInstance().createRepeatingTask(duration.toJavaDuration()) {
                    owner.refresh()
                }
            }
        }

        owner.close {
            if (GuiManager.getInstance().getActiveOfInstance(owner).isEmpty()) {
                task?.cancel()
                task = null
            }
        }
    }

}