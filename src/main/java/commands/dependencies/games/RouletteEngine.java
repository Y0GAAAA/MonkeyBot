package commands.dependencies.games;

import java.util.Random;
import java.util.function.Supplier;

public class RouletteEngine {
    
    public enum Tile {
        
        BLACK("⬛", 2),
        RED("\uD83D\uDFE5", 2),
        GREEN("\uD83D\uDFE9", 7),
        ;
        
        private final String display;
        private final int multiplier;
        
        Tile(String display, int multiplier) {
            this.display = display;
            this.multiplier = multiplier;
        }
        
        public String getDisplay() {
            return display;
        }
        public int getMultiplier() {
            return multiplier;
        }
    
        public static Tile fromColorString(String s) {
            switch (s.toLowerCase()) {
                case "black":
                    return Tile.BLACK;
                    
                case "red":
                    return Tile.RED;
                    
                case "green":
                    return Tile.GREEN;
                    
                default:
                    return null;
            }
        }
        
    }
    
    public static class TileGenerator implements Supplier<Tile> {
    
        private final Random randomizer = new Random();
        private Tile lastTile = Tile.GREEN;
        
        private static final int GREEN_TILE_FRACTION = 10;
        
        @Override
        public synchronized Tile get() {

            if (randomizer.nextInt(GREEN_TILE_FRACTION) == 0) {
                return Tile.GREEN;
            }
            
            switch (lastTile) {
                
                case RED:
                    lastTile = Tile.BLACK;
                    return Tile.BLACK;
                    
                case BLACK:
                    lastTile = Tile.RED;
                    return Tile.RED;
             
                default:
                    normalizeLastTile();
                    return get();
                    
            }
            
        }
    
        private void normalizeLastTile() {
            lastTile = randomizer.nextBoolean() ? Tile.RED : Tile.BLACK;
        }
        
    }
    
}