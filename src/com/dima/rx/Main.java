package com.dima.rx;

public class Main {

    public interface Snippet {
        void run();
    }

    public static void main(String[] args) {
        // new KeyTypes().run();
        // new LifetimeManagement().run();
        new CreatingSequence().run();
    }
}
