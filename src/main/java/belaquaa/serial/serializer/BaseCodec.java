package belaquaa.serial.serializer;

import java.math.BigInteger;

// Используем все 128 символов аськи, здесь внимательно, т.к. используются и управляющие символы
// Если хотим на 100% избежать проблем с отображением, можно перейти на Base95, так будет хуже на 6-7%
public class BaseCodec {
    private static char digitToChar(int digit) {
        return (char) digit;
    }

    private static int charToDigit(char c) {
        return (int) c;
    }

    public static String encode(byte[] data) {
        BigInteger bi = new BigInteger(1, data);
        int expectedDigits = (int) Math.ceil(data.length * Math.log(256) / Math.log(128));
        StringBuilder sb = new StringBuilder(expectedDigits);
        BigInteger base = BigInteger.valueOf(128);
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
        BigInteger base = BigInteger.valueOf(128);
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
