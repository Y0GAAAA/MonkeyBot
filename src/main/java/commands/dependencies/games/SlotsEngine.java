package commands.dependencies.games;

import java.util.*;

import io.github.pr0methean.betterrandom.prng.adapter.ThreadLocalRandomWrapper;

public class SlotsEngine {

    private static final char HORIZONTAL_SEPARATOR = '─';
    private static final String ROW_WIN_INDICATOR = " `<`";
    
    private final String[] tokens;
    private final IntegerArray2D slotsGrid;
    private final Boolean[] matches;
    
    private final Random randomizer = ThreadLocalRandomWrapper.wrapJavaUtilRandom(System.currentTimeMillis());
    
    private final int maxColumnIndex;
    
    private int currentColumnIndex = 0;
    
    public SlotsEngine(int width, int height, String[] tokens) {
        this.slotsGrid = new IntegerArray2D(width, height);
        this.matches = new Boolean[height];
        this.tokens = tokens;
        this.maxColumnIndex = width - 1;
    }
    
    public int getWidth() {
        return slotsGrid.getWidth();
    }
    public int getHeight() {
        return slotsGrid.getHeight();
    }
    public int[] getRow(int i) {
        return slotsGrid.getRow(i);
    }
    public int[] getColumn(int i) {
        return slotsGrid.getColumn(i);
    }
    
    public boolean spinNextColumn() {
        
        slotsGrid.apply((x, y, value) -> {
            if (x == currentColumnIndex) {
                return randomizer.nextInt(tokens.length);
            } else {
             return value;
            }
        });
    
        currentColumnIndex++;
    
        if (currentColumnIndex > maxColumnIndex) {
            currentColumnIndex = 0;
            return false;
        }
        
        return true;
        
    }
    public void fullSpin() {
        slotsGrid.apply((x, y, value) -> randomizer.nextInt(tokens.length));
    }
    public void endSpinSequence() {
        for (int i = 0; i < getHeight(); i++) {
            int[] row = getRow(i);
            int[] successfulMatch = new int[getWidth()];
            
            Arrays.fill(successfulMatch, row[0]);
            matches[i] = Arrays.equals(row, successfulMatch);
        }
    }
    
    public int getMatchingLineCount() {
        return (int)Arrays.stream(matches)
                          .filter(c -> c)
                          .count();
    }
    
    public String getSlotsString(boolean showWin) {
    
        final StringBuilder rouletteBuilder = new StringBuilder();
    
        String separatorString = new String(
                                    new char[slotsGrid.getWidth() * 2]
                                 ).replace('\0', HORIZONTAL_SEPARATOR);

        for (int rowIndex = 0; rowIndex < slotsGrid.getHeight(); rowIndex++) {
        
            for (int value : slotsGrid.getRow(rowIndex)) {
                rouletteBuilder.append(tokens[value]);
            }
            
            if (showWin && matches[rowIndex]) {
                rouletteBuilder.append(ROW_WIN_INDICATOR);
            }
            
            if (rowIndex != slotsGrid.getHeight() - 1) {
                rouletteBuilder.append('\n');
                rouletteBuilder.append(separatorString);
                rouletteBuilder.append('\n');
            }
            
        }
        
        return rouletteBuilder.toString();
        
    }
    
}