package com.mattmx.ktgui.utils

import org.bukkit.Bukkit

object Dependencies {
    var papi = false

    init {
        papi = Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null
    }
}