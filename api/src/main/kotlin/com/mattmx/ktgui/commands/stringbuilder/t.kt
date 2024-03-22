package com.mattmx.ktgui.commands.stringbuilder

import com.mattmx.ktgui.commands.usage.CommandUsageOptions
import com.mattmx.ktgui.utils.StopWatch
import org.bukkit.command.CommandSender

fun main() {
    val player by argument<String>("player")
    val msg by argument<String>("msg", true)

    val testCmd =
        "/test $player $msg"<CommandSender> {
            runs {
                val msg by argument()

                println("[${player()}]: ${msg()}")
            }
        } missing {
            println("Missing argument '${missingArgument.name()}'")
        } args {
            "player" {
                description = "The username of the player you wish to message. "
            }
            "msg" {
                description = "The message to send to the player. "
            }
        }

//    "/hello"<CommandSender> {
//        runs {
//            println("hello")
//        }
//    } + "world"<CommandSender> {
//        runs {
//            println("hello world")
//        }
//    } + "<msg:string...?>"<CommandSender> {
//        runs {
//            val msg by argument()
//            println("hello world ($msg)")
//        }
//    }

    testCmd.invoke(RawCommandContext(listOf("mattmx", "hello", "world")))
    testCmd.invoke(RawCommandContext(listOf("1etho", "foo", "bar")))
    // Empty command invocation
    testCmd.invoke(RawCommandContext(listOf()))

    println(testCmd.getUsage(CommandUsageOptions().invoke {
        arguments {
            prefix = "<"
            suffix = " />"
            showDescriptions = true
            descriptionsPrefix = "[ ] "
        }
    }))

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