package com.mattmx.ktgui.scoreboard

import com.mattmx.ktgui.TaskWrapper
import net.kyori.adventure.text.Component

open class ScoreboardContent(
    val owner: Scoreboard
) {
    val content = mutableListOf<ScoreboardLine>()

    operator fun Component.unaryPlus() {
        require(content.size <= MAX_ENTRIES)

        content.add(ScoreboardLine.of(this))
    }

    operator fun (() -> Component).unaryPlus(): UpdatableScoreboardLine {
        require(content.size <= MAX_ENTRIES)

        return UpdatableScoreboardLine(owner, content.size, this).also { content.add(this) }
    }

    operator fun set(line: Int, text: () -> Component) {
        require(line < MAX_ENTRIES)

        if (fillUntil(line) > 0) {
            remove(line)
        }

        content[line] = UpdatableScoreboardLine(owner, line, text)
    }

    operator fun set(line: Int, text: Component) {
        require(line < MAX_ENTRIES)

        if (fillUntil(line) > 0) {
            remove(line)
        }

        content[line] = ScoreboardLine.of(text)
    }

    fun remove(line: Int) : Boolean {
        if (content.size <= line) {
            return false
        }

        content.removeAt(line).let { content ->
            if (content is UpdatableScoreboardLine) {
                content.updatingTask.ifPresent(TaskWrapper::cancel)
            }
        }
        return true
    }

    fun clear() {
        val length = content.size
        repeat(length) { remove(0) }
    }

    fun fillUntil(line: Int) : Int {
        require(line < MAX_ENTRIES)

        var i = 0
        while (content.size < line) {
            content.add(ScoreboardLine.empty())
            i++
        }

        return i
    }

    companion object {
        const val MAX_ENTRIES = 15
    }
}