package com.mattmx.ktgui.components

import org.bukkit.event.inventory.ClickType
import org.bukkit.event.inventory.InventoryClickEvent

class ClickCallback {
    private val callbacks = hashMapOf<Array<ClickType>, InventoryClickEvent.() -> Unit>()
    private lateinit var anyCallback: (InventoryClickEvent.() -> Unit)

    /**
     * Called when a click event is received for this GUI element.
     *
     * @param event
     */
    fun run(event: InventoryClickEvent) {
        // Get relevant callbacks
        if (callbacks.isEmpty() && ::anyCallback.isInitialized) {
            return anyCallback(event)
        }

        val relevant = callbacks.entries.filter { it.key.contains(event.click) }
        if (relevant.isEmpty() && ::anyCallback.isInitialized) {
            return anyCallback(event)
        }
        relevant.forEach { it.value(event) }
    }

    /**
     * DSL utility functions. Allows us to create different callbacks for
     * all different types of [ClickType] enums.
     * e.g:
     * <pre>{@code
     * click {
     *      (ClickType.LEFT + ClickType.LEFT_SHIFT) {
     *          // code will run on LEFT or LEFT_SHIFT
     *      }
     *      ClickType.DROP {
     *          // code will run on DROP
     *      }
     *      any {
     *          // code will run for any other type of [ClickType]
     *      }
     * }
     * }</pre>
     */
    fun any(callback: InventoryClickEvent.() -> Unit) {
        anyCallback = callback
    }
    operator fun ClickType.invoke(callback: InventoryClickEvent.() -> Unit) {
        callbacks[arrayOf(this)] = callback
    }
    operator fun Array<ClickType>.invoke(callback: InventoryClickEvent.() -> Unit) {
        callbacks[this] = callback
    }
    operator fun ClickType.plus(clickType: ClickType) = arrayOf(this, clickType)
    operator fun Array<ClickType>.plus(clickType: ClickType) = arrayOf(*this, clickType)
}