package com.mattmx.ktgui.examples;

import com.mattmx.ktgui.GuiManager;
import com.mattmx.ktgui.components.GuiPattern;
import com.mattmx.ktgui.components.button.ButtonClickedEvent;
import com.mattmx.ktgui.components.button.GuiButton;
import com.mattmx.ktgui.components.screen.GuiScreen;
import com.mattmx.ktgui.components.signal.Signal;
import com.mattmx.ktgui.event.PreGuiBuildEvent;
import com.mattmx.ktgui.scheduling.TaskTracker;
import com.mattmx.ktgui.scoreboards.ScoreboardBuilder;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

<<<<<<< Updated upstream
import static com.mattmx.ktgui.dsl.EventKt.javaEvent;
=======
import static com.mattmx.ktgui.dsl.EventKt.event;
import static com.mattmx.ktgui.dsl.EventKt.event;
import static com.mattmx.ktgui.scheduling.SchedulingKt.sync;
import static com.mattmx.ktgui.utils.ColorKt.component;
>>>>>>> Stashed changes

public class JavaUpdateExample implements Example {
    @Override
    @SuppressWarnings({"unchecked", "rawtypes"})
    public void run(@NotNull Player player) {
        GuiScreen gui = new GuiScreen(Component.text("Title"), 3)
                .addChild(
                        new GuiButton(Material.DIRT)
                                .named(Component.text("Item"))
                                .click(ClickType.LEFT, (event) -> {
                                    // todo find a way to reduce this cast, for some reason `event` is `Object`, Kotlin -> Java issue
                                    ((ButtonClickedEvent) event).getPlayer().sendMessage(Component.text("clicked"));
                                })
                                .lore(Component.text("Lore line one"))
                                .lore(Component.text("Lore line two"))
                                .slot(3)
                );

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
        gui.open((p) -> {
            p.sendMessage(Component.text("opened."));
            return null;
        }).close((e) -> {
            e.getPlayer().sendMessage(Component.text("closed."));
            return null;
        });

        gui.open(player);
    }

    public void eventsTest(JavaPlugin plugin) {
        javaEvent(plugin, PlayerJoinEvent.class, (event) -> {
            event.getPlayer().sendMessage(Component.text("welcome!"));
            return null;
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
        if (!gui.getId().equals("ktgui.example.hook")) return;

        // todo add stuff
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
}
