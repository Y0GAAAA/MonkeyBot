package cosmetics;

import discord4j.rest.util.Color;

import java.util.Random;

public class Static {

    private static final Random randomizer = new Random();

    public static Color getRandomColor() {
        return Color.of(
                randomizer.nextInt(256),
                randomizer.nextInt(256),
                randomizer.nextInt(256)
        );
    }

    public static String encapsulate(String enc, String barrier) {
        return barrier + enc + barrier;
    }

}
