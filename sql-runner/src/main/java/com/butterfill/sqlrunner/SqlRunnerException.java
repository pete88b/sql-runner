package com.butterfill.sqlrunner;

import java.sql.SQLException;

/**
 * Exception thrown by SQL runner.
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

    /**
     * The exception thrown by rolling back.
     */
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

    /**
     * Returns the exception thrown by rolling back
     * or null if rolling back did not cause an exception.
     * @return
     *   The exception thrown by rolling back.
     */
    public SQLException getRollbackFailedException() {
        return rollbackFailedException;
    }

    /**
     * Sets the exception thrown by rolling back.
     * @param rollbackFailedException
     *   The exception thrown by rolling back.
     */
    public void setRollbackFailedException(final SQLException rollbackFailedException) {
        this.rollbackFailedException = rollbackFailedException;
    }

}
