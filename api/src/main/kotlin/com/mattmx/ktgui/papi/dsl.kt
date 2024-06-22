package com.mattmx.ktgui.papi

import com.mattmx.ktgui.commands.declarative.ChainCommandBuilder
import org.bukkit.plugin.java.JavaPlugin

inline fun JavaPlugin.placeholderExpansion(builder: PlaceholderExpansionWrapper.() -> Unit) =
    PlaceholderExpansionWrapper(this).apply(builder).apply { register() }

fun placeholder(string: String, supplier: PlaceholderParseContext.() -> Any?) =
    Placeholder(ChainCommandBuilder(string), supplier)

fun placeholder(chain: ChainCommandBuilder, supplier: PlaceholderParseContext.() -> Any?) =
    Placeholder(chain, supplier)