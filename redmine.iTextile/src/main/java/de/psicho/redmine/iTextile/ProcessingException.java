package de.psicho.redmine.iTextile;

public class ProcessingException extends RuntimeException {

    private static final long serialVersionUID = 6027709816413893669L;

    /**
     * <p>Constructs a <CODE>ProcessingException</CODE> without a message.
     */
    public ProcessingException() {
        super();
    }

    /**
     * <p>Creates a <code>ProcessingException</code>.
     *
     * @param ex an exception that has to be turned into a ProcessingException
     */
    public ProcessingException(Exception ex) {
        super(ex);
    }

    /**
     * <p>Constructs a <code>ProcessingException</code> with a message.
     *
     * @param message a message describing the exception
     */
    public ProcessingException(String message) {
        super(message);
    }

    /**
     * <p>Constructs a <code>ProcessingException</code> with a message and an <code>Exception</code>.
     *
     * @param message a message describing the exception
     * @param ex an exception that has to be turned into a ProcessingException
     */
    public ProcessingException(String message, Exception ex) {
        super(message, ex);
    }
}
