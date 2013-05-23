package blancas.tinypost;

/**
 * This exception is thrown to exit a running procedure.
 * The cached xe will be the procedure's return value.
 */
public class ExitException extends Exception {

    /**
     * The interpreter's execution environment.
     */
    Object xe;

    public ExitException(Object xe) {
        this.xe = xe;
    }

    public Object getXE() {
        return xe;
    }

}
