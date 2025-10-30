package com.mattmx.ktgui

import com.mattmx.ktgui.screen.GuiScreen
import com.mattmx.ktgui.tasks.TaskSpec
import com.mattmx.ktgui.trait.AbstractTrait
import java.util.*
import kotlin.time.Duration

// TODO: convert to trait
open class RefreshBlock(
    protected val block: (RefreshBlock) -> Unit,
    protected val refreshDuration: Duration,
    protected val owner: GuiScreen<*, *>
) : AbstractTrait<GuiScreen<*, *>>(owner) {
    protected var task: TaskWrapper? = null

    override fun onEnable() {
        block(this)

        owner.open {
            if (task == null) {
                task = createTask()
            }
        }

        owner.close {
            if (KtGui.getInstance().getActiveOfInstance(owner).isEmpty()) {
                task?.cancel()
                task = null
            }
        }
    }

    override fun onDisable() {
        stop()
    }

    fun createTask(): TaskWrapper {

        val spec = TaskSpec<Any>(
            callback = {
                block(this)
                owner.refresh()
            },
            true,
            Optional.of(refreshDuration),
            Optional.of(refreshDuration)
        )

        return KtGui.getInstance()
            .getTaskProvider()
            .createTaskAny(spec)
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