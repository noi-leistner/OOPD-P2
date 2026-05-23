package Business;

/**
 * Result codes returned by DAO and manager persistance operations.
 */
public enum DaoResult {
    SUCCESS,                    /* Operation completed successfully */
    ALREADY_EXISTS,             /* Record with the same identifier as one existing */
    NOT_FOUND,                  /* Target record does not exist in the database */
    DATABASE_ERROR,             /* Unexpected SQL or connection error */
    CANNOT_REMOVE_OCCUPIED      /* Attempted to delete or modify a space that is currently occupied */
}
