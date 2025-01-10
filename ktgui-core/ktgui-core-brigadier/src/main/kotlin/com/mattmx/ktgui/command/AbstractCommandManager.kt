package com.mattmx.ktgui.command

abstract class AbstractCommandManager<C> {
    protected val commands = mutableSetOf<C>()

    open fun register(command: C) {
        this.commands.add(command)
    }

    abstract fun unregister(command: C)

    open fun dispose(command: C) {
        unregister(command)
        commands.remove(command)
    }

    fun getCommands() = commands.toList()

    companion object {
        private lateinit var instance: AbstractCommandManager<*>

        fun setInstance(manager: AbstractCommandManager<*>) {
            instance = manager
        }

        fun <C, T : AbstractCommandManager<C>> getInstance() : T = instance as T
    }
}