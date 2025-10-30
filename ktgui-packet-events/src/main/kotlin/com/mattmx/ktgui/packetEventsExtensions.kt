package com.mattmx.ktgui

import com.github.retrooper.packetevents.PacketEvents
import com.github.retrooper.packetevents.protocol.sound.SoundCategory
import com.github.retrooper.packetevents.protocol.sound.Sounds
import com.github.retrooper.packetevents.util.Vector3i
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSoundEffect
import com.mattmx.ktgui.impl.PacketEventsKtGui
import net.kyori.adventure.audience.Audience
import net.kyori.adventure.sound.Sound

fun <T : Audience> T.tryPlaySound(sound: Sound) {
    KtGui.getInstance<PacketEventsKtGui>()
        .playerLocationTracker
        .ifPresent { playerLocationTracker ->
            val key = Sounds.getByNameOrCreate(sound.name().asString())

            val packet = WrapperPlayServerSoundEffect(
                key,
                SoundCategory.fromId(sound.source().ordinal),
                playerLocationTracker.getLocation(this)
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