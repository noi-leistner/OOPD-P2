package Business;

public enum AuthResult {
    SUCCESS,                            /*Successful log in */
    INVALID_CREDENTIALS,                /*Either email or password wrong */
    ACCOUNT_INACTIVE,                   /*Account deactivated */
    EMPTY_FIELDS,                       /*1 or more field that are null */
    EMAIL_ALREADY_EXISTS,               /*Email already created when registering */
    DATABASE_ERROR,                     /*Error in the database*/
    WEAK_PASSWORD;                      /*The password does not contain a special char and be +8 in length

    /**
     * Translates enumeration into a readable message:
     */

    public String toDisplayMessage () {
        return switch (this) {
            case SUCCESS -> "Login Successful";
            case INVALID_CREDENTIALS -> "Invalid Credentials";
            case ACCOUNT_INACTIVE -> "Account Inactive";
            case EMPTY_FIELDS -> "Empty Fields";
            case EMAIL_ALREADY_EXISTS -> "Email Already Exists";
            case DATABASE_ERROR -> "Database Error";
            case WEAK_PASSWORD -> "Password must contain 8 letters and a special character";
        };
    }

    public boolean isSuccess () {return this == SUCCESS;}

}


