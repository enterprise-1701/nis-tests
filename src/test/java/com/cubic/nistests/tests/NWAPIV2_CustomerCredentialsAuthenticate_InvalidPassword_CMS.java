package com.cubic.nistests.tests;

import java.util.Hashtable;

import org.testng.ITestContext;
import org.testng.annotations.Test;

import com.cubic.accelerators.RESTActions;
import com.cubic.nisjava.apiobjects.WSAuthErrorList;
import com.cubic.nisjava.constants.AppConstants;
import com.cubic.nisjava.dataproviders.NISDataProviderSource;
import com.google.gson.Gson;

public class NWAPIV2_CustomerCredentialsAuthenticate_InvalidPassword_CMS extends NWAPIV2_CustomerCredentialsAuthenticate_InvalidPassword {

	/**
	 * Call the customer/credentials/authenticate API with an invalid password. 
	 * 
	 * testRailId = 687516
	 * 
	 * @param context  The TestNG test context object.
	 * @param data The test Data from the JSON input file.
	 * @throws Throwable  Thrown if something goes wrong.
	 */
	@Test(dataProvider = AppConstants.DATA_PROVIDER, dataProviderClass = NISDataProviderSource.class)
	public void tryToAuthenticateWithInvalidPassword(ITestContext context, Hashtable<String, String> data) throws Throwable {
		testMain( context, data );
	}
	
	/**
	 * Helper method to test the response from the API.
	 * 
	 * @param data The test Data from the JSON input file.
	 * @param restActions  The RESTActions object used by the test method.
	 * @param response  The API response in String form.
	 */
	@Override
	protected void verifyResponse(Hashtable<String,String> data, RESTActions restActions, String response) {
		Gson gson = new Gson();
		
		LOG.info("##### Parsing the response content...");
		WSAuthErrorList errorMsg = gson.fromJson(response, WSAuthErrorList.class);  
	
		LOG.info("##### Testing the response content...");
		
		String expectedAuthCode = data.get(EXPECTED_AUTH_CODE);
		String actualAuthCode = errorMsg.getAuthCode();
		restActions.assertTrue(expectedAuthCode.equals(actualAuthCode), 
				String.format(BAD_AUTH_CODE_FMT,expectedAuthCode,actualAuthCode));
		
		String sExpectedArraySize = data.get(EXPECTED_ARRAY_SIZE);
		int expectedArraySize = Integer.parseInt( sExpectedArraySize );
		int actualArraySize = errorMsg.getAuthErrors().size();
		restActions.assertTrue(expectedArraySize == actualArraySize, 
				String.format(BAD_AUTH_ERROR_LIST_SIZE_FMT,
						expectedArraySize,actualArraySize));
		
		String expectedErrorKey = data.get(EXPECTED_ERROR_KEY);
		String actualErrorKey = errorMsg.getAuthErrors().get(0).getErrorKey();
		restActions.assertTrue(expectedErrorKey.equals(actualErrorKey), 
				String.format(BAD_ERROR_KEY_FMT,expectedErrorKey,actualErrorKey));		
	}
}
