package commands.dependencies.games;

import reactor.function.Function3;

public class IntegerArray2D {

    private final int[][] nestedArray;
    
    public IntegerArray2D(int width, int height) {
        this.nestedArray = new int[width][height];
    }

    public int getWidth() { return nestedArray.length; }
    public int getHeight() {
        return nestedArray[0].length;
    }
    
    public int[] getColumn(int i) {
        return nestedArray[i];
    }
    public int[] getRow(int i) {
    
        int width = getWidth();
        int[] row = new int[width];
        
        for (int j = 0; j < width; j++) {
            row[j] = getColumn(j)[i];
        }
    
        return row;
    }
    
    public void apply(Function3<Integer, Integer, Integer, Integer> f) {
        for (int x = 0; x < getWidth(); x++) {
            for (int y = 0; y < getHeight(); y++) {
                nestedArray[x][y] = f.apply(x, y, nestedArray[x][y]);
            }
        }
    }
    
}