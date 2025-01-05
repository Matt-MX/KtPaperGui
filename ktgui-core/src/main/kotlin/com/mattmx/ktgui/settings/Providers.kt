package com.mattmx.ktgui.settings

import com.mattmx.ktgui.util.not
import net.kyori.adventure.key.Key
import net.kyori.adventure.text.Component
import kotlin.time.Duration

object Providers {
    fun ktguiKey(value: String) = Key.key("ktgui", value)

    val COOLDOWN = ProviderIdentifier<Duration, Component>(ktguiKey("cooldown")) { duration ->
        !"&cPlease wait $duration before doing that again."
    }
    val ERROR_MISSING_ARG = ProviderIdentifier<Any, Component>(ktguiKey("error_missing_arg")) {
        !"Missing argument"
    }
    val ERROR_UNKNOWN_COMMAND = ProviderIdentifier<Any, Component>(ktguiKey("error_unknown_command")) {
        !"Unknown command"
    }
    val ERROR_INVALID_SENDER = ProviderIdentifier<Any, Component>(ktguiKey("error_invalid_sender")) {
        !"This command is not available to your sender type"
    }
}