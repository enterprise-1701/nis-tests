package com.cubic.nistests.tests;

import java.util.Hashtable;
import org.testng.ITestContext;
import org.testng.annotations.Test;
import com.cubic.accelerators.RESTActions;
import com.cubic.accelerators.RESTEngine;
import com.cubic.nisjava.api.PatronRegisterPost;
import com.cubic.nisjava.constants.AppConstants;
import com.cubic.nisjava.dataproviders.NISPatronRegister;

public class PatronRegisterWithInvalidPhoneNumberTest extends RESTEngine{

	//*******  Patron Register - Invalid Phone number  ******//*
			@Test(dataProvider = AppConstants.DATA_PROVIDER, dataProviderClass = NISPatronRegister.class)
			public void patronRegisterWithInvalidPhoneNumber(ITestContext context,Hashtable<String, String> data) throws Throwable {
				
				String testCaseNameWithInvalidData = data.get("TestCase_Description");
				RESTActions restActions = setupAutomationTest(context, testCaseNameWithInvalidData);
		        
				
				try {
					if (data.get("RunMode").equals("Y")) {
						  PatronRegisterPost.verifyPatronAccountWithInvalidPhoneNumber(data, restActions);
		                  
					}
				} catch (Exception e) {
					e.printStackTrace();
					throw new RuntimeException(e);
				} finally {
					teardownAutomationTest(context, testCaseNameWithInvalidData);

				}
			}
		  

}
