package com.mattmx.ktgui.components

import com.mattmx.ktgui.components.button.ButtonClickedEvent
import com.mattmx.ktgui.components.button.IGuiButton
import com.mattmx.ktgui.event.ContinuousEventCallback
import org.bukkit.event.inventory.ClickType

class ClickCallback<T : IGuiButton<*>> {
    private var callbacks = arrayListOf<Pair<ClickType, ContinuousEventCallback<ButtonClickedEvent<T>>>>()
    private var any = ContinuousEventCallback<ButtonClickedEvent<T>>()

    private fun getOrDefault(clickType: ClickType): ContinuousEventCallback<ButtonClickedEvent<T>> =
        callbacks.firstOrNull { it.first == clickType }?.second
            ?: run {
                val cb = ContinuousEventCallback<ButtonClickedEvent<T>>()
                callbacks.add(clickType to cb)
                return cb
            }

    /**
     * Called when a click event is received for this GUI element.
     *
     * @param event
     */
    fun run(event: ButtonClickedEvent<*>) {
        if (!event.shouldContinueCallback()) return

        // Get relevant callbacks
        if (!any.invoke(event as ButtonClickedEvent<T>)) {
            return
        }

        val relevant =
            callbacks.filter { it.first == event.event.click }

        relevant.forEach {
            if (!(it.second.invoke(event)))
                return@forEach
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
        any.invoke(callback)
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
    fun handleClicks(callback: ButtonClickedEvent<T>.() -> Unit, vararg clickType: ClickType) {
        for (click in clickType) {
            val cb = getOrDefault(click)
            cb.invoke(callback)
        }
    }

    operator fun ClickType.invoke(callback: ButtonClickedEvent<T>.() -> Unit) {
        handleClicks(callback, this)
    }

    operator fun Array<ClickType>.invoke(callback: ButtonClickedEvent<T>.() -> Unit) {
        handleClicks(callback, *this)
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
        any.clear()
        callbacks.clear()
    }

    fun clone() = ClickCallback<T>().let { copy ->
        copy.any = any.clone()
        copy.callbacks = ArrayList(callbacks.map { it.first to it.second.clone() })
        return@let copy
    }
}