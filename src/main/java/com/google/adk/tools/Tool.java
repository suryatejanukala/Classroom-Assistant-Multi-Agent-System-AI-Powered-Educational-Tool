package com.google.adk.tools;

public @interface Tool {
    String name() default "";
    String description() default "";
}