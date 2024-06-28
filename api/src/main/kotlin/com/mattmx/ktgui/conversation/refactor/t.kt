package com.mattmx.ktgui.conversation.refactor

import com.mattmx.ktgui.conversation.refactor.steps.EnumConversationStep
import com.mattmx.ktgui.utils.not
import org.bukkit.Bukkit
import org.bukkit.entity.Player

fun main() {
    conversation {
        exit {
            println("exit")
        }

        start {
            println("start")
        }

        getEnum<EnumConversationStep.Empty, Player> {
            runs {
                println(result.orElse(null))
            } invalid {
                println("${result.get()} is not a valid choice.")
            }
        }

        getChoice {
            choices = mutableListOf("yes", "no")
            runs {
                println(result.orElse(null))
            } invalid {
                println("${result.get()} is not a valid choice.")
            }
        }

        getString {
            message = !"""
                &7Please input a player's username.
            """.trimIndent()

            matches {
                Bukkit.getOnlinePlayers()
                    .firstOrNull { it.name == result.orElse(null) } != null
            } invalid {
                println("not a valid username '${result.get()}'")
            } runs {
                println(result.get())
            }
        }

        getRegExp {
            regex = "[A-Za-z0-9_]{3,16}".toRegex()
            runs {
                println(result.get())
            } invalid {
                println("${result.get()} doesn't match")
            }
        }

        getInteger {
            range = (1..20)
            runs {
                println(result.get() * 2)
            }
        }
    } timeout 30 exitOn "cancel"
}