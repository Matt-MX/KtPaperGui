package com.mattmx.ktgui.commands.alpha

import kotlin.concurrent.thread

fun <S : CommandSender, T> KtCommandBuilder<S>.argument(description: String? = null, suggests: (CommandContext<S>.() -> List<String>)? = null) =
    Argument<S, T, String>(ArgumentType.REQUIRED_SINGLE, { c -> c.args[0] }, suggests).apply {
        this withDescription description
        this@argument.expectedArguments += this
    }

fun <S : CommandSender> command(name: String, builder: KtCommandBuilder<S>.() -> Unit) =
    KtCommandBuilder<S>(name).apply(builder)

fun argsOf(string: String) = string.split(" ").toTypedArray()
fun main() {
    val msg = command<CommandSender>("msg") {
        val username by argument("The user of whom to directly message.") { listOf("meow") }
        val message by argument("The message you would like to send.")

        permission { /* Boolean Condition */ true }

        runs {
            thread {
                Thread.sleep(1000)
                println(ref("username"))
                println("[You -> $username] $message")
            }
        }
    }

    println(msg.getUsage(true))
    msg(CommandContext(CommandSender(), argsOf("MattMX test")))
    msg(CommandContext(CommandSender(), argsOf("GabbySimon")))
}