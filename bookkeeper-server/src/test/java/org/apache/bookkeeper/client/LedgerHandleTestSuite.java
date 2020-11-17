package org.apache.bookkeeper.client;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(value= Suite.class)
@Suite.SuiteClasses(value={LedgerHandleGetNumBookiesTest.class, LedgerHandleAddEntryTest.class, PitTest.class})
public class LedgerHandleTestSuite {
}
