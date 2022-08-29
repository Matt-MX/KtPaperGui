package com.mattmx.ktguis

import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin

class KotlinBukkitGui : JavaPlugin() {
    override fun onEnable() {
        // register example guis
        version = description.version
    }

    companion object {
        val papi : Boolean = Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null
        lateinit var version : String
    }
}