package com.mattmx.ktgui

import com.mattmx.ktgui.command.CommandManager
import com.mattmx.ktgui.example.createInventorySeeCommand
import com.mattmx.ktgui.impl.PaperGuiManagerImpl
import org.bukkit.plugin.java.JavaPlugin

class PaperKtGuiPlugin : JavaPlugin() {
    val manager = PaperGuiManagerImpl(this)

    override fun onEnable() {
        instance = this

        CommandManager.inject()
        manager.registerListeners()

        createInventorySeeCommand()
    }

    override fun onDisable() {
        manager.unregisterListeners()
    }

    companion object {
        private lateinit var instance: PaperKtGuiPlugin
        fun getInstance() = instance
    }
}