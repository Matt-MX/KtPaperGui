package com.mattmx.ktgui.commands.usage

class CommandUsageOptions {
    var namePrefix = "/"
    var gap = " "

    val arguments = ArgumentUsageOptions()
    val subCommands = SubCommandOptions()

    class ArgumentUsageOptions {
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

        inline operator fun invoke(block: ArgumentUsageOptions.() -> Unit) = apply(block)
    }

    class SubCommandOptions {
        var prefix = ""
        var divider = "|"
        var suffix = ""

        inline operator fun invoke(block: SubCommandOptions.() -> Unit) = apply(block)
    }

    inline operator fun invoke(block: CommandUsageOptions.() -> Unit) = apply(block)
}