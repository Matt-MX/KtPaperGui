package com.mattmx.ktgui.settings

import net.kyori.adventure.key.Key
import java.util.function.Function

open class ProviderIdentifier<I, O>(
    val key: Key,
    val default: Function<I, O>
)