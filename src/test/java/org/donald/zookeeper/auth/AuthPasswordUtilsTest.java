package org.donald.zookeeper.auth; 

import org.junit.Test; 
import org.junit.Before; 
import org.junit.After; 

/** 
* AuthPasswordUtils Tester. 
* 
* @author Donaldhan
* @since <pre>05/13/2018</pre>
* @version 1.0 
*/
public class AuthPasswordUtilsTest {

    @Before
    public void before() throws Exception {
    }

    @After
    public void after() throws Exception {
    }

    /**
    *
    * Method: generateDigest(String password)
    *
    */
    @Test
    public void testGenerateDigest() throws Exception {
        String passwod = "123456";
        AuthPasswordUtils.generateDigest(passwod);
    }
}
