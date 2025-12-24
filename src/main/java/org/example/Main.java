package org.example;



import org.example.stats.FloatStats;
import org.example.stats.IntegerStats;
import org.example.stats.StringStats;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import static org.example.DataClassifier.DataType;



public class Main {

    // Хранилище для открытых потоков записи.
    // Map для открытия/закрытия файла на каждой строке.
    private static final Map<DataType, BufferedWriter> writers = new HashMap<>();

    public static void main(String[] args) {

        // Обработка аргументов и исключений - "Эффективное программирование на Java", Глава 10.
        Config config = new Config();
        try {
            config.parseArgs(args);
        } catch (IllegalArgumentException e) {
            System.err.println("Ошибка аргументов: " + e.getMessage());
            printUsage();
            return;
        }

        IntegerStats intStats = new IntegerStats();
        FloatStats floatStats = new FloatStats();
        StringStats stringStats = new StringStats();

        try {
            Files.createDirectories(config.getOutputPath());
        } catch (IOException e) {
            System.err.println("Критическая ошибка: Не удалось создать выходную директорию " + config.getOutputPath());
            System.err.println("Причина: " + e.getMessage());
            return;
        }


        processFiles(config, intStats, floatStats, stringStats);

// Обязательное закрытие ресурсов - "Effective Java" (Джошуа Блох), пункт 9.
        closeWriters();


        printStatistics(config.isFullStats(), intStats, floatStats, stringStats);
    }

    // Итерация по коллекции - "Основы Java", Глава 5.
    private static void processFiles(Config config, IntegerStats intStats, FloatStats floatStats, StringStats stringStats) {
        for (String fileName : config.getInputFiles()) {
            Path filePath = Paths.get(fileName);

            if (!Files.exists(filePath)) {
                System.err.printf("Предупреждение: Входной файл не найден: %s. Пропускаем.\n", fileName);
                continue;
            }


            try (BufferedReader reader = Files.newBufferedReader(filePath)) {
                String line;
                while ((line = reader.readLine()) != null) {
                    processLine(line, config, intStats, floatStats, stringStats);
                }
            } catch (IOException e) {
                System.err.printf("Ошибка чтения файла %s: %s. Продолжаем обработку.\n", fileName, e.getMessage());
            }
        }
    }


    private static void processLine(String line, Config config,
                                    IntegerStats intStats, FloatStats floatStats, StringStats stringStats) {
        DataType type = DataClassifier.classify(line);

        try {

            writeData(type, line, config);


            switch (type) {
                case INTEGER:
                    intStats.add(line);
                    break;
                case FLOAT:
                    floatStats.add(line);
                    break;
                case STRING:
                    stringStats.add(line);
                    break;
            }
        } catch (IOException e) {

            System.err.printf("Ошибка записи данных типа %s: %s\n", type, e.getMessage());
        }
    }


    private static void writeData(DataType type, String data, Config config) throws IOException {
        BufferedWriter writer = writers.get(type);


        if (writer == null) {
            String baseName;
            switch (type) {
                case INTEGER: baseName = Config.DEFAULT_INTEGER_FILE; break;
                case FLOAT: baseName = Config.DEFAULT_FLOAT_FILE; break;
                case STRING: baseName = Config.DEFAULT_STRING_FILE; break;
                default: throw new IllegalStateException("Неизвестный тип данных.");
            }

            Path filePath = config.getOutputFilePath(baseName);


            FileWriter fw = new FileWriter(filePath.toFile(), config.isAppendMode());
            writer = new BufferedWriter(fw);
            writers.put(type, writer);
        }

        writer.write(data);
        writer.newLine();
    }


    private static void closeWriters() {
        for (BufferedWriter writer : writers.values()) {
            try {
                writer.close();
            } catch (IOException e) {
                System.err.println("Ошибка при закрытии выходного файла: " + e.getMessage());
            }
        }
    }


    private static void printStatistics(boolean fullStats, IntegerStats intStats, FloatStats floatStats, StringStats stringStats) {
        System.out.println("\n=======================================");
        System.out.println("СТАТИСТИКА ФИЛЬТРАЦИИ");
        System.out.println("Режим: " + (fullStats ? "Полный" : "Краткий"));
        System.out.println("=======================================");

        if (intStats.getCount() > 0) {
            System.out.println("\n--- Целые числа ---");
            System.out.print(intStats.generateReport(fullStats));
        }

        if (floatStats.getCount() > 0) {
            System.out.println("\n--- Вещественные числа ---");
            System.out.print(floatStats.generateReport(fullStats));
        }

        if (stringStats.getCount() > 0) {
            System.out.println("\n--- Строки ---");
            System.out.print(stringStats.generateReport(fullStats));
        }

        System.out.println("=======================================");
    }

    private static void printUsage() {
        System.out.println("\nИспользование: java Main [Опции] <Входные файлы...>");
        System.out.println("Опции:");
        System.out.println("  -o <путь>   Указать директорию для выходных файлов.");
        System.out.println("  -p <префикс> Указать префикс для имен выходных файлов.");
        System.out.println("  -a          Режим добавления (Append). По умолчанию - перезапись.");
        System.out.println("  -s          Краткая статистика (только количество).");
        System.out.println("  -f          Полная статистика (по умолчанию).");
        System.out.println("\nПример: java Main -s -a -p sample- in1.txt in2.txt");
    }
}