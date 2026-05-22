package Business;

public enum AuthResult {
    SUCCESS,                            /*Successful log in */
    INVALID_CREDENTIALS,                /*Password wrong */
    USER_NOT_FOUND,                     /*User is not registered*/
    ACCOUNT_INACTIVE,                   /*Account deactivated */
    EMPTY_FIELDS,                       /*1 or more field that are null */
    EMAIL_ALREADY_EXISTS,               /*Email already created when registering */
    DATABASE_ERROR,                     /*Error in the database*/
    WEAK_PASSWORD,                      /*The password does not contain a special char and be +8 in length*/
    INVALID_EMAIL;                      /*Email does not contain '@' or '.'*/

    /**
     * Translates enumeration into a readable message:
     */

    public String toDisplayMessage () {
        return switch (this) {
            case SUCCESS -> "Login Successful";
            case INVALID_CREDENTIALS -> "Wrong password. Please try again.";
            case USER_NOT_FOUND -> "No account found with that email.";
            case ACCOUNT_INACTIVE -> "Account Inactive";
            case EMPTY_FIELDS -> "Empty Fields";
            case EMAIL_ALREADY_EXISTS -> "Email Already Exists";
            case DATABASE_ERROR -> "Database Error";
            case WEAK_PASSWORD -> "Password must contain 8 letters and a special character";
            case INVALID_EMAIL -> "Email must be a real email";
        };
    }

}


