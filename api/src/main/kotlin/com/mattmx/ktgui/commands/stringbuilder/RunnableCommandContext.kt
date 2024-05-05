package com.mattmx.ktgui.commands.stringbuilder

import com.mattmx.ktgui.commands.stringbuilder.arg.Argument
import com.mattmx.ktgui.commands.stringbuilder.arg.ArgumentContext
import com.mattmx.ktgui.utils.JavaCompatibility
import org.bukkit.command.CommandSender
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

class RunnableCommandContext<T : CommandSender>(
    private val providedArgs: HashMap<String, ArgumentContext<*>>,
    rawArgs: List<String>
) : RawCommandContext<T>(rawArgs) {

    fun argument() = ReadOnlyProperty { owner: Nothing?, prop: KProperty<*> ->
        //  todo might be optional
        providedArgs[prop.name] ?: error("Unknown argument '${prop.name}'")
    }

    @JavaCompatibility
    fun <S : Any> getArgumentContext(name: String) = providedArgs[name]?.let { it as ArgumentContext<S> }

    val <S : Any> Argument<S>.context: ArgumentContext<S>
        get() = getArgumentContext(name())
            ?: error("The argument ${name()} is not available in this command context.")

    operator fun <S : Any> Argument<S>.invoke(): S? {
        val ctx = context
        return ctx.getOrNull()
    }

}