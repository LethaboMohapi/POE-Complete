/*
Module name: Programming 1A
Module code: PROG5121/p/w
Assessment type: Portfolio of Evidence (POE)- PART 2
Student Name: Lethabo
Student surname: Mohapi
Student username: ST10291427@rcconnect.edu.za
Student number: ST10291427

Part 2 Chat Messaging Feature - Enhanced Unit Tests
MessageTest.java - Unit tests for the Message class functionality
*/
package chatapp;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.After;
import javax.swing.JOptionPane;

public class MessageTest {
    
    // Mock objects for testing
    private Message message1;
    private Message message2;

    @org.junit.BeforeClass
    public static void setUpClass() throws Exception {
    }

    @org.junit.AfterClass
    public static void tearDownClass() throws Exception {
    }

    @org.junit.Before
    public void setUp() throws Exception {
    }

    @org.junit.After
    public void tearDown() throws Exception {
    }
    
    @Before
    public void setUp() {
        // Create test messages as per test data
        try {
            message1 = new Message(
                "kyl_1", 
                "+27718693002", 
                "Hi Mike, can you join us for dinner tonight.", 
                1
            );
            
            // For message2, we'll create it but expect validation to fail for the phone number
            // This test will be in a separate method
        } catch (IllegalArgumentException e) {
            fail("Exception during setup: " + e.getMessage());
        }
    }
    
    @After
    public void tearDown() {
        // Clean up if needed
    }
    
    @Test
    public void testCheckMessageID() {
        // Test that message ID is not more than 10 characters
        assertTrue("Message ID should be valid", message1.checkMessageID());
        
        // Mock a message with ID > 10 chars for failure case
        Message mockMessage = message1;
        try {
            // Use reflection to set an invalid messageID
            java.lang.reflect.Field field = Message.class.getDeclaredField("messageID");
            field.setAccessible(true);
            field.set(mockMessage, "12345678901"); // 11 characters
            
            assertFalse("Should fail with message ID > 10 chars", mockMessage.checkMessageID());
        } catch (Exception e) {
            fail("Reflection failure: " + e.getMessage());
        }
    }
    
    @Test
    public void testCheckRecipientCell() {
        // Test valid South African cell number
        assertEquals("Valid SA number should return 1", 1, message1.checkRecipientCell());
        
        // Test invalid number
        try {
            Message invalidMessage = new Message(
                "kyl_1", 
                "08575975889", // Invalid format - missing +27
                "Test message", 
                2
            );
            fail("Should have thrown exception for invalid phone number");
        } catch (IllegalArgumentException e) {
            // Expected exception
            assertTrue(e.getMessage().contains("Recipient phone number"));
        }
    }
    
    @Test
    public void testCreateMessageHash() {
        // Test message hash creation for first test message
        // Expected format: First two numbers of messageID, colon, messageNumber, colon, first and last words
        
        // Get first two chars of the ID
        String idPrefix = message1.getMessageID().substring(0, 2);
        // Expected hash pattern
        String expectedPattern = idPrefix + ":1:HITONIGHT";
        
        assertEquals("Message hash should match expected pattern", 
                    expectedPattern, 
                    message1.getMessageHash());
    }
    
    @Test
    public void testMessageLengthValidation() {
        // Test success case - message under 250 chars
        String shortMessage = "This is a short test message.";
        assertEquals("Should indicate message is ready", 
                    "Message ready to send", 
                    Message.validateMessageLength(shortMessage));
        
        // Test failure case - message over 250 chars
        StringBuilder longMessageBuilder = new StringBuilder();
        for (int i = 0; i < 260; i++) {
            longMessageBuilder.append("a");
        }
        String longMessage = longMessageBuilder.toString();
        
        assertEquals("Should indicate message exceeds limit by 10", 
                    "Message exceeds 250 characters by 10, please reduce size.", 
                    Message.validateMessageLength(longMessage));
    }
    
    @Test
    public void testRecipientNumberValidation() {
        // Test success case - valid South African number
        String validNumber = "+27718693002";
        assertEquals("Should confirm valid cell number", 
                    "Cell phone number successfully captured.", 
                    Message.validateRecipientNumber(validNumber));
        
        // Test failure case - invalid format
        String invalidNumber = "08575975889";
        assertEquals("Should reject invalid cell number", 
                    "Cell phone number is incorrectly formatted or does not contain an international code. Please correct the number and try again.", 
                    Message.validateRecipientNumber(invalidNumber));
    }
    
    @Test
    public void testMessageCreation() {
        // Verify message ID was created
        assertNotNull("Message ID should be generated", message1.getMessageID());
        assertEquals("Message ID should be 10 digits", 10, message1.getMessageID().length());
        
        // Verify other properties
        assertEquals("Message number should be 1", 1, message1.getMessageNumber());
        assertEquals("Recipient should match", "+27718693002", message1.getRecipient());
        assertEquals("Content should match", "Hi Mike, can you join us for dinner tonight.", message1.getMessageContent());
    }
    
    @Test
    public void testMessageStatusHandling() {
        // Test setting various statuses
        message1.setStatus("Sent");
        assertEquals("Status should be Sent", "Sent", message1.getStatus());
        
        message1.setStatus("Stored");
        assertEquals("Status should be Stored", "Stored", message1.getStatus());
        
        message1.setStatus("Discarded");
        assertEquals("Status should be Discarded", "Discarded", message1.getStatus());
        
        // Test invalid status
        try {
            message1.setStatus("Invalid");
            fail("Should throw exception for invalid status");
        } catch (IllegalArgumentException e) {
            // Expected exception
            assertTrue(e.getMessage().contains("Invalid status"));
        }
    }
    
    /**
     * Test the specific message hash for Test Message 1
     * Note: This test assumes we can control the messageID for testing purposes
     */
    @Test
    public void testSpecificMessageHash() {
        // Create a test message with controlled ID for deterministic hash testing
        try {
            Message controlledMessage = new Message(
                "kyl_1", 
                "+27718693002", 
                "Hi Mike, can you join us for dinner tonight.", 
                0  // Message number set to 0 for the test
            );
            
            // Use reflection to set a controlled messageID
            java.lang.reflect.Field field = Message.class.getDeclaredField("messageID");
            field.setAccessible(true);
            field.set(controlledMessage, "0012345678");
            
            // Regenerate the hash with our controlled values
            field = Message.class.getDeclaredField("messageHash");
            field.setAccessible(true);
            field.set(controlledMessage, controlledMessage.createMessageHash());
            
            // Now test that the hash is as expected: "00:0:HITONIGHT"
            assertEquals("Hash should match expected test value", 
                       "00:0:HITONIGHT", 
                       controlledMessage.getMessageHash());
            
        } catch (Exception e) {
            fail("Test failed: " + e.getMessage());
        }
    }

    /**
     * Test of sentMessage method, of class Message.
     */
    @org.junit.Test
    public void testSentMessage() {
        System.out.println("sentMessage");
        Message instance = null;
        String expResult = "";
        String result = instance.sentMessage();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of printMessages method, of class Message.
     */
    @org.junit.Test
    public void testPrintMessages() {
        System.out.println("printMessages");
        String expResult = "";
        String result = Message.printMessages();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of returnTotalMessages method, of class Message.
     */
    @org.junit.Test
    public void testReturnTotalMessages() {
        System.out.println("returnTotalMessages");
        int expResult = 0;
        int result = Message.returnTotalMessages();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of storeMessage method, of class Message.
     */
    @org.junit.Test
    public void testStoreMessage() {
        System.out.println("storeMessage");
        Message instance = null;
        instance.storeMessage();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of validateMessageLength method, of class Message.
     */
    @org.junit.Test
    public void testValidateMessageLength() {
        System.out.println("validateMessageLength");
        String content = "";
        String expResult = "";
        String result = Message.validateMessageLength(content);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of validateRecipientNumber method, of class Message.
     */
    @org.junit.Test
    public void testValidateRecipientNumber() {
        System.out.println("validateRecipientNumber");
        String phoneNumber = "";
        String expResult = "";
        String result = Message.validateRecipientNumber(phoneNumber);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getMessageID method, of class Message.
     */
    @org.junit.Test
    public void testGetMessageID() {
        System.out.println("getMessageID");
        Message instance = null;
        String expResult = "";
        String result = instance.getMessageID();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getMessageNumber method, of class Message.
     */
    @org.junit.Test
    public void testGetMessageNumber() {
        System.out.println("getMessageNumber");
        Message instance = null;
        int expResult = 0;
        int result = instance.getMessageNumber();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getRecipient method, of class Message.
     */
    @org.junit.Test
    public void testGetRecipient() {
        System.out.println("getRecipient");
        Message instance = null;
        String expResult = "";
        String result = instance.getRecipient();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getMessageContent method, of class Message.
     */
    @org.junit.Test
    public void testGetMessageContent() {
        System.out.println("getMessageContent");
        Message instance = null;
        String expResult = "";
        String result = instance.getMessageContent();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getMessageHash method, of class Message.
     */
    @org.junit.Test
    public void testGetMessageHash() {
        System.out.println("getMessageHash");
        Message instance = null;
        String expResult = "";
        String result = instance.getMessageHash();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getSender method, of class Message.
     */
    @org.junit.Test
    public void testGetSender() {
        System.out.println("getSender");
        Message instance = null;
        String expResult = "";
        String result = instance.getSender();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getStatus method, of class Message.
     */
    @org.junit.Test
    public void testGetStatus() {
        System.out.println("getStatus");
        Message instance = null;
        String expResult = "";
        String result = instance.getStatus();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setStatus method, of class Message.
     */
    @org.junit.Test
    public void testSetStatus() {
        System.out.println("setStatus");
        String status = "";
        Message instance = null;
        instance.setStatus(status);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of toString method, of class Message.
     */
    @org.junit.Test
    public void testToString() {
        System.out.println("toString");
        Message instance = null;
        String expResult = "";
        String result = instance.toString();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
}