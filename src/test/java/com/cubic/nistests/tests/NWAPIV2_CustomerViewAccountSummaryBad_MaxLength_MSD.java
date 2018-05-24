package com.cubic.nistests.tests;

import java.util.Hashtable;

import org.testng.ITestContext;
import org.testng.annotations.Test;

import com.cubic.nisjava.constants.AppConstants;
import com.cubic.nisjava.dataproviders.NISDataProviderSource;

public class NWAPIV2_CustomerViewAccountSummaryBad_MaxLength_MSD 
	extends NWAPIV2_CustomerViewAccountSummaryBad_MaxLength {

	/**
	 * Try to get an Account Summary with a Customer Id that is the Max Length.
	 * 
	 * 
	 * testRailId: 687521
	 * 
	 * @param context
	 * @param data
	 * @throws Throwable
	 */
	@Test(dataProvider = AppConstants.DATA_PROVIDER, dataProviderClass = NISDataProviderSource.class)
	public void tryToGetAccountSummaryWithCustomerIdThatIsMaxLength(ITestContext context, Hashtable<String, String> data) throws Throwable {
		testMain( context, data );
	}
}
