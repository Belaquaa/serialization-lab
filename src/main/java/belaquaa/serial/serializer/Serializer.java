package belaquaa.serial.serializer;

public interface Serializer {
    String serialize(int[] data);

    int[] deserialize(String s);
}
