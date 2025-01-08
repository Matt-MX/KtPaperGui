package com.mattmx.ktgui.sound

import com.mattmx.ktgui.GuiManager
import com.mattmx.ktgui.TaskWrapper
import net.kyori.adventure.audience.Audience
import net.kyori.adventure.key.Key
import kotlin.time.Duration

open class MultiSoundBuilder {
    private var steps = arrayListOf<Step>()

    fun play(sound: Key) = SoundBuilder(sound.key())
        .also { steps.add(Step(Step.Type.SOUND, it)) }

    fun thenPlay(sound: Key) = thenPlay(SoundBuilder(sound))

    fun thenPlay(sound: SoundBuilder) = apply {
        steps.add(Step(Step.Type.SOUND, sound))
    }

    infix fun mapSounds(block: SoundBuilder.() -> Unit) = apply {
        steps.filter { it.type == Step.Type.SOUND }
            .forEach { block.invoke(it.sound()) }
    }

    @JvmName("waitTicks")
    fun wait(delay: Duration) {
        this.steps.add(Step(Step.Type.WAIT, delay))
    }

    fun thenWait(delay: Duration) = apply {
        wait(delay)
    }

    fun playFor(audience: Audience) {
        val taskTracker = GuiManager.getInstance().createTaskTracker<TaskWrapper>(Unit)

        var index = 0

        // Anonymous recursive function to play steps without any thread starvation
        fun next(delay: Duration) {
            taskTracker.runAsyncDelayed(delay) {
                val step = steps[index]

                index++

                when (step.type) {
                    Step.Type.SOUND -> {
                        step.sound().play(audience)
                        next(Duration.ZERO)
                    }

                    Step.Type.WAIT -> {
                        next(step.delay())
                    }
                }

                if (index >= steps.size) {
                    taskTracker.cancelAll()
                }
            }
        }

        next(Duration.ZERO)
    }

    class Step(
        val type: Type,
        val value: Any
    ) {
        fun delay() = value as Duration
        fun sound() = value as SoundBuilder

        enum class Type {
            SOUND,
            WAIT
        }
    }

    companion object {
        fun Audience.playSound(sound: MultiSoundBuilder) {
            sound.playFor(this)
        }
    }
}