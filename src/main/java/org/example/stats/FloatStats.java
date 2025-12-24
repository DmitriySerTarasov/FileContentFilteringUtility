package org.example.stats;


public class FloatStats {
    private long count = 0;
    private Double min = null;
    private Double max = null;
    private double sum = 0.0;

    public void add(String line) {
        try {
            Double value = Double.parseDouble(line.trim());
            count++;
            sum += value;

            if (min == null || value < min) {
                min = value;
            }
            if (max == null || value > max) {
                max = value;
            }
        } catch (NumberFormatException ignored) {}
    }

    public long getCount() {
        return count;
    }

    public String generateReport(boolean fullStats) {
        if (count == 0) return "";

        StringBuilder sb = new StringBuilder();
        sb.append("Количество: ").append(count).append("\n");

        if (fullStats) {
            sb.append("  Минимум: ").append(min).append("\n");
            sb.append("  Максимум: ").append(max).append("\n");
            sb.append("  Сумма: ").append(sum).append("\n");

            double average = sum / count;
            sb.append("  Среднее: ").append(String.format("%.5f", average)).append("\n");
        }
        return sb.toString();
    }
}