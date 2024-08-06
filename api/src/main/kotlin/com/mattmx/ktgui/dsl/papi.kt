package com.mattmx.ktgui.dsl

import com.mattmx.ktgui.commands.declarative.ChainCommandBuilder
import com.mattmx.ktgui.commands.declarative.arg.Argument
import com.mattmx.ktgui.papi.Placeholder
import com.mattmx.ktgui.papi.PlaceholderExpansionWrapper
import com.mattmx.ktgui.papi.PlaceholderParseContext
import com.mattmx.ktgui.papi.RelationalPlaceholderParseContext
import com.mattmx.ktgui.scheduling.syncDelayed
import org.bukkit.plugin.java.JavaPlugin
import java.util.*

inline fun JavaPlugin.placeholderExpansion(builder: PlaceholderExpansionWrapper.() -> Unit) =
    PlaceholderExpansionWrapper(this)
        .apply(builder)
        .apply {
            syncDelayed(2) { register() }
        }

fun PlaceholderExpansionWrapper.placeholder(string: String, supplier: PlaceholderParseContext.() -> Any?) =
    Placeholder(this, ChainCommandBuilder(string)).apply {
        parseDefault = Optional.of(supplier)
        registerPlaceholder(this)
    }

fun PlaceholderExpansionWrapper.placeholder(chain: ChainCommandBuilder, supplier: PlaceholderParseContext.() -> Any?) =
    Placeholder(this, chain).apply {
        parseDefault = Optional.of(supplier)
        registerPlaceholder(this)
    }

fun PlaceholderExpansionWrapper.placeholder(argument: Argument<*>, supplier: PlaceholderParseContext.() -> Any?) =
    Placeholder(
        this,
        Placeholder.emptyCommandBuilder().argument(argument)
    ).apply {
        parseDefault = Optional.of(supplier)
        registerPlaceholder(this)
    }

fun PlaceholderExpansionWrapper.relational(string: String, supplier: RelationalPlaceholderParseContext.() -> Any?) =
    Placeholder(this, ChainCommandBuilder(string)).apply {
        parseRelational = Optional.of(supplier)
        registerPlaceholder(this)
    }

fun PlaceholderExpansionWrapper.relational(chain: ChainCommandBuilder, supplier: RelationalPlaceholderParseContext.() -> Any?) =
    Placeholder(this, chain).apply {
        parseRelational = Optional.of(supplier)
        registerPlaceholder(this)
    }

fun PlaceholderExpansionWrapper.relational(argument: Argument<*>, supplier: RelationalPlaceholderParseContext.() -> Any?) =
    Placeholder(
        this,
        Placeholder.emptyCommandBuilder().argument(argument)
    ).apply {
        parseRelational = Optional.of(supplier)
        registerPlaceholder(this)
    }