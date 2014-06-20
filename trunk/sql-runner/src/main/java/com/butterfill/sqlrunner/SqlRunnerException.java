package com.butterfill.sqlrunner;


import java.sql.SQLException;

/**
 * Exception thrown by SQL runner.
 * SqlRunner will rollback when executing a statement causes an error;
 * <ul>
 * <li>
 *   The SQLException thrown by executing the statement will be wrapped by an instance of this class
 *   - i.e. it will be the cause of this exception
 * </li>
 * <li>
 *   If rolling back also throws a SQLException, it will be saved on an instance of this class
 *   - so you can retrieve it via getRollbackFailedException()
 * </li>
 * </ul>
 *
 * @author Peter Butterfill
 */
public class SqlRunnerException extends RuntimeException {

    private SQLException rollbackFailedException;

    /**
     * Creates a new SqlRunnerException.
     * @param message
     *   The exception message.
     * @param cause
     *   The cause of this exception.
     */
    public SqlRunnerException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public SQLException getRollbackFailedException() {
        return rollbackFailedException;
    }

    public void setRollbackFailedException(final SQLException rollbackFailedException) {
        this.rollbackFailedException = rollbackFailedException;
    }

}
