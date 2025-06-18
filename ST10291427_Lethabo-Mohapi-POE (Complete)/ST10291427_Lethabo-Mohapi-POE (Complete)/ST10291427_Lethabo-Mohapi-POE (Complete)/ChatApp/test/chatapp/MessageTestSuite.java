/*
Module name: Programming 1A
Module code: PROG5121/p/w
Assessment type: Portfolio of Evidence (POE)- PART 2
Student Name: Lethabo
Student surname: Mohapi
Student username: ST10291427@rcconnect.edu.za
Student number: ST10291427

Part 2 Chat Messaging Feature - Unit Test Suite
MessageTestSuite.java - Comprehensive unit tests for the Message class
*/
package chatapp;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

/**
 * Test suite that runs all the message-related tests
 */
@RunWith(Suite.class)
@SuiteClasses({
    MessageLengthTest.class,
    RecipientNumberTest.class,
    MessageHashTest.class,
    MessageIDTest.class,
    MessageSentTest.class
})
public class MessageTestSuite {
    // This class remains empty, it's just a holder for the annotations
}

/**
 * Tests for message length validation
 */
class MessageLengthTest {
    
    @org.junit.Test
    public void testSuccessfulMessageLength() {
        // Test with a valid message (under 250 chars)
        String shortMessage = "This is a valid message under 250 characters.";
        String result = Message.validateMessageLength(shortMessage);
        org.junit.Assert.assertEquals("Message ready to send", result);
    }
    
    @org.junit.Test
    public void testFailedMessageLength() {
        // Test with an invalid message (over 250 chars)
        StringBuilder longMessageBuilder = new StringBuilder();
        for (int i = 0; i < 260; i++) {
            longMessageBuilder.append("a");
        }
        String longMessage = longMessageBuilder.toString();
        
        String result = Message.validateMessageLength(longMessage);
        org.junit.Assert.assertEquals(
            "Message exceeds 250 characters by 10, please reduce size.", 
            result
        );
    }
    
    @org.junit.Test
    public void testExactMessageLength() {
        // Test with a message exactly 250 chars
        StringBuilder exactMessageBuilder = new StringBuilder();
        for (int i = 0; i < 250; i++) {
            exactMessageBuilder.append("a");
        }
        String exactMessage = exactMessageBuilder.toString();
        
        String result = Message.validateMessageLength(exactMessage);
        org.junit.Assert.assertEquals("Message ready to send", result);
    }
}

/**
 * Tests for recipient phone number validation
 */
class RecipientNumberTest {
    
    @org.junit.Test
    public void testValidSouthAfricanNumber() {
        // Test with a valid South African phone number
        String validNumber = "+27718693002";
        String result = Message.validateRecipientNumber(validNumber);
        org.junit.Assert.assertEquals("Cell phone number successfully captured.", result);
    }
    
    @org.junit.Test
    public void testInvalidNumberFormat() {
        // Test with an invalid phone number format
        String invalidNumber = "08575975889";
        String result = Message.validateRecipientNumber(invalidNumber);
        org.junit.Assert.assertEquals(
            "Cell phone number is incorrectly formatted or does not contain an international code. Please correct the number and try again.", 
            result
        );
    }
    
    @org.junit.Test
    public void testInvalidNumberLength() {
        // Test with an invalid length but correct format
        String invalidNumber = "+27123"; // Too short
        String result = Message.validateRecipientNumber(invalidNumber);
        org.junit.Assert.assertEquals(
            "Cell phone number is incorrectly formatted or does not contain an international code. Please correct the number and try again.", 
            result
        );
    }
    
    @org.junit.Test
    public void testInvalidCountryCode() {
        // Test with an invalid country code
        String invalidNumber = "+12123456789"; // US number, not South African
        String result = Message.validateRecipientNumber(invalidNumber);
        org.junit.Assert.assertEquals(
            "Cell phone number is incorrectly formatted or does not contain an international code. Please correct the number and try again.", 
            result
        );
    }
}

/**
 * Tests for message hash creation
 */
class MessageHashTest {
    
    @org.junit.Test
    public void testMessageHashFormat() {
        try {
            // Create a message with known values
            Message message = new Message(
                "kyl_1", 
                "+27718693002", 
                "Hi Mike, can you join us for dinner tonight.", 
                1
            );
            
            // Get the hash
            String hash = message.getMessageHash();
            
            // Extract components from the hash
            String[] parts = hash.split(":");
            
            // Verify format: ID prefix:msgNumber:firstWordlastWord
            org.junit.Assert.assertEquals(3, parts.length);
            org.junit.Assert.assertEquals("1", parts[1]); // Message number
            org.junit.Assert.assertEquals("HITONIGHT", parts[2]); // First+Last word
            
        } catch (Exception e) {
            org.junit.Assert.fail("Test failed: " + e.getMessage());
        }
    }
    
    @org.junit.Test
    public void testSpecificMessageHash() {
        try {
            // Create a message with forced ID using reflection
            Message message = new Message(
                "kyl_1", 
                "+27718693002", 
                "Hi Mike, can you join us for dinner tonight.", 
                0
            );
            
            // Use reflection to set a controlled messageID
            java.lang.reflect.Field field = Message.class.getDeclaredField("messageID");
            field.setAccessible(true);
            field.set(message, "0012345678");
            
            // Regenerate the hash
            field = Message.class.getDeclaredField("messageHash");
            field.setAccessible(true);
            field.set(message, message.createMessageHash());
            
            // Verify hash matches expected value
            org.junit.Assert.assertEquals("00:0:HITONIGHT", message.getMessageHash());
            
        } catch (Exception e) {
            org.junit.Assert.fail("Test failed: " + e.getMessage());
        }
    }
    
    @org.junit.Test
    public void testMultipleMessageHashes() {
        try {
            // Create multiple messages and verify hashes are created correctly
            String[] messages = {
                "Hi Mike, can you join us for dinner tonight.",
                "Just checking in, how are you doing?",
                "Meeting at 3pm tomorrow, don't be late!",
                "Thanks for your help yesterday."
            };
            
            String[] expectedLastWords = {
                "HITONIGHT",
                "HIDOING",
                "MEETINGLATE",
                "THANKSYESTERDAY"
            };
            
            for (int i = 0; i < messages.length; i++) {
                Message message = new Message(
                    "kyl_1", 
                    "+27718693002", 
                    messages[i], 
                    i
                );
                
                // Use reflection to set a controlled messageID
                java.lang.reflect.Field field = Message.class.getDeclaredField("messageID");
                field.setAccessible(true);
                field.set(message, "0012345678");
                
                // Regenerate the hash
                field = Message.class.getDeclaredField("messageHash");
                field.setAccessible(true);
                field.set(message, message.createMessageHash());
                
                // Verify hash format
                String[] parts = message.getMessageHash().split(":");
                org.junit.Assert.assertEquals(3, parts.length);
                org.junit.Assert.assertEquals("00", parts[0]);
                org.junit.Assert.assertEquals(String.valueOf(i), parts[1]);
                org.junit.Assert.assertEquals(expectedLastWords[i], parts[2]);
            }
            
        } catch (Exception e) {
            org.junit.Assert.fail("Test failed: " + e.getMessage());
        }
    }
}

/**
 * Tests for message ID generation
 */
class MessageIDTest {
    
    @org.junit.Test
    public void testMessageIDGeneration() {
        try {
            // Create a message
            Message message = new Message(
                "kyl_1", 
                "+27718693002", 
                "Test message", 
                1
            );
            
            // Verify message ID properties
            String messageID = message.getMessageID();
            org.junit.Assert.assertNotNull("Message ID should not be null", messageID);
            org.junit.Assert.assertEquals("Message ID should be 10 digits", 10, messageID.length());
            
            // Verify it contains only digits
            org.junit.Assert.assertTrue(
                "Message ID should contain only digits", 
                messageID.matches("^\\d+$")
            );
            
        } catch (Exception e) {
            org.junit.Assert.fail("Test failed: " + e.getMessage());
        }
    }
    
    @org.junit.Test
    public void testMessageIDUniqueness() {
        try {
            // Create multiple messages and verify IDs are unique
            int numMessages = 10;
            java.util.Set<String> messageIDs = new java.util.HashSet<>();
            
            for (int i = 0; i < numMessages; i++) {
                Message message = new Message(
                    "kyl_1", 
                    "+27718693002", 
                    "Test message " + i, 
                    i
                );
                
                messageIDs.add(message.getMessageID());
            }
            
            // Verify all IDs are unique
            org.junit.Assert.assertEquals(
                "All message IDs should be unique", 
                numMessages, 
                messageIDs.size()
            );
            
        } catch (Exception e) {
            org.junit.Assert.fail("Test failed: " + e.getMessage());
        }
    }
}

/**
 * Tests for message status handling
 */
class MessageSentTest {
    
    @org.junit.Test
    public void testSendMessage() {
        try {
            // Create a message
            Message message = new Message(
                "kyl_1", 
                "+27718693002", 
                "Test message", 
                1
            );
            
            // Set status to sent
            message.setStatus("Sent");
            org.junit.Assert.assertEquals("Sent", message.getStatus());
            
        } catch (Exception e) {
            org.junit.Assert.fail("Test failed: " + e.getMessage());
        }
    }
    
    @org.junit.Test
    public void testStoreMessage() {
        try {
            // Create a message
            Message message = new Message(
                "kyl_1", 
                "+27718693002", 
                "Test message", 
                1
            );
            
            // Set status to stored
            message.setStatus("Stored");
            org.junit.Assert.assertEquals("Stored", message.getStatus());
            
        } catch (Exception e) {
            org.junit.Assert.fail("Test failed: " + e.getMessage());
        }
    }
    
    @org.junit.Test
    public void testDiscardMessage() {
        try {
            // Create a message
            Message message = new Message(
                "kyl_1", 
                "+27718693002", 
                "Test message", 
                1
            );
            
            // Set status to discarded
            message.setStatus("Discarded");
            org.junit.Assert.assertEquals("Discarded", message.getStatus());
            
        } catch (Exception e) {
            org.junit.Assert.fail("Test failed: " + e.getMessage());
        }
    }
    
    @org.junit.Test
    public void testInvalidStatus() {
        try {
            // Create a message
            Message message = new Message(
                "kyl_1", 
                "+27718693002", 
                "Test message", 
                1
            );
            
            // Try to set an invalid status
            try {
                message.setStatus("Invalid");
                org.junit.Assert.fail("Should throw exception for invalid status");
            } catch (IllegalArgumentException e) {
                // Expected exception
                org.junit.Assert.assertTrue(e.getMessage().contains("Invalid status"));
            }
            
        } catch (Exception e) {
            org.junit.Assert.fail("Test failed: " + e.getMessage());
        }
    }
}