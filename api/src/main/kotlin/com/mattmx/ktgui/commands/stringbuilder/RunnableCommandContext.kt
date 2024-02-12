package com.mattmx.ktgui.commands.stringbuilder

import com.mattmx.ktgui.commands.stringbuilder.arg.ArgumentContext
import org.bukkit.command.CommandSender
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

class RunnableCommandContext<T : CommandSender>(
    private val providedArgs: HashMap<String, ArgumentContext<*>>,
    rawArgs: List<String>
) : RawCommandContext<T>(rawArgs) {

    fun argument() = ReadOnlyProperty { owner: Nothing?, prop: KProperty<*> ->
        providedArgs[prop.name] ?: error("Unknown argument '${prop.name}'")
    }

}