package com.mattmx.ktgui.commands.declarative

import com.mattmx.ktgui.GuiManager
import com.mattmx.ktgui.commands.declarative.arg.Argument
import com.mattmx.ktgui.commands.declarative.arg.ArgumentContext
import com.mattmx.ktgui.commands.declarative.arg.ArgumentProcessor
import com.mattmx.ktgui.commands.declarative.arg.consumer.GreedyArgumentConsumer
import com.mattmx.ktgui.commands.declarative.arg.consumer.SingleArgumentConsumer
import com.mattmx.ktgui.commands.declarative.arg.consumers.ArgumentConsumer
import com.mattmx.ktgui.commands.declarative.arg.impl.FlagArgument
import com.mattmx.ktgui.commands.declarative.arg.impl.OptionArgument
import com.mattmx.ktgui.commands.declarative.arg.impl.OptionSyntax
import com.mattmx.ktgui.commands.declarative.invocation.*
import com.mattmx.ktgui.commands.declarative.syntax.*
import com.mattmx.ktgui.commands.suggestions.CommandSuggestion
import com.mattmx.ktgui.commands.usage.CommandUsageOptions
import com.mattmx.ktgui.cooldown.ActionCoolDown
import com.mattmx.ktgui.utils.JavaCompatibility
import com.mattmx.ktgui.utils.not
import com.mattmx.ktgui.utils.pretty
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.permissions.Permission
import org.bukkit.permissions.PermissionDefault
import java.time.Duration
import java.util.*
import java.util.function.Consumer

open class DeclarativeCommandBuilder(
    val name: String
) {
    var description = "Command '$name'"
    var aliases = arrayOf<String>()
    var subcommands = setOf<DeclarativeCommandBuilder>()
    var expectedArguments = arrayOf<Argument<*>>()
    var localArgumentSuggestions = hashMapOf<String, CommandSuggestion<*>>()
    val permittedFlags = mutableSetOf<FlagArgument>()
    val permittedOptions = mutableSetOf<OptionArgument<*>>()
    val optionsSyntax = OptionSyntax()
    var coolDown = Optional.empty<ActionCoolDown<CommandSender>>()
        private set
    var coolDownCallback = Optional.empty<(StorageCommandContext<*>) -> Unit>()
        private set
    var buildAutomaticPermissions = Optional.empty<String>()
        private set
    var permission: Optional<(StorageCommandContext<*>) -> Boolean> = Optional.empty()
        private set

    // todo give this T
//    var runs: Optional<(RunnableCommandContext<*>) -> Unit> = Optional.empty()
//        private set
    var runs = hashMapOf<Class<*>, (RunnableCommandContext<*>) -> Unit>()
    var missing: Optional<(InvalidArgContext<*>) -> Unit> = Optional.empty()
        private set
    var invalid: Optional<(InvalidArgContext<*>) -> Unit> = Optional.empty()
        private set
    var incorrectExecutor: Optional<(StorageCommandContext<*>) -> Unit> = Optional.empty()
        private set
    var invalidPermissions: Optional<(StorageCommandContext<*>) -> Unit> = Optional.empty()
        private set
    var unknownCommand: Optional<(StorageCommandContext<*>) -> Unit> = Optional.empty()
        private set

    fun cooldown(duration: Duration, block: (StorageCommandContext<*>.() -> Unit)? = null) = apply {
        this.coolDown = Optional.of(ActionCoolDown(duration))
        this.coolDownCallback = Optional.ofNullable(block)
    }

    infix fun permission(block: StorageCommandContext<*>.() -> Boolean) = apply {
        this.permission = Optional.of(block)
    }

    infix fun permission(node: String) = apply {
        permission { sender.hasPermission(node) }
    }

    infix fun noPermissions(block: StorageCommandContext<*>.() -> Unit) = apply {
        this.invalidPermissions = Optional.of(block)
    }

    infix fun unknownCommand(block: StorageCommandContext<*>.() -> Unit) = apply {
        this.unknownCommand = Optional.of(block)
    }

    infix fun buildAutomaticPermissions(root: String?) = apply {
        this.buildAutomaticPermissions = Optional.ofNullable(root)
    }

    infix fun incorrectExecutor(block: (StorageCommandContext<*>.() -> Unit)?) = apply {
        this.incorrectExecutor = Optional.ofNullable(block)
    }

    infix fun missing(block: InvalidArgContext<*>.() -> Unit) = apply {
        this.missing = Optional.of(block)
    }

    infix fun invalid(block: InvalidArgContext<*>.() -> Unit) = apply {
        this.invalid = Optional.of(block)
    }

    inline infix fun <reified T : CommandSender> runs(noinline block: RunnableCommandContext<T>.() -> Unit) = apply {
        runs(T::class.javaObjectType, block)
    }

    fun <T : CommandSender> runs(senderClass: Class<T>, block: RunnableCommandContext<T>.() -> Unit) = apply {
        this.runs[senderClass] = block as (RunnableCommandContext<*>) -> Unit
    }

    inline fun <reified T : CommandSender> runs(
        vararg argsProvided: Argument<*>,
        noinline block: RunnableCommandContext<T>.() -> Unit
    ) = apply {
        runs(T::class.javaObjectType, *argsProvided, block = block)
    }

    fun <T : CommandSender> runs(
        senderClass: Class<T>,
        vararg argsProvided: Argument<*>,
        block: RunnableCommandContext<T>.() -> Unit
    ) = apply {
        TODO("Not yet implemented, use runs without argsProvided")
        this.runs[senderClass] = block as (RunnableCommandContext<*>) -> Unit
    }

    @JavaCompatibility
    fun <T : CommandSender> runs(senderClass: Class<T>, block: Consumer<RunnableCommandContext<T>>) = apply {
        runs(senderClass) {
            block.accept(this)
        }
    }

    fun withDefaultUsageSubCommand(options: CommandUsageOptions = CommandUsageOptions()) = apply {
        val self = this

        subcommand("usage") {
            aliases += "help"

            runs<CommandSender> {
                reply(!self.getUsage(options))
            }
        }
    }

    infix fun Argument<*>.suggests(suggest: CommandSuggestion<*>) = apply {
        localArgumentSuggestions[name()] = suggest
    }

    fun getSuggestions(argument: Argument<*>?): CommandSuggestion<out Any?>? {
        if (argument == null) return null
        return localArgumentSuggestions[argument.name()]
            ?: argument.suggests.orElse(null)
    }

    inline operator fun ChainCommandBuilder.invoke(block: DeclarativeSubCommandBuilder.() -> Unit) =
        subcommand(this, block)

    inline operator fun String.invoke(block: DeclarativeSubCommandBuilder.() -> Unit) =
        fromString(this).let {
            val subCommand = DeclarativeSubCommandBuilder(it.name)
                .apply {
                    expectedArguments += it.expectedArguments
                    subcommands += it.subcommands
                }
                .apply(block)

            subcommands += subCommand
        }

    inline fun subcommand(
        chain: ChainCommandBuilder,
        block: DeclarativeSubCommandBuilder.() -> Unit
    ) =
        chain.build(DeclarativeSubCommandBuilder(chain.name)).apply(block).let {
            subcommands += it
            it
        }

    fun subcommand(name: String, block: DeclarativeSubCommandBuilder.() -> Unit) =
        subcommand(DeclarativeSubCommandBuilder(name).apply(block))

    fun subcommand(cmd: DeclarativeSubCommandBuilder) =
        cmd.let {
            subcommands += it
            it
        }

    @JavaCompatibility
    fun withFlag(flagArgument: FlagArgument) = apply {
        this.permittedFlags.add(flagArgument)
    }

    @JavaCompatibility
    fun <T : Any> withOption(optionArgument: OptionArgument<T>) = apply {
        this.permittedOptions.add(optionArgument)
    }

    operator fun FlagArgument.unaryPlus() = withFlag(this)
    operator fun <T : Any> OptionArgument<T>.unaryPlus() = withOption(this)
    operator fun <T : Any> Argument<T>.unaryPlus() = OptionArgument(this).unaryPlus()

    fun getCurrentCommand(context: SuggestionInvocation<*>): Pair<SuggestionInvocation<*>, DeclarativeCommandBuilder?> {
        val firstArg = context.rawArgs.firstOrNull()
            ?: return context to this

        for (cmd in subcommands) {
            if (cmd.nameEquals(firstArg)) {
                return cmd.getCurrentCommand(context.clone(context.rawArgs.subList(1, context.rawArgs.size)))
            }
        }

        return context to this
    }

    fun getSuggestions(context: SuggestionInvocation<*>): List<String> {
        val cmds = subcommands
            .filter { it.nameStarts(context.last) }
            .map { listOf(it.name) + it.aliases }
        val cmdsList = cmds.flatten()

        val list = context.rawArgs.toMutableList()
        val suggestedArgs = arrayListOf(*cmdsList.toTypedArray())

        for (arg in expectedArguments) {
            val processor = ArgumentProcessor(this, list)
            val result = arg.consume(processor)

            if (result.isEmpty() && !arg.isRequired()) continue

            val thisArgSuggestions = getSuggestions(arg)?.getLastArgSuggestion(context)
            if (thisArgSuggestions != null) {
                suggestedArgs.addAll(thisArgSuggestions)
            }
        }

//        val arg = expectedArguments.getOrNull(context.rawArgs.size - 1)
//        val suggestedArgs = getSuggestions(arg)?.getLastArgSuggestion(context)

        return suggestedArgs
    }

    fun nameEquals(arg: String?) = name == arg || aliases.any { it == arg }

    fun nameStarts(arg: String) = name.startsWith(arg, true) || aliases.any { it.startsWith(arg, true) }

    infix fun register(localObject: Any) = apply {
        val permissionNodePrefix = buildAutomaticPermissions.orElse(null)
        if (permissionNodePrefix != null) {
            registerPermissionWithPrefixRecursively(permissionNodePrefix)
        }

        val wrapper = DeclarativeCommandWrapper(name, this)
        GuiManager.registerCommand(localObject::class.javaObjectType, wrapper)
    }

    fun registerPermissionWithPrefixRecursively(prefix: String) {
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

    fun invoke(context: StorageCommandContext<*>) {

        if (permission.isPresent && !permission.get().invoke(context)) {
            if (invalidPermissions.isPresent) {
                invalidPermissions.get().invoke(context)
            } else {
                context.reply(Bukkit.permissionMessage())
            }

            return
        }

        if (coolDown.isPresent && !coolDown.get().test(context.sender)) {
            coolDownCallback.ifPresentOrElse({ it.invoke(context) }) {
                context.reply(
                    !"&cPlease wait ${
                        coolDown.get().durationRemaining(context.sender).pretty()
                    } before running the command again."
                )
            }
            return
        }

        val firstArg = context.rawArgs.firstOrNull()

        val cmd = subcommands.firstOrNull { it.nameEquals(firstArg) }
        if (cmd != null) {
            return cmd.invoke(
                context.clone(context.rawArgs.subList(1, context.rawArgs.size))
            )
        }

        // Move onto args

        val argumentValues = hashMapOf<String, ArgumentContext<*>>()

//        var expectedArgumentIndex = 0
//        var providedArgumentIndex = 0

        val argumentProcessor = ArgumentProcessor(this, context.rawArgs)
        for ((index, arg) in expectedArguments.withIndex()) {
            if (argumentProcessor.done()) {
                if (arg.isRequired()) {

                    val missingArgContext =
                        InvalidArgContext(context.sender, context.alias, context.rawArgs, arg, null)

                    // Try and invoke this specific missing arg
                    if (arg.invokeMissing(missingArgContext)) {
                        return
                    } else if (arg.invokeInvalid(missingArgContext)) {
                        return
                    }

                    // Invoke the global missing or invalid
                    if (missing.isPresent) {
                        missing.get().invoke(missingArgContext)
                    } else if (invalid.isPresent) {
                        invalid.get().invoke(missingArgContext)
                    }

                    return
                }
                continue
            }

            val processorClone = argumentProcessor.clone()
            val result = arg.consumer.consume(processorClone)
            val actualValue = arg.getValueOfString(this, context, result.stringValue)

            if (arg.isOptional() || (result.isEmpty() && actualValue != null)) {
                argumentValues[arg.name()] = arg.createContext(result.stringValue, actualValue)
                argumentProcessor.pointer = processorClone.pointer
                argumentProcessor.optionsAndFlagsValues = processorClone.optionsAndFlagsValues
            } else {
                val invalidArgumentContext =
                    InvalidArgContext(context.sender, context.alias, context.rawArgs, arg, result.stringValue)

                if (arg.invokeInvalid(invalidArgumentContext)) {
                    return
                }

                invalid.ifPresent { it.invoke(invalidArgumentContext) }
            }
        }

        if (!argumentProcessor.done()) {
            // Too many args, unknown command maybe?
            if (unknownCommand.isPresent) {
                unknownCommand.get().invoke(context)
            } else {
                context.reply(!"&cUnknown sub-command or arguments.")
            }
            return
        }

        val runnableContext =
            RunnableCommandContext(context.sender, context.alias, context.rawArgs, argumentValues)

        val executor = runs.entries.firstOrNull { (clazz, _) ->
            clazz.isAssignableFrom(context.sender.javaClass)
        }
            ?: return context.reply(!"&cThis command can only be ran by ${runs.values.joinToString("/") { "${it.javaClass.simpleName}s" }}.")

        executor.value.invoke(runnableContext)

//        while (providedArgumentIndex < context.rawArgs.size) {
//            val arg = context.rawArgs[providedArgumentIndex]
//
//            // Check sub-commands
//            // todo could match multiple subcommands
//            val cmd = subcommands.firstOrNull { it.nameEquals(arg) }
//            if (cmd != null) {
//                return cmd.invoke(
//                    context.clone(context.rawArgs.subList(1, context.rawArgs.size))
//                )
//            }
//
//            // Check arguments
//            val expectedArg = expectedArguments.getOrNull(expectedArgumentIndex)
//            if (expectedArg != null) {
//                // Get full argument string using consumer
//                val consumed =
//                    expectedArg.consumer.consume(context.rawArgs.subList(providedArgumentIndex, context.rawArgs.size))
//                providedArgumentIndex += consumed.size
//
//                // If the value is empty and IS required
//                if (expectedArg.isRequired() && consumed.isEmpty()) {
//                    val missingArgContext =
//                        InvalidArgContext(context.sender, context.alias, context.rawArgs, expectedArg, null)
//
//                    // Try and invoke this specific missing arg
//                    if (expectedArg.invokeMissing(missingArgContext)) {
//                        return
//                    } else if (expectedArg.invokeInvalid(missingArgContext)) {
//                        return
//                    }
//
//                    // Invoke the global missing or invalid
//                    if (missing.isPresent) {
//                        missing.get().invoke(missingArgContext)
//                    } else if (invalid.isPresent) {
//                        invalid.get().invoke(missingArgContext)
//                    }
//
//                    return
//                } else {
//                    var isValid = expectedArg.validate(consumed)
//
//                    val stringValue = consumed.joinToString(" ")
//                    val actualValue = expectedArg.getValueOfString(this, context, consumed)
//
//                    isValid = isValid && (expectedArg.isOptional() || actualValue != null)
//
//                    if (!isValid) {
//                        val invalidArgumentContext =
//                            InvalidArgContext(context.sender, context.alias, context.rawArgs, expectedArg, stringValue)
//
//                        if (expectedArg.invokeInvalid(invalidArgumentContext)) {
//                            return
//                        }
//
//                        invalid.ifPresent { it.invoke(invalidArgumentContext) }
//                        return
//                    }
//
//                    argumentValues[expectedArg.name()] = expectedArg.createContext(stringValue, actualValue)
//
//                    expectedArgumentIndex++
//                }
//            } else {
//                if (unknownCommand.isPresent) {
//                    unknownCommand.get().invoke(context)
//                } else {
//                    context.reply(!"&cUnknown sub-command or arguments.")
//                }
//                return
//            }
//        }
//
//        val missing = expectedArguments.filter { arg ->
//            !argumentValues.containsKey(arg.name())
//        }
//
//        if (missing.isNotEmpty()) {
//            missing.forEach { arg ->
//                val missingArgContext =
//                    InvalidArgContext(context.sender, context.alias, context.rawArgs, arg, null)
//
//                // Try and invoke this specific missing arg
//                if (arg.invokeMissing(missingArgContext)) {
//                    return
//                } else if (arg.invokeInvalid(missingArgContext)) {
//                    return
//                }
//            }
//            return
//        }
//
//        val runnableContext =
//            RunnableCommandContext(context.sender, context.alias, context.rawArgs, argumentValues)
//
//        val executor =
//            runs.entries.firstOrNull { (clazz, _) ->
//                clazz.isAssignableFrom(context.sender.javaClass)
//            }
//                ?: return context.reply(!"&cThis command can only be ran by ${runs.values.joinToString("/") { "${it.javaClass.simpleName}s" }}.")
//
//        executor.value.invoke(runnableContext)
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
                    } else "${options.arguments.typeChar}${arg.type()}}"
                } else "${options.arguments.typeChar}${arg.type()}}"

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

        @JvmStatic
        fun fromString(source: String): DeclarativeCommandBuilder {
            var name: String? = null
            val expectedArguments = arrayListOf<Argument<*>>()
            val parsed = Parser(source).parse()

            for (syntax in parsed) {
                when (syntax) {
                    is VariableDeclarationSyntax -> {
                        expectedArguments += Argument<Any>(
                            syntax.getName(),
                            syntax.getType()
                        ).apply { optional(syntax.isOptional()) }
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
            return DeclarativeCommandBuilder(name)
                .apply {
                    this.expectedArguments += expectedArguments
                }
        }
    }
}

inline operator fun String.invoke(block: DeclarativeCommandBuilder.() -> Unit) =
    DeclarativeCommandBuilder(this).apply(block)

inline fun <reified S : CommandSender> String.runs(noinline block: RunnableCommandContext<S>.() -> Unit) =
    DeclarativeCommandBuilder(this).runs(block)

inline fun command(name: String, block: DeclarativeCommandBuilder.() -> Unit) =
    DeclarativeCommandBuilder(name).apply(block)

@JavaCompatibility
fun command(name: String) = DeclarativeCommandBuilder(name)