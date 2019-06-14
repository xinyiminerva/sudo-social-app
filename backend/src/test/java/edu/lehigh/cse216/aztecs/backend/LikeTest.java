package edu.lehigh.cse216.aztecs.backend;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.util.ArrayList;

/**
 * Unit test for Like objects.
 */
public class LikeTest extends TestCase {
    /**
     * Create the test case
     * @param name name of the test case
     */
    public LikeTest(String name) {
        super(name);
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite() {
        return new TestSuite(LikeTest.class);
    }

    /**
     * Test functionality of single-Like serialization to JSON
     */
    public void testSingleSerialization() {
    	Like myLike = new Like(true, 824, 39);
    	String json = Like.serialize(myLike);
    	String expected = "{\"liked\":true,\"author_id\":824,\"message_id\":39}";

    	assertEquals(expected, json);
    }
    
    /**
     * Test functionality of Like-List serialization to JSON
     */
    public void testListSerializarion() {
    	ArrayList<Like> myLikes = new ArrayList<Like>();
    	myLikes.add(new Like(false, 83, 92));
    	myLikes.add(new Like(true, 39, 83));
    	myLikes.add(new Like(false, 27, 7));
    	myLikes.add(new Like(false, 12, 71));
    	myLikes.add(new Like(true, 239, 982));
    	String json = Like.serialize(myLikes);
    	String expected = "[{\"liked\":false,\"author_id\":83,\"message_id\":92}," +
    			"{\"liked\":true,\"author_id\":39,\"message_id\":83}," +
    			"{\"liked\":false,\"author_id\":27,\"message_id\":7}," +
    			"{\"liked\":false,\"author_id\":12,\"message_id\":71}," +
    			"{\"liked\":true,\"author_id\":239,\"message_id\":982}]";
    	
    	assertEquals(expected, json);
    }
    
    /**
     * Test functionality of Empty-List serialization to JSON
     */
    public void testEmptyListSerializarion() {
    	ArrayList<Like> myLikes = new ArrayList<Like>();
    	String json = Like.serialize(myLikes);
    	String expected = "[]";
    	
    	assertEquals(expected, json);
    }
    
    /**
     * Test the functionality of deserializing full User objects from JSON
     */
    public void testFullDeserialization() {
    	String json = "{ \"liked\": false, \"author_id\": 923, \"message_id\": 219 }";
    	Like myLike = Like.deserialize(json);
    	
    	assertEquals(new Boolean(false), myLike.liked);
    	assertEquals(new Integer(923), myLike.author_id);
    	assertEquals(new Integer(219), myLike.message_id);
    }
    
    /**
     * Test the functionality of deserializing a half Like object from JSON
     */
    public void testHalfDeserialization() {
    	String json = "{ \"liked\": false, \"message_id\": 56 }";
    	Like myLike = Like.deserialize(json);
    	
    	assertNull(myLike.author_id);
    	assertEquals(new Boolean(false), myLike.liked);
    	assertEquals(new Integer(56), myLike.message_id);
    }
    
    /**
     * Test the functionality of deserializing a minimal Like object from JSON
     */
    public void testMinimalDeserialization() {
    	String json = "{ \"liked\": true }";
    	Like myLike = Like.deserialize(json);
    	
    	assertNull(myLike.author_id);
    	assertNull(myLike.message_id);
    	assertEquals(new Boolean(true), myLike.liked);
    }
}
