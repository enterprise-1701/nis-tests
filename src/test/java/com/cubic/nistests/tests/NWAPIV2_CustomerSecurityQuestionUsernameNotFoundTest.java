package com.cubic.nistests.tests;

import java.lang.invoke.MethodHandles;
import java.util.Hashtable;

import org.apache.log4j.Logger;
import org.testng.ITestContext;
import org.testng.annotations.Test;

import com.cubic.accelerators.RESTActions;
import com.cubic.accelerators.RESTConstants;
import com.cubic.backoffice.constants.BackOfficeGlobals;
import com.cubic.backoffice.utils.BackOfficeUtils;
import com.cubic.nisjava.apiobjects.WSXCubHdrJSON;
import com.cubic.nisjava.constants.AppConstants;
import com.cubic.nisjava.dataproviders.NISDataProviderSource;
import com.cubic.nisjava.utils.NISUtils;
import com.google.gson.Gson;

public class NWAPIV2_CustomerSecurityQuestionUsernameNotFoundTest extends NWAPIV2_CustomerBase  {
	
    private static final String CLASS_NAME = MethodHandles.lookup().lookupClass().getSimpleName();
    private static final Logger LOG = Logger.getLogger(CLASS_NAME);	
	
	/**
	 * Get a Customer's Security Question using an invalid username,
	 * so as to evoke a 'Username not found' error message.
	 * 
	 * testRailId = 960975
	 * 
	 * @param context  The TestNG Context object.
	 * @param data  The Test Data from the JSON input file.
	 * @throws Throwable  Thrown if something goes wrong.
	 */
	@Test(dataProvider = AppConstants.DATA_PROVIDER, dataProviderClass = NISDataProviderSource.class)
	public void securityQuestion(ITestContext context, Hashtable<String, String> data) throws Throwable {
		String testCaseName = data.get("TestCase_Description");
		LOG.info("##### Starting Test Case " + testCaseName);
		
		try {
			LOG.info("##### Setting up automation test...");
			BackOfficeGlobals.ENV.setEnvironmentVariables();
			RESTActions restActions = setupAutomationTest(context, testCaseName);
			
            LOG.info("##### Creating GET request headers");
			Hashtable<String,String> headerTable = BackOfficeUtils.createRESTHeader(RESTConstants.APPLICATION_JSON);
			
			LOG.info("##### Get the user's Security Question");
			String response = NISUtils.securityQuestion( restActions, data, headerTable, "no-such-username@cubic.com" );
			
			verifyResponse( restActions, data, response );
		} finally {
			teardownAutomationTest(context, testCaseName);
			LOG.info("##### Done!");
		}
	}
	
	/**
	 * Helper method to verify the response from the security questions API.
	 * 
	 * @param restActions  The RESTActions object used by the @Test method.
	 * @param data  The Test Data from the JSON input file.
	 * @param response  The API's response, in String form.
	 */
	private void verifyResponse( RESTActions restActions, Hashtable<String, String> data, String response ) {
		LOG.info("##### Verifying the response...");
		
		Gson gson = new Gson();
		LOG.info("##### Parsing the response content...");
		WSXCubHdrJSON errorMsg = gson.fromJson(response, WSXCubHdrJSON.class);            
        
		LOG.info("##### Testing the response content...");
		String expectedResult = data.get(EXPECTED_RESULT);
		String actualResult = errorMsg.getHdr().getResult();
		restActions.assertTrue(expectedResult.equals(actualResult),
				String.format(RESULT_FMT, expectedResult, actualResult));
		
		String uid = errorMsg.getHdr().getUid();
		restActions.assertTrue(uid != null, BAD_UI_NULL_FMT);
		
		if ( uid != null ) {
			restActions.assertTrue(uid.length() > 0, BAD_UI_LENGTH_0_MSG);
		}
		
		String actualFieldName = errorMsg.getHdr().getFieldName();
		String expectedFieldName = data.get(EXPECTED_FIELDNAME);
		restActions.assertTrue(expectedFieldName.equals(actualFieldName),
				String.format(BAD_FIELD_NAME_FMT, expectedFieldName, actualFieldName));
		
		String expectedErrorKey = data.get(EXPECTED_ERROR_KEY);
		String actualErrorKey = errorMsg.getHdr().getErrorKey();
		restActions.assertTrue(expectedErrorKey.equals(actualErrorKey),
				String.format(BAD_ERROR_KEY_FMT, expectedErrorKey, actualErrorKey));
	}
}
