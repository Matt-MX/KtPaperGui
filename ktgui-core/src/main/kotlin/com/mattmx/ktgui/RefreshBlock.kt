package com.mattmx.ktgui

import com.mattmx.ktgui.screen.GuiScreen
import kotlin.time.Duration
import kotlin.time.toJavaDuration

open class RefreshBlock(
    protected val block: () -> Unit,
    protected val refreshDuration: Duration,
    protected val owner: GuiScreen<*, *>
) {
    protected var task: TaskWrapper? = null

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
        return GuiManager.getInstance().createRepeatingTask(refreshDuration.toJavaDuration()) {
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