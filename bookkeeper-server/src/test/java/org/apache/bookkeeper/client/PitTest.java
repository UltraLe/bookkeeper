package org.apache.bookkeeper.client;

import org.junit.Assert;
import org.junit.Test;

public class PitTest {

    //LOCALMENTE
    //Make only one test, and buld only its module ! FINALLY !!
    //mvn -pl bookkeeper-server -am verify -Dtest=PitTest test -DfailIfNoTests=false

    //ONLINE
    //

    @Test
    public void simpleTest(){

        DeleteMePlease dmp1 = new DeleteMePlease(false);
        DeleteMePlease dmp2 = new DeleteMePlease(true);


        Assert.assertEquals(dmp1.getSomething(), "notA");
        Assert.assertEquals(dmp2.getSomething(), "A");
    }
}
