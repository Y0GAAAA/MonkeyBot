package parsing.exceptions.internal;

import java.util.ArrayList;
import java.util.concurrent.Callable;

public class AggregateException extends Throwable {

    private final ArrayList<Exception> innerExceptions = new ArrayList<>();
    
    public AggregateException() {}

    public void add(Exception exception) {
        innerExceptions.add(exception);
    }
    public void addIfThrowing(Callable<?> callable) {
        try {
            callable.call();
        } catch (Exception ex) { innerExceptions.add(ex); }
    }
    
    public void printExceptionMessages() {
        System.err.println("Multiple exceptions occurred :");
        for (Exception ex : innerExceptions) {
            System.err.println(ex.getMessage());
        }
    }
    
    public boolean isEmpty() { return innerExceptions.isEmpty(); }
    
}