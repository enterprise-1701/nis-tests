package com.cubic.nistests.tests;

/**
 *  Author:Vijaya Bhaskar Palem
 *  Jun 4, 2018
 */
import java.util.Hashtable;

import org.testng.ITestContext;
import org.testng.annotations.Test;

import com.cubic.accelerators.RESTActions;
import com.cubic.accelerators.RESTEngine;
import com.cubic.nisjava.api.CreateMerchantSubaccountPost;
import com.cubic.nisjava.constants.AppConstants;
import com.cubic.nisjava.dataproviders.NISDataProviderRetailAPI;
             
public class CreateMerchantSubaccountWithPasswordBlankTest extends RESTEngine{
	
	//*******  Create Merchant Subaccount with Password Blank ******//
			@Test(dataProvider = AppConstants.DATA_PROVIDER, dataProviderClass = NISDataProviderRetailAPI.class)
			public void createMerchantSubaccountWithPasswordBlank(ITestContext context,Hashtable<String, String> data) throws Throwable {
				
				String testCaseNameWithInvalidData = data.get("TestCase_Description");
				RESTActions restActions = setupAutomationTest(context, testCaseNameWithInvalidData);
		       
				
				try {
					if (data.get("RunMode").equals("Y")) {
						CreateMerchantSubaccountPost.CreateMerchantSubaccountPasswordBlank(data, restActions);
		                  
					}
				} catch (Exception e) {
					e.printStackTrace();
					throw new RuntimeException(e);
				} finally {
					teardownAutomationTest(context, testCaseNameWithInvalidData);

				}
			}


}
