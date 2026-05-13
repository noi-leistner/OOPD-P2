package Presentation.controllers;

import Business.AuthManager;
import Business.AuthResult;
import Business.Entities.User;
import Business.ParkingLotManager;
import Business.SessionManager;

import javax.swing.*;

public class AuthController {

    private AuthManager authManager;
    private SessionManager sessionManager;

    public AuthController(AuthManager authManager, SessionManager sessionManager) {
        this.authManager = authManager;
        this.sessionManager = sessionManager;
    }

    public AuthResult logIn(String email, String password) {
        if (email.isEmpty() && password.isEmpty()) {return AuthResult.EMPTY_FIELDS;}
        return authManager.login(email, password);
    }

    public AuthResult signUp(User user) {
        if (user == null) return AuthResult.DATABASE_ERROR;

        AuthResult result = authManager.signUp(user);
        if (result == AuthResult.SUCCESS) sessionManager.login(user);
        return result;
    }

    // TODO: Delete everything!!!
    public void logOut() {
        System.out.println("\nUser logged out.");
        sessionManager.logout();
    }

    public boolean deleteAccount(){
        Business.Entities.User currentUser = sessionManager.getCurrentUser();
        if (currentUser == null) return false;

        boolean deleted = authManager.deleteAccount(currentUser.getId());
        if (deleted) {
            logOut();
        }
        System.out.println("\nAccount deleted!\n");
        return deleted;
    }

    // When implementing the button for logging out / delete account, just call these two functions.

    private ParkingLotManager parkingLotManager;

    public void setParkingLotManager(ParkingLotManager parkingLotManager) {
        this.parkingLotManager = parkingLotManager;
    }

    public void checkAndShowCancelledNotification() {
        if (parkingLotManager == null) return;

        User user = sessionManager.getCurrentUser();
        if (user == null) return;

        if (parkingLotManager.hasCancelledReservations(user.getId())) {
            JOptionPane.showMessageDialog(
                    null,
                    "One or more of your reservations have been cancelled by an administrator.\n" +
                            "Please make a new reservation if needed.",
                    "Reservation Cancelled",
                    JOptionPane.WARNING_MESSAGE
            );
            parkingLotManager.clearCancelledNotifications(user.getId());
        }
    }
}

//h