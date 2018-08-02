package com.vishnu.utils;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Objects;

/**
 * @author vishnu
 */

public class FileUtils {

    // creating the current working directory (where the jar is asked to run)
    private static final String RESULTS_FILE = "results.txt";

    // could have used apache commons, but not sure if I am allowed to use.
    public static void createFile() {
        try (Writer fileWriter = new FileWriter(RESULTS_FILE)) {
        } catch (Exception e) {
            System.out.println("Cannot create file");
        }
    }

    public static void appendTextToFile(String data) {
        Objects.requireNonNull(data);
        try(Writer fileWriter = new FileWriter(RESULTS_FILE, true)) {
            fileWriter.append(data);
        }catch (IOException e) {
            System.out.println("Cannot append data to results file");
        }
    }
}
