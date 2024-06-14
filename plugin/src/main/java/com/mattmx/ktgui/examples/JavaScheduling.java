package com.mattmx.ktgui.examples;

import com.mattmx.ktgui.scheduling.builder.Task;

public class JavaScheduling {
    public void test() {
        var repeating = Task.async()
                .repeating((task) -> {
                    System.out.println("Ran this task " + task.getIterations() + " times.");
                }).delay(2)
                .period(2)
                .repeat(20)
                .run();

        var later = Task.sync()
                .later((task) -> {
                    System.out.println("1s later");
                })
                .delay(20)
                .run();
    }
}
