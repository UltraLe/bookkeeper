package org.apache.bookkeeper.client;

import org.apache.bookkeeper.net.BookieId;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.Collection;

public class BookkeeperAdminGetAvBoTest extends UtilTestClass {

    private Collection<BookieId> avaliableBookie;
    private BookKeeperAdmin bkAdmin;

    @Before
    public void initUtilClasses() throws InterruptedException, BKException, IOException {
        bkAdmin = new BookKeeperAdmin(zkUtil.getZooKeeperConnectString());
        bkc.createLedger(3, 1, digestType, ledgerPassword);
    }

    /*
        Creating a cluster of different num of bookies, and
        checking if the value returned is
        the count of unique bookies that own part of 'this' ledger
        by going over all the fragments of the ledger.
     */
    @Test
    public void getAvailabeBookiesTest0() throws Exception{

        //3 Bookie
        LOG.info("3 bookie");
        avaliableBookie = bkAdmin.getAvailableBookies();
        Assert.assertEquals(3, avaliableBookie.size());

        /*
            Row 481, BookkeeperClusterTestCase:

            Kill a bookie by its socket address. Also, stops the autorecovery process
            for the corresponding bookie server, if isAutoRecoveryEnabled is true.

            This mean that if a bookie is killed it does no more own the ledger.
         */

        //Zero Bookies
        LOG.info("Zero Bookies");
        stopAllBookies();
        avaliableBookie = bkAdmin.getAvailableBookies();
        Assert.assertEquals(0, avaliableBookie.size());

        //restarting bookies
        LOG.info("Restarting bookies");
        startNewBookie();
        startNewBookie();
        startNewBookie();
        avaliableBookie = bkAdmin.getAvailableBookies();
        Assert.assertEquals(3, avaliableBookie.size());

        if(improvedTestSuite){
            getAvailabeBookies1();
            getAvailabeBookies2();
        }

    }

    public void getAvailabeBookies1() throws Exception {

        //After Deleating a bookie
        LOG.info("After Deleating a bookie");
        Assert.assertNotNull(killBookie(0));
        avaliableBookie = bkAdmin.getAvailableBookies();
        Assert.assertEquals(2, avaliableBookie.size());

        //After Adding a bookie
        LOG.info("After Adding a bookie");
        startNewBookie();
        avaliableBookie = bkAdmin.getAvailableBookies();
        Assert.assertEquals(3, avaliableBookie.size());
    }

    public void getAvailabeBookies2() throws Exception {

        //2 consecutive delete and 2 consecutive add
        LOG.info("2 consecutive delete and 2 consecutive add");
        Assert.assertNotNull(killBookie(0));
        Assert.assertNotNull(killBookie(1));
        startNewBookie();
        startNewBookie();
        avaliableBookie = bkAdmin.getAvailableBookies();
        Assert.assertEquals(3, avaliableBookie.size());
    }

}
