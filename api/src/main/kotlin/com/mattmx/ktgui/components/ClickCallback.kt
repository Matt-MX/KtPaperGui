package com.mattmx.ktgui.components

import com.mattmx.ktgui.components.button.ButtonClickedEvent
import org.bukkit.event.inventory.ClickType
import org.bukkit.event.inventory.InventoryClickEvent

class ClickCallback {
    private var callbacks = hashMapOf<Array<ClickType>, ButtonClickedEvent.() -> Unit>()
    private lateinit var anyCallback: (ButtonClickedEvent.() -> Unit)

    /**
     * Called when a click event is received for this GUI element.
     *
     * @param event
     */
    fun run(event: ButtonClickedEvent) {
        // Get relevant callbacks
        if (callbacks.isEmpty() && ::anyCallback.isInitialized) {
            return anyCallback(event)
        }

        val relevant = callbacks.entries.filter { it.key.contains(event.event.click) }
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
    fun any(callback: ButtonClickedEvent.() -> Unit) {
        anyCallback = callback
    }

    /**
     * For Java support since they don't support operator overloading.
     * Adds a callback handler for the clicks specified
     * Alternative for:
     * <pre>{@code
     * (ClickType.RIGHT) {
     *      // code
     * }
     *
     * // instead:
     * handleClicks(ClickType.RIGHT) {
     *      // code
     * }
     * }</pre>
     *
     * @param clickType click types to handle
     * @param callback callback for when clicked
     */
    fun handleClicks(vararg clickType: ClickType, callback: ButtonClickedEvent.() -> Unit) {
        callbacks[clickType.asList().toTypedArray()] = callback
    }

    operator fun ClickType.invoke(callback: ButtonClickedEvent.() -> Unit) {
        callbacks[arrayOf(this)] = callback
    }

    operator fun Array<ClickType>.invoke(callback: ButtonClickedEvent.() -> Unit) {
        callbacks[this] = callback
    }

    operator fun ClickType.plus(clickType: ClickType) = arrayOf(this, clickType)
    operator fun Array<ClickType>.plus(clickType: ClickType) = arrayOf(*this, clickType)

    fun clone() = ClickCallback().let { copy ->
        copy.callbacks = this.callbacks.clone() as HashMap<Array<ClickType>, ButtonClickedEvent.() -> Unit>
        copy.anyCallback = this.anyCallback
        return@let copy
    }
}

fun main() {
    ClickCallback().apply {
        handleClicks(ClickType.LEFT, ClickType.RIGHT) ret@ {
            if (!player.hasPermission("click.this"))
                return@ret
        }

        any {
            if (!player.hasPermission("click.slot.$slot"))
                return@any
        }
    }
}