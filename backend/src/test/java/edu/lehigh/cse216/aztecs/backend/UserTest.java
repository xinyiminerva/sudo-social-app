package edu.lehigh.cse216.aztecs.backend;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.util.ArrayList;

/**
 * Unit test for User objects.
 */
public class UserTest extends TestCase {
    /**
     * Create the test case
     * 
     * @param name name of the test case
     */
    public UserTest(String name) {
        super(name);
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite() {
        return new TestSuite(UserTest.class);
    }

    /**
     * Test functionality of single-User serialization to JSON
     */
    public void testSingleSerialization() {
        User myUser = new User(42, "Buckley Ross");
        String json = User.serialize(myUser);
        String expected = "{\"id\":42,\"name\":\"Buckley Ross\"}";

        assertEquals(expected, json);
    }

    /**
     * Test functionality of Authorized User serialization to JSON
     */
    /** public void testAuthorizedSerialization() {
        User myUser = new User(50, "Robbie Rotten", "HA", "HEHE", "we are #1", "glomp");
        String json = User.serialize(myUser);
        String expected = "{\"id\":50,\"name\":\"Robbie Rotten\",\"pwd_hash\":\"HA\",\"salt\":\"HEHE\",\"token\":\"glomp\",\"bio\":\"we are #1\"}";

        assertEquals(expected, json);
    }**/

    /**
     * Test functionality of User-List serialization to JSON
     */
    public void testListSerializarion() {
        ArrayList<User> myUsers = new ArrayList<User>();
        myUsers.add(new User(40, "Buckley Ross"));
        myUsers.add(new User(41, "Brian Chen"));
        myUsers.add(new User(42, "Jason Loew"));
        myUsers.add(new User(43, "Mark Erle"));
        myUsers.add(new User(44, "Michael Spear"));
        String json = User.serialize(myUsers);
        String expected = "[{\"id\":40,\"name\":\"Buckley Ross\"},"
                + "{\"id\":41,\"name\":\"Brian Chen\"},"
                + "{\"id\":42,\"name\":\"Jason Loew\"},"
                + "{\"id\":43,\"name\":\"Mark Erle\"},"
                + "{\"id\":44,\"name\":\"Michael Spear\"}]";

        assertEquals(expected, json);
    }

    /**
     * Test functionality of Empty-List serialization to JSON
     */
    public void testEmptyListSerializarion() {
        ArrayList<User> myUsers = new ArrayList<User>();
        String json = User.serialize(myUsers);
        String expected = "[]";

        assertEquals(expected, json);
    }

    /**
     * Test the functionality of deserializing full User objects from JSON
     */
    public void testFullDeserialization() {
        String json = "{ \"name\": \"Dr. Evil\", \"id\": 202 }";
        User myUser = User.deserialize(json);

        assertEquals(new Integer(202), myUser.id);
        assertEquals("Dr. Evil", myUser.name);
    }

    /**
     * Test the functionality of deserializing a partial User object from JSON
     */
    public void testPartialDeserialization() {
        String json = "{ \"name\": \"John Cena\" }";
        User myUser = User.deserialize(json);

        assertNull(myUser.id);
        assertEquals("John Cena", myUser.name);
    }
}
