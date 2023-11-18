package com.mattmx.ktgui.commands.smart

import kotlin.concurrent.thread

fun <S : CommandSender, T> KtCommandBuilder<S>.argument(suggests: (CommandContext<S>.() -> List<String>)?) =
    Argument<S, T, String>(ArgumentType.REQUIRED_SINGLE, { c -> c.args[0] }, suggests).apply { this@argument.expectedArguments += this }

fun <S : CommandSender> command(name: String, builder: KtCommandBuilder<S>.() -> Unit) =
    KtCommandBuilder<S>(name).apply(builder)

fun argsOf(string: String) = string.split(" ").toTypedArray()
fun main() {
    val msg = command<CommandSender>("msg") {
        val username by argument { listOf("meow") }

        runs {
            thread {
                Thread.sleep(1000)
                println("[You -> $username]")
            }
        }
    }

    println(msg.getUsage())
    msg(CommandContext(CommandSender(), argsOf("MattMX")))
    msg(CommandContext(CommandSender(), argsOf("GabbySimon")))
}