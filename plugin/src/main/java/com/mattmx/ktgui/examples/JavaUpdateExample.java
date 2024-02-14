package com.mattmx.ktgui.examples;

import com.mattmx.ktgui.components.GuiPattern;
import com.mattmx.ktgui.components.button.ButtonClickedEvent;
import com.mattmx.ktgui.components.button.GuiButton;
import com.mattmx.ktgui.components.screen.GuiScreen;
import com.mattmx.ktgui.components.signal.Signal;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.jetbrains.annotations.NotNull;

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
        Signal<String> signalExample = gui.createSignal("mattmx");

        gui.effectBlock(() -> new GuiButton(Material.DIAMOND)
                .named(Component.text(signalExample.get()))
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
}
