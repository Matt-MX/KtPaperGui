package com.mattmx.ktgui

import com.mattmx.ktgui.screen.GuiScreen
import java.time.Duration
import java.util.*

abstract class GuiManager<P, B : GuiButton<*, *, *, *>, G : GuiScreen<P, B>> {
    private val activeSessions = Collections.synchronizedMap(hashMapOf<P, G>())

    fun getActiveGui(player: P): G? {
        synchronized(activeSessions) {
            return activeSessions[player]
        }
    }

    fun <T> getActiveOfClass(clazz: Class<T>) : Map<P, T> {
        return activeSessions.mapNotNull { (player, gui) ->
            (gui as? T)?.let { player to it }
        }.toMap()
    }

    fun getActiveOfId(id: String) : Map<P, G> {
        return activeSessions.filterValues { it.windowIdentifier == id }
    }

    abstract fun createRepeatingTask(repeat: Duration, task: () -> Unit) : TaskWrapper
}