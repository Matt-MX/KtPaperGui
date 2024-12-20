package com.mattmx.ktgui

import com.github.retrooper.packetevents.PacketEvents
import com.github.retrooper.packetevents.protocol.item.type.ItemType
import com.github.retrooper.packetevents.protocol.sound.SoundCategory
import com.github.retrooper.packetevents.protocol.sound.Sounds
import com.github.retrooper.packetevents.util.Vector3d
import com.github.retrooper.packetevents.util.Vector3i
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSoundEffect
import com.mattmx.ktgui.screen.GuiType
import net.kyori.adventure.audience.Audience
import net.kyori.adventure.sound.Sound
import net.kyori.adventure.text.Component

fun gui(title: Component, type: GuiType, block: PacketGuiScreen<*>.() -> Unit) =
    PacketGuiScreen(type, title).apply(block)

fun button(material: ItemType, block: PacketGuiButton<*>.() -> Unit) =
    PacketGuiButton(material).apply(block)

fun <T : Audience> T.tryPlaySound(sound: Sound) {
    GuiManager.getInstance<PacketEventsGuiManager>()
        .playerLocationTracker
        .ifPresent { playerLocationTracker ->
            val key = Sounds.getByNameOrCreate(sound.name().asString())

            val packet = WrapperPlayServerSoundEffect(
                key,
                SoundCategory.fromId(sound.source().ordinal),
                playerLocationTracker.getCachedLocation(this)
                    ?.pos
                    ?.toVector3i()
                    ?: Vector3i.zero(),
                sound.volume(),
                sound.pitch(),
                sound.seed().orElse(0L)
            )

            PacketEvents.getAPI()
                .playerManager
                .sendPacket(this, packet)
        }
}