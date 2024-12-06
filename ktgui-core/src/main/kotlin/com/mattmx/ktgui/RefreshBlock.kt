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
        block()

        owner.open {
            if (task == null) {
                task = createTask()
            }
        }

        owner.close {
            if (GuiManager.getInstance().getActiveOfInstance(owner).isEmpty()) {
                task?.cancel()
                task = null
            }
        }
    }

    fun createTask() : TaskWrapper {
        return GuiManager.getInstance().createRepeatingTask(duration.toJavaDuration()) {
            block()
            owner.refresh()
        }
    }

    fun resume() {
        if (task == null) {
            task = createTask()
        }
    }

    fun stop() {
        task?.cancel()
        task = null
    }

    fun isActive() = task != null
}