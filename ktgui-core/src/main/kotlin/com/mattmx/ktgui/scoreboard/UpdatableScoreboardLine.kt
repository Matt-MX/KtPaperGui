package com.mattmx.ktgui.scoreboard

import com.mattmx.ktgui.TaskWrapper
import net.kyori.adventure.text.Component
import java.util.*
import kotlin.time.Duration

class UpdatableScoreboardLine(
    val owner: Scoreboard,
    val slot: Int,
    val supplier: () -> Component
) : ScoreboardLine {
    var updatingTask = Optional.empty<TaskWrapper>()
        private set

    infix fun updateEvery(duration: Duration) = apply {
        this.updatingTask = Optional.of(owner.tasks.runAsyncRepeat(duration) {
            owner.updateLine(slot)
        })
    }

    override fun getComponent(): Component {
        return supplier()
    }

}