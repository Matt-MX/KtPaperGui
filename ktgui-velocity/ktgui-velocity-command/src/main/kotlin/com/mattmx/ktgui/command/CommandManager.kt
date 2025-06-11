package com.mattmx.ktgui.command

import com.velocitypowered.api.command.BrigadierCommand
import com.velocitypowered.api.proxy.ProxyServer

object CommandManager {
    private lateinit var proxyInstance: ProxyServer

    fun getProxyInstance(): ProxyServer {
        if (!::proxyInstance.isInitialized) {
            error("CommandManager is not initialized yet. Call CommandManager.inject(...) before registering any commands.")
        }

        return proxyInstance
    }

    fun inject(proxyServer: ProxyServer) {
        if (::proxyInstance.isInitialized) {
            return
        }

        this.proxyInstance = proxyServer
    }

    fun register(command: RegisteredVelocityCommand) {
        val instance = BrigadierCommand(command.root)

        getProxyInstance()
            .commandManager
            .register(command.meta.build(), instance)
    }

    fun unregister(command: RegisteredVelocityCommand) {
        getProxyInstance()
            .commandManager
            .unregister(command.root.name)
    }

}