package org.apache.bookkeeper.client;

import org.apache.bookkeeper.test.BookKeeperClusterTestCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public abstract class UtilTestClass extends BookKeeperClusterTestCase {

    public static final int numBookies = 3;
    public static final boolean improvedTestSuite = true;

    static final Logger LOG = LoggerFactory
            .getLogger(BookieWriteLedgerTest.class);
    final BookKeeper.DigestType digestType = BookKeeper.DigestType.CRC32;
    final byte[] ledgerPassword = "pwd".getBytes();


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

    /*
        Method used to define the initial test suite, in order to not overload it.
        Given a list of objects of the same sizes, this method return the lists of all the parameters used in position i.
     */
    public static List<List<Object>> nonMultidimensionalTestCases(List<List<Object>> inputLists){

        List<Object> paramAtSamePosition;
        List<List<Object>> testCases = new ArrayList<>();

        //all the lists has to have te same size
        int prev = inputLists.get(0).size();
        for(int i = 1; i < inputLists.size(); ++i){
            if(inputLists.get(i).size() != prev){
                return null;
            }
            prev = inputLists.get(i).size();
        }

        for(int i = 0; i < inputLists.get(0).size(); ++i){

            paramAtSamePosition = new ArrayList<>();

            for(int j = 0; j < inputLists.size(); ++j){
                paramAtSamePosition.add(inputLists.get(j).get(i));
            }

            testCases.add(paramAtSamePosition);
        }

        return testCases;
    }
}
