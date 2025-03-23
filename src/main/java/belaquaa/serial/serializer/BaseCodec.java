package belaquaa.serial.serializer;

import java.math.BigInteger;

public class BaseCodec {
    private static char digitToChar(int digit) {
        return (char) (digit + 32);
    }

    private static int charToDigit(char c) {
        return c - 32;
    }

    public static String encode(byte[] data) {
        BigInteger bi = new BigInteger(1, data);
        int expectedDigits = (int) Math.ceil(data.length * Math.log(256) / Math.log(95));
        StringBuilder sb = new StringBuilder(expectedDigits);
        BigInteger base = BigInteger.valueOf(95);
        while (bi.compareTo(BigInteger.ZERO) > 0) {
            BigInteger[] divRem = bi.divideAndRemainder(base);
            sb.append(digitToChar(divRem[1].intValue()));
            bi = divRem[0];
        }
        while (sb.length() < expectedDigits) {
            sb.append(digitToChar(0));
        }
        return sb.reverse().toString();
    }

    public static byte[] decode(String s, int numBytes) {
        BigInteger bi = BigInteger.ZERO;
        BigInteger base = BigInteger.valueOf(95);
        for (int i = 0; i < s.length(); i++) {
            bi = bi.multiply(base).add(BigInteger.valueOf(charToDigit(s.charAt(i))));
        }
        byte[] data = bi.toByteArray();
        if (data.length > numBytes && data[0] == 0 && data.length - 1 == numBytes) {
            byte[] tmp = new byte[numBytes];
            System.arraycopy(data, 1, tmp, 0, numBytes);
            data = tmp;
        }
        if (data.length < numBytes) {
            byte[] padded = new byte[numBytes];
            System.arraycopy(data, 0, padded, numBytes - data.length, data.length);
            data = padded;
        }
        return data;
    }
}
