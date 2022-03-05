package commands.dependencies.chess;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.function.Function;

public class BufferedPuzzleClient {

    private final PuzzleClient puzzleClient = new PuzzleClient();
    private ArrayBlockingQueue<PuzzleResponse> puzzleQueue = null;
    private Function<PuzzleResponse, Boolean> predicate = null;
    
    public BufferedPuzzleClient(int bufferSize, Function<PuzzleResponse, Boolean> predicate) {

        this.puzzleQueue = new ArrayBlockingQueue<PuzzleResponse>(bufferSize);
        this.predicate = predicate;
        
        Thread fillingThread = new Thread(() -> {
            
            while (true) {
                
                final PuzzleResponse puzzle = puzzleClient.getRandomPuzzle();
                
                if (!this.predicate.apply(puzzle)) {
                    continue;
                }
                
                try {
                    puzzleQueue.put(puzzle);
                } catch (InterruptedException e) {
                    System.err.println("Puzzle queue filling thread got interrupted.");
                    break;
                }
                
            }
            
        });
        fillingThread.start();

    }

    public PuzzleResponse popPuzzle() {
        try {
            return puzzleQueue.take();
        } catch (InterruptedException ignored) {
            return null;
        }
    }

}