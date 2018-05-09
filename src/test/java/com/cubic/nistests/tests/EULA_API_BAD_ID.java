package com.cubic.nistests.tests;

import java.util.Hashtable;

import org.testng.ITestContext;
import org.testng.annotations.Test;

import com.cubic.nisjava.constants.AppConstants;
import com.cubic.nisjava.dataproviders.NISDataProviderSource;

public class EULA_API_BAD_ID extends EULA_API_BAD {

	/**
	 * Try to get a EULA giving a non-existent EulaId
	 * in order to elicit a errors.general.record.not.found
	 * error.
	 * 
	 * testRailId: 633247
	 * 
	 * @param context 	The TestNG context reference.
	 * @param data		The test data.
	 * @throws Throwable Thrown if something goes wrong.
	 */
	@Test(dataProvider = AppConstants.DATA_PROVIDER, dataProviderClass = NISDataProviderSource.class)
	public void tryToGetEULAWithBadId(ITestContext context, Hashtable<String, String> data) throws Throwable {
		mainTest( context, data );
	}
}
