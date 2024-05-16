package com.mattmx.ktgui.commands.declarative

import com.mattmx.ktgui.commands.declarative.arg.*
import com.mattmx.ktgui.commands.declarative.invocation.SuggestionInvocation
import com.mattmx.ktgui.commands.suggestions.CommandSuggestion
import com.mattmx.ktgui.commands.usage.CommandUsageOptions
import com.mattmx.ktgui.utils.not
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import kotlin.math.pow

fun main() {
    val player by argument<FakePlayer>("fakePlayer")
    val msg by argument<String>("string", true)

    val msgcmd = ("msg" / player / msg) {
        runs<Player> {
            println("[${player().name}]: ${msg()}")
        }
    } invalid {
        println("Invalid argument '${argument.name()}'.")
    }

    val op by argument<(Double, Double) -> Double>("operation")

    op suggests object : CommandSuggestion<(Double, Double) -> Double> {
        val operations = hashMapOf<String, (Double, Double) -> Double>(
            "+" to Double::plus,
            "-" to Double::minus,
            "/" to Double::div,
            "*" to Double::times,
            "^" to Double::pow
        )

        override fun getSuggestion(invocation: SuggestionInvocation<*>): List<String>? {
            return operations.keys.toList()
        }

        override fun getValue(argumentString: String?): ((Double, Double) -> Double)? {
            return operations[argumentString]
        }
    } missing { reply(!"&cYou must provide an operator.") }

    val a by doubleArgument()
    val b by doubleArgument()
    val mathCommand = ("math" / a / op / b) {
        runs<CommandSender> {
            reply(!"$a ${op.context.stringValue()} $b = ${op()(a(), b())}")
        }

        invalid { reply(!"&cProvide a number, operator and another number.") }
    }

    val splitArgs = { s: String -> s.split(" ") }

//    msgcmd.invoke(RawCommandContext(splitArgs("MattMX hello world")))
//    msgcmd.invoke(RawCommandContext(splitArgs("1etho foo bar")))
    // Empty command invocation
//    msgcmd.invoke(RawCommandContext(listOf("GabbySimon")))
//    msgcmd.invoke(RawCommandContext(listOf()))

    val myCommandUsage = CommandUsageOptions {
        arguments {
            showDescriptions = true
            showSuggestions = true
            descriptionsPrefix = "[] "
        }
    }

//    println(msgcmd.getUsage(myCommandUsage))
    val someArg by argument<FakePlayer>("fakePlayer")
    val foo = command("foo") {

        subcommand("bar") {
            runs<CommandSender> {
                println("bar")
            }
        }

        subcommand("fizz" / someArg) {
            runs<CommandSender> {
                println("fizzzzz ${someArg().name}")
            }

            invalid {
                someArg {
                    println("$provided couldn't be found!")
                }
            }
        }

        runs<CommandSender> {
            println("foo!")
        }
    }

    val foo1 =
        ("foo" /
                listOf(
                    ("fizz" / someArg) {
                        runs<CommandSender> {
                            println("fizzzzz ${someArg().name}")
                        }
                    },
                    ("bar") {
                        runs<CommandSender> {
                            println("bar")
                        }
                    })
                ) {
            runs<CommandSender> {
                println("foo!")
            }
        }

    println(foo.getUsage(myCommandUsage))
}