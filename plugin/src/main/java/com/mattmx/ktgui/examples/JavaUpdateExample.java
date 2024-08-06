package com.mattmx.ktgui.examples;

import com.mattmx.ktgui.GuiManager;
import com.mattmx.ktgui.commands.declarative.ChainCommandBuilder;
import com.mattmx.ktgui.components.GuiPattern;
import com.mattmx.ktgui.components.button.ButtonClickedEvent;
import com.mattmx.ktgui.components.button.GuiButton;
import com.mattmx.ktgui.components.button.BaseGuiButton;
import com.mattmx.ktgui.components.screen.GuiScreen;
import com.mattmx.ktgui.components.signal.Signal;
import com.mattmx.ktgui.event.PreGuiBuildEvent;
import com.mattmx.ktgui.scheduling.TaskTracker;
import com.mattmx.ktgui.scoreboards.ScoreboardBuilder;
import com.mattmx.ktgui.sound.ChainSoundBuilder;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import static com.mattmx.ktgui.dsl.EventKt.event;
import static com.mattmx.ktgui.dsl.EventKt.event;
import static com.mattmx.ktgui.sound.ChainSoundBuilderKt.sound;
import static com.mattmx.ktgui.utils.ColorKt.component;

public class JavaUpdateExample implements Example {
    @Override
    @SuppressWarnings({"unchecked", "rawtypes"})
    public void run(@NotNull Player player) {
        GuiScreen gui = new GuiScreen(Component.text("Title"), 3)
                .addChild(
                        new BaseGuiButton(Material.DIRT, null)
                                .named(Component.text("Item"))
                                .click(ClickType.LEFT, (event) -> {
                                    event.getPlayer().sendMessage(Component.text("clicked"));
                                })
                                .lore(Component.text("Lore line one"))
                                .lore(Component.text("Lore line two"))
                                .slot(3)
                );
        gui.setId("ktgui.example.hook.java");

        // Test Patterns
        GuiPattern pattern = new GuiPattern("""
                ---------
                ---------
                --------x
                """);
        pattern.set('x',
                new GuiButton(Material.SPECTRAL_ARROW)
                        .named(Component.text("Close"))
                        .click(ClickType.LEFT, (event) -> {
                            gui.forceClose(((ButtonClickedEvent) event).getPlayer());
                        })
        );
        pattern.apply(gui);

        // Test Signals
        Signal<String> signalExample = gui.createSignal("foo");

        gui.effectBlock(() -> new GuiButton(Material.DIAMOND)
                .named(Component.text(signalExample.get()))
                .click(ClickType.LEFT, (event) -> signalExample.setTo(signalExample.get().equals("foo") ? "bar" : "foo"))
                .slot(5)
                .childOf(gui)
        );

        // Test Callbacks
        gui.onOpen((p) -> {
            p.sendMessage(component("opened."));
        }).onClose((e) -> {
            e.getPlayer().sendMessage(component("closed."));
        });

        gui.open(player);
    }

    public void eventsTest(JavaPlugin plugin) {
        event(plugin, PlayerJoinEvent.class, (event) -> {
            event.getPlayer().sendMessage(Component.text("welcome!"));
        });
    }

    public void scoreboard(Player player) {
        ScoreboardBuilder builder = new ScoreboardBuilder(Component.text("Scoreboard test"))
                .addLine(Component.text("Test"))
                .addLine(Component.text("two"));

        Bukkit.getScheduler().runTaskLater(GuiManager.owningPlugin, () -> {
            builder.set(0, Component.text("5 seconds have passed"));
        }, 20 * 5L);

        builder.showFor(player);
    }

    public void hookTest(PreGuiBuildEvent event) {
        if (!(event.getGui() instanceof GuiScreen)) return;
        GuiScreen gui = (GuiScreen) event.getGui();
        if (!gui.getId().equals("ktgui.example.hook.java")) return;

        // todo add stuff
        gui.addChild(new GuiButton(Material.STONE).slot(10));
    }

    public void testTaskTracker() {
        TaskTracker tracker = new TaskTracker();

        tracker.runAsyncLater(20L, (task) -> {
            System.out.println("1 second passed");
            tracker.cancelAll();
            return null;
        });

        tracker.runAsyncLater(40L, (task) -> {
            System.out.println("Will not execute.");
            return null;
        });
    }

    public void testSound(Player player) {
        ChainSoundBuilder sound = new ChainSoundBuilder()
            .thenPlay(Sound.ENTITY_ITEM_PICKUP)
            .thenWait(1L)
            .thenPlay(
                sound(Sound.ENTITY_ALLAY_DEATH)
                    .pitch(1f)
            );

        sound.playFor(player);
    }
}
