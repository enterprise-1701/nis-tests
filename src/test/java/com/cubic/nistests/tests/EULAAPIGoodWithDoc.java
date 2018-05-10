package com.cubic.nistests.tests;

import java.util.Hashtable;

import org.testng.ITestContext;
import org.testng.annotations.Test;

import com.cubic.nisjava.constants.AppConstants;
import com.cubic.nisjava.dataproviders.NISDataProviderSource;

public class EULAAPIGoodWithDoc extends EULAAPIGood {
	
	/**
	 * GET the EULA from NIS by eula-id and locale.
	 * 
	 * testRailId: 621694
	 * 
	 * @param context 	The TestNG context reference.
	 * @param data		The test data.
	 * @throws Throwable Thrown if something goes wrong.
	 */
	@Test(dataProvider = AppConstants.DATA_PROVIDER, dataProviderClass = NISDataProviderSource.class)
	public void getEULA(ITestContext context, Hashtable<String, String> data) throws Throwable {
		mainTest( context, data );
	}
}

