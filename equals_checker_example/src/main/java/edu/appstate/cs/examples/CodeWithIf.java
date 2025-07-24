package edu.appstate.cs.examples;

public class CodeWithIf {
    public static void someMethod() {
        if (true) {
            System.out.println("I'm here");
        }

        if (Math.random() < 0.5) {
            System.out.println("I'm here 2");
        } else {
            System.out.println("I'm here 3");
        }
    }
}
