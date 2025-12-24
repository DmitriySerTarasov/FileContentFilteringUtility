package org.example;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;


public class Config {

    public static final String DEFAULT_INTEGER_FILE = "integers.txt";
    public static final String DEFAULT_FLOAT_FILE = "floats.txt";
    public static final String DEFAULT_STRING_FILE = "strings.txt";

    private Path outputPath = Paths.get(".");
    private String prefix = "";
    private boolean appendMode = false;
    private boolean fullStats = true;

    private final List<String> inputFiles = new ArrayList<>();


    public void parseArgs(String[] args) throws IllegalArgumentException {

        for (int i = 0; i < args.length; i++) {
            String arg = args[i];

            switch (arg) {
                case "-o":

                    if (i + 1 < args.length) {
                        outputPath = Paths.get(args[++i]);
                    } else {
                        throw new IllegalArgumentException("Опция -o требует указания пути.");
                    }
                    break;
                case "-p":

                    if (i + 1 < args.length) {
                        prefix = args[++i];
                    } else {
                        throw new IllegalArgumentException("Опция -p требует указания префикса.");
                    }
                    break;
                case "-a":
                    appendMode = true;
                    break;
                case "-s":
                    fullStats = false;
                    break;
                case "-f":
                    fullStats = true;
                    break;
                default:

                    inputFiles.add(arg);
                    break;
            }
        }

        if (inputFiles.isEmpty()) {
            throw new IllegalArgumentException("Необходимо указать как минимум один входной файл.");
        }
    }


    public Path getOutputPath() {
        return outputPath;
    }

    public boolean isAppendMode() {
        return appendMode;
    }

    public boolean isFullStats() {
        return fullStats;
    }

    public List<String> getInputFiles() {
        return inputFiles;
    }


    public Path getOutputFilePath(String baseName) {

        return outputPath.resolve(prefix + baseName);
    }
}