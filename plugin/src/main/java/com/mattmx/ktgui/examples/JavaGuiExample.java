package com.mattmx.ktgui.examples;

import com.mattmx.ktgui.components.button.GuiButton;
import com.mattmx.ktgui.components.button.GuiToggleButton;
import com.mattmx.ktgui.components.screen.GuiScreen;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.jetbrains.annotations.NotNull;

import static com.mattmx.ktgui.item.ItemBuilderDslKt.itemBuilder;
import static com.mattmx.ktgui.utils.ColorKt.component;

public class JavaGuiExample extends GuiScreen implements Example {

    @SuppressWarnings({"rawtypes"})
    public JavaGuiExample() {
        super(component("&#7f52ffJava Example GUI"), 3, null);
        new GuiButton()
                .material(Material.DIRT)
                .named(component("&#7F52FF&lJava"))
                .slots(10, 11, 12, 13, 14, 15, 16)
                .childOf(this);
        new GuiToggleButton(
                itemBuilder(Material.LIME_STAINED_GLASS_PANE).name(component("&aEnabled")).build(),
                itemBuilder(Material.RED_STAINED_GLASS_PANE).name(component("&cDisabled")).build()
        ).changeWithClickType(ClickType.LEFT)
                .changed(e -> {
                    e.getPlayer().sendMessage(
                            component("&#7f52ffJust a quick example to show that all functionality is 1:1 from KtGui -> Java. ("
                                    + e.getButton().enabled()
                                    + ")"
                            )
                    );
                    return null;
                }).slot(22).childOf(this);
    }


    @Override
    public void run(@NotNull Player player) {
        open(player);
    }
}
