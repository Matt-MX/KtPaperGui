package com.mattmx.ktgui.sound

import net.kyori.adventure.audience.Audience
import net.kyori.adventure.key.Key
import net.kyori.adventure.sound.Sound
import net.kyori.adventure.sound.Sound.Emitter
import net.kyori.adventure.sound.Sound.Source
import java.util.*
import java.util.function.Supplier

// TODO impl for proxy
open class SoundBuilder(
    val sound: Key
) {
    var emitterType = EmitterType.EMITTER
        private set
    var emitter = Emitter.self()
        private set
    var location = Optional.empty<Supplier<LocationEmitter>>()
        private set
    var volume: Float = 1f
        private set
    var pitch: Float = 1f
        private set
    var source: Source = Source.MASTER
        private set

    infix fun volume(vol: Float) = apply {
        volume = vol
    }

    infix fun pitch(pit: Float) = apply {
        pitch = pit
    }

    infix fun source(src: Source) = apply {
        this.source = src
    }

    infix fun emitter(emitter: Emitter) = apply {
        this.emitterType = EmitterType.EMITTER
        this.emitter = emitter
    }

    infix fun location(block: LocationEmitter.() -> Unit): SoundBuilder {
        return location(Supplier { LocationEmitter().apply(block) })
    }

    infix fun location(location: LocationEmitter) = location(Supplier { location })

    infix fun location(location: Supplier<LocationEmitter>) = apply {
        emitterType = EmitterType.LOCATION
        this.location = Optional.of(location)
    }

    fun play(audience: Audience) {
        when (emitterType) {
            EmitterType.EMITTER -> build().let { sound ->
                audience.playSound(sound, emitter)
            }

            EmitterType.LOCATION -> build().let { sound ->
                val vec = location.get().get()
                audience.playSound(sound, vec.x, vec.y, vec.z)
            }
        }
    }

    fun build() = Sound.sound(sound, source, volume, pitch)

    companion object {
        fun Audience.playSound(sound: SoundBuilder) {
            forEachAudience { audience ->
                sound.play(audience)
            }
        }
    }
}