package net.alexhyisen.pathfinder.utility;

import java.util.Random;
import java.util.concurrent.Callable;

//A dummy task sleep 4s and then return a random boolean.
public class DummyTask implements Callable<Boolean> {
    private static Random rand = new Random(17);

    @Override
    public Boolean call() {
        try {
            Thread.sleep(4000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return rand.nextBoolean();
    }
}
