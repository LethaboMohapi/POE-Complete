/*
Module name: Programming 1A
Module code: PROG5121/p/w
Assessment type: Portfolio of Evidence (POE)- PART 1
Student Name: Lethabo
Student surname: Mohapi
Student username: ST10291427@rcconnect.edu.za
Student number: ST10291427

Part 1 Registration and Login feature - 2
CreateUserAccount.java - Handles user account creation and validation

*/
package chatapp;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CreateUserAccount 
// Registration feature
// Create and account by entering a username, password and South African cell phone number
{
   private String username, password, phoneNumber;
   
   /**
    * Constructor for CreateUserAccount - validates all inputs
    * @param username The username to register
    * @param password The password to register
    * @param phoneNumber The phone number to register
    * @throws IllegalArgumentException if any validation check fails
    */
   public CreateUserAccount(String username, String password, String phoneNumber) throws IllegalArgumentException
   {
       // Validate username
       if (!isValidUsername(username)) {
           throw new IllegalArgumentException("Username must be 5 characters or less and contain an underscore");
       }
       
       // Validate password
       if (!isValidPassword(password)) {
           throw new IllegalArgumentException("Password must be 8-20 characters long, contain at least one uppercase letter, " +
                                             "one lowercase letter, one digit, and one special character");
       }
       
       // Validate phone number
       if (!isValidPhoneNumber(phoneNumber)) {
           throw new IllegalArgumentException("Phone number must start with +27 followed by 9 digits (South African format)");
       }
       
       // If all validations pass, assign the values
       this.username = username;
       this.password = password;
       this.phoneNumber = phoneNumber;
   }
   
   public String getUsername()
   {
       return username;
   }
   
   public String getPassword()
   {
       return password;
   }
   
   public String getPhoneNumber()
   {
       return phoneNumber;
   }
   
   /**
    * Validates that the username contains an underscore and is 5 chars or less
    * @param username The username to validate
    * @return true if the username is valid, false otherwise
    */
   private boolean isValidUsername(String username)
   //This method ensure that the username contains an underscore       
   {
       return username.length() <= 5 && username.contains("_");
   }
  
   /**
    * Validates that the password meets complexity requirements
    * @param password The password to validate
    * @return true if the password is valid, false otherwise
    */
   private boolean isValidPassword(String password)
   //This method ensures that the passwords meet the complexity rules.
   {
       String regex ="^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[`~!@#$%^&*()-_=+[{]}\\|:;\"'<,>.?/])" + 
                     "[A-Za-z\\d`~!@#$%^&*()-_=+[{]}\\|:;\"'<,>.?/]{8,20}$";
       Pattern pattern = Pattern.compile(regex);
       Matcher matcher = pattern.matcher(password);
       return matcher.matches();
   }
   
   /**
    * Validates that the phone number is in South African format
    * @param phoneNumber The phone number to validate
    * @return true if the phone number is valid, false otherwise
    */
   private boolean isValidPhoneNumber(String phoneNumber)
   // This method ensure that the cell phone number is the correct length and contains the South African international country code
   {
       String regex = "^\\+27\\d{9}$";
       return phoneNumber != null && phoneNumber.matches(regex);
   }
   
   /**
    * Returns a string representation of this account
    * @return A string representing this account's data (with masked password)
    */
   @Override
   public String toString() {
       return "Username: " + username + ", Phone: " + phoneNumber + ", Password: ********";
   }
}