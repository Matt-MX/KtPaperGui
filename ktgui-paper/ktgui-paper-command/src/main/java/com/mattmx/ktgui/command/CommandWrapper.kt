package com.mattmx.ktgui.command

import com.mattmx.ktgui.event.ContinuousEvent
import com.mattmx.ktgui.event.ContinuousEventCallback
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import io.papermc.paper.command.brigadier.CommandSourceStack

class CommandWrapper(
    val root: LiteralArgumentBuilder<*>
) {
    var description: String = "No description provided"
    private val preCommandExecution = hashMapOf<String, ContinuousEventCallback<CommandInvocationEvent>>()

    fun preCommand(command: String, callback: CommandInvocationEvent.() -> Unit) {
        preCommandExecution.getOrPut(command) { ContinuousEventCallback() }.invoke(callback)
    }

    class CommandInvocationEvent(
        val context: CommandContextWrapper<CommandSourceStack, *>
    ) : ContinuousEvent {
        private var cancelled = false

        override fun shouldContinueCallback(value: Boolean) {
            cancelled = value
        }

        override fun shouldContinueCallback(): Boolean {
            return cancelled
        }
    }
}