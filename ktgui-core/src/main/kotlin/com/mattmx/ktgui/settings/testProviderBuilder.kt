package com.mattmx.ktgui.settings

import com.mattmx.ktgui.settings.Providers.ERROR_INVALID_SENDER
import com.mattmx.ktgui.settings.Providers.ERROR_MISSING_ARG
import com.mattmx.ktgui.settings.Providers.ERROR_UNKNOWN_COMMAND
import com.mattmx.ktgui.util.not
import net.kyori.adventure.text.Component

fun main() {
    val provider = PlatformPluginConfig()
        .provider(ERROR_MISSING_ARG) { !"Missing argument" }
        .provider(ERROR_UNKNOWN_COMMAND) { !"&cThat command is unknown" }
        .provider(ERROR_INVALID_SENDER) { Component.empty() }

}