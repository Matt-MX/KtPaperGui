package com.mattmx.ktgui.examples;

import com.mattmx.ktgui.components.button.GuiButton;
import com.mattmx.ktgui.components.button.LegacyGuiToggleButton;
import com.mattmx.ktgui.components.screen.GuiScreen;
import com.mattmx.ktgui.item.ItemBuilder;
import org.bukkit.Material;

import static com.mattmx.ktgui.utils.ColorKt.color;

public class JavaGuiExample extends GuiScreen {
    public JavaGuiExample() {
        super("&#7f52ffJava Example GUI", 3, null);
        new GuiButton()
                .material(Material.DIRT)
                .named("&#7F52FF&lJava")
                .lore(lore -> {
                    lore.add("&8Using this library in Java is nowhere");
                    lore.add("&8as neat as in Kotlin, but it's still fairly");
                    lore.add("&8nice to use! The code amount is fairly similar.");
                    lore.add(" ");
                    lore.add("&8&o(Java still doodoo)");
                    return null; // Idk why java wants a return value for a callback but ite cool
                })
                .slots(10, 11, 12, 13, 14, 15, 16)
                .childOf(this);
        new LegacyGuiToggleButton(
                new ItemBuilder(Material.LIME_STAINED_GLASS_PANE).name("&aEnabled").make(),
                new ItemBuilder(Material.RED_STAINED_GLASS_PANE).name("&cDisabled").make()
        ).onChange(e -> {
            e.getPlayer().sendMessage(color(
                    "&#7f52ffJust a quick example to show that all functionality is 1:1 from KtGui -> Java. ("
                            + ((LegacyGuiToggleButton) e.getButton()).enabled() + ")",
                    e.getPlayer()));
            return null;
        }).slot(22).childOf(this);
    }
}
