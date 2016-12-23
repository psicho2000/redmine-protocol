package de.psicho.redmine.iTextile;

public class DocumentCreationException extends Exception {

    private static final long serialVersionUID = -4113329404814053184L;

    /**
     * <p>Constructs a <CODE>DocumentCreationException</CODE> without a message.
     */
    public DocumentCreationException() {
        super();
    }

    /**
     * <p>Creates a <code>DocumentCreationException</code>.
     *
     * @param ex an exception that has to be turned into a DocumentCreationException
     */
    public DocumentCreationException(Exception ex) {
        super(ex);
    }

    /**
     * <p>Constructs a <code>DocumentCreationException</code> with a message.
     *
     * @param message a message describing the exception
     */
    public DocumentCreationException(String message) {
        super(message);
    }

    /**
     * <p>Constructs a <code>DocumentCreationException</code> with a message and an <code>Exception</code>.
     *
     * @param message a message describing the exception
     * @param ex an exception that has to be turned into a DocumentCreationException
     */
    public DocumentCreationException(String message, Exception ex) {
        super(message, ex);
    }
}
