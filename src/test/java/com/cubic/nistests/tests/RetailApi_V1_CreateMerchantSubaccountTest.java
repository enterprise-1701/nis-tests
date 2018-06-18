package com.cubic.nistests.tests;

import java.util.Hashtable;

import org.testng.ITestContext;
import org.testng.annotations.Test;

import com.cubic.accelerators.RESTActions;
import com.cubic.accelerators.RESTEngine;
import com.cubic.nisjava.api.CreateMerchantSubaccountPost;
import com.cubic.nisjava.constants.AppConstants;
import com.cubic.nisjava.dataproviders.NISDataProviderRetailAPI;

public class RetailApi_V1_CreateMerchantSubaccountTest extends RESTEngine{
	
	//*******  Create Merchant Subaccount ******//
			@Test(dataProvider = AppConstants.DATA_PROVIDER, dataProviderClass = NISDataProviderRetailAPI.class)
			public void createMerchantSubaccount(ITestContext context,Hashtable<String, String> data) throws Throwable {
				
				String testCaseNameWithvalidData = data.get("TestCase_Description");
				RESTActions restActions = setupAutomationTest(context, testCaseNameWithvalidData);
		       
				
				try {
					if (data.get("RunMode").equals("Y")) {
						CreateMerchantSubaccountPost.CreateMerchantSubaccount(data, restActions);
		                  
					}
				} catch (Exception e) {
					e.printStackTrace();
					throw new RuntimeException(e);
				} finally {
					teardownAutomationTest(context, testCaseNameWithvalidData);

				}
			}


}
