package belaquaa.serial.serializer;

import belaquaa.serial.io.BitReader;
import belaquaa.serial.io.BitWriter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CombinedSerializer implements Serializer {
    private static int bitsNeeded(int x) {
        return x <= 1 ? 1 : (int) Math.ceil(Math.log(x) / Math.log(2));
    }

    @Override
    public String serialize(int[] data) {
        int n = data.length;
        int[] sorted = data.clone();
        Arrays.sort(sorted);

        List<int[]> runs = new ArrayList<>();
        int curDiff = sorted[0];
        int curCount = 1;
        int maxDiff = curDiff;
        int maxRun = 1;
        for (int i = 1; i < n; i++) {
            int diff = sorted[i] - sorted[i - 1];
            if (diff == curDiff) {
                curCount++;
            } else {
                runs.add(new int[]{curDiff, curCount});
                if (curDiff > maxDiff) {
                    maxDiff = curDiff;
                }
                if (curCount > maxRun) {
                    maxRun = curCount;
                }
                curDiff = diff;
                curCount = 1;
            }
        }
        runs.add(new int[]{curDiff, curCount});
        if (curDiff > maxDiff) {
            maxDiff = curDiff;
        }
        if (curCount > maxRun) {
            maxRun = curCount;
        }

        int totalRuns = runs.size();
        int diffBits = bitsNeeded(maxDiff + 1);
        int runBits = bitsNeeded(maxRun + 1);
        int headerBits = 10 + 10 + 4 + 4;
        int totalRunBits = totalRuns * (diffBits + runBits);
        int totalBits = headerBits + totalRunBits;
        int numBytes = (totalBits + 7) / 8;

        BitWriter writer = new BitWriter(numBytes);
        writer.writeBits(n, 10);
        writer.writeBits(totalRuns, 10);
        writer.writeBits(diffBits, 4);
        writer.writeBits(runBits, 4);
        for (int[] run : runs) {
            writer.writeBits(run[0], diffBits);
            writer.writeBits(run[1], runBits);
        }
        byte[] bytes = writer.getData();
        String encodedData = BaseCodec.encode(bytes);
        int firstDigit = numBytes / 95;
        int secondDigit = numBytes % 95;
        return "" + (char) (firstDigit + 32) + (char) (secondDigit + 32) + encodedData;
    }

    @Override
    public int[] deserialize(String s) {
        int numBytes = (s.charAt(0) - 32) * 95 + (s.charAt(1) - 32);
        String encodedData = s.substring(2);
        byte[] bytes = BaseCodec.decode(encodedData, numBytes);
        BitReader reader = new BitReader(bytes);
        int totalNums = reader.readBits(10);
        int totalRuns = reader.readBits(10);
        int diffBits = reader.readBits(4);
        int runBits = reader.readBits(4);
        int totalDiffs = 0;
        int[] runsDiff = new int[totalRuns];
        int[] runsCount = new int[totalRuns];
        for (int i = 0; i < totalRuns; i++) {
            runsDiff[i] = reader.readBits(diffBits);
            runsCount[i] = reader.readBits(runBits);
            totalDiffs += runsCount[i];
        }
        if (totalDiffs != totalNums) {
            throw new IllegalArgumentException("Corrupted data");
        }
        int[] diffs = new int[totalNums];
        int idx = 0;
        for (int i = 0; i < totalRuns; i++) {
            for (int j = 0; j < runsCount[i]; j++) {
                diffs[idx++] = runsDiff[i];
            }
        }
        int[] sorted = new int[totalNums];
        sorted[0] = diffs[0];
        for (int i = 1; i < totalNums; i++) {
            sorted[i] = sorted[i - 1] + diffs[i];
        }
        return sorted;
    }
}
