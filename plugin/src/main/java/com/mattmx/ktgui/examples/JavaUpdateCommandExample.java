package com.mattmx.ktgui.examples;

import com.mattmx.ktgui.commands.SimpleCommandBuilder;
import com.mattmx.ktgui.commands.declarative.ChainCommandBuilder;
import com.mattmx.ktgui.commands.declarative.DeclarativeCommandBuilder;
import com.mattmx.ktgui.commands.declarative.arg.Argument;
import com.mattmx.ktgui.commands.declarative.arg.ArgumentContext;
import com.mattmx.ktgui.commands.declarative.arg.consumer.GreedyArgumentConsumer;
import com.mattmx.ktgui.commands.declarative.arg.impl.OnlinePlayerArgument;
import com.mattmx.ktgui.commands.declarative.arg.impl.StringArgument;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static com.mattmx.ktgui.commands.declarative.DeclarativeCommandBuilderKt.command;
import static com.mattmx.ktgui.utils.ColorKt.component;

public class JavaUpdateCommandExample {
    private void test() {
        new SimpleCommandBuilder("mattmx")
                .permission("ktgui.command.mattmx")
                .executes((invocation) -> {
                    invocation.player().sendMessage(Component.text("Command ran"));
                    return null;
                })
                .subCommand(
                        new SimpleCommandBuilder("ping")
                                .executes((invocation) -> {
                                    invocation.player().sendMessage("pong");
                                    return null;
                                })
                )
                .register(false);

        DeclarativeCommandBuilder.fromString("/hello <arg:string>")
                .runs(Player.class, (context) -> {
                    ArgumentContext<String> arg = context.getArgumentContext("arg");

                    if (arg == null) {
                        System.out.println("arg was null");
                    }

                    assert arg != null;
                    System.out.println(arg.getOrNull());
                });

        Argument<Player> player = new OnlinePlayerArgument("player", "player");
        Argument<String> msg = new StringArgument("msg", "string", new GreedyArgumentConsumer())
                .min(1);

        new ChainCommandBuilder("msg")
                .argument(player)
                .argument(msg)
                .build()
                .runs(Player.class, (context) -> {
                    String msgValue = msg.getValue(context);
                    String str = String.format("[%s -> Me]: %s", context.getSender().name(), msgValue);
                    player.getValue(context).sendMessage(component(str));
                }).register(this);
    }
}
