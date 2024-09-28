package canaryprism.commons.cursed;

/**
 * <p>
 * A runtime exception that is thrown when the
 * {@link canaryprism.commons.cursed.CursedUtils#catchExceptions()} or
 * {@link canaryprism.commons.cursed.CursedUtils#catchExceptionsWithParams()}
 * method fails
 * </p>
 */
public class CursedException extends RuntimeException {
    public CursedException(String message) {
        super(message);
    }
    public CursedException(String string, Throwable t) {
        super(string,t);
    }
}
