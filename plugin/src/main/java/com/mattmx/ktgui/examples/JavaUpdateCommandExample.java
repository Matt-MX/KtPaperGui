package com.mattmx.ktgui.examples;

import com.mattmx.ktgui.commands.SimpleCommandBuilder;
import com.mattmx.ktgui.commands.declarative.DeclarativeCommandBuilder;
import com.mattmx.ktgui.commands.declarative.arg.ArgumentContext;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

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
    }
}
