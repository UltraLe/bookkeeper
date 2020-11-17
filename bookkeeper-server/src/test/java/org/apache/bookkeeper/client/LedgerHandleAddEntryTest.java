package org.apache.bookkeeper.client;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;

@RunWith(value= Parameterized.class)
public class LedgerHandleAddEntryTest extends LedgerHandleTestClass {

    private byte[] entryToAdd;
    ArrayList<byte[]> entries = new ArrayList<byte[]>();

    @Parameterized.Parameters
    public static Collection<byte[]> getParameters(){
        Random r = new Random(1234567);
        //inserting random bytes, at group pf 30
        byte[] data = new byte[30];

        //param1 = random byte array
        r.nextBytes(data);
        ArrayList<byte[]> parameters = new ArrayList<>();
        parameters.add(data);

        //param2 = null
        parameters.add(null);

        return parameters;
    }

    public LedgerHandleAddEntryTest(byte[] data){
        this.entryToAdd = data;
    }

    @Test
    public void addEntryTest() {

        long entryId;

        try {
            LedgerHandle lh = bkc.createLedger(3, 1, digestType, ledgerPassword);
            entryId = lh.addEntry(this.entryToAdd);

            Assert.assertTrue(entryId >= 0);
        } catch (Exception e) {
            //NULL parameter does not pass
            //Here if handling null entry add
            //We do not want that a null pointer exception is raised
            Assert.assertNotEquals(e.getClass().getCanonicalName(), "java.lang.NullPointerException");
        }

    }
}
