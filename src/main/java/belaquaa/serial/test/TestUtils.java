package belaquaa.serial.test;

import belaquaa.serial.serializer.Serializer;

import java.util.Arrays;

public class TestUtils {

    public static class TestResult {
        public double avgRatioCompact;
        public double avgRatioCombined;
        public int totalErrors;

        public TestResult(double avgRatioCompact, double avgRatioCombined, int totalErrors) {
            this.avgRatioCompact = avgRatioCompact;
            this.avgRatioCombined = avgRatioCombined;
            this.totalErrors = totalErrors;
        }
    }

    public static TestResult runCombinedTest(String testName, int[] data, Serializer compact, Serializer combined) {
        String original = arrayToString(data);
        double totalRatioCompact = 0;
        double totalRatioCombined = 0;
        String sampleSerializedCompact = "";
        String sampleSerializedCombined = "";
        String deserializedCompactStr = "";
        String deserializedCombinedStr = "";
        int errorCountCompact = 0;
        int errorCountCombined = 0;

        for (int i = 0; i < 10; i++) {
            String serialized = compact.serialize(data);
            double ratio = (double) serialized.length() / original.length();
            totalRatioCompact += ratio;
            int[] deserialized = compact.deserialize(serialized);
            if (!Arrays.equals(data, deserialized)) {
                errorCountCompact++;
            }
            if (i == 0) {
                sampleSerializedCompact = serialized;
                deserializedCompactStr = arrayToString(deserialized);
            }
        }

        for (int i = 0; i < 10; i++) {
            String serialized = combined.serialize(data);
            double ratio = (double) serialized.length() / original.length();
            totalRatioCombined += ratio;
            int[] deserialized = combined.deserialize(serialized);
            int[] sortedOriginal = data.clone();
            Arrays.sort(sortedOriginal);
            if (!Arrays.equals(sortedOriginal, deserialized)) {
                errorCountCombined++;
            }
            if (i == 0) {
                sampleSerializedCombined = serialized;
                deserializedCombinedStr = arrayToString(deserialized);
            }
        }

        double avgRatioCompact = totalRatioCompact / 10;
        double avgRatioCombined = totalRatioCombined / 10;
        int totalErrors = errorCountCompact + errorCountCombined;

        System.out.println("Тест: " + testName);
        System.out.println("Исходная строка (1 запуск): " + original);
        System.out.println("Сжатая строка (Compact):  " + sampleSerializedCompact);
        System.out.println("Сжатая строка (Combined): " + sampleSerializedCombined);
        System.out.printf("Средний коэффициент сжатия за 10 запусков (Compact):  %.2f\n", avgRatioCompact);
        System.out.printf("Средний коэффициент сжатия за 10 запусков (Combined): %.2f\n", avgRatioCombined);
        System.out.println("Десериализованная строка (Compact):  " + deserializedCompactStr);
        System.out.println("Десериализованная строка (Combined): " + deserializedCombinedStr);
        System.out.println("Ошибок десериализации для методов: " + totalErrors);
        System.out.println("---------------------------------------------------");

        return new TestResult(avgRatioCompact, avgRatioCombined, totalErrors);
    }

    public static String arrayToString(int[] arr) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < arr.length; i++) {
            if (i > 0) {
                sb.append(",");
            }
            sb.append(arr[i]);
        }
        return sb.toString();
    }
}
