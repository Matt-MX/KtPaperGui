package com.mattmx.ktgui.settings

import java.util.function.Function

class KtGuiConfiguration(
    val plugin: Any
) {
    val messageProviders = hashMapOf<ProviderIdentifier<*, *>, Function<*, *>>()

    fun <I, O> provider(identifier: ProviderIdentifier<I, O>, function: Function<I, O>) = apply {
        messageProviders[identifier] = function
    }

    operator fun <I, O> set(identifier: ProviderIdentifier<I, O>, function: Function<I, O>) = apply {
        messageProviders[identifier] = function
    }

    operator fun <I, O> get(providerIdentifier: ProviderIdentifier<I, O>): Function<I, O> {
        return messageProviders[providerIdentifier] as? Function<I, O> ?: providerIdentifier.default
    }

    operator fun <I, O> ProviderIdentifier<I, O>.invoke(function: Function<I, O>) = set(this, function)

    companion object {
        fun KtGui(plugin: Any, block: (KtGuiConfiguration.() -> Unit)? = null) : KtGuiConfiguration {
            return KtGuiConfiguration(plugin).also { block?.invoke(it) }
        }
    }
}