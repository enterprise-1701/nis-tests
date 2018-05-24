package com.cubic.nistests.tests;

import java.util.Hashtable;

import org.testng.ITestContext;
import org.testng.annotations.Test;

import com.cubic.nisjava.constants.AppConstants;
import com.cubic.nisjava.dataproviders.NISDataProviderSource;

public class NWAPIV2_CustomerViewAccountSummaryGood_ReturnSubset_CMS
	extends NWAPIV2_CustomerViewAccountSummaryGood_ReturnSubset {

	/**
	 * Try to get an Account Summary with a valid Customer Id.
	 * 
	 * 
	 * testRailId: 687525
	 * 
	 * @param context  The TestNG context object reference.
	 * @param data  The test Data from the JSON file
	 * @throws Throwable  Thrown if something goes wrong.
	 */
	@Test(dataProvider = AppConstants.DATA_PROVIDER, dataProviderClass = NISDataProviderSource.class)
	public void getAccountSummaryWithCustomerId(ITestContext context, Hashtable<String, String> data) throws Throwable {
		testMain( context, data );
	}
}
