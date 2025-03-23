package belaquaa.serial.io;

public class BitWriter {
    private static final int[] MASKS = new int[33];

    static {
        for (int i = 0; i <= 32; i++) {
            MASKS[i] = (i == 0) ? 0 : (1 << i) - 1;
        }
    }

    private final byte[] data;
    private int pos;
    private int bitBuffer;
    private int bitCount;

    public BitWriter(int capacity) {
        data = new byte[capacity];
        pos = 0;
        bitBuffer = 0;
        bitCount = 0;
    }

    public void writeBits(int value, int numBits) {
        bitBuffer = (bitBuffer << numBits) | (value & MASKS[numBits]);
        bitCount += numBits;
        while (bitCount >= 8) {
            bitCount -= 8;
            data[pos++] = (byte) (bitBuffer >> bitCount);
            bitBuffer &= MASKS[bitCount];
        }
    }

    public byte[] getData() {
        if (bitCount > 0) {
            data[pos++] = (byte) (bitBuffer << (8 - bitCount));
            bitCount = 0;
        }
        return data;
    }
}
