package com.mattmx.ktgui.scheduling.builder

import com.mattmx.ktgui.scheduling.IteratingTask
import java.util.function.Consumer

interface TaskBuilder {

    fun repeating() = RepeatingTaskBuilder(isAsync())

    infix fun repeating(block: IteratingTask.() -> Unit) =
        repeating().apply { runs(block) }

    infix fun repeating(block: Consumer<IteratingTask>) =
        repeating().apply { runs { block(this) } }

    fun later() = LaterTaskBuilder(isAsync())

    infix fun later(block: IteratingTask.() -> Unit) =
        later().apply { runs(block) }

    infix fun later(block: Consumer<IteratingTask>) =
        later().apply { runs { block(this) } }

    fun isAsync() : Boolean

}

class AsyncTaskBuilder : TaskBuilder {
    override fun isAsync() = true
}

class SyncTaskBuilder : TaskBuilder {
    override fun isAsync() = false
}