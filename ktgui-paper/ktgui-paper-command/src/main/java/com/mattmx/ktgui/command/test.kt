package com.mattmx.ktgui.command

import com.mattmx.ktgui.command.arg.*
import com.mattmx.ktgui.util.not
import com.mojang.brigadier.Command
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.context.CommandContext
import io.papermc.paper.command.brigadier.CommandSourceStack
import io.papermc.paper.command.brigadier.Commands
import io.papermc.paper.command.brigadier.MessageComponentSerializer
import io.papermc.paper.plugin.lifecycle.event.LifecycleEventManager
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents
import org.bukkit.entity.Player
import org.bukkit.plugin.Plugin
import org.bukkit.plugin.java.JavaPlugin


class YourPluginClass : JavaPlugin() {
    override fun onEnable() {
        val manager: LifecycleEventManager<Plugin> = this.lifecycleManager
        manager.registerEventHandler(LifecycleEvents.COMMANDS, { event ->
            val commands: Commands = event.registrar()
            commands.register(
                Commands.literal("new-command")
                    .executes { ctx: CommandContext<CommandSourceStack> ->
                        ctx.source.sender.sendPlainMessage("some message")
                        Command.SINGLE_SUCCESS
                    }
                    .build(),
                "some bukkit help description string",
                listOf("an-alias")
            )
        })
    }

    fun createIrisPlayerType(): CustomArgumentTypeBuilder<Player, String> {
        return customArgument<Player, String>(StringArgumentType.string()) {
            convert { string ->
                TODO("Implement iris provider for thie player by username")
            }

            suggests { context, builder ->
                builder.suggest("MattMX", MessageComponentSerializer.message().serialize(!"&aOwner"))

                builder.buildFuture()
            }
        }
    }

    fun registerMsgCommand() {
        val irisPlayerType = createIrisPlayerType()
        val irisPlayer = { custom(irisPlayerType) }

        val msg by greedyString()
        val globalPlayer by irisPlayer()

        command("msg" / globalPlayer / msg) {
            runs<Player> {
                globalPlayer().sendMessage(!"${sender.name} -> You: ${msg()}")
                sender.sendMessage(!"You -> ${globalPlayer().name}: ${msg()}")
            }
        }.register(this).unregister()

        val user by player()
        command("fly") {
            runs<Player> {
                sender.allowFlight = !sender.allowFlight
            }

            sub(user) {
                runs<Player> {
                    val otherPlayer: Player = user.first()
                    otherPlayer.allowFlight = !otherPlayer.allowFlight
                }
            }
        }.register(this)
    }
}
