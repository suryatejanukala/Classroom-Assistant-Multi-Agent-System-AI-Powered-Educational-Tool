package com.google.adk.agents;

public @interface Agent {
    String name() default "";
    String description() default "";
}