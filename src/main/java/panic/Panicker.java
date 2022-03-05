package panic;

public class Panicker {

    private final String message;
    private final int exitCode;

    public Panicker(String message, int exitCode) {
        this.message = message;
        this.exitCode = exitCode;
    }

    public Panicker(Exception exception, int exitCode) {
        this.message = exception.getMessage();
        this.exitCode = exitCode;
    }

    public void panic() {
        System.err.println(this.message);
        System.exit(this.exitCode);
    }

}