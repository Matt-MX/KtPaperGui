package com.mattmx.ktgui.commands.stringbuilder

import org.bukkit.command.CommandSender

fun main() {
    val testCmd = +
    "/test <player:player> <msg:string...>"<CommandSender> {
        runs {
            val player by argument()
            val msg by argument()

            println("[${player}]: $msg")
        }
    } missing {
        println("Missing arg '${argument.name()}'")
    }

    "/hello"<CommandSender> {
        runs {
            println("hello")
        }
    } + "world"<CommandSender> {
        runs {
            println("hello world")
        }
    } + "<msg:string...?>"<CommandSender> {
        runs {
            val msg by argument()
            println("hello world ($msg)")
        }
    }

    testCmd.invoke(RawCommandContext(listOf("mattmx", "hello", "world")))

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

    println(testCmd.getUsage())
}