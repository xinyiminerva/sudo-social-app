package edu.lehigh.cse216.aztecs.admin;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit tests for simple The Buzz App.
 */
public class ParentDataTest extends TestCase {
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public ParentDataTest( String testName ) {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite() {
        return new TestSuite( ParentDataTest.class );
    }

    /**
     * Test the id
     */
    public void testId() {
        Parent.ParentData myParent = new Parent.ParentData(42, 55);
        assert(myParent.getParentId() == 42);
    }

    /**
    * Test the child id
    */
    public void testMessage() {
      Parent.ParentData myParent = new Parent.ParentData(42, 55);
      assert(myParent.getMessageId() == 55);
    }
}
