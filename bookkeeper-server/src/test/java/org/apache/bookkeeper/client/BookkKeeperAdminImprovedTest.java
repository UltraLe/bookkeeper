package org.apache.bookkeeper.client;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Random;

import static org.junit.Assert.assertEquals;

public class BookkKeeperAdminImprovedTest extends UtilTestClass {

    private long ledgerId;
    private BookKeeperAdmin bkAdmin;

    private static final long numEntriesToWrite = 3;
    private Random rng;

    private ArrayList<byte[]> writtenEntries;

    @Before
    public void initTests() throws BKException, InterruptedException, IOException {

        bkAdmin = new BookKeeperAdmin(zkUtil.getZooKeeperConnectString());
        rng = new Random(123456);
        LedgerHandle lh = bkc.createLedger(digestType, ledgerPassword);
        ledgerId = lh.getId();
        writtenEntries = new ArrayList<>();

        //Scrittura di numEntriesToWrite entry
        for (int i = 0; i < numEntriesToWrite; i++) {
            ByteBuffer entry = ByteBuffer.allocate(4);
            entry.putInt(rng.nextInt());
            entry.position(0);

            writtenEntries.add(entry.array());
            lh.addEntry(entry.array());
        }

    }

    @Test
    public void readEntriesExtendedTest() throws BKException, InterruptedException {

        if(!UtilTestClass.improvedTestSuite){
            return;
        }

        int firstEntry = 0;
        int[] lastEntries = {-1,-2};

        for(int lastEntry: lastEntries){

            Iterable<LedgerEntry> readEntries = bkAdmin.readEntries(ledgerId,firstEntry, lastEntry);

            int i = firstEntry;
            for(LedgerEntry entry : readEntries) {

                //prelevo le entry a partire fa first entry, che pò essere maggiore di 0.
                ByteBuffer origbb = ByteBuffer.wrap(writtenEntries.get(i++));
                Integer origEntry = origbb.getInt();

                ByteBuffer result = ByteBuffer.wrap(entry.getEntry());
                Integer retrEntry = result.getInt();
                assertEquals(origEntry, retrEntry);

            }

            //se last entry è negativo ma diverso da -1, da specifiche non dovrei leggere nulla
            if(lastEntry < -1){
                Assert.assertEquals(0, i);
            }else{
                //sono state lette tutte le entry con last entry a -1 ?
                Assert.assertEquals( numEntriesToWrite, i);
            }
        }
    }

    @Test
    public void readEntriesLedgerIdNegativeTest() throws InterruptedException, BKException, IOException {

        bkAdmin = new BookKeeperAdmin(zkUtil.getZooKeeperConnectString());

        LedgerHandle lh = bkc.createLedger(digestType, ledgerPassword);
        int negativeLedgerId = -1;
        lh.addEntry("someData".getBytes());

        try {
            bkAdmin.readEntries(negativeLedgerId,0, 1);
        } catch (Exception e) {
            assertEquals("java.lang.IllegalArgumentException", e.getClass().getCanonicalName());
        }

    }

}
