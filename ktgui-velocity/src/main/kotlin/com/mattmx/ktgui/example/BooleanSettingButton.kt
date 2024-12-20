package com.mattmx.ktgui.example

import com.github.retrooper.packetevents.protocol.component.ComponentTypes
import com.github.retrooper.packetevents.protocol.item.type.ItemTypes
import com.github.retrooper.packetevents.util.Dummy
import com.mattmx.ktgui.GuiButton
import com.mattmx.ktgui.PacketGuiButton
import com.mattmx.ktgui.PacketGuiScreen
import com.mattmx.ktgui.button
import com.mattmx.ktgui.click.ClickTypes
import com.mattmx.ktgui.screen.GuiScreen
import com.mattmx.ktgui.util.not
import net.kyori.adventure.text.Component
import kotlin.reflect.KMutableProperty

class BooleanSettingButton(
    private val property: KMutableProperty<Boolean>,
    private val parent: GuiScreen<*, *>
) : PacketGuiButton<BooleanSettingButton>(ItemTypes.DIRT) {
    val switch = button(if (get()) ItemTypes.LIME_CANDLE else ItemTypes.GRAY_CANDLE) {}
    val meta = property.annotations
        .filterIsInstance<BooleanOption>()
        .firstOrNull()
        ?: BooleanOption(property.name, "minecraft:dirt")

    init {
        click(ClickTypes.LEFT) {
            property.setter.call(!get())
            applyChanges(button)
            applyChanges(switch)
            refresh(parent)
        }

        applyChanges(this)
        applyChanges(switch)
    }

    fun applyChanges(button: PacketGuiButton<*>) = button.apply {
        val state = get()
        named(if (state) !"<green>${meta.name}" else !"<red>${meta.name}")

        material = if (button == switch) {
            if (get()) ItemTypes.LIME_CANDLE else ItemTypes.GRAY_CANDLE
        } else {
            ItemTypes.getByName(meta.icon) ?: material
        }

        component(ComponentTypes.HIDE_ADDITIONAL_TOOLTIP, Dummy.DUMMY)

        lore {
            clear()

            +Component.empty()
            +if (state) !"<green>Enabled" else !"<red>Disabled"
            +Component.empty()
        }
    }

    fun get() = property.getter.call()

}

annotation class BooleanOption(
    val name: String,
    val icon: String
)

fun PacketGuiScreen<*>.booleanButton(property: KMutableProperty<Boolean>) =
    BooleanSettingButton(property, this)