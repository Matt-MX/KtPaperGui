package com.mattmx.ktgui.impl

import com.github.retrooper.packetevents.PacketEvents
import com.github.retrooper.packetevents.protocol.score.ScoreFormat
import com.github.retrooper.packetevents.wrapper.PacketWrapper
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerDisplayScoreboard
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerResetScore
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerScoreboardObjective
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerScoreboardObjective.ObjectiveMode
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerUpdateScore
import com.mattmx.ktgui.GuiManager
import com.mattmx.ktgui.TaskWrapper
import com.mattmx.ktgui.scoreboard.Scoreboard
import com.mattmx.ktgui.tasks.TaskTracker
import net.kyori.adventure.text.Component
import java.util.*

open class PacketScoreboard(initialTitle: Component) : Scoreboard(initialTitle) {
    val id = UUID.randomUUID().toString()

    override fun addViewer(uuid: UUID) : Boolean {
        if (viewers.add(uuid)) {
            sendPacket(uuid, createScoreboardCreatePacket(ObjectiveMode.CREATE))
            sendPacket(uuid, createShowScoreboardPacket())
            createScoreboardContentPacket().forEach { packet -> sendPacket(uuid, packet) }
            return true
        }
        return false
    }

    override fun removeViewer(uuid: UUID): Boolean {
        if (viewers.remove(uuid)) {
            sendPacket(uuid, createScoreboardCreatePacket(ObjectiveMode.REMOVE))
            return true
        }
        return false
    }

    override fun update() {
        sendPacketToViewers(*createScoreboardContentPacket().toTypedArray())
    }

    override fun updateLine(index: Int) {
        sendPacketToViewers(createScoreboardEntryPacket(index))
    }

    override fun updateTitle() {
        sendPacketToViewers(createScoreboardCreatePacket(ObjectiveMode.UPDATE))
    }

    override fun updating(plugin: Any) = apply {
        this.tasks = GuiManager.getInstance().createTaskTracker<TaskWrapper>(plugin)
    }

    /**
     * Packet(s) to update the contents in the scoreboard
     */
    fun createScoreboardContentPacket(): MutableList<PacketWrapper<*>> {
        val list = mutableListOf<PacketWrapper<*>>()

        for (i in (0..<16)) {
            list += createScoreboardEntryPacket(i)
        }

        return list
    }

    fun createScoreboardEntryPacket(line: Int) : PacketWrapper<*> {
        val component = content.content.getOrNull(line)

        return if (component != null) {
            WrapperPlayServerUpdateScore(
                "$id$line",
                WrapperPlayServerUpdateScore.Action.CREATE_OR_UPDATE_ITEM,
                id,
                16 - line,
                component.getComponent(),
                ScoreFormat.blankScore()
            )
        } else {
            WrapperPlayServerResetScore("$id$line", this.id)
        }
    }

    /**
     * Packet to register/unregister/update the scoreboard objective
     */
    fun createScoreboardCreatePacket(type: ObjectiveMode): WrapperPlayServerScoreboardObjective {
        return WrapperPlayServerScoreboardObjective(
            id,
            type,
            title,
            WrapperPlayServerScoreboardObjective.RenderType.INTEGER
        )
    }

    /**
     * Packet to actually display the scoreboard
     */
    fun createShowScoreboardPacket(): WrapperPlayServerDisplayScoreboard {
        return WrapperPlayServerDisplayScoreboard(1, this.id)
    }

    fun sendPacketToViewers(vararg packets: PacketWrapper<*>) {
        for (viewer in viewers) {
            packets.forEach { packet -> sendPacket(viewer, packet) }
        }
    }

    fun sendPacket(uuid: UUID, packetWrapper: PacketWrapper<*>) {
        val packetEvents = PacketEvents.getAPI()
        val channel = packetEvents.protocolManager.getChannel(uuid)
            ?: return

        packetEvents.protocolManager.sendPacket(channel, packetWrapper)
    }

    companion object {
        fun scoreboard(title: Component, block: (PacketScoreboard.() -> Unit)? = null): PacketScoreboard {
            return PacketScoreboard(title).also { block?.invoke(it) }
        }
    }
}