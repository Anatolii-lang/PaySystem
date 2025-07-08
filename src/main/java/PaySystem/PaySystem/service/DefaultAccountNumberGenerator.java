package PaySystem.PaySystem.service;

import java.util.Random;

public class DefaultAccountNumberGenerator implements AccountNumberGenerator {

    @Override
    public Integer generate() {
        Random random = new Random();
        return 1000 + random.nextInt(9000); // от 1000 до 9999
    }
}
