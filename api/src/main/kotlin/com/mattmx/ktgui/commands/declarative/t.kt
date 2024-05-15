package com.mattmx.ktgui.commands.declarative

import com.mattmx.ktgui.commands.usage.CommandUsageOptions
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

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