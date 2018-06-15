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
import com.cubic.nisjava.api.MerchantPatronAuthenticatePost;
import com.cubic.nisjava.constants.AppConstants;
import com.cubic.nisjava.dataproviders.NISDataProviderRetailAPI;

public class PatronAuthenticateRegisteredDeviceTest extends RESTEngine {
	
	/**********************  C190399 Merchant Login With Registered Device **********************/
	@Test(dataProvider = AppConstants.DATA_PROVIDER, dataProviderClass = NISDataProviderRetailAPI.class)
	public void patronAuthenticateWithRegisteredDevice(ITestContext context,Hashtable<String, String> data) throws Throwable {
		
		String testCaseName = data.get("TestCase_Description");
		RESTActions restActions = setupAutomationTest(context, testCaseName);
        
		try {
			
			if (data.get("RunMode").equals("Y")) {
				MerchantPatronAuthenticatePost.verifyPatronAuthenticateWithRegisteredDevice(data , restActions);
				 				                
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		} finally {
			teardownAutomationTest(context, testCaseName);

		}
	}
}
