package belaquaa.serial.io;

public class BitReader {
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

    public BitReader(byte[] data) {
        this.data = data;
        pos = 0;
        bitBuffer = 0;
        bitCount = 0;
    }

    public int readBits(int numBits) {
        while (bitCount < numBits) {
            bitBuffer = (bitBuffer << 8) | (data[pos++] & 0xFF);
            bitCount += 8;
        }
        int shift = bitCount - numBits;
        int value = (bitBuffer >> shift) & MASKS[numBits];
        bitCount -= numBits;
        bitBuffer &= MASKS[bitCount];
        return value;
    }
}
