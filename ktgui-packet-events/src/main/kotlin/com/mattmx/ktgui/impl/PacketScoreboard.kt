package com.mattmx.ktgui.impl

import com.github.retrooper.packetevents.PacketEvents
import com.github.retrooper.packetevents.protocol.score.ScoreFormat
import com.github.retrooper.packetevents.wrapper.PacketWrapper
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerScoreboardObjective
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerScoreboardObjective.ObjectiveMode
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerUpdateScore
import com.mattmx.ktgui.scoreboard.Scoreboard
import net.kyori.adventure.text.Component
import java.util.*

class PacketScoreboard(initialTitle: Component) : Scoreboard(initialTitle) {
    val id = UUID.randomUUID().toString()
    private val packetEvents = PacketEvents.getAPI()

    override fun addViewer(uuid: UUID) {
        if (viewers.add(uuid)) {
            val user = packetEvents.playerManager.getUser(uuid)
                ?: return

            user.sendPacket(createScoreboardCreatePacket(ObjectiveMode.CREATE))
            createScoreboardContentPacket().forEach(user::sendPacket)
        }
    }

    override fun removeViewer(uuid: UUID) {
        if (viewers.remove(uuid)) {
            val user = packetEvents.playerManager.getUser(uuid)
                ?: return

            user.sendPacket(createScoreboardCreatePacket(ObjectiveMode.REMOVE))
        }
    }

    override fun update() {
        sendPacketToViewers(*createScoreboardContentPacket().toTypedArray())
    }

    override fun updateTitle() {
        sendPacketToViewers(createScoreboardCreatePacket(ObjectiveMode.UPDATE))
    }

    fun createScoreboardContentPacket(): MutableList<WrapperPlayServerUpdateScore> {
        val list = mutableListOf<WrapperPlayServerUpdateScore>()

        for (i in (0..<16)) {
            val component = content.content.getOrNull(i)
            val action = component?.let { WrapperPlayServerUpdateScore.Action.CREATE_OR_UPDATE_ITEM }
                ?: WrapperPlayServerUpdateScore.Action.REMOVE_ITEM

            list += WrapperPlayServerUpdateScore(
                "$id$i",
                action,
                id,
                16 - i,
                component,
                ScoreFormat.blankScore()
            )
        }

        return list
    }

    fun createScoreboardCreatePacket(type: ObjectiveMode): WrapperPlayServerScoreboardObjective {
        return WrapperPlayServerScoreboardObjective(
            id,
            type,
            title,
            WrapperPlayServerScoreboardObjective.RenderType.INTEGER
        )
    }

    fun sendPacketToViewers(vararg packets: PacketWrapper<*>) {
        for (viewer in viewers) {
            val user = packetEvents.playerManager.getUser(viewer)
                ?: continue

            packets.forEach { packet -> user.sendPacket(packet) }
        }
    }
}