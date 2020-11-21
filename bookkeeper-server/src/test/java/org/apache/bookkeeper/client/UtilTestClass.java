package org.apache.bookkeeper.client;

import org.apache.bookkeeper.test.BookKeeperClusterTestCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public abstract class UtilTestClass extends BookKeeperClusterTestCase {

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

    public UtilTestClass() {
        super(numBookies);
    }

    public static List<List<Object>> multidimensionalTestCases(List<List<Object>> inputLists) {
        List<List<Object>> cartesianProducts = new ArrayList<>();
        if (inputLists != null && inputLists.size() > 0) {
            // separating the list at 0th index
            List<Object> initialList = inputLists.get(0);
            // recursive call
            List<List<Object>> remainingLists = multidimensionalTestCases(inputLists.subList(1, inputLists.size()));
            // calculating the cartesian product
            initialList.forEach(element -> {
                remainingLists.forEach(remainingList -> {
                    ArrayList<Object> cartesianProduct = new ArrayList<>();
                    cartesianProduct.add(element);
                    cartesianProduct.addAll(remainingList);
                    cartesianProducts.add(cartesianProduct);
                });
            });
        } else {
            // Base Condition for Recursion (returning empty List as only element)
            cartesianProducts.add(new ArrayList<>());
        }
        return cartesianProducts;
    }
}
