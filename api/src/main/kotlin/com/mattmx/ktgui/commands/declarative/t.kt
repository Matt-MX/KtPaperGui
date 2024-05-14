package com.mattmx.ktgui.commands.declarative

import com.mattmx.ktgui.commands.usage.CommandUsageOptions
import org.bukkit.command.CommandSender

fun main() {
    val player by argument<FakePlayer>("fakePlayer")
    val msg by argument<String>("string", true)

    val msgcmd = ("msg" / player / msg)<CommandSender> {
        runs {
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
    val foo = command<CommandSender>("foo") {

        subcommand<CommandSender>("bar") {
            runs {
                println("bar")
            }
        }

        subcommand<CommandSender>("fizz" / someArg) {
            runs {
                println("fizzzzz ${someArg().name}")
            }

            invalid {
                someArg {
                    println("$provided couldn't be found!")
                }
            }
        }

        runs {
            println("foo!")
        }
    }

    val foo1 = ("foo" /
            (
                    ("fizz" / someArg)<CommandSender> {
                        runs {
                            println("fizzzzz ${someArg().name}")
                        }
                    } + ("bar")<CommandSender> {
                        runs {
                            println("bar")
                        }
                    })
            )<CommandSender> {
        runs {
            println("foo!")
        }
    }

    println(foo.getUsage(myCommandUsage))
}