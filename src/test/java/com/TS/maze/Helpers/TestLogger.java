package com.TS.maze.Helpers;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class TestLogger {

    private static final String LOG_FILE = "src/test/java/com/TS/maze/Logs/failed_seeds.txt";
    private static boolean cleared = false;

    static {
        try (PrintWriter out = new PrintWriter(new FileWriter(LOG_FILE))){
            out.println();
            cleared = true;
        } catch (IOException e){
            System.err.println("IOException: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void logFailure(String message) {
        try (PrintWriter out = new PrintWriter(new FileWriter(LOG_FILE, true))) {
            out.println(message);
        } catch (IOException e){
            System.err.println("IOException: " + e.getMessage());
            e.printStackTrace();
        }
    }

}
