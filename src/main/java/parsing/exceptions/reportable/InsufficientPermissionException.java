package parsing.exceptions.reportable;

import parsing.exceptions.ReportableException;

public class InsufficientPermissionException extends ReportableException {
    
    public InsufficientPermissionException(String errorString) {
        super(errorString);
    }
    
    public static InsufficientPermissionException notAdmin() {
        return new InsufficientPermissionException("insufficient permission, administrator rights required");
    }
    
}