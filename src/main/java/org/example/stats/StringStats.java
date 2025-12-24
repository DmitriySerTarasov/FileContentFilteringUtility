package org.example.stats;


public class StringStats {
    private long count = 0;
    private int minLength = Integer.MAX_VALUE;
    private int maxLength = 0;

    public void add(String line) {
        count++;
        int length = line.length();

        if (length < minLength) {
            minLength = length;
        }
        if (length > maxLength) {
            maxLength = length;
        }
    }

    public long getCount() {
        return count;
    }

    public String generateReport(boolean fullStats) {
        if (count == 0) return "";

        StringBuilder sb = new StringBuilder();
        sb.append("Количество: ").append(count).append("\n");

        if (fullStats) {

            sb.append("  Длина самой короткой строки: ").append(minLength).append("\n");
            sb.append("  Длина самой длинной строки: ").append(maxLength).append("\n");
        }
        return sb.toString();
    }
}