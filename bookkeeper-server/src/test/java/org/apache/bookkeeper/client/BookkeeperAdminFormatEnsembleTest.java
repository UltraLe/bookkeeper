package org.apache.bookkeeper.client;

import org.apache.bookkeeper.net.BookieId;
import org.apache.commons.lang.StringUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

public class BookkeeperAdminFormatEnsembleTest extends UtilTestClass {

    private char[] markerS;
    private List<List<BookieId>> ensembleS;
    private List<Set<BookieId>> bookieSrcS;

    @Before
    public void initTests(){

        ensembleS = new ArrayList<>();
        bookieSrcS = new ArrayList<>();
        Random r = new Random(12345678);

        //First set of BookieId lists
        List<BookieId> ensemble = new ArrayList<>();
        Set<BookieId> bookieSrc = new HashSet<>();

        /*
            Generating a enseble of random bookieId
         */
        int numEns = r.nextInt(20);
        for(int i = 0; i < numEns; ++i){
            ensemble.add(BookieId.parse(String.valueOf(r.nextInt())));
        }
        /*
            Generating a set of bookieId with a random number
            of them that are also in the previous enseble
         */
        int numEquals = 0;
        while((numEquals=r.nextInt(numEns)) == 0);

        for(int i = 0; i < numEquals; ++i){
            bookieSrc.add(ensemble.get(i));
        }

        ensembleS.add(ensemble);
        bookieSrcS.add(bookieSrc);
        markerS = new char[3];
        markerS[0]=('*');
        markerS[1]=('Å');

        //Second set of BookieId lists (one empty)
        ensemble = new ArrayList<>();
        numEns = r.nextInt(20);
        for(int i = 0; i < numEns; ++i){
            ensemble.add(BookieId.parse(String.valueOf(r.nextInt())));
        }
        bookieSrc = new HashSet<>();
        ensembleS.add(ensemble);
        bookieSrcS.add(bookieSrc);

        //Third set of BookieId lists (one empty)
        ensemble = new ArrayList<>();
        bookieSrc = new HashSet<>();
        numEns = r.nextInt(20);
        for(int i = 0; i < numEns; ++i){
            bookieSrc.add(BookieId.parse(String.valueOf(r.nextInt())));
        }
        ensembleS.add(ensemble);
        bookieSrcS.add(bookieSrc);

    }

    @Test
    public void formatEnsembleTest() {


        for(char marker : markerS) {
            int i = 0;
            for (List<BookieId> ensemble : ensembleS) {

                Set<BookieId> bookieSrc = bookieSrcS.get(i);

                int numEquals = 0;
                for (BookieId b : bookieSrc) {
                    if (ensemble.contains(b)) {
                        numEquals++;
                    }
                }


                String s = BookKeeperAdmin.formatEnsemble(ensemble, bookieSrc, marker);
                Assert.assertEquals(numEquals, StringUtils.countMatches(s, String.valueOf(marker)));
                i++;
            }
        }
    }

}
