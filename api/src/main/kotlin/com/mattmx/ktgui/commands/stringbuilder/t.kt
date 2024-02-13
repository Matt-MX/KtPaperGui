package com.mattmx.ktgui.commands.stringbuilder

import com.mattmx.ktgui.commands.usage.CommandUsageOptions
import org.bukkit.command.CommandSender

fun main() {
    val testCmd =
        "/test <player:player> <msg:string...>"<CommandSender> {
            runs {
                val player by argument()
                val msg by argument()

                println("[${player}]: $msg")
            }
        } missing {
            println("Missing argument '${missingArgument.name()}'")
        } args {
            "player" {
                description = "The username of the player you wish to message. "
                suggests { listOf("mattmx", "gabs") }
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