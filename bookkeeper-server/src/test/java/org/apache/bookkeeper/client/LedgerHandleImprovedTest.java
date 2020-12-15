package org.apache.bookkeeper.client;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.nio.ByteBuffer;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

import static org.junit.Assert.assertEquals;

@RunWith(value= Parameterized.class)
public class LedgerHandleImprovedTest extends UtilTestClass {

    public static int seqNum = 0;
    private byte[] entryToAdd;
    private int offset;
    private int length;

    @Parameterized.Parameters
    public static Collection<List<Object>> getParameters(){
        List<Object> dataBorderValues = new ArrayList<>();
        List<Object> offsetBorderValues = new ArrayList<>();
        List<Object> lengthBorderValues = new ArrayList<>();

        /*
            Covering this condition
            if (offset < 0 || length < 0 || (offset + length) > data.length) {
        */

        dataBorderValues.add("someData".getBytes());
        offsetBorderValues.add(1);
        lengthBorderValues.add(1);

        dataBorderValues.add("a".getBytes());
        offsetBorderValues.add(3);
        lengthBorderValues.add(3);


        dataBorderValues.add(null);
        offsetBorderValues.add(-1);
        lengthBorderValues.add(-1);

        List<List<Object>> L  = new ArrayList<>();
        L.add(dataBorderValues);
        L.add(offsetBorderValues);
        L.add(lengthBorderValues);

        return nonMultidimensionalTestCases(L);
    }

    public LedgerHandleImprovedTest(List<Object> parameters){

        this.entryToAdd = (byte[])parameters.get(0);
        this.offset = (int)parameters.get(1);
        this.length = (int)parameters.get(2);

        seqNum++;
    }

    @Test
    public void entriesAddedSequentiallyTest() throws BKException, InterruptedException {
        if(!UtilTestClass.improvedTestSuite || seqNum > 1){
            return;
        }

        int numEntriesToWrite = 10;

        LedgerHandle lh = bkc.createLedger(digestType, ledgerPassword);

        //Scrittura delle entry: (1, 2, 3, ..., numEntriesToWrite).
        for (int i = 0; i < numEntriesToWrite; i++) {
            ByteBuffer entry = ByteBuffer.allocate(4);
            entry.putInt(i);
            entry.position(0);
            lh.addEntry(entry.array());
        }

        Enumeration<LedgerEntry> readEntries = lh.readEntries(0, numEntriesToWrite-1);

        Integer expectedEntryValue = 0;
        while (readEntries.hasMoreElements()) {
            ByteBuffer result = ByteBuffer.wrap(readEntries.nextElement().getEntry());
            Integer retrEntry = result.getInt();
            assertEquals(expectedEntryValue, retrEntry);
            expectedEntryValue++;
        }

    }

    @Test
    public void entriesAddedSequentiallyTest2() throws BKException, InterruptedException {
        if(!UtilTestClass.improvedTestSuite || seqNum > 1){
            return;
        }

        Random rng = new Random(123456);

        int numEntriesToWrite = 10;

        LedgerHandle lh = bkc.createLedger(digestType, ledgerPassword);
        ArrayList<byte[]> written = new ArrayList<>();

        //Scrittura delle entry: (1, 2, 3, ..., numEntriesToWrite).
        for (int i = 0; i < numEntriesToWrite; i++) {
            byte[] entry = new byte[55];
            rng.nextBytes(entry);
            int rndOffset = ThreadLocalRandom.current().nextInt(0, 20);
            int rndLength = ThreadLocalRandom.current().nextInt(0, 50-rndOffset);
            written.add(Arrays.copyOfRange(entry, rndOffset, rndLength+rndOffset));
            lh.addEntry(entry, rndOffset, rndLength);
        }

        Enumeration<LedgerEntry> readEntries = lh.readEntries(0, numEntriesToWrite-1);

        int i = 0;
        while (readEntries.hasMoreElements()) {
            Assert.assertEquals(Arrays.toString(written.get(i)), Arrays.toString(readEntries.nextElement().getEntry()));
            i++;
        }

    }


    @Test
    public void addEntryCCTest(){
        if(!UtilTestClass.improvedTestSuite){
            return;
        }

        long entryId;
        try {
            LedgerHandle lh = bkc.createLedger(3, 1, digestType, ledgerPassword);

            entryId = lh.addEntry(this.entryToAdd, this.offset, this.length);

            Assert.assertTrue(entryId >= 0);
        } catch (Exception e) {
            //NULL parameter does not pass
            //Here if handling null entry add
            //We do not want that a null pointer exception is raised
            Assert.assertNotEquals("java.lang.NullPointerException", e.getClass().getCanonicalName());
        }
    }

    @Test
    public void readEntriesImproved() throws BKException, InterruptedException {
        if(!UtilTestClass.improvedTestSuite || seqNum > 1){
            return;
        }

        LedgerHandle lh = bkc.createLedger(digestType, ledgerPassword);

        for (int i = 0; i < 3; i++) {
            byte[] entry = "dummyData".getBytes();
            lh.addEntry(entry, 0, entry.length);
        }

        /*
            Covering 'if (lastEntry > lastAddConfirmed) {'
         */
        try {
            lh.readEntries(0, 4);
        }catch (Exception e){
            //reading with last entry greather than lastAddConfirmed will throw this exception
            Assert.assertEquals("org.apache.bookkeeper.client.BKException.BKReadException", e.getClass().getCanonicalName());
        }

    }

}
