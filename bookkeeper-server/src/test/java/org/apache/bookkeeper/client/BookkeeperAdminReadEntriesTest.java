package org.apache.bookkeeper.client;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.*;

import static org.junit.Assert.assertEquals;

@RunWith(value= Parameterized.class)
public class BookkeeperAdminReadEntriesTest extends UtilTestClass {

    private long firstEntry;
    private long lastEntry;
    private long ledgerId;
    private BookKeeperAdmin bkAdmin;

    private static final long numEntriesToWrite = 3;
    private Random rng;

    private ArrayList<byte[]> writtenEntries;

    public BookkeeperAdminReadEntriesTest(List<Object> patameters){
        this.firstEntry = (int)patameters.get(0);
        this.lastEntry = (int)patameters.get(1);
        writtenEntries = new ArrayList<>();
        rng = new Random();

    }

    @Before
    public void initTests() throws BKException, InterruptedException, IOException {

        bkAdmin = new BookKeeperAdmin(zkUtil.getZooKeeperConnectString());

        LedgerHandle lh = bkc.createLedger(digestType, ledgerPassword);
        ledgerId = lh.getId();

        //Scrittura di numEntriesToWrite entry
        for (int i = 0; i < numEntriesToWrite; i++) {
            ByteBuffer entry = ByteBuffer.allocate(4);
            entry.putInt(rng.nextInt());
            entry.position(0);

            writtenEntries.add(entry.array());
            lh.addEntry(entry.array());
        }

    }

    @Parameterized.Parameters
    public static Collection<List<Object>> getParameters(){

        List<Object> le = new ArrayList<>();
        List<Object> fe = new ArrayList<>();

        //Minimal Test suite
        le.add(-1);
        fe.add(-1);

        le.add(1);
        fe.add(1);

        le.add(0);
        fe.add(0);

        List<List<Object>> L  = new ArrayList<>();
        L.add(le);
        L.add(fe);

        if(!UtilTestClass.improvedTestSuite){
            return nonMultidimensionalTestCases(L);
        }

        return multidimensionalTestCases(L);
    }

    @Test
    public void readEntriesTest() throws BKException, InterruptedException {

        Iterable<LedgerEntry> readEntries = null;
        try {
            readEntries = bkAdmin.readEntries(ledgerId,firstEntry, lastEntry);
        } catch (Exception e) {
            //Controllo la gestione di eventuali errori dovuti a combinazioni sbagliate tra
            //lastEntry e firstEntry, che devono essere gestite da bookkeeper
            assertEquals("java.lang.IllegalArgumentException", e.getClass().getCanonicalName());
            return;
        }

        for(LedgerEntry entry : readEntries) {

            //prelevo le entry a partire fa first entry, che pò essere maggiore di 0.
            ByteBuffer origbb = ByteBuffer.wrap(writtenEntries.get((int)firstEntry++));
            Integer origEntry = origbb.getInt();

            ByteBuffer result = ByteBuffer.wrap(entry.getEntry());
            Integer retrEntry = result.getInt();
            assertEquals(origEntry, retrEntry);
        }

    }

    //TODO, provare last entry > first entry, ai test migliorati.

}
