package com.mattmx.ktgui.commands.suggestions

import com.mattmx.ktgui.commands.declarative.invocation.StorageCommandContext
import com.mattmx.ktgui.commands.declarative.invocation.SuggestionInvocation
import kotlin.reflect.KProperty
import kotlin.reflect.jvm.javaMethod

open class SimpleCommandSuggestion<V, F>(
    val field: KProperty<F>,
    val list: () -> List<V>
) : CommandSuggestion<V> {

    override fun getSuggestion(invocation: StorageCommandContext<*>): List<String>? {
        val list = list()

        return list.map { obj -> field.getter.javaMethod?.invoke(obj).toString() }
    }

    override fun getValue(argumentString: String?): V? {
        val list = list()

        return list.firstOrNull { obj -> field.getter.javaMethod?.invoke(obj) == argumentString }
    }
}