package edu.lehigh.cse216.aztecs.backend;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.util.ArrayList;

/**
 * Unit test for Message objects.
 */
public class MessageTest extends TestCase {
    /**
     * Create the test case
     * @param name name of the test case
     */
    public MessageTest(String name) {
        super(name);
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite() {
        return new TestSuite(MessageTest.class);
    }

    /**
     * Test functionality of single-message serialization to JSON
     */
    public void testSingleSerialization() {
		ArrayList<Integer> attachments = new ArrayList<Integer>();
		attachments.add(42);
		attachments.add(18);
    	Message myMessage = new Message(423, 982, 823, "SAMPLE TEXT", 42, false, 74, attachments);
    	String json = Message.serialize(myMessage);
    	String expected = "{\"id\":423,\"author_id\":982,\"parent_id\":823,\"message\":\"SAMPLE TEXT\",\"likes\":42,\"liked\":false,\"comments\":74,\"attachments\":[42,18]}";

    	assertEquals(expected, json);
    }
    
    /**
     * Test functionality of Message-List serialization to JSON
     */
    public void testListSerializarion() {
		ArrayList<Integer> attachments = new ArrayList<Integer>();
		attachments.add(42);
		attachments.add(18);
    	ArrayList<Message> myMessages = new ArrayList<Message>();
    	myMessages.add(new Message(42, 9824, 82323, "SAMPLE TEXT", 42, false, 74, attachments));
    	myMessages.add(new Message(219, 4337, 43, "ifaawahsfd", 97, false, 98, attachments));
    	myMessages.add(new Message(1243, 987, 406, "This is really cool", 87, true, 56, attachments));
    	myMessages.add(new Message(5, 98, 234, "SELECT * FROM memes", 987, true, 3, attachments));
    	myMessages.add(new Message(912, 159, 847, "INSERT INTO controversial VALUES (?, ?, ?)", -9723, false, 987231, attachments));
    	String json = Message.serialize(myMessages);
    	String expected = "[{\"id\":42,\"author_id\":9824,\"parent_id\":82323,\"message\":\"SAMPLE TEXT\",\"likes\":42,\"liked\":false,\"comments\":74,\"attachments\":[42,18]}," +
    			"{\"id\":219,\"author_id\":4337,\"parent_id\":43,\"message\":\"ifaawahsfd\",\"likes\":97,\"liked\":false,\"comments\":98,\"attachments\":[42,18]}," +
    			"{\"id\":1243,\"author_id\":987,\"parent_id\":406,\"message\":\"This is really cool\",\"likes\":87,\"liked\":true,\"comments\":56,\"attachments\":[42,18]}," +
    			"{\"id\":5,\"author_id\":98,\"parent_id\":234,\"message\":\"SELECT * FROM memes\",\"likes\":987,\"liked\":true,\"comments\":3,\"attachments\":[42,18]}," +
    			"{\"id\":912,\"author_id\":159,\"parent_id\":847,\"message\":\"INSERT INTO controversial VALUES (?, ?, ?)\",\"likes\":-9723,\"liked\":false,\"comments\":987231,\"attachments\":[42,18]}]";
    	
    	assertEquals(expected, json);
    }
    
    /**
     * Test functionality of Empty-List serialization to JSON
     */
    public void testEmptyListSerializarion() {
    	ArrayList<Message> myMessages = new ArrayList<Message>();
    	String json = Message.serialize(myMessages);
    	String expected = "[]";
    	
    	assertEquals(expected, json);
    }
    
    /**
     * Test the functionality of deserializing full Message objects from JSON
     */
    public void testFullDeserialization() {
    	String json = "{\"id\":293874,\"author_id\":2134,\"parent_id\":1092,\"message\":\"... and then everybody clapped\",\"likes\":-15,\"liked\":false,\"comments\":2222}";
    	Message myMessage = Message.deserialize(json);
    	
    	assertEquals(new Integer(293874), myMessage.id);
    	assertEquals(new Integer(2134), myMessage.author_id);
    	assertEquals(new Integer(1092), myMessage.parent_id);
    	assertEquals("... and then everybody clapped", myMessage.message);
    	assertEquals(new Integer(-15), myMessage.likes);
    	assertEquals(new Boolean(false), myMessage.liked);
    	assertEquals(new Integer(2222), myMessage.comments);
    }
    
    /**
     * Test the functionality of deserializing a half Message object from JSON
     */
    public void testHalfDeserialization() {
    	String json = "{\"parent_id\":9211,\"message\":\"According to all known laws of aviation, there is no way a bee should be able to fly ...\"}";
    	Message myMessage = Message.deserialize(json);
    	
    	assertNull(myMessage.id);
    	assertNull(myMessage.author_id);
    	assertEquals(new Integer(9211), myMessage.parent_id);
    	assertEquals("According to all known laws of aviation, there is no way a bee should be able to fly ...", myMessage.message);
    	assertNull(myMessage.likes);
    	assertNull(myMessage.liked);
    	assertNull(myMessage.comments);
    }
    
    /**
     * Test the functionality of deserializing a minimal Like object from JSON
     */
    public void testMinimalDeserialization() {
    	String json = "{\"message\":\"Welcome to the Aperture Science Computer-Aided Enrichment Center ...\"}";
    	Message myMessage = Message.deserialize(json);
    	
    	assertNull(myMessage.id);
    	assertNull(myMessage.author_id);
    	assertNull(myMessage.parent_id);
    	assertEquals("Welcome to the Aperture Science Computer-Aided Enrichment Center ...", myMessage.message);
    	assertNull(myMessage.likes);
    	assertNull(myMessage.liked);
    	assertNull(myMessage.comments);
    }
}

