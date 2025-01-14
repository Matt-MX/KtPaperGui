package com.mattmx.ktgui.command

import com.mattmx.ktgui.command.arg.ArgumentBuilderWrapper
import com.mojang.brigadier.Command
import com.mojang.brigadier.builder.ArgumentBuilder
import com.mojang.brigadier.context.CommandContext
import io.papermc.paper.command.brigadier.CommandSourceStack
import org.bukkit.command.CommandSender
import java.util.concurrent.CompletableFuture

var ArgumentBuilder<CommandSourceStack, *>.permission: String
    set(value) = requires { it.sender.hasPermission(value) }.let {  }
    get() = error("Cannot retrieve the permission string from a predicate.")

inline fun <reified S : CommandSender> ArgumentBuilderWrapper.runsAsync(
    crossinline block: PaperCommandContextWrapper<S>.() -> Unit
) = runs<S> {
    CompletableFuture.supplyAsync { block(this) }
}

inline fun <reified S : CommandSender> ArgumentBuilderWrapper.runs(
    crossinline block: PaperCommandContextWrapper<S>.() -> Unit
) = apply {
//    val existingExecution = this.command
    val senderClass = S::class.java

    owner.executes { invocation ->
        if (invocation.source !is CommandSourceStack) {
            return@executes 0
        }

        val castInvocation = invocation as CommandContext<CommandSourceStack>

//        existingExecution?.run(invocation)

        if (senderClass.isInstance(castInvocation.source.sender)) {
            val wrapper = PaperCommandContextWrapper<S>(castInvocation)
            block.invoke(wrapper)
        }

        Command.SINGLE_SUCCESS
    }
}