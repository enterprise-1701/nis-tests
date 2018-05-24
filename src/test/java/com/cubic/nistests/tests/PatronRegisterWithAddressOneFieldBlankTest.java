package com.cubic.nistests.tests;

import java.util.Hashtable;
import org.testng.ITestContext;
import org.testng.annotations.Test;
import com.cubic.accelerators.RESTActions;
import com.cubic.accelerators.RESTEngine;
import com.cubic.nisjava.api.PatronRegisterPost;
import com.cubic.nisjava.constants.AppConstants;
import com.cubic.nisjava.dataproviders.NISPatronRegister;

public class PatronRegisterWithAddressOneFieldBlankTest extends RESTEngine {
	
	//*******  Patron Register - Address1 field is Blank ******//*
			@Test(dataProvider = AppConstants.DATA_PROVIDER, dataProviderClass = NISPatronRegister.class)
			public void patronRegisterWithAddress1FieldBlank(ITestContext context,Hashtable<String, String> data) throws Throwable {
				
				String testCaseNameWithInvalidData = data.get("TestCase_Description");
				RESTActions restActions = setupAutomationTest(context, testCaseNameWithInvalidData);
		        
				
				try {
					if (data.get("RunMode").equals("Y")) {
						  PatronRegisterPost.verifyPatronAccountWithAddress1Blank(data, restActions);
		                  
					}
				} catch (Exception e) {
					e.printStackTrace();
					throw new RuntimeException(e);
				} finally {
					teardownAutomationTest(context, testCaseNameWithInvalidData);

				}
			}
			

}
