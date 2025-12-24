package org.example;


public class DataClassifier {

    public enum DataType {
        INTEGER, FLOAT, STRING
    }

    public static DataType classify(String line) {

        String trimmedLine = line.trim();
        if (trimmedLine.isEmpty()) {
            return DataType.STRING;
        }


        try {

            Long.parseLong(trimmedLine);
            return DataType.INTEGER;
        } catch (NumberFormatException e) {

        }


        try {

            Double.parseDouble(trimmedLine);
            return DataType.FLOAT;
        } catch (NumberFormatException e) {

        }


        return DataType.STRING;
    }
}