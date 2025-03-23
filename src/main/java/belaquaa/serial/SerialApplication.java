package belaquaa.serial;

import belaquaa.serial.test.TestRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SerialApplication {

    public static void main(String[] args) {
        SpringApplication.run(SerialApplication.class, args);
        TestRunner.runTests();
    }
}