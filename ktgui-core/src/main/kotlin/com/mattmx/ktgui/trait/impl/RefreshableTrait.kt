package com.mattmx.ktgui.trait.impl

import com.mattmx.ktgui.GuiManager
import com.mattmx.ktgui.TaskWrapper
import com.mattmx.ktgui.screen.GuiScreen
import com.mattmx.ktgui.tasks.TaskSpec
import com.mattmx.ktgui.trait.AbstractTrait
import java.util.*
import kotlin.time.Duration

class RefreshableTrait(
    protected val block: (RefreshableTrait) -> Unit,
    protected val refreshDuration: Duration,
    owner: GuiScreen<*, *>
) : AbstractTrait<GuiScreen<*, *>>(owner) {
    protected var task: TaskWrapper? = null

    override fun onEnable() {
        getOwner().open {
            if (task == null) {
                task = createTask()
            }
        }

        getOwner().close {
            if (GuiManager.getInstance().getActiveOfInstance(getOwner()).isEmpty()) {
                task?.cancel()
                task = null
            }
        }
    }

    fun createTask(): TaskWrapper {

        val spec = TaskSpec<Any>(
            callback = {
                block(this)
            },
            true,
            Optional.of(refreshDuration),
            Optional.of(refreshDuration)
        )

        return GuiManager.getInstance()
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