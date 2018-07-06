package com.cubic.nistests.tests;

/**
 * @author 205974
 *
 */

import java.util.Hashtable;
import org.testng.ITestContext;
import org.testng.annotations.Test;
import com.cubic.accelerators.RESTActions;
import com.cubic.accelerators.RESTEngine;
import com.cubic.nisjava.api.RetailAPIUpdateMerchantDevicePATCH;
import com.cubic.nisjava.constants.AppConstants;
import com.cubic.nisjava.dataproviders.NISDataProviderRetailAPI;

public class RetailApi_V1_UpdateMerchantProfileWithPhoneNoTooLongTest extends RESTEngine {
	
	/*****************  C967817 : Edit Merchant Profile - phone - phone[0].number value too long *****************/
	@Test(dataProvider = AppConstants.DATA_PROVIDER, dataProviderClass = NISDataProviderRetailAPI.class)
	public void updateMerchantProfileWithPhoneNoValueTooLong(ITestContext context,Hashtable<String, String> data) throws Throwable {
		
		String testCaseName = data.get("TestCase_Description");
		RESTActions restActions = setupAutomationTest(context, testCaseName);
        
		try {
			
			if (data.get("RunMode").equals("Y")) {
				RetailAPIUpdateMerchantDevicePATCH.verifyEditMerchantProfileWithPhoneNoValueTooLong(data, restActions);
				 				                
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		} finally {
			teardownAutomationTest(context, testCaseName);

		}
	}
}
