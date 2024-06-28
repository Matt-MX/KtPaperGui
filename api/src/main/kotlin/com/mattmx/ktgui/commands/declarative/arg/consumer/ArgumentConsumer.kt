package com.mattmx.ktgui.commands.declarative.arg.consumer

fun interface ArgumentConsumer {

    fun consume(args: List<String>) : List<String>

    fun isVarArg() = false

}