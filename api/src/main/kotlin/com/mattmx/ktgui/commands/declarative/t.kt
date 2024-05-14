package com.mattmx.ktgui.commands.declarative

import com.mattmx.ktgui.commands.declarative.invocation.StorageCommandContext
import com.mattmx.ktgui.commands.usage.CommandUsageOptions
import org.bukkit.command.CommandSender

fun main() {
    val player by argument<FakePlayer>("fakePlayer")
    val msg by argument<String>("string", true)

    val msgcmd =
        ("msg" / player / msg)<CommandSender> {
            runs {
                println("[${player()?.name}]: ${msg()}")
            }
        } missing {
            println("Missing argument '${argument.name()}'.")
        } invalid {
            player {
                println("The user '$provided' is not online!")
            }
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

    val foo = command<CommandSender>("foo") {

        subcommand<CommandSender>("bar") {
            runs {
                println("bar")
            }
        }

        val someArg by argument<FakePlayer>("fakePlayer")
        subcommand<CommandSender>("fizz" / someArg) {
            runs {
                println("fizzzzz ${someArg()?.name}")
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

    println(foo.getUsage(myCommandUsage))
}