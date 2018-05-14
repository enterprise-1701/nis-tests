package com.cubic.nistests.tests;

import java.util.Hashtable;

import org.testng.ITestContext;
import org.testng.annotations.Test;

import com.cubic.nisjava.constants.AppConstants;
import com.cubic.nisjava.dataproviders.NISDataProviderSource;

public class EULAAPIBadLocale extends EULAAPIBad {

	/**
	 * Try to Get a EULA using a locale for which there is no document,
	 * in order to elicit an errors.general.record.not.found error.
	 * 
	 * testRailId: 633286
	 * 
	 * @param context 	The TestNG context reference.
	 * @param data		The test data.
	 * @throws Throwable Thrown if something goes wrong.
	 */
	@Test(dataProvider = AppConstants.DATA_PROVIDER, dataProviderClass = NISDataProviderSource.class)
	public void tryToGetEULAWithBadLocale(ITestContext context, Hashtable<String, String> data) throws Throwable {
		mainTest( context, data );
	}
}

