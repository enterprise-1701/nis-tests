package com.cubic.nistests.tests;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Hashtable;

import org.testng.ITestContext;
import org.testng.annotations.Test;

import com.cubic.accelerators.RESTActions;
import com.cubic.accelerators.RESTConstants;
import com.cubic.backoffice.constants.BackOfficeGlobals;
import com.cubic.backoffice.utils.BackOfficeUtils;
import com.cubic.nisjava.apiobjects.WSXCubHdrJSON;
import com.cubic.nisjava.constants.AppConstants;
import com.cubic.nisjava.dataproviders.NISDataProviderSource;
import com.google.gson.Gson;
import com.sun.jersey.api.client.ClientResponse;

/**
 * The 'Username Forgot' API is called without either email or phone input
 * in order to elicit an error message 'errors.email.or.phone.are.required'.
 * 
 * @author 203402
 *
 */
public class NWAPIV2_CustomerUsernameForgotEmailOrPhoneRequiredTest extends NWAPIV2_CustomerBase {

	/**
	 * The 'Username Forgot' API is called without either email or phone input.
	 * This elicits an error message 'errors.email.or.phone.are.required'.
	 * 
	 * testRailId = 960992
	 * 
	 * @param context  The TestNG Context object.
	 * @param data  The test Data from the JSON input file.
	 * @throws Throwable  Thrown if something goes wrong.
	 */
	@Test(dataProvider = AppConstants.DATA_PROVIDER, dataProviderClass = NISDataProviderSource.class)
	public void usernameForgot(ITestContext context, Hashtable<String, String> data) throws Throwable {
		String testCaseName = data.get("TestCase_Description");
		LOG.info("##### Starting Test Case " + testCaseName);
		
		try {
			LOG.info("##### Setting up automation test...");
			BackOfficeGlobals.ENV.setEnvironmentVariables();
			RESTActions restActions = setupAutomationTest(context, testCaseName);
			
            LOG.info("##### Creating GET request headers");
			Hashtable<String,String> headerTable = BackOfficeUtils.createRESTHeader(RESTConstants.APPLICATION_JSON);			
			
			LOG.info("##### Get the user's Username Forgot API");
			ClientResponse clientResponse = usernameForgot( restActions, data, headerTable, null );
			
			String response = clientResponse.getEntity(String.class);
			LOG.info("##### Got the response body");
			restActions.assertTrue(response != null, RESPONSE_IS_NULL);
			LOG.info( response );
			
			verifyResponse( restActions, data, response );
		} finally {
			teardownAutomationTest(context, testCaseName);
			LOG.info("##### Done!");
		}
	}
	
	/**
	 * Helper method to verify the response from the 'Username Forgot' API.
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
	
	/**
	 * Helper method to build the 'Username Forgot' request body.
	 * 
	 * @param email  The email address to use
	 * @return  the 'Username Forgot' request body
	 */
	@Override
	protected String buildUsernameForgotRequestBody( String email ) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter( sw );
		pw.println("{");
		pw.println("    \"postalCode\":\"92131\"");
		pw.println("}");
		return sw.toString();
	}	
	
}
