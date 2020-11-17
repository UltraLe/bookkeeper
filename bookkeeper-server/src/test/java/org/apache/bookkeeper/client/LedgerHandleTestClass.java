package org.apache.bookkeeper.client;

import org.apache.bookkeeper.test.BookKeeperClusterTestCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class LedgerHandleTestClass extends BookKeeperClusterTestCase {

    public static final int numBookies = 3;
    final boolean improvedTestSuite = false;

    static final Logger LOG = LoggerFactory
            .getLogger(BookieWriteLedgerTest.class);
    final BookKeeper.DigestType digestType = BookKeeper.DigestType.CRC32;
    final byte[] ledgerPassword = "pwd".getBytes();

    /*
    Enumeration<LedgerEntry> ls;

    // test related variables
    int numEntriesToWrite = 100;
    int maxInt = Integer.MAX_VALUE;

    Random rng; // Random Number Generator
    ArrayList<byte[]> entries1; // generated entries
    ArrayList<byte[]> entries2; // generated entries
     */

    public LedgerHandleTestClass() {
        super(numBookies);
    }
}
