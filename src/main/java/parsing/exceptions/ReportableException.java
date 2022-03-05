package parsing.exceptions;

public abstract class ReportableException extends Exception {
    public ReportableException(String errorString) {
        super(errorString);
    }
}