package com.mattmx.ktgui.commands

import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.command.ConsoleCommandSender
import org.bukkit.entity.Player
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

class KtCommandBuilder<T : CommandSender>(val name: String) {
    var aliases = arrayOf<String>()
    var subCommands = arrayOf<KtCommandBuilder<*>>()
    var arguments = arrayOf<Argument<*, *>>()
    private lateinit var permission: (T) -> Boolean
    private lateinit var runs: (T) -> Unit

    fun permission(block: T.() -> Boolean) {
        this.permission = block
    }

    fun runs(block: T.() -> Unit) {
        this.runs = block
    }

    fun register() {

    }

    fun getSuggestions(args: String, sender: T) : List<String> {
        // todo get suggestions for current argument
        TODO()
    }

    /**
     * Used to find the current argument
     *
     * @param args the current arguments of the command invocation
     * @return the current argument or null if it is invalid
     */
    private fun getArgument(args: String) : Argument<*, *>? {
        TODO()
    }

    /**
     * Used to find the current sub-command
     *
     * @param args of the current command invocation
     * @return the current sub-command or null if it is invalid
     */
    private fun getCommand(args: String, sender: T) : KtCommandBuilder<*>? {
        TODO()
    }

    /**
     * Builds a usage for this command
     *
     * @return a formatted string for usage of the command
     */
    fun getUsage() = "$name " + arguments.joinToString(" ") { arg ->
        val suggestions = arg.suggests?.invoke()?.let {
            if (it.size <= 3) " = [" + it.joinToString("|") + "]"
            else null
        }

        when (arg.type) {
            ArgumentType.REQUIRED_SINGLE -> "<${arg.id}!$suggestions>"
            ArgumentType.OPTIONAL_SINGLE -> "<${arg.id}?$suggestions>"
            else -> "<${arg.id}...>"
        }
    }
}


inline fun <T : CommandSender> command(name: String, builder: KtCommandBuilder<T>.() -> Unit): KtCommandBuilder<T> {
    val commandBuilder = KtCommandBuilder<T>(name)
    builder(commandBuilder)
    return commandBuilder
}

inline fun <V : CommandSender> KtCommandBuilder<*>.subCommand(
    name: String,
    builder: KtCommandBuilder<V>.() -> Unit
): KtCommandBuilder<V> {
    val subCommand = command(name, builder)
    // todo register
    return subCommand
}

open class Argument<T, V>(
    val id: String,
    val suggests: (() -> List<String>)?,
    val type: ArgumentType,
    defaultValue: V
) : ReadWriteProperty<T, V> {
    private var value: V = defaultValue

    fun get() = value

    override fun getValue(thisRef: T, property: KProperty<*>): V {
        return value
    }

    override fun setValue(thisRef: T, property: KProperty<*>, value: V) {
        this.value = value
    }
}

enum class ArgumentType {
    REQUIRED_SINGLE,
    OPTIONAL_SINGLE,
    GREEDY
}

fun <S : CommandSender, T> KtCommandBuilder<S>.greedyArgument(
    id: String,
    suggests: (() -> List<String>)? = null
): Argument<T, List<String>> {
    val argument = Argument<T, List<String>>(id, suggests, ArgumentType.GREEDY, listOf<String>())
    this.arguments += argument
    return argument
}

fun <S : CommandSender, T> KtCommandBuilder<S>.optionalArgument(id: String, suggests: (() -> List<String>)? = null) =
    argument<S, T, String?>(id, suggests, ArgumentType.OPTIONAL_SINGLE, null)

fun <S : CommandSender, T> KtCommandBuilder<S>.argument(id: String, suggests: (() -> List<String>)? = null) =
    argument<S, T, String>(id, suggests, ArgumentType.REQUIRED_SINGLE, "")

fun <S : CommandSender, T, V> KtCommandBuilder<S>.argument(
    id: String,
    suggests: (() -> List<String>)?,
    type: ArgumentType,
    defaultValue: V
): Argument<T, V> {
    val argument = Argument<T, V>(id, suggests, type, defaultValue)
    this.arguments += argument
    return argument
}

fun main() {
    // runs "/hello <player!> <shouldPing? = [true|false]> <optional = ...>"
    command<Player>("hello") {
        aliases += "hey"

        permission { hasPermission("hello.run") }

        // [something] is required -> Lists all online players
        val something by argument("name") { Bukkit.getOnlinePlayers().map { it.name } }
        // [shouldPing] is optional -> Lists "true" and "false" as options
        val shouldPing by optionalArgument("shouldPing") { listOf("true", "false") }
        // [optional] is the rest of the arguments
        val optional by greedyArgument("additional")

        runs {
            sendMessage("[You -> $something] ${optional.joinToString(" ")}")
            if (shouldPing == "true") {
                val player = Bukkit.getPlayer(something)
                player?.sendMessage("[$name -> You] ${optional.joinToString(" ")})")
            }
        }
    }.register()

    // runs "/test [player|console]"
    command<CommandSender>("test") {
        // Add subcommand called "player" that only a player may run
        subCommand<Player>("player") {
            runs {
                sendMessage("You're a player!")
            }
        }
        // Add subcommand called "console" that only console sender may run
        subCommand<ConsoleCommandSender>("console") {
            runs {
                sendMessage("Hello, console!")
            }
        }

        runs {
            sendMessage("Hello, please specify if you are a 'player' or 'console'")
        }
    }
}