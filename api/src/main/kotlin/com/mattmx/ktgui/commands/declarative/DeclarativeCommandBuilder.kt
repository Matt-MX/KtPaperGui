package com.mattmx.ktgui.commands.declarative

import com.mattmx.ktgui.GuiManager
import com.mattmx.ktgui.commands.declarative.arg.Argument
import com.mattmx.ktgui.commands.declarative.arg.ArgumentContext
import com.mattmx.ktgui.commands.declarative.invocation.InvalidArgContext
import com.mattmx.ktgui.commands.declarative.invocation.RunnableCommandContext
import com.mattmx.ktgui.commands.declarative.invocation.StorageCommandContext
import com.mattmx.ktgui.commands.declarative.invocation.SuggestionInvocation
import com.mattmx.ktgui.commands.declarative.syntax.*
import com.mattmx.ktgui.commands.suggestions.CommandSuggestion
import com.mattmx.ktgui.commands.usage.CommandUsageOptions
import com.mattmx.ktgui.utils.JavaCompatibility
import com.mattmx.ktgui.utils.not
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.permissions.Permission
import org.bukkit.permissions.PermissionDefault
import java.util.*
import java.util.function.Consumer
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

class DeclarativeCommandBuilder<T : CommandSender>(
    val name: String,
    val senderClass: Class<T>
) {
    var description = "Command '$name'"
    var aliases = arrayOf<String>()
    var subcommands = setOf<DeclarativeCommandBuilder<*>>()
    var expectedArguments = arrayOf<Argument<*>>()
    var localArgumentSuggestions = hashMapOf<String, CommandSuggestion<*>>()
    var buildAutomaticPermissions = Optional.empty<String>()
        private set
    var permission: Optional<(StorageCommandContext<*>) -> Boolean> = Optional.empty()
        private set
    var runs: Optional<(RunnableCommandContext<T>) -> Unit> = Optional.empty()
        private set
    var missing: Optional<(InvalidArgContext<T>) -> Unit> = Optional.empty()
        private set
    var invalid: Optional<(InvalidArgContext<T>) -> Unit> = Optional.empty()
        private set
    var incorrectExecutor: Optional<(StorageCommandContext<*>) -> Unit> = Optional.empty()
        private set
    var invalidPermissions: Optional<(StorageCommandContext<*>) -> Unit> = Optional.empty()
        private set
    var unknownCommand: Optional<(StorageCommandContext<T>) -> Unit> = Optional.empty()
        private set

    infix fun permission(block: StorageCommandContext<*>.() -> Boolean) = apply {
        this.permission = Optional.of(block)
    }

    infix fun permission(node: String) = apply {
        permission { sender.hasPermission(node) }
    }

    infix fun noPermissions(block: StorageCommandContext<*>.() -> Unit) = apply {
        this.invalidPermissions = Optional.of(block)
    }

    infix fun unknownCommand(block: StorageCommandContext<T>.() -> Unit) = apply {
        this.unknownCommand = Optional.of(block)
    }

    infix fun buildAutomaticPermissions(root: String) = apply {
        this.buildAutomaticPermissions = Optional.of(root)
    }

    infix fun incorrectExecutor(block: (StorageCommandContext<*>.() -> Unit)?) = apply {
        this.incorrectExecutor = Optional.ofNullable(block)
    }

    infix fun missing(block: InvalidArgContext<T>.() -> Unit) = apply {
        this.missing = Optional.of(block)
    }

    infix fun invalid(block: InvalidArgContext<T>.() -> Unit) = apply {
        this.invalid = Optional.of(block)
    }

    infix fun runs(block: RunnableCommandContext<T>.() -> Unit) = apply {
        this.runs = Optional.of(block)
    }

    fun withDefaultUsageSubCommand(options: CommandUsageOptions = CommandUsageOptions()) = apply {
        val self = this

        subcommand<CommandSender>("usage") {
            aliases += "help"

            runs {
                reply(!self.getUsage(options))
            }
        }
    }

    infix fun Argument<*>.suggests(suggest: CommandSuggestion<T>) = apply {
        localArgumentSuggestions[name()] = suggest
    }

    fun getSuggestions(argument: Argument<*>?): CommandSuggestion<out Any?>? {
        if (argument == null) return null
        return localArgumentSuggestions[argument.name()]
            ?: argument.suggests.orElse(null)
    }

    @JavaCompatibility
    fun runs(block: Consumer<RunnableCommandContext<T>>) = apply {
        this.runs = Optional.of {
            block.accept(it)
        }
    }


    fun <S : CommandSender> checkSender(sender: S) =
        senderClass.isAssignableFrom(sender::class.java)

    inline operator fun <reified V : CommandSender> String.invoke(block: DeclarativeCommandBuilder<V>.() -> Unit) =
        fromString<V>(this).apply(block).let {
            subcommands += it
            it
        }

    inline fun <reified T : CommandSender> subcommand(
        chain: ChainCommandBuilder,
        block: DeclarativeCommandBuilder<T>.() -> Unit
    ) =
        chain.build<T>().apply(block).let {
            subcommands += it
            it
        }

    inline fun <reified T : CommandSender> subcommand(name: String, block: DeclarativeCommandBuilder<T>.() -> Unit) =
        subcommand(DeclarativeCommandBuilder(name, T::class.javaObjectType).apply(block))

    fun <T : CommandSender> subcommand(cmd: DeclarativeCommandBuilder<T>) =
        cmd.let {
            subcommands += it
            it
        }

    fun getCurrentCommandAnySender(context: SuggestionInvocation<*>): Pair<SuggestionInvocation<*>, DeclarativeCommandBuilder<*>?> {
        if (context.sender.isPresent) {
            if (!checkSender(context.sender.get())) return context to null
        }
        return getCurrentCommand(context as SuggestionInvocation<T>)
    }

    private fun getCurrentCommand(context: SuggestionInvocation<T>): Pair<SuggestionInvocation<*>, DeclarativeCommandBuilder<*>?> {
        val firstArg = context.rawArgs.firstOrNull()
            ?: return context to this

        for (cmd in subcommands) {
            if (cmd.nameEquals(firstArg)) {
                return cmd.getCurrentCommandAnySender(context.clone(context.rawArgs.subList(1, context.rawArgs.size)))
            }
        }

        return context to this
    }

    fun getSuggestions(context: SuggestionInvocation<*>): List<String> {
        val cmds = subcommands
            .filter { it.nameStarts(context.last) }
            .map { listOf(it.name) + it.aliases }
        val cmdsList = cmds.flatten()

        val arg = expectedArguments.getOrNull(context.rawArgs.size - 1)
        val suggestedArgs = getSuggestions(arg)?.getLastArgSuggestion(context)

        return if (suggestedArgs != null) {
            cmdsList + suggestedArgs
        } else cmdsList
    }

    fun nameEquals(arg: String) = name == arg || aliases.any { it == arg }

    fun nameStarts(arg: String) = name.startsWith(arg, true) || aliases.any { it.startsWith(arg, true) }

    infix fun register(localObject: Any) {
        val permissionNodePrefix = buildAutomaticPermissions.orElse(null)
        if (permissionNodePrefix != null) {
            registerPermissionWithPrefixRecursively(permissionNodePrefix)
        }

        val wrapper = DeclarativeCommandWrapper(name, this)
        GuiManager.registerCommand(localObject::class.javaObjectType, wrapper)
    }

    private fun registerPermissionWithPrefixRecursively(prefix: String) {
        val finalPrefix = "$prefix.$name"
        for (sub in subcommands) {
            sub.registerPermissionWithPrefixRecursively(finalPrefix)
        }
        registerPermissionWithPrefix(finalPrefix)
    }

    private fun registerPermissionWithPrefix(node: String) {
        val permission = Permission(node, description, PermissionDefault.FALSE)

        Bukkit.getPluginManager().addPermission(permission)
        if (this.permission.isEmpty) {
            permission(node)
        } else {
            val old = this@DeclarativeCommandBuilder.permission.get()
            permission { old.invoke(this) && sender.hasPermission(node) }
        }
    }

    fun invokeAnySender(context: StorageCommandContext<*>) {
        val result = runCatching {
            // todo we should find the command before invoking it
            invoke(context as StorageCommandContext<T>)
        }
        if (result.isFailure) {
            if (incorrectExecutor.isPresent) {
                incorrectExecutor.get().invoke(context)
            } else {
                context.reply(!"&cThis command can only be ran by ${senderClass.simpleName}s.")
            }
        }
    }

    fun invoke(context: StorageCommandContext<T>) {

        if (permission.isPresent && !permission.get().invoke(context)) {
            if (invalidPermissions.isPresent) {
                invalidPermissions.get().invoke(context)
            } else {
                context.reply(Bukkit.permissionMessage())
            }

            return
        }

        val argumentValues = hashMapOf<String, ArgumentContext<*>>()
        for ((index, arg) in context.rawArgs.withIndex()) {

            // Check sub-commands
            // todo could match multiple subcommands
            val cmd = subcommands.firstOrNull { it.nameEquals(arg) }
            if (cmd != null) {
                return cmd.invokeAnySender(
                    context.clone(context.rawArgs.subList(1, context.rawArgs.size))
                )
            }

            // Check arguments
            val expectedArg = expectedArguments.getOrNull(index)
            if (expectedArg != null) {
                val value = if (expectedArg.type().isVararg) {
                    context.rawArgs.subList(index, context.rawArgs.size).joinToString(" ")
                } else context.rawArgs.getOrNull(index)

                if (expectedArg.isRequired() && value == null) {
                    val missingArgContext =
                        InvalidArgContext(context.sender, context.alias, context.rawArgs, expectedArg, null)

                    // Try and invoke this specific missing arg
                    if (expectedArg.invokeMissing(missingArgContext)) {
                        return
                    } else if (expectedArg.invokeInvalid(missingArgContext)) {
                        return
                    }

                    // Invoke the global missing or invalid
                    if (missing.isPresent) {
                        missing.get().invoke(missingArgContext)
                    } else if (invalid.isPresent) {
                        invalid.get().invoke(missingArgContext)
                    }

                    return
                } else {
                    val actualValue = if (localArgumentSuggestions.contains(expectedArg.name())) {
                        localArgumentSuggestions[expectedArg.name()]?.getValue(value.toString())
                    } else if (expectedArg.suggests.isPresent) {
                        expectedArg.suggests.get().getValue(value.toString())
                    } else value

                    if (actualValue == null) {
                        val invalidArgumentContext =
                            InvalidArgContext(context.sender, context.alias, context.rawArgs, expectedArg, value)

                        if (expectedArg.invokeInvalid(invalidArgumentContext)) {
                            return
                        }

                        invalid.ifPresent { it.invoke(invalidArgumentContext) }
                        return
                    }

                    argumentValues[expectedArg.name()] = expectedArg.createContext(value, actualValue)

                    // todo args types should have a method to "eat up" arguments at will
                    // e.g when calling arg.createContext we should return what arguments have been used in this argument
                    //  hence, the index in the list should be adjusted to match. This way varargs can be limited lengths.

                    if (expectedArg.type().isVararg) {
                        break
                    }
                }
            } else {
                if (unknownCommand.isPresent) {
                    unknownCommand.get().invoke(context)
                } else {
                    context.reply(!"&cUnknown sub-command or arguments.")
                }
                return
            }
        }

        val missing = expectedArguments.filter { arg ->
            !argumentValues.containsKey(arg.name())
        }

        if (missing.isNotEmpty()) {
            missing.forEach { arg ->
                val missingArgContext =
                    InvalidArgContext(context.sender, context.alias, context.rawArgs, arg, null)

                // Try and invoke this specific missing arg
                if (arg.invokeMissing(missingArgContext)) {
                    return
                } else if (arg.invokeInvalid(missingArgContext)) {
                    return
                }
            }
            return
        }

        val runnableContext =
            RunnableCommandContext(context.sender, context.alias, context.rawArgs, argumentValues)
        runs.ifPresent { it.invoke(runnableContext) }
    }

    /**
     * Builds a usage for this command.
     * You can create your own method with the [Configuration] class.
     *
     * @return a formatted string for usage of the command
     */
    fun getUsage(options: CommandUsageOptions = CommandUsageOptions(), rootCommand: Boolean = true): String {
        var builder = "${if (rootCommand) options.namePrefix else ""}$name${options.gap}"

        if (subcommands.isNotEmpty()) {
            builder += subcommands.joinToString(options.subCommands.divider) { subcommand -> subcommand.name }
            if (expectedArguments.isNotEmpty()) {
                builder += options.subCommands.divider
            }
        }

        var end = ""
        builder += expectedArguments.joinToString(" ") { arg ->

            val suggestions =
                if (options.arguments.showSuggestions) {
                    val suggestions = arg.getDefaultSuggestions()
                    if (!suggestions.isNullOrEmpty()) {
                        val opt = options.arguments
                        "${opt.suggestionsChar}${opt.suggestionsPrefix}${suggestions.joinToString(opt.suggestionsDivider)}${opt.suggestionsSuffix}"
                    } else "${options.arguments.typeChar}${arg.type().typeName}${if (arg.type().isVararg) "..." else ""}"
                } else "${options.arguments.typeChar}${arg.type().typeName}${if (arg.type().isVararg) "..." else ""}"

            // Apply descriptions
            if (options.arguments.showDescriptions) {
                val extra =
                    if (arg.isRequired()) options.arguments.descriptionsRequired else options.arguments.descriptionsOptional
                end += "\n${options.arguments.descriptionsPrefix}${arg.name()}${options.arguments.descriptionDivider}${arg.description()}$extra"
            }

            "${options.arguments.prefix}${arg.name()}${if (arg.isRequired()) options.arguments.required else options.arguments.optional}$suggestions${options.arguments.suffix}"
        }
        builder += end
        return builder
    }

    companion object {

        inline fun <reified T : CommandSender> fromString(source: String): DeclarativeCommandBuilder<T> =
            fromString(T::class.javaObjectType, source)

        @JvmStatic
        @JavaCompatibility
        fun <T : CommandSender> fromString(targetSender: Class<T>, source: String): DeclarativeCommandBuilder<T> {
            var name: String? = null
            val expectedArguments = arrayListOf<Argument<*>>()
            val parsed = Parser(source).parse()

            for (syntax in parsed) {
                when (syntax) {
                    is VariableDeclarationSyntax -> {
                        expectedArguments += Argument<Any>(
                            syntax.getName(),
                            syntax.getType(),
                            null,
                            !syntax.getType().isOptional
                        )
                    }

                    is CommandDeclarationSyntax -> {
                        // todo should be top level only
                        name = syntax.getName()
                    }

                    is SubCommandDeclarationSyntax -> {
                        name = syntax.getName()
                    }
                }
            }

            if (name == null) error("Name cannot be null for a command!")
            return DeclarativeCommandBuilder<T>(name, targetSender)
                .apply {
                    this.expectedArguments += expectedArguments
                }
        }
    }
}

inline operator fun <reified T : CommandSender> String.invoke(block: DeclarativeCommandBuilder<T>.() -> Unit) =
    DeclarativeCommandBuilder.fromString<T>(this).apply(block)

inline fun <reified T : CommandSender> command(name: String, block: DeclarativeCommandBuilder<T>.() -> Unit) =
    DeclarativeCommandBuilder<T>(name, T::class.java).apply(block)

fun <T : Any> argument(type: String, isVarArg: Boolean = false): ReadOnlyProperty<Nothing?, Argument<T>> {
    val arg = argument<T>(type, "delegated_arg", isVarArg)

    return ReadOnlyProperty { ref: Nothing?, property: KProperty<*> ->
        arg.apply {
            name(property.name)
        }
    }
}

fun <T : Any> argument(type: String, name: String, isVarArg: Boolean) =
    Argument<T>(name, VariableType(type, isVarArg, false))