package com.mattmx.ktgui.test

import com.mattmx.ktgui.argument.*
import com.mattmx.ktgui.command.CommandInvocation

class FakePlayer(val name: String) {
    fun sendMessage(line: String) {
        println("Console -> $name: $line")
    }

    override fun toString(): String {
        return name
    }
}

fun main() {
    val mattMx = FakePlayer("MattMX")

    val feature by multiChoiceArgument(
        "SeasonalFeature" to false,
        "BoxArena" to true
    )
    val command = command("iris") {
        sub("feature" / feature) {
            runs<FakePlayer> {
                sender.sendMessage("Info for ${feature.getContext()}")
            }

            "enable".runs<FakePlayer> {
                sender.sendMessage("Enable ${feature.getContext()}")
            }

            "disable".runs<FakePlayer> {
                sender.sendMessage("Disable ${feature.getContext()}")
            }

            "reload".runs<FakePlayer> {
                sender.sendMessage("Reload ${feature.getContext()}")
            }

            val t by intArgument()
            sub("test-inner" / t).runs<FakePlayer> {
                sender.sendMessage("t = ${feature.getContext()} ${t()}")
            }
        }
    }

    val invocation = CommandInvocation(mattMx, "feature BoxArena test-inner 3".split(" ").toTypedArray(), "foo")

    command.run(invocation)
}