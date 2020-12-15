package org.apache.bookkeeper.client;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(value= Suite.class)
@Suite.SuiteClasses(value={BookkeeperAdminGetAvBoTest.class, BookkeeperAdminReadEntriesTest.class, BookkeeperAdminFormatEnsembleTest.class, BookkKeeperAdminImprovedTest.class})
public class BookkeeperAdminTestSuite {
}