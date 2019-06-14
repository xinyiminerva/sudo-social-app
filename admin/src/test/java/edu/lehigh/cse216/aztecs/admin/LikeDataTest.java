package edu.lehigh.cse216.aztecs.admin;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit tests for simple The Buzz App.
 */
public class LikeDataTest extends TestCase {
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public LikeDataTest( String testName ) {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite() {
        return new TestSuite( LikeDataTest.class );
    }

    /**
     * Test the status
     */
    public void testStatus() {
        Like.LikesData myLike = new Like.LikesData(true, 42, 55);
        assert(myLike.getLikeStatus() == true);
    }

    /**
     * Test the message id
     */
    public void testMessage() {
        Like.LikesData myLike = new Like.LikesData(true, 42, 55);
        assert(myLike.getMessageId() == 42);
    }

    /**
     * Test the user id
     */
    public void testUser() {
        Like.LikesData myLike = new Like.LikesData(true, 42, 55);
        assert(myLike.getLikerId() == 55);
    }
}
