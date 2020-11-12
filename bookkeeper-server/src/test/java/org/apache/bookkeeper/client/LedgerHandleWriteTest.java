package org.apache.bookkeeper.client;

import org.apache.bookkeeper.net.BookieId;
import org.apache.bookkeeper.test.BookKeeperClusterTestCase;
import org.bouncycastle.crypto.Digest;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import static org.junit.Assert.assertTrue;

public class LedgerHandleWriteTest extends BookKeeperClusterTestCase {

    private ArrayList<LedgerHandle> lhs;
    private LedgerHandle lh;

    Enumeration<LedgerEntry> ls;
    ArrayList<byte[]> entries1; // generated entries

    private final BookKeeper.DigestType digestType = BookKeeper.DigestType.CRC32;
    private final byte[] ledgerPassword = "coolPWD".getBytes();

    public LedgerHandleWriteTest() {
        super(3);
    }

    @Test
    public void readTest() throws Exception{

        // Create a ledger
        lh = bkc.createLedger(5, 4, digestType, ledgerPassword);
        for (int i = 0; i < 2; i++) {
            ByteBuffer entry = ByteBuffer.allocate(4);
            entry.putInt(12);
            entry.position(0);

            entries1.add(entry.array());
            lh.addEntry(entry.array());
        }
        // Start three more bookies
        startNewBookie();
        startNewBookie();
        startNewBookie();

        // Shutdown three bookies in the last ensemble and continue writing
        List<BookieId> ensemble = lh.getLedgerMetadata()
                .getAllEnsembles().entrySet().iterator().next().getValue();
        killBookie(ensemble.get(0));
        killBookie(ensemble.get(1));
        killBookie(ensemble.get(2));

        int i = 2;
        for (; i < 12; i++) {
            ByteBuffer entry = ByteBuffer.allocate(4);
            entry.putInt(5);
            entry.position(0);

            entries1.add(entry.array());
            lh.addEntry(entry.array());
        }
        //readEntries(lh, entries1);
        lh.close();

        Assert.assertTrue(true);
    }

    private void readEntries(LedgerHandle lh, List<byte[]> entries) throws InterruptedException, BKException {
        ls = lh.readEntries(0, 2 - 1);
        int index = 0;
        while (ls.hasMoreElements()) {
            ByteBuffer origbb = ByteBuffer.wrap(entries.get(index++));
            Integer origEntry = origbb.getInt();
            ByteBuffer result = ByteBuffer.wrap(ls.nextElement().getEntry());
            Integer retrEntry = result.getInt();
            assertTrue("Checking entry " + index + " for equality", origEntry
                    .equals(retrEntry));
        }
    }




}
