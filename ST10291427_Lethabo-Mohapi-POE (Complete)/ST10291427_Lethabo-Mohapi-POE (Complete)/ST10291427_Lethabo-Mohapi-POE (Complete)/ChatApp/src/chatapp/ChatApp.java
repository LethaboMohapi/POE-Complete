/*
Module name: Programming 1A
Module code: PROG5121/p/w
Assessment type: Portfolio of Evidence (POE)- PART 2
Student Name: Lethabo
Student surname: Mohapi
Student username: ST10291427@rcconnect.edu.za
Student number: ST10291427

// Part 1 Registration and Login feature - 2
// Part 2 Chat Messaging Feature - Enhanced
//ChatApp.java - Main class with program entry point
************************
 Test Data
username: kyl_1
password: Ch&&sec@ke99!
phonenumber: +27838968976
*************************
*/
package chatapp;
import javax.swing.JOptionPane;
import java.util.ArrayList;
import java.util.List;

public class ChatApp 
{
    // List to store multiple user accounts
    private static List<CreateUserAccount> userAccounts = new ArrayList<>();
    
    // Message manager instance
    private static MessageManager messageManager = new MessageManager();
    
    public static void main(String[] args) 
    {
        displayWelcomeMessage();
        
        boolean exitProgram = false;
        
        while (!exitProgram) {
            // Main menu
            String[] options = {"Register New Account", "Login", "Exit"};
            int choice = JOptionPane.showOptionDialog(
                null,
                "Welcome to QuickChat\n" +
                "Number of registered users: " + userAccounts.size(),
                "QuickChat Application",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.INFORMATION_MESSAGE,
                null,
                options,
                options[0]
            );
            
            switch (choice) {
                case 0: // Register
                    registerNewUser();
                    break;
                case 1: // Login
                    if (userAccounts.isEmpty()) {
                        JOptionPane.showMessageDialog(
                            null,
                            "No accounts registered yet. Please register first.",
                            "Login Error",
                            JOptionPane.ERROR_MESSAGE
                        );
                    } else {
                        loginUser();
                    }
                    break;
                case 2: // Exit
                case -1: // Window closed
                    exitProgram = true;
                    JOptionPane.showMessageDialog(
                        null,
                        "Thank you for using QuickChat!",
                        "Goodbye",
                        JOptionPane.INFORMATION_MESSAGE
                    );
                    break;
            }
        }
    }
    
    /**
     * Displays the welcome message
     */
    private static void displayWelcomeMessage() {
        JOptionPane.showMessageDialog(
            null,
            "Welcome to QuickChat.",
            "QuickChat Application",
            JOptionPane.INFORMATION_MESSAGE
        );
    }

    /**
     * Register a new user account
     */
    private static void registerNewUser() {
        boolean registrationSuccessful = false;
        
        while (!registrationSuccessful) {
            try {
                CreateUserAccount account = collectUserData();
                
                // Check if username already exists
                if (isUsernameTaken(account.getUsername())) {
                    throw new IllegalArgumentException("Username already taken. Please choose another username.");
                }
                
                // Add account to list of users
                userAccounts.add(account);
                
                // Initialize message tracking for this user
                messageManager.initializeUser(account.getUsername());
                
                registrationSuccessful = true;
                
                // Show success message
                JOptionPane.showMessageDialog(
                    null,
                    "Account created successfully!\nUsername: " + account.getUsername() + 
                    "\nPhone: " + account.getPhoneNumber(),
                    "Registration Success",
                    JOptionPane.INFORMATION_MESSAGE
                );
                
            } catch (IllegalArgumentException e) {
                int retry = JOptionPane.showConfirmDialog(
                    null,
                    "Failed to create an account: " + e.getMessage() + "\nWould you like to try again?",
                    "Registration Failed",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.ERROR_MESSAGE
                );
                
                if (retry != JOptionPane.YES_OPTION) {
                    break;
                }
            }
        }
    }
    
    /**
     * Helper method to check if a username is already taken
     * @param username The username to check
     * @return true if the username is already taken, false otherwise
     */
    private static boolean isUsernameTaken(String username) {
        for (CreateUserAccount account : userAccounts) {
            if (account.getUsername().equals(username)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Helper method to collect user registration data
     * @return CreateUserAccount if data is valid
     * @throws IllegalArgumentException if validation fails
     */
    private static CreateUserAccount collectUserData() throws IllegalArgumentException {
        JOptionPane.showMessageDialog(null, "Create a user account", "Registration", JOptionPane.INFORMATION_MESSAGE);
        
        String username = JOptionPane.showInputDialog(null, "Enter username(Username must be 5 characters or less and contain an underscore):", "Registration", JOptionPane.QUESTION_MESSAGE);
        String password = JOptionPane.showInputDialog(null, "Enter password (Password must be 8-20 characters long, contain at least one uppercase letter): ", "Registration", JOptionPane.QUESTION_MESSAGE);
        String phoneNumber = JOptionPane.showInputDialog(null, 
                                                      "Enter a phone number with international code (e.g., +27821234567):",
                                                      "Registration", 
                                                      JOptionPane.QUESTION_MESSAGE);
        
        return new CreateUserAccount(username, password, phoneNumber);
    }
    
    /**
     * Helper method to handle login attempts
     */
    private static void loginUser() {
        boolean loginSuccessful = false;
        
        while (!loginSuccessful) {
            JOptionPane.showMessageDialog(null, "Login to your account", "Login", JOptionPane.INFORMATION_MESSAGE);
            
            String loginUsername = JOptionPane.showInputDialog(null, "Enter username:", "Login", JOptionPane.QUESTION_MESSAGE);
            String loginPassword = JOptionPane.showInputDialog(null, "Enter password:", "Login", JOptionPane.QUESTION_MESSAGE);
            
            // Find the account with matching username
            CreateUserAccount matchedAccount = null;
            for (CreateUserAccount account : userAccounts) {
                if (account.getUsername().equals(loginUsername)) {
                    matchedAccount = account;
                    break;
                }
            }
            
            if (matchedAccount == null) {
                JOptionPane.showMessageDialog(
                    null,
                    "Login failed: User not found!",
                    "Login Error",
                    JOptionPane.ERROR_MESSAGE
                );
            } else {
                // Create login instance for the matched account
                Login login = new Login(matchedAccount);
                boolean success = login.loginUser(loginUsername, loginPassword);
                
                if (success) {
                    JOptionPane.showMessageDialog(
                        null,
                        login.returnLoginStatus(),
                        "Login Status",
                        JOptionPane.INFORMATION_MESSAGE
                    );
                    
                    // If login successful, enter chat interface
                    chatInterface(matchedAccount.getUsername());
                    
                    loginSuccessful = true;
                } else {
                    JOptionPane.showMessageDialog(
                        null,
                        login.returnLoginStatus(),
                        "Login Status",
                        JOptionPane.ERROR_MESSAGE
                    );
                    
                    int retry = JOptionPane.showConfirmDialog(
                        null,
                        "Would you like to try logging in again?",
                        "Login Failed",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE
                    );
                    
                    if (retry != JOptionPane.YES_OPTION) {
                        break;
                    }
                }
            }
        }
    }
    
    /**
     * Chat interface after successful login - Updated to properly loop and continue
     * @param username The logged in user's username
     */
    private static void chatInterface(String username) {
        boolean exitChat = false;
        
        // Main chat loop - continues until user chooses to quit
        while (!exitChat) {
            String[] chatOptions = {"Send Message", "View My Messages", "Quit"};
            int chatChoice = JOptionPane.showOptionDialog(
                null,
                "QuickChat - Logged in as: " + username + 
                "\nMessages sent: " + messageManager.getMessageCount(username),
                "QuickChat",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.INFORMATION_MESSAGE,
                null,
                chatOptions,
                chatOptions[0]
            );
            
            switch (chatChoice) {
                case 0: // Send Message
                    boolean messageCreated = messageManager.createMessage(username);
                    
                    // Show result and continue to chat menu
                    if (messageCreated) {
                        JOptionPane.showMessageDialog(
                            null,
                            "Message operation completed. Returning to chat menu.",
                            "Message Status",
                            JOptionPane.INFORMATION_MESSAGE
                        );
                    }
                    // Always continue to loop back to chat menu regardless of message result
                    break;
                    
                case 1: // View My Messages
                    messageManager.displayMessages(username);
                    // Continue to loop back to chat menu
                    break;
                    
                case 2: // Quit
                case -1: // Window closed
                    exitChat = true;
                    JOptionPane.showMessageDialog(
                        null,
                        "Logging out. Returning to main menu.",
                        "Logout",
                        JOptionPane.INFORMATION_MESSAGE
                    );
                    break;
            }
        }
    }
}