package com.mattmx.ktgui.papi

import com.mattmx.ktgui.commands.declarative.ChainCommandBuilder
import com.mattmx.ktgui.commands.declarative.arg.Argument
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
    Placeholder(this, ChainCommandBuilder(string), supplier).apply { registerPlaceholder(this) }

fun PlaceholderExpansionWrapper.placeholder(chain: ChainCommandBuilder, supplier: PlaceholderParseContext.() -> Any?) =
    Placeholder(this, chain, supplier).apply { registerPlaceholder(this) }

fun PlaceholderExpansionWrapper.placeholder(argument: Argument<*>, supplier: PlaceholderParseContext.() -> Any?) =
    Placeholder(
        this,
        Placeholder.emptyCommandBuilder().argument(argument),
        supplier
    ).apply { registerPlaceholder(this) }