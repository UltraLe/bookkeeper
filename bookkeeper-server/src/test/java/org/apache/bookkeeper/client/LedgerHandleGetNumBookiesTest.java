package org.apache.bookkeeper.client;

import org.apache.bookkeeper.net.BookieId;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

public class LedgerHandleGetNumBookiesTest extends UtilTestClass {


    /*
        Creating a cluster of different num of bookies, and
        checking if the value returned is
        the count of unique bookies that own part of 'this' ledger
        by going over all the fragments of the ledger.
     */
    @Test
    public void getNumBookiesTest() throws Exception{

        long numBookies;

        //3 Bookie
        LOG.info("3 bookie");
        LedgerHandle lh = bkc.createLedger(3, 1, digestType, ledgerPassword);
        numBookies = lh.getNumBookies();
        Assert.assertEquals(3, numBookies);


        LOG.info("Still 3 Bookies");
        stopAllBookies();
        numBookies = lh.getNumBookies();
        Assert.assertEquals(3, numBookies);

        //restarting bookies
        LOG.info("Restarting bookies");
        startAllBookies();
        numBookies = lh.getNumBookies();
        Assert.assertEquals(3, numBookies);

        if(improvedTestSuite){
            getNumBookieImproved();
        }

    }

    public void getNumBookieImproved() throws Exception {

        long numBookies;
        LedgerHandle lh = bkc.createLedger(3, 1, digestType, ledgerPassword);
        List<BookieId> ensemble = lh.getLedgerMetadata()
                .getAllEnsembles().entrySet().iterator().next().getValue();

        //After Deleating a bookie
        LOG.info("After Deleating a bookie");
        Assert.assertNotNull(killBookie(ensemble.get(0)));
        numBookies = lh.getNumBookies();
        Assert.assertEquals(2, numBookies);

        //After Adding a bookie
        LOG.info("After Adding a bookie");
        startNewBookie();
        numBookies = lh.getNumBookies();
        Assert.assertEquals(3, numBookies);

        //2 consecutive delete and 2 consecutive add
        LOG.info("2 consecutive delete and 2 consecutive add");
        Assert.assertNotNull(killBookie(ensemble.get(0)));
        Assert.assertNotNull(killBookie(ensemble.get(1)));
        startNewBookie();
        startNewBookie();
        Assert.assertEquals(3, numBookies);

    }

}
