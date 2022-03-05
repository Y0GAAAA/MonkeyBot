package parsing.exceptions.reportable;

import parsing.exceptions.ReportableException;

public class MismatchingArgumentCount extends ReportableException {

    public MismatchingArgumentCount(int expectedArgumentCount, int foundArgumentCount) {
        super("expected " + expectedArgumentCount + " arguments but received " + foundArgumentCount + " argument" + (foundArgumentCount == 1 ? "" : "s"));
    }
    
}
