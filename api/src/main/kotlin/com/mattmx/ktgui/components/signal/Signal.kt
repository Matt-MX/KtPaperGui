package com.mattmx.ktgui.components.signal

import kotlin.math.sign
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/**
 * Signals keep track of your variable and automatically update any registered
 * [SignalListener]s when the variable value changes.
 *
 * <pre>
 *      val signal = Signal<Any, String>("Hello", owner)
 *
 *      signal.addDependency { println("Value of signal changed -> $signal") }
 *
 *      signal.invoke("new value of signal")
 * </pre>
 *
 * @param initial value of the [Signal]
 * @param owner owner object of this [Signal]
 */
class Signal<V>(initial: V, private val owner: SignalOwner) : ReadWriteProperty<Nothing?, V> {
    private var value: V = initial
    private val listeners = arrayListOf<SignalListener<V>>()

    /**
     * Returns current value of [value], will also call [GuiSignalOwner.addDependency].
     */
    override fun getValue(thisRef: Nothing?, property: KProperty<*>): V {
        owner.addDependency(this)
        return this.value
    }

    /**
     * Sets the value of [value]
     */
    override fun setValue(thisRef: Nothing?, property: KProperty<*>, value: V) {
        this.value = value
        invokeListeners()
    }

    /**
     * Get the value of [value] and add [Signal] dependency
     *
     * @return [value]
     */
    operator fun SignalOwner.invoke() = value.apply {
        addDependency(this@Signal)
    }

    fun SignalOwner.get() = invoke()

    /**
     * Set the value of [value].
     * Updates all [listeners].
     *
     * @param newValue new value of [value]
     */
    infix operator fun invoke(newValue: V) {
        this.value = newValue
        invokeListeners()
    }

    /**
     * Allows you to modify [value] internally.
     * Updates all [listeners].
     *
     * @param mutate block where you modify [value]
     */
    infix fun mut(mutate: V.() -> Unit) {
        mutate(value)
        invokeListeners()
    }

    /**
     * Sets the value of [value] to the return of [mutate].
     * Updates all [listeners].
     *
     * @param mutate change the variable and return it.
     */
    infix fun setTo(mutate: V.() -> V) {
        this.value = mutate(value)
        invokeListeners()
    }

    /**
     * Sets the value of [value] to [newValue].
     * Updates all [listeners].
     *
     * @param newValue new value of [value]
     */
    infix fun setTo(newValue: V) {
        this.value = newValue
        invokeListeners()
    }

    /**
     * Updates all [listeners].
     */
    fun invokeListeners() {
        listeners.toMutableList().forEach { it.onChange(this.value) }
    }

    /**
     * Adds a dependency if it isn't already registered.
     *
     * Will not duplicate dependencies
     *
     * @param signalListener the [SignalListener] to register.
     */
    fun addDependency(signalListener: SignalListener<V>) : Boolean {
        if (this.listeners.contains(signalListener)) return false
        this.listeners += signalListener
        return true
    }

    fun listeners() = listeners.toMutableList()
    fun removeListener(listener: SignalListener<V>) = listeners.remove(listener)
}