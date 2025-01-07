package com.mattmx.ktgui.impl

import com.github.retrooper.packetevents.PacketEvents
import com.github.retrooper.packetevents.protocol.score.ScoreFormat
import com.github.retrooper.packetevents.wrapper.PacketWrapper
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerDisplayScoreboard
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerResetScore
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerScoreboardObjective
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerScoreboardObjective.ObjectiveMode
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerUpdateScore
import com.mattmx.ktgui.scoreboard.Scoreboard
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

    override fun updateTitle() {
        sendPacketToViewers(createScoreboardCreatePacket(ObjectiveMode.UPDATE))
    }

    /**
     * Packet(s) to update the contents in the scoreboard
     */
    fun createScoreboardContentPacket(): MutableList<PacketWrapper<*>> {
        val list = mutableListOf<PacketWrapper<*>>()

        for (i in (0..<16)) {
            val component = content.content.getOrNull(i)

            if (component != null) {
                list += WrapperPlayServerUpdateScore(
                    "$id$i",
                    WrapperPlayServerUpdateScore.Action.CREATE_OR_UPDATE_ITEM,
                    id,
                    16 - i,
                    component,
                    ScoreFormat.blankScore()
                )
            } else {
                list += WrapperPlayServerResetScore("$id$i", this.id)
            }
        }

        return list
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