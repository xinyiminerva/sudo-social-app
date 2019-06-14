package edu.lehigh.cse216.aztecs.admin;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit tests for simple The Buzz App.
 */
public class UserDataTest extends TestCase {
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public UserDataTest( String testName ) {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite() {
        return new TestSuite( UserDataTest.class );
    }

    /**
     * Test the id
     */
    public void testId() {
        User.UserData myUser = new User.UserData(42, "Somebody", "About Me!");
        assert(myUser.getId() == 42);
    }

    /**
    * Test the username
    */
    public void testUsername() {
        User.UserData myUser = new User.UserData(42, "Somebody", "About Me!");
        assert(myUser.getUsername().equals("Somebody"));
    }

    /**
    * Test the bio
    */
    public void testBio() {
        User.UserData myUser = new User.UserData(42, "Somebody", "About Me!");
        assert(myUser.getBio().equals("About Me!"));
    }
}
