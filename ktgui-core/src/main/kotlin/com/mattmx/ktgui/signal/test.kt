package com.mattmx.ktgui.signal

class IGui {
    val buttons = mutableListOf<IButton>()

    fun <T : Any> registerIntents(signal: Signal<T>) {
        signal.receivers.add { newValue ->

        }
    }
}

class IButton : SignalSubscriber<Any> {
    lateinit var block: IButton.() -> Unit

    override fun onUpdate(newValue: Any) {
        block.invoke(this)
    }

    operator fun invoke(block: IButton.() -> Unit) {
        this.block = block
    }
}

fun main() {
    val gui = IGui()
    var mySignal by Signal("meow") {
        gui.registerIntents(this)
    }

    val button = IButton()
    val button2 = IButton()
    gui.buttons.add(button)
    gui.buttons.add(button2)

    button {
        println(mySignal)
    }

    mySignal = "test"
}