package com.cubic.nistests.tests;

import java.util.Hashtable;

import org.testng.ITestContext;
import org.testng.annotations.Test;

import com.cubic.accelerators.RESTActions;
import com.cubic.nisjava.apiobjects.WSXCubHdrJSON;
import com.cubic.nisjava.constants.AppConstants;
import com.cubic.nisjava.dataproviders.NISDataProviderSource;
import com.cubic.nisjava.utils.NISUtils;
import com.google.gson.Gson;

/**
 * This test class will call the GET
 * http://10.252.1.21:8201/nis/nwapi/v2/customer/999999999999999999/notificationpreferences
 * API, where 999999999999999999 is a Customer Id that doesn't exist, so that the API
 * returns 404 and errorKey "errors.general.record.not.found".
 * 
 * @author 203402
 *
 */
public class NWAPIV2_CustomerNotificationPreferences_InvalidCustomerIdTest extends NWAPIV2_GetBase {

	private static final String CUSTOMER_ID = "CUSTOMER_ID";
	private static final String EXPECTED_RESULT = "EXPECTED_RESULT";
	private static final String RESULT_FMT = "WRONG RESULT - EXPECTED %s FOUND %s";
	private static final String BAD_UID_NULL_FMT = "UID IS NULL BUT SHOULD NOT BE";
	private static final String BAD_UID_LENGTH_0_MSG = "BAD UID HAS LENGTH ZERO";
	private static final String EXPECTED_ERROR_KEY = "EXPECTED_ERROR_KEY";
	private static final String BAD_ERROR_KEY_FMT = "BAD ERROR KEY - EXPECTED %s FOUND %s";
	
	/**
	 * Test method to call the GET
	 * http://10.252.1.21:8201/nis/nwapi/v2/customer/999999999999999999/notificationpreferences
	 * API, where 999999999999999999 is a Customer Id that doesn't exist, so that the API
	 * returns 404 and errorKey "errors.general.record.not.found".
	 * 
	 * testRailId = 1094303
	 * 
	 * @param context  The TestNG Context object
	 * @param data  The Test Data from the JSON input file
	 * @throws Throwable  Thrown if something goes wrong
	 */
	@Test(dataProvider = AppConstants.DATA_PROVIDER, dataProviderClass = NISDataProviderSource.class)
	public void getNotificationPreferences(ITestContext context, Hashtable<String, String> data) throws Throwable {
		testMain(context, data);
	}
	
	/**
	 * Helper method to verify the response from the API.
	 * 
	 * @param data  The Test Data read from the JSON input file
	 * @param restActions  The RESTActions object created by the @Test method
	 * @param response  The response in String form
	 */
	@Override
	protected void verifyResponse(Hashtable<String, String> data, RESTActions restActions, String response) {
		Gson gson = new Gson();
		
		LOG.info("##### Parsing the response content...");
		WSXCubHdrJSON errorMsg = gson.fromJson(response, WSXCubHdrJSON.class);            
        
		LOG.info("##### Testing the response content...");
		String expectedResult = data.get(EXPECTED_RESULT);
		String actualResult = errorMsg.getHdr().getResult();
		restActions.assertTrue(expectedResult.equals(actualResult),
				String.format(RESULT_FMT, expectedResult, actualResult));
		
		String uid = errorMsg.getHdr().getUid();
		restActions.assertTrue(uid != null, BAD_UID_NULL_FMT);
		
		if ( uid != null ) {
			restActions.assertTrue(uid.length() > 0, BAD_UID_LENGTH_0_MSG);
		}
		
		String expectedErrorKey = data.get(EXPECTED_ERROR_KEY);
		String actualErrorKey = errorMsg.getHdr().getErrorKey();
		restActions.assertTrue(expectedErrorKey.equals(actualErrorKey),
				String.format(BAD_ERROR_KEY_FMT, expectedErrorKey, actualErrorKey));
	}

	/**
	 * Helper method to build the URL used to call the API.
	 * 
	 * @param data  The Test Data from the JSON input file.
	 * @return The URL in string form.
	 */
	@Override
	protected String buildURL(Hashtable<String, String> data) {
        String sURL = NISUtils.getURL()
                + "/nis/nwapi/v2/customer/" + data.get(CUSTOMER_ID)
                + "/notificationpreferences";
		return sURL;
	}

}
