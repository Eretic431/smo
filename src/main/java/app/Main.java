package app;

import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        new Controller(Main::print).start();
    }

    public static void print(Object object) {
        System.out.println(object);
        if (Config.STEPMODE) {
            try {
                System.in.read();
            } catch (IOException ignored) {
            }
        }
    }
}