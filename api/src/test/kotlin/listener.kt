interface VariableListener<T> {
    fun onChange(value: T)
}

class Listenable<T>(private var value: T) {
    private val listeners = arrayListOf<VariableListener<T>>()

    fun get() = value
    fun mut(modify: T.() -> Unit) : T {
        modify(value)
        update()
        return value
    }

    fun set(newValue: T) {
        value = newValue
        update()
    }

    fun update() {
        listeners.toMutableList().forEach { it.onChange(value) }
    }

    fun addListener(listener: VariableListener<T>) {
        this.listeners.add(listener)
    }

    fun removeListener(listener: VariableListener<T>) {
        this.listeners.remove(listener)
    }
}

class SimpleVarListener<T> : VariableListener<T> {
    override fun onChange(value: T) {
        println(value)
    }
}

fun main() {
    val value = Listenable(arrayListOf<String>())

    // Debug whacky code
    value.addListener(SimpleVarListener())

    value.mut {
        add("Test")
        add("Test")
        add("Test")
        add("Test")
    }
}