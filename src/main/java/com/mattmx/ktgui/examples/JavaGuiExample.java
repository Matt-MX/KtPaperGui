package com.mattmx.ktgui.examples;

import com.mattmx.ktgui.components.button.GuiButton;
import com.mattmx.ktgui.components.button.GuiToggleButton;
import com.mattmx.ktgui.components.screen.GuiScreen;
import com.mattmx.ktgui.item.ItemBuilder;
import com.mattmx.ktgui.utils.Chat;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.w3c.dom.ranges.Range;

import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class JavaGuiExample extends GuiScreen {
    public JavaGuiExample() {
        super("&8&l⤷ &#7f52ffK&#984fd8t&#b14bb1G&#c94889u&#e24462i &8» &#7f52ffJava Example GUI", 3, null);
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
                }).slots(IntStream.rangeClosed(10, 16).boxed().collect(Collectors.toList()))
                .childOf(this);
        new GuiToggleButton(
                new ItemBuilder(Material.LIME_STAINED_GLASS_PANE).name("&aEnabled").make(),
                new ItemBuilder(Material.RED_STAINED_GLASS_PANE).name("&cDisabled").make(),
                false,
                (button, event, value) -> {
                    event.getWhoClicked().sendMessage(Chat.INSTANCE.format(
                            "&8&l⤷ &#7f52ffJust a quick example to show that all functionality is 1:1 from KtGui -> Java. (" + value + ")",
                            (Player) event.getWhoClicked()));
                    return null;
                }).slot(22).childOf(this);
    }
}
