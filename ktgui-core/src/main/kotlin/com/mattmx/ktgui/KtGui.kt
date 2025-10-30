package com.mattmx.ktgui

import com.mattmx.ktgui.button.GuiButton
import com.mattmx.ktgui.screen.GuiScreen
import com.mattmx.ktgui.screen.GuiType
import com.mattmx.ktgui.tasks.KeyedTaskTracker
import com.mattmx.ktgui.tasks.TaskProvider
import com.mattmx.ktgui.tasks.TaskTracker
import net.kyori.adventure.key.Key
import net.kyori.adventure.text.Component
import java.util.*

abstract class KtGui<P : Any, B : GuiButton<*, *, *, *>, G : GuiScreen<P, B>> {
    private val activeSessions = Collections.synchronizedMap(hashMapOf<P, G>())

    fun setActiveGui(player: Any, gui: Any) {
        val finalPlayer = player as? P ?: return
        val finalGui = gui as? G ?: return
        synchronized(activeSessions) {
            activeSessions[finalPlayer] = finalGui
        }
    }

    fun removeActiveGui(player: Any): G? {
        val finalPlayer = player as? P ?: return null
        synchronized(activeSessions) {
            return activeSessions.remove(finalPlayer)
        }
    }

    fun getActiveGui(player: P): G? {
        synchronized(activeSessions) {
            return activeSessions[player]
        }
    }

    fun <T> getActiveOfClass(clazz: Class<T>): Map<P, T> {
        return activeSessions.mapNotNull { (player, gui) ->
            (gui as? T)?.let { player to it }
        }.toMap()
    }

    fun getActiveOfId(id: String): Map<P, G> {
        return activeSessions.filterValues { it.windowIdentifier == id }
    }

    fun getActiveOfInstance(g: GuiScreen<*, *>): Map<P, G> {
        return activeSessions.filterValues { gui -> gui == g }
    }

    abstract fun forcefullyClose(player: Any)

    abstract fun createPlatformButtonOfType(typeKeyed: Key): B

    abstract fun createPlatformButton(type: Any): B

    abstract fun createPlatformGui(title: Component, type: GuiType): G

    abstract fun getTaskProvider() : TaskProvider<*>

    abstract fun <T : TaskWrapper> createTaskTracker(plugin: Any): TaskTracker<T>

    abstract fun <T : TaskWrapper> createKeyedTaskTracker(plugin: Any): KeyedTaskTracker<T>

    companion object {
        private lateinit var instance: KtGui<*, *, *>

        fun setInstance(instance: KtGui<*, *, *>) {
            this.instance = instance
        }

        @JvmName("getInstanceAny")
        fun getInstance(): KtGui<*, *, *> {
            if (!::instance.isInitialized) {
                error("GuiManager is not initialized yet!")
            }

            return instance
        }

        @Suppress("UNCHECKED_CAST")
        fun <T : KtGui<*, *, *>> getInstance(): T {
            return getInstance() as T
        }
    }
}