package com.mattmx.ktgui.commands.stringbuilder

import com.mattmx.ktgui.commands.usage.CommandUsageOptions
import org.bukkit.command.CommandSender

fun main() {
    val player by argument<FakePlayer>("string")
    val msg by argument<String>("string", true)

    val testCmd =
        "/msg $player $msg"<CommandSender> {
            runs {
                println("[${player()?.name}]: ${msg()}")
            }
        } missing {
            println("Missing argument '${missingArgument.name()}'")
        } invalid {
            player {
                println("That user is not online!")
            }
        }

    println(testCmd.source)

    val splitArgs = { s: String -> s.split(" ")  }

    testCmd.invoke(RawCommandContext(splitArgs("MattMX hello world")))
    testCmd.invoke(RawCommandContext(splitArgs("1etho foo bar")))
    // Empty command invocation
    testCmd.invoke(RawCommandContext(listOf("GabbySimon")))
    testCmd.invoke(RawCommandContext(listOf()))

    println(
        testCmd.getUsage(
            CommandUsageOptions {
                arguments {
                    showDescriptions = true
                    descriptionsPrefix = "[] "
                }
            }
        )
    )

    val foo = +
    "/foo"<CommandSender> {

        "bar"<CommandSender> {
            runs {
                println("foo bar")
            }
        }

        "fizz"<CommandSender> {
            runs {
                println("foo fizz")
            }
        }

        runs {
            println("foo!")
        }

    } missing {
        println("missing arg")
    }
}