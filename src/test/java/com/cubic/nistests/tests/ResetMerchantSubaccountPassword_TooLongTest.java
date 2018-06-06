/**
 * 
 */
package com.cubic.nistests.tests;

import java.util.Hashtable;

import org.testng.ITestContext;
import org.testng.annotations.Test;

import com.cubic.accelerators.RESTActions;
import com.cubic.accelerators.RESTEngine;
import com.cubic.nisjava.api.ResetMerchantSubaccountPasswordPATCH;
import com.cubic.nisjava.constants.AppConstants;
import com.cubic.nisjava.dataproviders.NISDataProviderRetailAPI;

/**
 * @author 203610
 * Jun 5, 2018
 */
public class ResetMerchantSubaccountPassword_TooLongTest extends RESTEngine
{
	@Test(dataProvider = AppConstants.DATA_PROVIDER, dataProviderClass = NISDataProviderRetailAPI.class)
	public void verifyResetMerchantSubaccountPasswordTooLong(ITestContext context,Hashtable<String, String> data) throws Throwable
	{
		String testCaseName = data.get("TestCase_Description");
		RESTActions restActions = setupAutomationTest(context, testCaseName);

		try
		{
			if (data.get("RunMode").equals("Y"))
			{
				ResetMerchantSubaccountPasswordPATCH.verifyResetMerchantSubaccountPasswordTooLong(data, restActions);             
			}
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
