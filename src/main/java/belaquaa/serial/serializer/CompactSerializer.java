package belaquaa.serial.serializer;

import belaquaa.serial.io.BitReader;
import belaquaa.serial.io.BitWriter;

public class CompactSerializer implements Serializer {
    private static final int COUNT_BITS = 10;
    private static final int NUMBER_BITS = 9;

    @Override
    public String serialize(int[] data) {
        byte[] bytes = toBytes(data);
        int numBytes = bytes.length;
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
        return fromBytes(bytes);
    }

    public byte[] toBytes(int[] data) {
        int n = data.length;
        int totalBits = COUNT_BITS + n * NUMBER_BITS;
        int numBytes = (totalBits + 7) / 8;
        BitWriter writer = new BitWriter(numBytes);
        writer.writeBits(n, COUNT_BITS);
        for (int num : data) {
            writer.writeBits(num - 1, NUMBER_BITS);
        }
        return writer.getData();
    }

    public int[] fromBytes(byte[] bytes) {
        BitReader reader = new BitReader(bytes);
        int n = reader.readBits(COUNT_BITS);
        int[] result = new int[n];
        for (int i = 0; i < n; i++) {
            result[i] = reader.readBits(NUMBER_BITS) + 1;
        }
        return result;
    }
}
