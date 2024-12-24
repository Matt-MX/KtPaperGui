package com.mattmx.ktgui.tasks

import com.mattmx.ktgui.TaskWrapper

abstract class TaskProvider<T : TaskWrapper> {

    fun createTaskAny(spec: TaskSpec<Any>): T {
        val specCast = spec as? TaskSpec<T>
            ?: error("Invalid TaskSpec type.")

        return createTask(specCast)
    }

    abstract fun createTask(spec: TaskSpec<T>): T

}