package com.mattmx.ktgui.commands.declarative

/**
 * Argument class
 *
 * @param S the command sender type ([Player], [ConsoleSender])
 * @param V the type of value for this argument
 */
open class Argument<S : CommandSender, T, V>(
    val type: ArgumentType,
    val getter: (CommandContext<S>) -> V,
    val suggests: ((CommandContext<S>) -> List<String>)? = null,
    private val cast: (String.() -> V)? = null
) {
    lateinit var id: String
    private var description: String? = null

    infix fun withDescription(description: String?) = apply {
        this.description = description
    }

    fun description() = description

    fun getDefaultSuggestions() : List<String>? {
        // todo need to invoke [suggests] with fake [CommandContext]
        return null
    }

    fun getValue(context: CommandContext<S>): V {
        println("getValue")
        return getter(context)
    }
}