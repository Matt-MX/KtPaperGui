package com.mattmx.ktgui.commands

import org.bukkit.Bukkit
import org.bukkit.command.CommandMap
import org.bukkit.command.CommandSender
import org.bukkit.plugin.java.JavaPlugin

open class SimpleCommandBuilder(
    var name: String = "",
    var permission: String? = null,
    vararg alias: String
) {
    var aliases = arrayListOf(*alias)
    var description: String? = ""
    val subCommands = arrayListOf<SimpleCommandBuilder>()
    var suggestSubCommands = false
    var playerOnly = false
    private var suggests: ((String) -> List<String>?)? = null
    private var execute: ((CommandSender, List<String>, String) -> Unit)? = null
    private var unknown: ((CommandSender, List<String>, String) -> Unit)? = null
    var noPermissions: ((CommandSender, List<String>, String) -> Unit)? = null
        private set

    infix fun permission(permission: String) : SimpleCommandBuilder {
        this.permission = permission
        return this
    }

    infix fun alias(alias: String) : SimpleCommandBuilder {
        this.aliases.add(alias)
        return this
    }

    fun noPermissions(cb: ((CommandSender, List<String>, String) -> Unit)? = null) : SimpleCommandBuilder {
        noPermissions = cb
        return this
    }

    infix fun subCommand(commandBuilder: SimpleCommandBuilder) : SimpleCommandBuilder {
        subCommands.add(commandBuilder)
        return this
    }

    infix fun hasPermission(executor: CommandSender) : Boolean {
        // todo check for subcommand permissions
        return permission == null || executor.hasPermission(permission!!)
    }

    infix fun executes(execute: (source: CommandSender, args: List<String>, alias: String) -> Unit) : SimpleCommandBuilder {
        this.execute = execute
        return this
    }

    infix fun unknownSubcommand(unknown: (source: CommandSender, args: List<String>, alias: String) -> Unit) : SimpleCommandBuilder {
        this.unknown = unknown
        return this
    }

    fun unknown(executor: CommandSender, args: List<String>, alias: String) {
        unknown?.let { it(executor, args, alias) }
    }

    fun executeFor(executor: CommandSender, args: List<String>, alias: String) {
        execute?.let { it(executor, args, alias) }
    }

    infix fun suggests(suggests: (String) -> List<String>?) : SimpleCommandBuilder {
        this.suggests = suggests
        return this
    }

    fun allAliases() : List<String> {
        return aliases.toMutableList() + name
    }

    fun getSuggetions(arg: String, source: CommandSender) : List<String> {
        suggests?.also {
            return it(arg) ?: listOf()
        } ?: run {
            if (suggestSubCommands) {
                return subCommands
                    .filter { it.hasPermission(source) }
                    .map { it.allAliases() }
                    .flatten()
                    .filter { it.startsWith(arg) }
            }
        }
        return listOf()
    }

    infix fun suggestSubCommands(value: Boolean) : SimpleCommandBuilder {
        this.suggestSubCommands = value
        return this
    }

    fun register(isInConfig: Boolean = false) {
        if (isInConfig) {
            Bukkit.getPluginCommand(name)?.setExecutor(DummyCommandExecutor(this))
        } else {
            val cmdMapField = Bukkit.getServer().javaClass.getDeclaredField("commandMap")
            cmdMapField.isAccessible = true
            val cmdMap = cmdMapField.get(Bukkit.getServer()) as CommandMap
            cmdMap.register(name, DummyCommand(this))
        }
    }

    fun couldBeCommand(arg: String) : Boolean {
        return name.startsWith(arg) || aliases.any { it.startsWith(arg) }
    }

    fun isCommand(arg: String) : Boolean {
        return name == arg || aliases.any { it == arg }
    }

    fun getCommand(args: List<String>) : SimpleCommandBuilder? {
        if (args.isEmpty()) return this
        subCommands.forEach { cmd ->
            if (cmd.isCommand(args[0])) {
                // if this is the last argument then this is the command
                return if (args.size > 1) cmd.getCommand(args.subList(1, args.size)) else cmd
            }
        }
        return this
    }

}

inline fun simpleCommand(cmd: (SimpleCommandBuilder.() -> Unit)) : SimpleCommandBuilder {
    val cmdB = SimpleCommandBuilder()
    cmd(cmdB)
    return cmdB
}
