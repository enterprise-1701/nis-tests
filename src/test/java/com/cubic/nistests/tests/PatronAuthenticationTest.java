package com.cubic.nistests.tests;

import java.util.Hashtable;

import org.testng.ITestContext;
import org.testng.annotations.Test;
import com.cubic.accelerators.RESTActions;
import com.cubic.accelerators.RESTEngine;
import com.cubic.nisjava.api.PatronAuthenticatePost;
import com.cubic.nisjava.constants.AppConstants;
import com.cubic.nisjava.dataproviders.NISPatronAutenticate;

public class PatronAuthenticationTest extends RESTEngine {
	
	/*******  Patron Authenticate -  Success  Scenario *******/
	@Test(dataProvider = AppConstants.DATA_PROVIDER, dataProviderClass = NISPatronAutenticate.class)
	public void patronAuthenticate(ITestContext context,Hashtable<String, String> data) throws Throwable {
		
		String testCaseName = data.get("TestCase_Description");
		RESTActions restActions = setupAutomationTest(context, testCaseName);
        
		
		try {
			
			if (data.get("RunMode").equals("Y")) {
				PatronAuthenticatePost.verifyPatronAhenticate(data , restActions);
				 				                
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		} finally {
			teardownAutomationTest(context, testCaseName);

		}
	}
}