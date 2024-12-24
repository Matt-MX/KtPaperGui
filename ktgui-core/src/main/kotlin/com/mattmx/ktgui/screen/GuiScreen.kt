package com.mattmx.ktgui.screen

import com.mattmx.ktgui.button.GuiButton
import com.mattmx.ktgui.GuiManager
import com.mattmx.ktgui.event.SlotUpdatedEvent
import com.mattmx.ktgui.trait.TraitHolder
import com.mattmx.ktgui.util.EventCallback
import com.mattmx.ktgui.util.ParentEventCallback
import net.kyori.adventure.text.Component

abstract class GuiScreen<P : Any, B : GuiButton<*, *, *, *>>(
    var guiType: GuiType,
    title: Component
) {
    open var title: Component = title
        set(value) {
            field = value
            refreshTitle()
        }
    var windowIdentifier: String = GuiButton.EMPTY_ID
    var items = hashMapOf<Int, B>()
    val slotUpdated = EventCallback<SlotUpdatedEvent<B>>()
    val traits = TraitHolder<GuiScreen<*, *>>(this)
    abstract val open: ParentEventCallback<P, *>
    abstract val close: ParentEventCallback<P, *>

    /**
     * Register a button to a slot.
     *
     * @param slot to add the button to
     * @return self (button invoked)
     */
    infix fun B.slot(slot: Int): B {
        val old = items.put(slot, this)

        val event = SlotUpdatedEvent(old, this, slot)
        slotUpdated.apply(event)

        return this
    }

    /**
     * Register a button to multiple slots.
     *
     * @param slots to add the button to
     * @return self (button invoked)
     */
    infix fun B.slots(slots: List<Int>): B {
        slots.forEach { slot(it) }

        return this
    }

    /**
     * Register a button to a slot.
     *
     * @param slot to add the [button] to
     * @param button to set
     * @return self (button invoked)
     */
    operator fun set(slot: Int, button: B) = button.slot(slot)

    /**
     * Register a button to a slot.
     *
     * @param slot to add the [button] to
     * @param button to set
     * @return self (button invoked)
     */
    operator fun set(slot: Int, button: Any) = (button as? B)?.slot(slot)

    /**
     * Register a button to a slot.
     *
     * @param x coordinate
     * @param y coordinate
     * @param button to set
     * @return self (button invoked)
     */
    operator fun set(x: Int, y: Int, button: B) = button.slot(slot(x, y))

    /**
     * Get a button in a slot.
     *
     * @param slot to check
     * @return a button in that slot or null
     */
    operator fun get(slot: Int) = items[slot]

    /**
     * Get a button in a slot by x/y.
     *
     * @param x coordinate
     * @param y coordinate
     * @return a button in that slot or null
     */
    operator fun get(x: Int, y: Int) = items[slot(x, y)]

    /**
     * Remove a button from a slot.
     *
     * @param slot to remove from
     * @return the removed button or null
     */
    fun remove(slot: Int): B? {
        return items.remove(slot)?.also { button ->
            slotUpdated.apply(SlotUpdatedEvent(button, null, slot))
        }
    }

    /**
     * Remove a button from a slot by x/y.
     *
     * @param x coordinate
     * @param y coordinate
     * @return the removed button or null
     */
    fun remove(x: Int, y: Int) = remove(slot(x, y))

    /**
     * Remove all instances of button.
     *
     * @param button to remove
     * @return the slots and buttons that were removed
     */
    fun <B : GuiButton<*, *, *, *>> remove(button: B) =
        getSlots(button).associateWith { remove(it) }

    /**
     * Remove all buttons that inherit a type.
     *
     * @param buttonClass the class of the button
     * @return the slots and buttons that were removed
     */
    fun <B : GuiButton<*, *, *, *>> remove(buttonClass: Class<B>) =
        getSlots(buttonClass).keys.associateWith { remove(it) }

    /**
     * Remove all buttons in a list of slots.
     *
     * @param slots the slots to clear (default is the entire screen)
     * @return the slots and buttons that were removed
     */
    fun clear(slots: List<Int> = getVisibleGuiButtons().toList()): Map<Int, B> {
        return slots.mapNotNull { remove(it)?.let { b -> it to b } }
            .also { removedSlots ->
                for ((slot, button) in removedSlots) {
                    val event = SlotUpdatedEvent(button, null, slot)
                    this.slotUpdated.apply(event)
                }
            }.toMap()
    }

    /**
     * Get the slots a button occupies
     */
    val B.slots: Set<Int>
        get() = items.filterValues { it == this }.keys

    /**
     * Get the slots a button occupies
     *
     * @param button the button to search for
     */
    @JvmName("getSlots1")
    fun <B : GuiButton<*, *, *, *>> getSlots(button: B): Set<Int> =
        items.filterValues { it == button }.keys

    /**
     * Get the slots a type of button occupies.
     *
     * @param buttonClass the type button to search for
     */
    fun <B : GuiButton<*, *, *, *>> getSlots(buttonClass: Class<B>): Map<Int, B> = items
        .filterValues { buttonClass.isInstance(it) }
        .mapValues { buttonClass.cast(it) }

    fun slot(x: Int, y: Int) = Slots.ofPosition(x, y)

    fun setActiveGui(player: P) {
        GuiManager.getInstance().setActiveGui(player, this)
    }

    fun unsetActiveGui(player: P) {
        GuiManager.getInstance().removeActiveGui(player)
    }

    open fun getVisibleGuiButtons(): IntRange = (guiType.first..guiType.last)

    abstract fun open(player: P): GuiScreen<P, B>

    abstract fun refresh(player: P)

    abstract fun refreshTitle(player: P)

    fun openAsAny(player: Any) = (player as? P)?.let { open(it) }
    fun refreshAsAny(player: Any) = (player as? P)?.let { refresh(it) }
    fun refreshTitleAsAny(player: Any) = (player as? P)?.let { refreshTitle(it) }

    fun forcefullyClose(player: Any) {
        GuiManager.getInstance().forcefullyClose(player)
    }

    fun forcefullyCloseAll() {
        for ((player, _) in getAllWatchingInstance()) {
            forcefullyClose(player)
        }
    }

    fun refresh() {
        for ((player, _) in getAllWatchingInstance()) {
            refreshAsAny(player)
        }
    }

    fun refreshTitle() {
        for ((player, _) in getAllWatchingInstance()) {
            refreshTitleAsAny(player)
        }
    }

    fun getAllWatchingInstance(): Map<out Any, GuiScreen<*, *>> {
        return GuiManager.getInstance().getActiveOfInstance(this)
    }
}