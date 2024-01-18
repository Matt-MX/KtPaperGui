package com.mattmx.ktgui.components

import com.mattmx.ktgui.components.button.ButtonClickedEvent
import com.mattmx.ktgui.components.button.IGuiButton
import org.bukkit.event.inventory.ClickType

class ClickCallback<T : IGuiButton<*>> {
    private var callbacks = mutableMapOf<Array<ClickType>, ButtonClickedEvent<T>.() -> Unit>()
    private lateinit var anyCallback: (ButtonClickedEvent<T>.() -> Unit)

    /**
     * Called when a click event is received for this GUI element.
     *
     * @param event
     */
    fun run(event: ButtonClickedEvent<*>) {
        if (!event.shouldContinueCallback()) return

        // Get relevant callbacks
        if (callbacks.isEmpty() && ::anyCallback.isInitialized) {
            return anyCallback(event as ButtonClickedEvent<T>)
        }

        val relevant = callbacks.entries.filter { it.key.contains(event.event.click) }
        if (relevant.isEmpty() && ::anyCallback.isInitialized) {
            return anyCallback(event as ButtonClickedEvent<T>)
        }
        relevant.forEach {
            if (!event.shouldContinueCallback()) return
            it.value(event as ButtonClickedEvent<T>)
        }
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
    fun any(callback: ButtonClickedEvent<T>.() -> Unit) {
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
    fun handleClicks(vararg clickType: ClickType, callback: ButtonClickedEvent<T>.() -> Unit) {
        callbacks[clickType.asList().toTypedArray()] = callback
    }

    operator fun ClickType.invoke(callback: ButtonClickedEvent<T>.() -> Unit) {
        callbacks[arrayOf(this)] = callback
    }

    operator fun Array<ClickType>.invoke(callback: ButtonClickedEvent<T>.() -> Unit) {
        callbacks[this] = callback
    }

    operator fun ClickType.plus(clickType: ClickType) = arrayOf(this, clickType)
    operator fun Array<ClickType>.plus(clickType: ClickType) = arrayOf(*this, clickType)

    fun left(callback: ButtonClickedEvent<T>.() -> Unit) = ClickType.LEFT.invoke(callback)
    fun right(callback: ButtonClickedEvent<T>.() -> Unit) = ClickType.RIGHT.invoke(callback)
    fun leftShift(callback: ButtonClickedEvent<T>.() -> Unit) = ClickType.SHIFT_LEFT.invoke(callback)
    fun rightShift(callback: ButtonClickedEvent<T>.() -> Unit) = ClickType.SHIFT_RIGHT.invoke(callback)
    fun drop(callback: ButtonClickedEvent<T>.() -> Unit) = ClickType.DROP.invoke(callback)
    fun ctrlDrop(callback: ButtonClickedEvent<T>.() -> Unit) = ClickType.CONTROL_DROP.invoke(callback)
    fun creative(callback: ButtonClickedEvent<T>.() -> Unit) = ClickType.CREATIVE.invoke(callback)
    fun windowBorderLeft(callback: ButtonClickedEvent<T>.() -> Unit) = ClickType.WINDOW_BORDER_LEFT.invoke(callback)
    fun windowBorderRight(callback: ButtonClickedEvent<T>.() -> Unit) = ClickType.WINDOW_BORDER_RIGHT.invoke(callback)
    fun middle(callback: ButtonClickedEvent<T>.() -> Unit) = ClickType.MIDDLE.invoke(callback)
    fun numberKey(callback: ButtonClickedEvent<T>.() -> Unit) = ClickType.NUMBER_KEY.invoke(callback)
    fun double(callback: ButtonClickedEvent<T>.() -> Unit) = ClickType.DOUBLE_CLICK.invoke(callback)
    fun offhand(callback: ButtonClickedEvent<T>.() -> Unit) = ClickType.SWAP_OFFHAND.invoke(callback)
    fun unknown(callback: ButtonClickedEvent<T>.() -> Unit) = ClickType.UNKNOWN.invoke(callback)

    fun clear() {
        anyCallback = {}
        callbacks.clear()
    }

    fun clone() = ClickCallback<T>().let { copy ->
        if (::anyCallback.isInitialized)
            copy.anyCallback = this.anyCallback
        copy.callbacks = mutableMapOf()
        for ((types, callback) in callbacks) {
            copy.callbacks[types] = callback
        }
        return@let copy
    }
}