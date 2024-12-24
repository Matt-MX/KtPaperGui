package com.mattmx.ktgui

import com.mattmx.ktgui.tasks.TaskSpec

interface TaskWrapper {

    fun getSpec() : TaskSpec<Any>

    fun cancel()

}