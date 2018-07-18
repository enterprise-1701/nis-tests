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

public class RetailApi_V1_UpdateMerchantProfileWithEmailStartingWithSpecialCharactersTest extends RESTEngine {
	
	/*****************  C612533 - Edit Merchant Profile - email - email cannot start with special characters *****************/
	@Test(dataProvider = AppConstants.DATA_PROVIDER, dataProviderClass = NISDataProviderRetailAPI.class)
	public void updateMerchantProfileWithEmailStartingWithSpecialCharacters(ITestContext context,Hashtable<String, String> data) throws Throwable {
		
		String testCaseName = data.get("TestCase_Description");
		RESTActions restActions = setupAutomationTest(context, testCaseName);
        
		try {
			
			if (data.get("RunMode").equals("Y")) {
				RetailAPIUpdateMerchantDevicePATCH.verifyEditMerchantProfileWithEmailStartingWithSpecialCharacters(data, restActions);
				 				                
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		} finally {
			teardownAutomationTest(context, testCaseName);

		}
	}
}
