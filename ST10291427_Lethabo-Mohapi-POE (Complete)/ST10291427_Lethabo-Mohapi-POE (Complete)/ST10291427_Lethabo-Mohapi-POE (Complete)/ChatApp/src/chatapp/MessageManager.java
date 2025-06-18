/*
Module name: Programming 1A
Module code: PROG5121/p/w
Assessment type: Portfolio of Evidence (POE)- PART 2
Student Name: Lethabo
Student surname: Mohapi
Student username: ST10291427@rcconnect.edu.za
Student number: ST10291427

Part 2 Chat Messaging Feature - Enhanced with Array Operations (IMPROVED VERSION)
MessageManager.java - Manages message creation, storage, and retrieval with enhanced array functionality
*/
package chatapp;

import javax.swing.JOptionPane;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class MessageManager {
    
    JSONParser parser = new JSONParser();
    // Map to store messages for each user (username -> list of messages)
    private HashMap<String, List<Message>> userMessages;
    // Map to store message counts for each user
    private HashMap<String, Integer> userMessageCounts;
    
    // Enhanced arrays as requested
    private static ArrayList<Message> sentMessages = new ArrayList<>();
    private static ArrayList<Message> disregardedMessages = new ArrayList<>();
    private static ArrayList<Message> storedMessages = new ArrayList<>();
    private static ArrayList<String> messageHashes = new ArrayList<>();
    private static ArrayList<String> messageIDs = new ArrayList<>();
    
    /**
     * Constructor for MessageManager
     */
    public MessageManager() {
        userMessages = new HashMap<>();
        userMessageCounts = new HashMap<>();
        loadStoredMessagesFromJSON();
    }
    
    /**
     * Initialize a new user in the message system
     * @param username The username to initialize
     */
    public void initializeUser(String username) {
        if (!userMessages.containsKey(username)) {
            userMessages.put(username, new ArrayList<>());
            userMessageCounts.put(username, 0);
        }
    }
    
    /**
     * Get the number of messages a user has sent
     * @param username The username to check
     * @return The number of messages sent
     */
    public int getMessageCount(String username) {
        return userMessageCounts.getOrDefault(username, 0);
    }
    
    /**
     * Handle the message creation workflow
     * @param sender The username of the sender
     * @return true if a message was successfully created, false otherwise
     */
    public boolean createMessage(String sender) {
        try {
            // Get recipient phone number
            String recipient = JOptionPane.showInputDialog(
                null,
                "Enter recipient's phone number (with +27 prefix):",
                "New Message",
                JOptionPane.QUESTION_MESSAGE
            );
            
            if (recipient == null || recipient.trim().isEmpty()) {
                return false; // User canceled or entered empty input
            }
            
            // Validate recipient format and show appropriate message
            String validationResult = Message.validateRecipientNumber(recipient);
            JOptionPane.showMessageDialog(
                null,
                validationResult,
                "Phone Number Validation",
                validationResult.startsWith("Cell phone number successfully") ? 
                    JOptionPane.INFORMATION_MESSAGE : JOptionPane.ERROR_MESSAGE
            );
            
            if (!validationResult.startsWith("Cell phone number successfully")) {
                return false; // Invalid phone number
            }
            
            // Get message content
            String content = JOptionPane.showInputDialog(
                null,
                "Enter message (max 250 characters):",
                "New Message",
                JOptionPane.QUESTION_MESSAGE
            );
            
            if (content == null || content.trim().isEmpty()) {
                return false; // User canceled or entered empty input
            }
            
            // Validate message length and show appropriate message
            String lengthValidation = Message.validateMessageLength(content);
            JOptionPane.showMessageDialog(
                null,
                lengthValidation,
                "Message Length Validation",
                lengthValidation.startsWith("Message ready") ? 
                    JOptionPane.INFORMATION_MESSAGE : JOptionPane.ERROR_MESSAGE
            );
            
            if (!lengthValidation.startsWith("Message ready")) {
                return false; // Message too long
            }
            
            // Increment message count for this user
            int messageNumber = userMessageCounts.get(sender) + 1;
            
            // Create the message
            Message message = new Message(sender, recipient, content, messageNumber);
            
            // Show message ID was created
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
                "Message Hash Created",
                JOptionPane.INFORMATION_MESSAGE
            );
            
            // Use the message's sentMessage method to handle user choice
            String result = message.sentMessage();
            
            // Update arrays based on message status
            updateArraysWithMessage(message);
            
            // If the message was sent or stored, update our counts
            if (message.getStatus().equals("Sent") || message.getStatus().equals("Stored")) {
                userMessages.get(sender).add(message);
                userMessageCounts.put(sender, messageNumber);
                
                // If it was sent, show the total messages sent
                if (message.getStatus().equals("Sent")) {
                    JOptionPane.showMessageDialog(
                        null,
                        "Total messages sent: " + Message.returnTotalMessages(),
                        "Message Count",
                        JOptionPane.INFORMATION_MESSAGE
                    );
                }
                
                return true;
            }
            
            return false;
            
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(
                null,
                e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE
            );
            return false;
        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                null,
                "An unexpected error occurred: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE
            );
            return false;
        }
    }
    
    /**
     * Updates the arrays based on message status
     * @param message The message to add to appropriate arrays
     */
    private void updateArraysWithMessage(Message message) {
        // Always add to message hashes and IDs arrays (avoid duplicates)
        if (!messageHashes.contains(message.getMessageHash())) {
            messageHashes.add(message.getMessageHash());
        }
        if (!messageIDs.contains(message.getMessageID())) {
            messageIDs.add(message.getMessageID());
        }
        
        // Add to appropriate status arrays
        switch (message.getStatus()) {
            case "Sent":
                if (!sentMessages.contains(message)) {
                    sentMessages.add(message);
                }
                break;
            case "Stored":
                if (!storedMessages.contains(message)) {
                    storedMessages.add(message);
                }
                break;
            case "Discarded":
                if (!disregardedMessages.contains(message)) {
                    disregardedMessages.add(message);
                }
                break;
        }
    }
    
    /**
     * Load stored messages from JSON files into the storedMessages array
     */
    private void loadStoredMessagesFromJSON() {
        try {
            File currentDir = new File(".");
            File[] files = currentDir.listFiles((dir, name) -> name.startsWith("message_") && name.endsWith(".json"));
            
            if (files != null) {
                JSONParser parser = new JSONParser();
                
                for (File file : files) {
                    try (FileReader reader = new FileReader(file)) {
                        JSONObject jsonMessage = (JSONObject) parser.parse(reader);
                        
                        // Extract message data from JSON
                        String messageID = (String) jsonMessage.get("messageID");
                        String sender = (String) jsonMessage.get("sender");
                        String recipient = (String) jsonMessage.get("recipient");
                        String content = (String) jsonMessage.get("content");
                        String hash = (String) jsonMessage.get("hash");
                        String status = (String) jsonMessage.get("status");
                        Long messageNum = (Long) jsonMessage.get("messageNumber");
                        
                        // Validate extracted data
                        if (messageID == null || sender == null || recipient == null || 
                            content == null || hash == null || status == null || messageNum == null) {
                            System.err.println("Invalid JSON data in file: " + file.getName());
                            continue;
                        }
                        
                        // Create message object from JSON data
                        Message message = new Message(sender, recipient, content, messageNum.intValue());
                        message.setStatus(status);
                        
                        // Add to stored messages if status is "Stored"
                        if ("Stored".equals(status)) {
                            storedMessages.add(message);
                        }
                        
                        // Add to appropriate arrays
                        if (!messageHashes.contains(hash)) {
                            messageHashes.add(hash);
                        }
                        if (!messageIDs.contains(messageID)) {
                            messageIDs.add(messageID);
                        }
                        
                    } catch (IOException | ParseException e) {
                        System.err.println("Error reading JSON file: " + file.getName() + " - " + e.getMessage());
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error loading stored messages: " + e.getMessage());
        }
    }
    
    /**
     * Display enhanced message operations menu
     * @param username The username of the current user
     */
    public void displayMessages(String username) {
        boolean exitMessageMenu = false;
        
        while (!exitMessageMenu) {
            String[] messageOptions = {
                "Display Sent Messages (Sender & Recipient)",
                "Display Longest Sent Message", 
                "Search Message by ID",
                "Search Messages by Recipient",
                "Delete Message by Hash",
                "Display Full Message Report",
                "Display Array Statistics", // New option
                "Back to Chat Menu"
            };
            
            int choice = JOptionPane.showOptionDialog(
                null,
                "Message Operations Menu\n\n" +
                "Sent Messages: " + sentMessages.size() + "\n" +
                "Stored Messages: " + storedMessages.size() + "\n" +
                "Discarded Messages: " + disregardedMessages.size() + "\n" +
                "Total Message IDs: " + messageIDs.size() + "\n" +
                "Total Message Hashes: " + messageHashes.size(),
                "Message Operations",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.INFORMATION_MESSAGE,
                null,
                messageOptions,
                messageOptions[0]
            );
            
            switch (choice) {
                case 0:
                    displaySentMessagesSenderRecipient();
                    break;
                case 1:
                    displayLongestSentMessage();
                    break;
                case 2:
                    searchMessageByID();
                    break;
                case 3:
                    searchMessagesByRecipient();
                    break;
                case 4:
                    deleteMessageByHash();
                    break;
                case 5:
                    displayFullMessageReport();
                    break;
                case 6:
                    displayArrayStatistics();
                    break;
                case 7:
                case -1:
                    exitMessageMenu = true;
                    break;
            }
        }
    }
    
    /**
     * Display sender and recipient of all sent messages
     */
    private void displaySentMessagesSenderRecipient() {
        if (sentMessages.isEmpty()) {
            JOptionPane.showMessageDialog(
                null,
                "No sent messages available.",
                "Sent Messages",
                JOptionPane.INFORMATION_MESSAGE
            );
            return;
        }
        
        StringBuilder display = new StringBuilder("Sent Messages (Sender & Recipient):\n\n");
        
        for (int i = 0; i < sentMessages.size(); i++) {
            Message msg = sentMessages.get(i);
            display.append("Message #").append(i + 1).append("\n")
                   .append("Sender: ").append(msg.getSender()).append("\n")
                   .append("Recipient: ").append(msg.getRecipient()).append("\n")
                   .append("Date: ").append(new java.util.Date()).append("\n\n"); // Added timestamp
        }
        
        JOptionPane.showMessageDialog(
            null,
            display.toString(),
            "Sent Messages - Sender & Recipient",
            JOptionPane.INFORMATION_MESSAGE
        );
    }
    
    /**
     * Display the longest sent message
     */
    private void displayLongestSentMessage() {
        if (sentMessages.isEmpty()) {
            JOptionPane.showMessageDialog(
                null,
                "No sent messages available.",
                "Longest Message",
                JOptionPane.INFORMATION_MESSAGE
            );
            return;
        }
        
        Message longestMessage = sentMessages.get(0);
        for (Message msg : sentMessages) {
            if (msg.getMessageContent().length() > longestMessage.getMessageContent().length()) {
                longestMessage = msg;
            }
        }
        
        String display = "Longest Sent Message:\n\n" +
                        "Message ID: " + longestMessage.getMessageID() + "\n" +
                        "Message Hash: " + longestMessage.getMessageHash() + "\n" +
                        "Sender: " + longestMessage.getSender() + "\n" +
                        "Recipient: " + longestMessage.getRecipient() + "\n" +
                        "Length: " + longestMessage.getMessageContent().length() + " characters\n" +
                        "Content: " + longestMessage.getMessageContent();
        
        JOptionPane.showMessageDialog(
            null,
            display,
            "Longest Sent Message",
            JOptionPane.INFORMATION_MESSAGE
        );
    }
    
    /**
     * Search for a message by ID and display recipient and message
     */
    private void searchMessageByID() {
        String searchID = JOptionPane.showInputDialog(
            null,
            "Enter Message ID to search:\n(10-digit number)",
            "Search by Message ID",
            JOptionPane.QUESTION_MESSAGE
        );
        
        if (searchID == null || searchID.trim().isEmpty()) {
            return;
        }
        
        // Validate input format
        if (!searchID.matches("\\d{10}")) {
            JOptionPane.showMessageDialog(
                null,
                "Invalid Message ID format. Please enter a 10-digit number.",
                "Search Error",
                JOptionPane.ERROR_MESSAGE
            );
            return;
        }
        
        // Search in all message arrays
        Message foundMessage = searchInAllArrays(searchID, "ID");
        
        if (foundMessage != null) {
            String display = "Message Found:\n\n" +
                           "Message ID: " + foundMessage.getMessageID() + "\n" +
                           "Message Hash: " + foundMessage.getMessageHash() + "\n" +
                           "Sender: " + foundMessage.getSender() + "\n" +
                           "Recipient: " + foundMessage.getRecipient() + "\n" +
                           "Message: " + foundMessage.getMessageContent() + "\n" +
                           "Status: " + foundMessage.getStatus() + "\n" +
                           "Message Number: " + foundMessage.getMessageNumber();
            
            JOptionPane.showMessageDialog(
                null,
                display,
                "Message Search Result",
                JOptionPane.INFORMATION_MESSAGE
            );
        } else {
            JOptionPane.showMessageDialog(
                null,
                "No message found with ID: " + searchID,
                "Search Result",
                JOptionPane.INFORMATION_MESSAGE
            );
        }
    }
    
    /**
     * Helper method to search for a message across all arrays
     * @param searchValue The value to search for
     * @param searchType "ID" or "HASH"
     * @return The found message or null
     */
    private Message searchInAllArrays(String searchValue, String searchType) {
        // Search in sent messages
        for (Message msg : sentMessages) {
            String compareValue = searchType.equals("ID") ? msg.getMessageID() : msg.getMessageHash();
            if (compareValue.equals(searchValue)) {
                return msg;
            }
        }
        
        // Search in stored messages
        for (Message msg : storedMessages) {
            String compareValue = searchType.equals("ID") ? msg.getMessageID() : msg.getMessageHash();
            if (compareValue.equals(searchValue)) {
                return msg;
            }
        }
        
        // Search in discarded messages
        for (Message msg : disregardedMessages) {
            String compareValue = searchType.equals("ID") ? msg.getMessageID() : msg.getMessageHash();
            if (compareValue.equals(searchValue)) {
                return msg;
            }
        }
        
        return null;
    }
    
    /**
     * Search for all messages sent to a particular recipient
     */
    private void searchMessagesByRecipient() {
        String searchRecipient = JOptionPane.showInputDialog(
            null,
            "Enter recipient phone number (+27 format):\nExample: +27821234567",
            "Search by Recipient",
            JOptionPane.QUESTION_MESSAGE
        );
        
        if (searchRecipient == null || searchRecipient.trim().isEmpty()) {
            return;
        }
        
        // Validate recipient format
        String validationResult = Message.validateRecipientNumber(searchRecipient);
        if (!validationResult.startsWith("Cell phone number successfully")) {
            JOptionPane.showMessageDialog(
                null,
                "Invalid phone number format: " + validationResult,
                "Search Error",
                JOptionPane.ERROR_MESSAGE
            );
            return;
        }
        
        List<Message> recipientMessages = new ArrayList<>();
        
        // Search in sent messages
        for (Message msg : sentMessages) {
            if (msg.getRecipient().equals(searchRecipient)) {
                recipientMessages.add(msg);
            }
        }
        
        // Search in stored messages
        for (Message msg : storedMessages) {
            if (msg.getRecipient().equals(searchRecipient)) {
                recipientMessages.add(msg);
            }
        }
        
        if (recipientMessages.isEmpty()) {
            JOptionPane.showMessageDialog(
                null,
                "No messages found for recipient: " + searchRecipient,
                "Search Result",
                JOptionPane.INFORMATION_MESSAGE
            );
            return;
        }
        
        StringBuilder display = new StringBuilder("Messages to " + searchRecipient + ":\n\n");
        
        for (int i = 0; i < recipientMessages.size(); i++) {
            Message msg = recipientMessages.get(i);
            display.append("Message #").append(i + 1).append("\n")
                   .append("ID: ").append(msg.getMessageID()).append("\n")
                   .append("Hash: ").append(msg.getMessageHash()).append("\n")
                   .append("Sender: ").append(msg.getSender()).append("\n")
                   .append("Content: ").append(msg.getMessageContent()).append("\n")
                   .append("Status: ").append(msg.getStatus()).append("\n\n");
        }
        
        display.append("Total messages to this recipient: ").append(recipientMessages.size());
        
        JOptionPane.showMessageDialog(
            null,
            display.toString(),
            "Messages by Recipient",
            JOptionPane.INFORMATION_MESSAGE
        );
    }
    
    /**
     * Delete a message using the message hash
     */
    private void deleteMessageByHash() {
        String searchHash = JOptionPane.showInputDialog(
            null,
            "Enter Message Hash to delete:\n(Format: XX:X:WORDWORD)",
            "Delete by Hash",
            JOptionPane.QUESTION_MESSAGE
        );
        
        if (searchHash == null || searchHash.trim().isEmpty()) {
            return;
        }
        
        // Confirm deletion
        int confirm = JOptionPane.showConfirmDialog(
            null,
            "Are you sure you want to delete the message with hash: " + searchHash + "?\nThis action cannot be undone.",
            "Confirm Deletion",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE
        );
        
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }
        
        boolean messageDeleted = false;
        String deletedMessageInfo = "";
        
        // Search and remove from sent messages
        for (int i = 0; i < sentMessages.size(); i++) {
            if (sentMessages.get(i).getMessageHash().equals(searchHash)) {
                Message deletedMessage = sentMessages.remove(i);
                deletedMessageInfo = "Deleted from sent messages:\nID: " + deletedMessage.getMessageID() + 
                                   "\nSender: " + deletedMessage.getSender() + 
                                   "\nRecipient: " + deletedMessage.getRecipient();
                messageDeleted = true;
                break;
            }
        }
        
        // Search and remove from stored messages if not found in sent
        if (!messageDeleted) {
            for (int i = 0; i < storedMessages.size(); i++) {
                if (storedMessages.get(i).getMessageHash().equals(searchHash)) {
                    Message deletedMessage = storedMessages.remove(i);
                    deletedMessageInfo = "Deleted from stored messages:\nID: " + deletedMessage.getMessageID() + 
                                       "\nSender: " + deletedMessage.getSender() + 
                                       "\nRecipient: " + deletedMessage.getRecipient();
                    messageDeleted = true;
                    break;
                }
            }
        }
        
        // Search and remove from discarded messages if not found elsewhere
        if (!messageDeleted) {
            for (int i = 0; i < disregardedMessages.size(); i++) {
                if (disregardedMessages.get(i).getMessageHash().equals(searchHash)) {
                    Message deletedMessage = disregardedMessages.remove(i);
                    deletedMessageInfo = "Deleted from discarded messages:\nID: " + deletedMessage.getMessageID() + 
                                       "\nSender: " + deletedMessage.getSender() + 
                                       "\nRecipient: " + deletedMessage.getRecipient();
                    messageDeleted = true;
                    break;
                }
            }
        }
        
        if (messageDeleted) {
            // Remove from hash and ID arrays as well
            messageHashes.remove(searchHash);
            
            JOptionPane.showMessageDialog(
                null,
                "Message successfully deleted!\n\n" + deletedMessageInfo,
                "Delete Result",
                JOptionPane.INFORMATION_MESSAGE
            );
        } else {
            JOptionPane.showMessageDialog(
                null,
                "No message found with hash: " + searchHash,
                "Delete Result",
                JOptionPane.INFORMATION_MESSAGE
            );
        }
    }
    
    /**
     * Display full details report of all sent messages
     */
    private void displayFullMessageReport() {
        if (sentMessages.isEmpty()) {
            JOptionPane.showMessageDialog(
                null,
                "No sent messages available for report.",
                "Message Report",
                JOptionPane.INFORMATION_MESSAGE
            );
            return;
        }
        
        StringBuilder report = new StringBuilder("FULL MESSAGE REPORT\n");
        report.append("=".repeat(50)).append("\n\n");
        report.append("Total Sent Messages: ").append(sentMessages.size()).append("\n");
        report.append("Generated on: ").append(new java.util.Date()).append("\n\n");
        
        // Calculate statistics
        int totalCharacters = 0;
        int shortestLength = Integer.MAX_VALUE;
        int longestLength = 0;
        
        for (Message msg : sentMessages) {
            int length = msg.getMessageContent().length();
            totalCharacters += length;
            if (length < shortestLength) shortestLength = length;
            if (length > longestLength) longestLength = length;
        }
        
        double averageLength = sentMessages.size() > 0 ? (double) totalCharacters / sentMessages.size() : 0;
        
        report.append("STATISTICS:\n")
              .append("Total Characters: ").append(totalCharacters).append("\n")
              .append("Average Message Length: ").append(String.format("%.2f", averageLength)).append(" characters\n")
              .append("Shortest Message: ").append(shortestLength == Integer.MAX_VALUE ? 0 : shortestLength).append(" characters\n")
              .append("Longest Message: ").append(longestLength).append(" characters\n\n");
        
        for (int i = 0; i < sentMessages.size(); i++) {
            Message msg = sentMessages.get(i);
            report.append("MESSAGE #").append(i + 1).append("\n")
                  .append("-".repeat(30)).append("\n")
                  .append("Message ID: ").append(msg.getMessageID()).append("\n")
                  .append("Message Hash: ").append(msg.getMessageHash()).append("\n")
                  .append("Sender: ").append(msg.getSender()).append("\n")
                  .append("Recipient: ").append(msg.getRecipient()).append("\n")
                  .append("Message Number: ").append(msg.getMessageNumber()).append("\n")
                  .append("Content Length: ").append(msg.getMessageContent().length()).append(" characters\n")
                  .append("Content: ").append(msg.getMessageContent()).append("\n")
                  .append("Status: ").append(msg.getStatus()).append("\n\n");
        }
        
        report.append("=".repeat(50)).append("\n")
              .append("END OF REPORT");
        
        JOptionPane.showMessageDialog(
            null,
            report.toString(),
            "Full Message Report",
            JOptionPane.INFORMATION_MESSAGE
        );
    }
    
    /**
     * Display comprehensive array statistics
     */
    private void displayArrayStatistics() {
        StringBuilder stats = new StringBuilder("ARRAY STATISTICS\n");
        stats.append("=".repeat(40)).append("\n\n");
        
        stats.append("MESSAGE ARRAYS:\n")
             .append("Sent Messages: ").append(sentMessages.size()).append("\n")
             .append("Stored Messages: ").append(storedMessages.size()).append("\n")
             .append("Discarded Messages: ").append(disregardedMessages.size()).append("\n")
             .append("Total Messages: ").append(sentMessages.size() + storedMessages.size() + disregardedMessages.size()).append("\n\n");
        
        stats.append("TRACKING ARRAYS:\n")
             .append("Message IDs: ").append(messageIDs.size()).append("\n")
             .append("Message Hashes: ").append(messageHashes.size()).append("\n\n");
        
        stats.append("MEMORY USAGE:\n")
             .append("Approximate memory per message: ~200 bytes\n")
             .append("Total estimated memory: ~").append((sentMessages.size() + storedMessages.size() + disregardedMessages.size()) * 200)
             .append(" bytes\n\n");
        
        stats.append("STATUS BREAKDOWN:\n");
        if (!sentMessages.isEmpty()) {
            stats.append("- Sent: ").append(String.format("%.1f%%", (double)sentMessages.size() * 100 / (sentMessages.size() + storedMessages.size() + disregardedMessages.size()))).append("\n");
        }
        if (!storedMessages.isEmpty()) {
            stats.append("- Stored: ").append(String.format("%.1f%%", (double)storedMessages.size() * 100 / (sentMessages.size() + storedMessages.size() + disregardedMessages.size()))).append("\n");
        }
        if (!disregardedMessages.isEmpty()) {
            stats.append("- Discarded: ").append(String.format("%.1f%%", (double)disregardedMessages.size() * 100 / (sentMessages.size() + storedMessages.size() + disregardedMessages.size()))).append("\n");
        }
        
        JOptionPane.showMessageDialog(
            null,
            stats.toString(),
            "Array Statistics",
            JOptionPane.INFORMATION_MESSAGE
        );
    }
    
    // Getter methods for the arrays (for testing or external access)
    public static ArrayList<Message> getSentMessages() {
        return new ArrayList<>(sentMessages); // Return copy for safety
    }
    
    public static ArrayList<Message> getDisregardedMessages() {
        return new ArrayList<>(disregardedMessages); // Return copy for safety
    }
    
    public static ArrayList<Message> getStoredMessages() {
        return new ArrayList<>(storedMessages); // Return copy for safety
    }
    
    public static ArrayList<String> getMessageHashes() {
        return new ArrayList<>(messageHashes); // Return copy for safety
    }
    
    public static ArrayList<String> getMessageIDs() {
        return new ArrayList<>(messageIDs); // Return copy for safety
    }
    
    /**
     * Clear all arrays (for testing purposes)
     */
    public static void clearAllArrays() {
        sentMessages.clear();
        disregardedMessages.clear();
        storedMessages.clear();
        messageHashes.clear();
        messageIDs.clear();
    }
}