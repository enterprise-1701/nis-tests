/**
 * @author 203610
 * Jul 6, 2018
 */
package com.cubic.nistests.tests;

import java.util.Hashtable;

import org.testng.ITestContext;
import org.testng.annotations.Test;

import com.cubic.accelerators.RESTActions;
import com.cubic.accelerators.RESTEngine;
import com.cubic.nisjava.api.ViewEditMerchantSubaccountGETAndPATCH;
import com.cubic.nisjava.constants.AppConstants;
import com.cubic.nisjava.dataproviders.NISDataProviderRetailAPI;

public class RetailApi_V1_ViewEditMerchantSubaccount_FNameLNameTest extends RESTEngine
{
	@Test(dataProvider = AppConstants.DATA_PROVIDER, dataProviderClass = NISDataProviderRetailAPI.class)
	public void viewEditMerchantSubaccountFNameLName(ITestContext context,Hashtable<String, String> data) throws Throwable
	{
		String testCaseName = data.get("TestCase_Description");
		RESTActions restActions = setupAutomationTest(context, testCaseName);
		try
		{		
			ViewEditMerchantSubaccountGETAndPATCH.viewEditMerchantSubaccountFNameLName(restActions,data);          
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
			throw new RuntimeException(e);
		} 
		finally 
		{
			teardownAutomationTest(context, testCaseName);
		}
	}
}
