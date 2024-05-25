package com.mattmx.ktgui.sound

import com.mattmx.ktgui.scheduling.TaskTracker
import com.mattmx.ktgui.scheduling.future
import com.mattmx.ktgui.utils.ticks
import net.kyori.adventure.audience.Audience
import net.kyori.adventure.key.Key
import net.kyori.adventure.sound.Sound.*
import org.bukkit.Location
import org.bukkit.Sound
import org.bukkit.entity.Player
import java.util.*
import java.util.function.Supplier

class ComplexSoundBuilder {
    private var defaultEmitter = EmitterType.SELF
    private var location = Optional.empty<Supplier<Location>>()
    private var steps = arrayListOf<Step>()

    infix fun relative(value: Boolean) = apply {
        defaultEmitter = if (value) EmitterType.SELF else EmitterType.LOCATION
    }

    infix fun location(location: Location) = location { location }

    infix fun location(location: Supplier<Location>) = apply {
        relative(false)
        this.location = Optional.of(location)
    }

    fun play(sound: Sound) = play(sound.key())

    fun play(sound: Key) =
        SoundBuilder(sound.key()).apply { steps.add(Step(Step.Type.SOUND, this)) }

    fun thenPlay(sound: Sound) = thenPlay(sound.key())

    fun thenPlay(sound: Key) = thenPlay(SoundBuilder(sound))

    fun thenPlay(sound: SoundBuilder) = apply {
        steps.add(Step(Step.Type.SOUND, sound))
    }

    fun wait(ticks: Long) {
        this.steps.add(Step(Step.Type.WAIT, ticks))
    }

    fun thenWait(ticks: Long) = apply {
        wait(ticks)
    }

    fun playFor(audience: Audience) = TaskTracker()
        .apply {
            runAsync {
                val it = steps.iterator()

                while (it.hasNext()) {
                    val step = it.next()

                    when (step.type) {
                        Step.Type.SOUND -> {
                            val wrapper = step.sound()
                            val sound = wrapper.build()
                            if (wrapper.emitter == EmitterType.LOCATION && wrapper.location.isPresent) {
                                val location = wrapper.location.get().get()
                                audience.playSound(sound, location.x, location.z, location.z)
                            } else {
                                audience.playSound(sound(wrapper.sound, wrapper.source, wrapper.volume, wrapper.pitch), Emitter.self())
                            }
                        }
                        else -> {
                            val delay = step.delay()

                            // todo this will lead to thread starvation - instead build to a chain of tasks
                            future {
                                runAsyncLater(delay) {
                                    complete(Unit)
                                }
                            }.get()
                        }
                    }
                }
            }
        }

    enum class EmitterType {
        LOCATION,
        SELF
    }

    class Step(
        val type: Type,
        val value: Any
    ) {
        fun delay() = value as Long
        fun sound() = value as SoundBuilder

        enum class Type {
            SOUND,
            WAIT
        }
    }

    class SoundBuilder(
        val sound: Key
    ) {
        var emitter = EmitterType.SELF
            private set
        var location = Optional.empty<Supplier<Location>>()
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

        infix fun relative(value: Boolean) = apply {
            emitter = if (value) EmitterType.SELF else EmitterType.LOCATION
        }

        infix fun location(location: Location) = location { location }

        infix fun location(location: Supplier<Location>) = apply {
            relative(false)
            this.location = Optional.of(location)
        }

        fun build() = net.kyori.adventure.sound.Sound.sound(sound, source, volume, pitch)
    }
}

fun soundBuilder(block: ComplexSoundBuilder.() -> Unit) =
    ComplexSoundBuilder().apply(block)

fun sound(sound: Sound) = sound(sound.key())

fun sound(key: Key) = ComplexSoundBuilder.SoundBuilder(key)

fun Audience.playSound(sound: ComplexSoundBuilder) = sound.playFor(this)

fun main(player: Player) {
    val custom = soundBuilder {
        location { player.location.clone().add(0.0, 100.0, 0.0) }

        play(Sound.ENTITY_ENDER_DRAGON_DEATH) volume 1f pitch 2f relative true
        wait(1.ticks)
        play(Sound.BLOCK_NOTE_BLOCK_BANJO) volume 2f pitch 0f relative true
    }

    val builder = ComplexSoundBuilder()
        .relative(true)
        .thenPlay(Sound.ENTITY_ENDER_DRAGON_DEATH)
        .thenWait(100)
        .thenPlay(sound(Sound.BLOCK_NOTE_BLOCK_BANJO) volume 0.4f)

    player.playSound(custom)
}