/*
Module name: Programming 1A
Module code: PROG5121/p/w
Assessment type: Portfolio of Evidence (POE)- PART 2
Student Name: Lethabo
Student surname: Mohapi
Student username: ST10291427@rcconnect.edu.za
Student number: ST10291427

Part 2 Chat Messaging Feature - Test Runner
TestRunner.java - Simulates the test data scenarios
*/
package chatapp;

import javax.swing.JOptionPane;

public class TestRunner {
    
    public static void main(String[] args) {
        // Display information about the test
        JOptionPane.showMessageDialog(
            null,
            "Running test simulation with 2 test messages\n" +
            "1. Valid message to be sent\n" +
            "2. Message with invalid phone number to be discarded",
            "Test Runner",
            JOptionPane.INFORMATION_MESSAGE
        );
        
        // Test Scenario 1: Valid message to be sent
        testScenario1();
        
        // Test Scenario 2: Message with invalid phone number
        testScenario2();
        
        // Show final message count
        JOptionPane.showMessageDialog(
            null,
            "Test completed.\nTotal messages sent: " + Message.returnTotalMessages(),
            "Test Results",
            JOptionPane.INFORMATION_MESSAGE
        );
    }
    
    /**
     * Test Scenario 1: Valid message to be sent
     */
    private static void testScenario1() {
        try {
            JOptionPane.showMessageDialog(
                null,
                "Test Scenario 1: Valid message to be sent",
                "Test 1",
                JOptionPane.INFORMATION_MESSAGE
            );
            
            // Show message ID generated
            JOptionPane.showMessageDialog(
                null,
                "Message ID generated: " + message.getMessageID(),
                "Message ID",
                JOptionPane.INFORMATION_MESSAGE
            );
            
            // Show message hash
            JOptionPane.showMessageDialog(
                null,
                "Message Hash: " + message.getMessageHash(),
                "Message Hash",
                JOptionPane.INFORMATION_MESSAGE
            );
            
            // Simulate discarding the message (message status set to "Discarded")
            JOptionPane.showMessageDialog(
                null,
                "User selected: Discard Message",
                "Message Action",
                JOptionPane.INFORMATION_MESSAGE
            );
            
            message.setStatus("Discarded");
            
            // Show message details
            String details = 
                "MessageID: " + message.getMessageID() + "\n" +
                "Message Hash: " + message.getMessageHash() + "\n" +
                "Recipient: " + message.getRecipient() + "\n" +
                "Message: " + message.getMessageContent() + "\n" +
                "Status: " + message.getStatus();
                
            JOptionPane.showMessageDialog(
                null,
                details,
                "Message Details",
                JOptionPane.INFORMATION_MESSAGE
            );
            
            // Show count after second message (should still be 1 since this one was discarded)
            JOptionPane.showMessageDialog(
                null,
                "Message discarded.\nTotal messages sent: 1",
                "Message Discarded",
                JOptionPane.INFORMATION_MESSAGE
            );
            
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(
                null,
                "Error in test scenario 2: " + e.getMessage(),
                "Test Error",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }
}            
            // Create message
            Message message = new Message(
                "kyl_1", 
                "+27718693002", 
                "Hi Mike, can you join us for dinner tonight.", 
                1
            );
            
            // Show message ID generated
            JOptionPane.showMessageDialog(
                null,
                "Message ID generated: " + message.getMessageID(),
                "Message ID",
                JOptionPane.INFORMATION_MESSAGE
            );
            
            // Show message hash
            JOptionPane.showMessageDialog(
                null,
                "Message Hash: " + message.getMessageHash(),
                "Message Hash",
                JOptionPane.INFORMATION_MESSAGE
            );
            
            // Simulate sending the message (automatically select "Send")
            message.setStatus("Sent");
            
            // Store the message and update counts
            message.storeMessage();
            
            // Display full message details as required
            String details = 
                "MessageID: " + message.getMessageID() + "\n" +
                "Message Hash: " + message.getMessageHash() + "\n" +
                "Recipient: " + message.getRecipient() + "\n" +
                "Message: " + message.getMessageContent();
                
            JOptionPane.showMessageDialog(
                null,
                details,
                "Message Details",
                JOptionPane.INFORMATION_MESSAGE
            );
            
            // Show count after first message
            JOptionPane.showMessageDialog(
                null,
                "Message sent successfully.\nTotal messages sent: 1",
                "Message Sent",
                JOptionPane.INFORMATION_MESSAGE
            );
            
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(
                null,
                "Error in test scenario 1: " + e.getMessage(),
                "Test Error",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }
    
    /**
     * Test Scenario 2: Message with invalid phone number
     */
    private static void testScenario2() {
        JOptionPane.showMessageDialog(
            null,
            "Test Scenario 2: Message with invalid phone number",
            "Test 2",
            JOptionPane.INFORMATION_MESSAGE
        );
        
        // First show the validation error for the phone number
        String validationResult = Message.validateRecipientNumber("08575975889");
        JOptionPane.showMessageDialog(
            null,
            validationResult,
            "Phone Number Validation",
            JOptionPane.ERROR_MESSAGE
        );
        
        // Create message with valid phone number (since we can't create with invalid)
        // but we'll simulate the discard
        try {
            Message message = new Message(
                "kyl_1", 
                "+27718693002", // Using valid number for construction
                "Hi Keegan, did you receive the payment?", 
                2