/*
Module name: Programming 1A
Module code: PROG5121/p/w
Assessment type: Portfolio of Evidence (POE)- PART 1
Student Name: Lethabo
Student surname: Mohapi
Student username: ST10291427@rcconnect.edu.za
Student number: ST10291427

Part 1 Registration and Login feature - 3
Login.java - Manages user authentication
 */
package chatapp;

public class Login 
{
    private CreateUserAccount createUserAccount;
    private boolean loggedIn = false;
    
    /**
     * Constructor for Login
     * @param createUserAccount The user account to use for authentication
     */
    public Login(CreateUserAccount createUserAccount)
    {
        this.createUserAccount = createUserAccount;
    }
    
    /**
     * Checks if the input username matches the registered username
     * @param inputUsername The username to check
     * @return true if the username matches, false otherwise
     */
    public boolean checkUserName(String inputUsername)
    {
        return inputUsername.equals(createUserAccount.getUsername());
    }
    
    /**
     * Checks if the input password matches the registered password
     * @param inputPassword The password to check
     * @return true if the password matches, false otherwise
     */
    public boolean checkPassword(String inputPassword)
    {
        return inputPassword.equals(createUserAccount.getPassword());
    }
    
    /**
     * Returns a message indicating the user was registered
     * @return A registration success message
     */
    public String registerUser()
    {
        return "User " + createUserAccount.getUsername() + " registered successfully! ";
    }
    
    /**
     * Attempts to log in with the provided credentials
     * @param username The username to try
     * @param password The password to try
     * @return true if login was successful, false otherwise
     */
    public boolean loginUser(String username, String password)
    {
        if (checkUserName(username) && checkPassword(password))
        {
            loggedIn = true;
        }
        else
        {
            loggedIn = false;
        }
        return loggedIn;
    }
    
    /**
     * Returns the current login status
     * @return A string indicating login success or failure
     */
    public String returnLoginStatus()
    {
        return loggedIn ? "\nLogin successful!" : "\nLogin failed: Incorrect username or password";
    }        
}