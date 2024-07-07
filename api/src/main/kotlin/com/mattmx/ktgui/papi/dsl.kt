package com.mattmx.ktgui.papi

import com.mattmx.ktgui.commands.declarative.ChainCommandBuilder
import com.mattmx.ktgui.scheduling.sync
import com.mattmx.ktgui.scheduling.syncDelayed
import org.bukkit.plugin.java.JavaPlugin

inline fun JavaPlugin.placeholderExpansion(builder: PlaceholderExpansionWrapper.() -> Unit) =
    PlaceholderExpansionWrapper(this)
        .apply(builder)
        .apply {
            syncDelayed(2) { register() }
        }

fun PlaceholderExpansionWrapper.placeholder(string: String, supplier: PlaceholderParseContext.() -> Any?) =
    Placeholder(ChainCommandBuilder(string), supplier).apply { registerPlaceholder(this) }

fun PlaceholderExpansionWrapper.placeholder(chain: ChainCommandBuilder, supplier: PlaceholderParseContext.() -> Any?) =
    Placeholder(chain, supplier).apply { registerPlaceholder(this) }