package com.mattmx.ktgui.tasks

abstract class TaskProvider<T, D : Any> {

    abstract fun createTask(spec: TaskSpec<T, D>): T

}