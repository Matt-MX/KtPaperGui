package com.mattmx.ktgui.examples;

import com.mattmx.ktgui.commands.declarative.arg.impl.DoubleArgument;
import com.mattmx.ktgui.papi.Placeholder;
import com.mattmx.ktgui.papi.PlaceholderExpansionWrapper;
import org.bukkit.plugin.java.JavaPlugin;

public class JavaPlaceholderExpansionExample {
    public void test(JavaPlugin plugin) {
        DoubleArgument a = new DoubleArgument("a", "double");
        DoubleArgument b = new DoubleArgument("b", "double");

        new PlaceholderExpansionWrapper(plugin)
            .author("MattMX")
            .id("+")
            .withPlaceholder(
                Placeholder.builder()
                    .matches(Placeholder.emptyCommandBuilder().argument(a).argument(b))
                    .supplier((context) ->
                        context.getContext(a).orElse(0.0) + context.getContext(b).orElse(0.0)
                    )
            );

        // Usage: %+_1.0_2.0% -> 3.0
        //        %+_1.4_-1.0% -> 0.4
    }
}
