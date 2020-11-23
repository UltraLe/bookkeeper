package org.apache.bookkeeper.client;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.nio.ByteBuffer;
import java.util.*;

import static org.junit.Assert.assertEquals;

@RunWith(value= Parameterized.class)
public class LedgerHandleReadEntryTest extends UtilTestClass {

    private long firstEntry;
    private long lastEntry;
    private static final long numEntriesToWrite = 3;
    private Random rng;

    private ArrayList<byte[]> writtenEntries;

    public LedgerHandleReadEntryTest(List<Object> patameters){

        this.firstEntry = (int)patameters.get(0);
        this.lastEntry = (int)patameters.get(1);

        writtenEntries = new ArrayList<>();
        rng = new Random();
    }


    @Parameterized.Parameters
    public static Collection<List<Object>> getParameters(){

        List<Object> borderValues1 = new ArrayList<>();
        List<Object> borderValues2 = new ArrayList<>();

        borderValues1.add(-1);
        borderValues1.add(0);
        borderValues1.add(1);

        borderValues2.add(-1);
        borderValues2.add(0);
        borderValues2.add(1);

        List<List<Object>> L  = new ArrayList<>();
        L.add(borderValues1);
        L.add(borderValues2);

        return multidimensionalTestCases(L);
    }

    @Test
    public void readEntriesTest() throws BKException, InterruptedException {

        LedgerHandle lh = bkc.createLedger(digestType, ledgerPassword);

        //Scrittura di numEntriesToWrite entry
        for (int i = 0; i < numEntriesToWrite; i++) {
            ByteBuffer entry = ByteBuffer.allocate(4);
            entry.putInt(rng.nextInt());
            entry.position(0);

            writtenEntries.add(entry.array());
            lh.addEntry(entry.array());
        }

        Enumeration<LedgerEntry> readEntries = null;
        try {
            readEntries = lh.readEntries(firstEntry, lastEntry);
        } catch (Exception e) {
            //Controllo la gestione di eventuali errori dovuti a combinazioni sbagliate tra
            //lastEntry e firstEntry, che devono essere gestite da bookkeeper
            assertEquals("org.apache.bookkeeper.client.BKException.BKIncorrectParameterException", e.getClass().getCanonicalName());
            return;
        }

        while (readEntries.hasMoreElements()) {
            //prelevo le entry a partire fa first entry, che pò essere maggiore di 0.
            ByteBuffer origbb = ByteBuffer.wrap(writtenEntries.get((int)firstEntry++));
            Integer origEntry = origbb.getInt();

            ByteBuffer result = ByteBuffer.wrap(readEntries.nextElement().getEntry());
            Integer retrEntry = result.getInt();
            assertEquals(origEntry, retrEntry);
        }

    }

    @Test
    public void readLastEntryTest() throws BKException, InterruptedException {

        LedgerHandle lh = bkc.createLedger(digestType, ledgerPassword);

        //Scrittura di numEntriesToWrite entry
        for (int i = 0; i < numEntriesToWrite; i++) {
            ByteBuffer entry = ByteBuffer.allocate(4);
            entry.putInt(rng.nextInt());
            entry.position(0);

            writtenEntries.add(entry.array());
            lh.addEntry(entry.array());
        }

        LedgerEntry lastEntry = null;
        try {
            lastEntry = lh.readLastEntry();
        } catch (Exception e) {
            //Controllo la gestione di eventuali errori dovuti a combinazioni sbagliate tra
            //lastEntry e firstEntry, che devono essere gestite da bookkeeper
            assertEquals("org.apache.bookkeeper.client.BKException.BKIncorrectParameterException", e.getClass().getCanonicalName());
            return;
        }

        //prelevo l'ultima entry aggiunta
        ByteBuffer origbb = ByteBuffer.wrap(writtenEntries.get((int)this.lastEntry));
        Integer origEntry = origbb.getInt();

        ByteBuffer result = ByteBuffer.wrap(lastEntry.getEntry());
        Integer retrEntry = result.getInt();
        assertEquals(origEntry, retrEntry);
    }

}
