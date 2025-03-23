package belaquaa.serial.test;

import belaquaa.serial.serializer.CombinedSerializer;
import belaquaa.serial.serializer.CompactSerializer;
import belaquaa.serial.serializer.Serializer;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TestRunner {
    public static void runTests() {
        Serializer cs = new CompactSerializer();
        Serializer comb = new CombinedSerializer();
        Random rand = new Random(42);

        List<TestUtils.TestResult> results = new ArrayList<>();

        int[] test1 = {1, 300, 150, 100, 10};
        results.add(TestUtils.runCombinedTest("Простейший короткий тест", test1, cs, comb));

        int[] test2 = new int[50];
        for (int i = 0; i < test2.length; i++) {
            test2[i] = rand.nextInt(300) + 1;
        }
        results.add(TestUtils.runCombinedTest("Случайные 50 чисел", test2, cs, comb));

        int[] test3 = new int[100];
        for (int i = 0; i < test3.length; i++) {
            test3[i] = rand.nextInt(300) + 1;
        }
        results.add(TestUtils.runCombinedTest("Случайные 100 чисел", test3, cs, comb));

        int[] test4 = new int[500];
        for (int i = 0; i < test4.length; i++) {
            test4[i] = rand.nextInt(300) + 1;
        }
        results.add(TestUtils.runCombinedTest("Случайные 500 чисел", test4, cs, comb));

        int[] test5 = new int[1000];
        for (int i = 0; i < test5.length; i++) {
            test5[i] = rand.nextInt(300) + 1;
        }
        results.add(TestUtils.runCombinedTest("Случайные 1000 чисел", test5, cs, comb));

        int[] test6 = new int[20];
        for (int i = 0; i < test6.length; i++) {
            test6[i] = rand.nextInt(9) + 1;
        }
        results.add(TestUtils.runCombinedTest("Граничный тест - все числа с 1 знаком", test6, cs, comb));

        int[] test7 = new int[90];
        for (int i = 0; i < test7.length; i++) {
            test7[i] = rand.nextInt(90) + 10;
        }
        results.add(TestUtils.runCombinedTest("Граничный тест - все числа из 2 знаков", test7, cs, comb));

        int[] test8 = new int[201];
        for (int i = 0; i < test8.length; i++) {
            test8[i] = rand.nextInt(201) + 100;
        }
        results.add(TestUtils.runCombinedTest("Граничный тест - все числа из 3 знаков", test8, cs, comb));

        int[] test9 = new int[900];
        int index = 0;
        for (int num = 1; num <= 300; num++) {
            for (int j = 0; j < 3; j++) {
                test9[index++] = num;
            }
        }
        results.add(TestUtils.runCombinedTest("Граничный тест - каждого числа по 3 раза", test9, cs, comb));

        double sumRatioCompact = 0;
        double sumRatioCombined = 0;
        int sumErrors = 0;
        double maxCompact = Double.NEGATIVE_INFINITY;
        double minCompact = Double.POSITIVE_INFINITY;
        double maxCombined = Double.NEGATIVE_INFINITY;
        double minCombined = Double.POSITIVE_INFINITY;

        for (TestUtils.TestResult res : results) {
            sumRatioCompact += res.avgRatioCompact;
            sumRatioCombined += res.avgRatioCombined;
            sumErrors += res.totalErrors;

            if (res.avgRatioCompact > maxCompact) {
                maxCompact = res.avgRatioCompact;
            }
            if (res.avgRatioCompact < minCompact) {
                minCompact = res.avgRatioCompact;
            }
            if (res.avgRatioCombined > maxCombined) {
                maxCombined = res.avgRatioCombined;
            }
            if (res.avgRatioCombined < minCombined) {
                minCombined = res.avgRatioCombined;
            }
        }
        int totalTests = results.size();
        double overallAvgCompact = sumRatioCompact / totalTests;
        double overallAvgCombined = sumRatioCombined / totalTests;

        System.out.println("Общая итоговая статистика по всем тестам:");
        System.out.printf("Общий средний коэффициент сжатия (Compact):  %.2f\n", overallAvgCompact);
        System.out.printf("Общий средний коэффициент сжатия (Combined): %.2f\n", overallAvgCombined);
        System.out.println("Максимальный коэффициент сжатия (Compact): " + String.format("%.2f", maxCompact));
        System.out.println("Максимальный коэффициент сжатия (Combined): " + String.format("%.2f", maxCombined));
        System.out.println("Минимальный коэффициент сжатия (Compact): " + String.format("%.2f", minCompact));
        System.out.println("Минимальный коэффициент сжатия (Combined): " + String.format("%.2f", minCombined));
        System.out.println("Общее количество ошибок десериализации: " + sumErrors);
    }
}
