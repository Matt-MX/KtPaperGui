package com.mattmx.ktgui.settings

import java.util.function.Function

class PlatformPluginConfig {
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
}