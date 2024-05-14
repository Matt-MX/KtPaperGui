package com.mattmx.ktgui.commands.usage

import com.mattmx.ktgui.utils.Invokable

class CommandUsageOptions : Invokable<CommandUsageOptions> {
    var namePrefix = "/"
    var gap = " "

    val arguments = ArgumentUsageOptions()
    val subCommands = SubCommandOptions()

    class ArgumentUsageOptions : Invokable<ArgumentUsageOptions> {
        var prefix = "<"

        var typeChar = ":"
        var required = "!"
        var optional = "?"

        var showSuggestions = false
        var maxSuggestions = 5
        var suggestionsPrefix = "["
        var suggestionsChar = "="
        var suggestionsDivider = "|"
        var suggestionsSuffix = "]"

        var suffix = ">"

        var showDescriptions = false
        var descriptionsPrefix = "> "
        var descriptionDivider = " - "
        var descriptionsRequired = "(Required)"
        var descriptionsOptional = "(Optional)"
    }

    class SubCommandOptions : Invokable<SubCommandOptions> {
        var prefix = ""
        var divider = "|"
        var suffix = ""
    }

    companion object {
        inline operator fun invoke(block: CommandUsageOptions.() -> Unit) = CommandUsageOptions().apply(block)
    }
}