package com.mattmx.ktgui

import com.github.retrooper.packetevents.protocol.item.ItemStack
import com.github.retrooper.packetevents.protocol.item.type.ItemTypes
import com.mattmx.ktgui.impl.PacketGuiButton
import com.mattmx.ktgui.impl.PacketGuiInventoryScreen
import com.mattmx.ktgui.screen.GuiScreen
import com.mattmx.ktgui.util.ParentEventCallback

class BooleanButtonTest(
    initialState: Boolean,
    private val parent: GuiScreen<*, PacketGuiButton<*>>
) : PacketGuiButton<BooleanButtonTest>(ItemTypes.AIR) {
    private var initialized = false
    val ifTrue = ParentEventCallback<BooleanButtonTest, BooleanButtonTest>(this)
    val ifFalse = ParentEventCallback<BooleanButtonTest, BooleanButtonTest>(this)

    var state = initialState
        set(value) {
            field = value

            invokeCallbacks()

            refresh(parent)
        }

    fun invokeCallbacks() {
        if (state) {
            ifTrue.apply(this)
        } else {
            ifFalse.apply(this)
        }
    }

    override fun buildItem(): ItemStack {
        if (!initialized) {
            invokeCallbacks()
            initialized = true
        }

        return super.buildItem()
    }
}

fun PacketGuiInventoryScreen<*>.booleanButton(initialState: Boolean) =
    BooleanButtonTest(initialState, this)