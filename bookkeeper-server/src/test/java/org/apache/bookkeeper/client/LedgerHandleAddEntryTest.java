package org.apache.bookkeeper.client;

import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.nio.ByteBuffer;
import java.util.*;

import static org.junit.Assert.assertEquals;

@RunWith(value= Parameterized.class)
public class LedgerHandleAddEntryTest extends UtilTestClass {

    private byte[] entryToAdd;
    private int offset;
    private int length;

    private static List<byte[]> dataParams = new ArrayList<>();

    public static final int BIG_DATA_MB = 100;



    @Parameterized.Parameters
    public static Collection<List<Object>> getParameters(){
        Random r = new Random(1234567);
        //inserting random bytes, at group pf 30
        byte[] data = new byte[30];
        byte[] emptyData = new byte[0];
        byte[] bigData = new byte[BIG_DATA_MB*1000000];

        r.nextBytes(data);
        r.nextBytes(bigData);

        List<Object> dataBorderValues = new ArrayList<>();
        List<Object> offsetBorderValues = new ArrayList<>();
        List<Object> lengthBorderValues = new ArrayList<>();

        //Minimal Test Suite
        dataBorderValues.add(data);
        offsetBorderValues.add(1);
        lengthBorderValues.add(1);

        dataBorderValues.add(emptyData);
        offsetBorderValues.add(0);
        lengthBorderValues.add(0);

        dataBorderValues.add(bigData);
        offsetBorderValues.add(1);
        lengthBorderValues.add(1);

        dataBorderValues.add(null);
        offsetBorderValues.add(-1);
        lengthBorderValues.add(-1);

        List<List<Object>> L  = new ArrayList<>();
        L.add(dataBorderValues);
        L.add(offsetBorderValues);
        L.add(lengthBorderValues);

        if(!UtilTestClass.improvedTestSuite){
            return nonMultidimensionalTestCases(L);
        }
        return multidimensionalTestCases(L);
    }


    public LedgerHandleAddEntryTest(List<Object> parameters){
        this.entryToAdd = (byte[])parameters.get(0);
        this.offset = (int)parameters.get(1);
        this.length = (int)parameters.get(2);
    }

    @Test
    public void addEntryTest() {

        long entryId;

        //skip tested parameters
        if(dataParams.contains(entryToAdd)){
            return;
        }else{
            dataParams.add(entryToAdd);
        }

        try {
            LedgerHandle lh = bkc.createLedger(3, 1, digestType, ledgerPassword);
            entryId = lh.addEntry(this.entryToAdd);

            Assert.assertTrue(entryId >= 0);
        } catch (Exception e) {
            //NULL parameter does not pass
            //Here if handling null entry add
            //We do not want that a null pointer exception is raised
            if(this.entryToAdd != null){
                Assert.assertNotEquals("java.lang.NullPointerException",e.getClass().getCanonicalName());
            }
        }

    }

    @Test
    public void addEntryWithParamsTest(){
        long entryId;
        try {
            LedgerHandle lh = bkc.createLedger(3, 1, digestType, ledgerPassword);
            entryId = lh.addEntry(this.entryToAdd, this.offset, this.length);

            Assert.assertTrue(entryId >= 0);
        } catch (Exception e) {

            //We do not want that a null pointer exception is raised, if a null parameter is NOT used
            if(this.entryToAdd != null){
                Assert.assertNotEquals("java.lang.NullPointerException", e.getClass().getCanonicalName());
            }

        }
    }
}
