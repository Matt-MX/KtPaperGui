package com.mattmx.ktgui.papi

import com.mattmx.ktgui.dsl.placeholderExpansion
import com.mattmx.ktgui.dsl.relational
import org.bukkit.plugin.java.JavaPlugin

fun main(plugin: JavaPlugin) {
    plugin.placeholderExpansion {

        relational("test") {
            one.name to two.name
        }

    } id "test"
}