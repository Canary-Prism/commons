package canaryprism.commons.cursed;
public class CursedException extends RuntimeException {
    public CursedException(String message) {
        super(message);
    }
    public CursedException(String string, Throwable t) {
        super(string,t);
    }
}
