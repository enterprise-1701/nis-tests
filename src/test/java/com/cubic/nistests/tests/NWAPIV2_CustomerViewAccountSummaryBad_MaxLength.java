package com.cubic.nistests.tests;

import java.util.Hashtable;

import org.apache.log4j.Logger;
import org.testng.ITestContext;

import com.cubic.accelerators.RESTActions;
import com.cubic.accelerators.RESTConstants;
import com.cubic.accelerators.RESTEngine;
import com.cubic.backoffice.constants.BackOfficeGlobals;
import com.cubic.backoffice.utils.BackOfficeUtils;
import com.cubic.nisjava.apiobjects.WSXCubHdrJSON;
import com.google.gson.Gson;
import com.sun.jersey.api.client.ClientResponse;

public class NWAPIV2_CustomerViewAccountSummaryBad_MaxLength extends RESTEngine {

	private static final String EXPECTED_HTTP_RESPONSE_CODE = "EXPECTED_HTTP_RESPONSE_CODE";
	private static final String RESPONSE_IS_NULL = "RESPONSE IS NULL BUT SHOULD NOT BE NULL";
	private static final String BAD_RESPONSE_CODE_FMT = "WRONG HTTP RESPONSE CODE - EXPECTED %s, FOUND %s";	
	private static final String EXPECTED_RESULT = "EXPECTED_RESULT";
	private static final String RESULT_FMT = "BAD RESULT: EXPECTED '%s', FOUND '%s'";
	private static final String BAD_UI_NULL_FMT = "BAD UID: UID IS NULL BUT IT SHOULD NOT BE";
	private static final String BAD_UI_LENGTH_0_MSG = "BAD UID: UID HAS LENGTH 0";
	private static final String BAD_ERROR_KEY_FMT = "BAD ERROR KEY: EXPECTED '%s', FOUND '%s'";
	private static final String BAD_FIELD_NAME_FMT = "BAD FIELD NAME: EXPECTED '%s', FOUND '%s'";
	private static final String EXPECTED_FIELDNAME = "EXPECTED_FIELDNAME";
	private static final String EXPECTED_ERROR_KEY = "EXPECTED_ERROR_KEY";
	
	private final Logger LOG = Logger.getLogger(this.getClass().getName());	
	
	/**
	 * Try to get an Account Summary from the nwapi_v2.0 customer API.
	 * 
	 * @param context  The TestNG context reference.
	 * @param data  The Test Data from the JSON input file.
	 * @throws Throwable  Thrown if something goes wrong.
	 */
	public void testMain(ITestContext context, Hashtable<String, String> data) throws Throwable {
		String testCaseName = data.get("TestCase_Description");
		LOG.info("##### Starting Test Case " + testCaseName);
		
		try {
			LOG.info("##### Setting up automation test...");
			BackOfficeGlobals.ENV.setEnvironmentVariables();
			RESTActions restActions = setupAutomationTest(context, testCaseName);
			
			String sURL = buildURL( data );
            LOG.info("##### Built URL: " + sURL);
            
            LOG.info("Creating GET request headers");
			Hashtable<String,String> headerTable = BackOfficeUtils.createRESTHeader(RESTConstants.APPLICATION_JSON);
			Hashtable<String, String> urlQueryParameters = new Hashtable<String, String>();
			
			LOG.info("##### Making HTTP request to get the EULA...");
			ClientResponse clientResponse = restActions.getClientResponse(sURL, headerTable, urlQueryParameters,
					RESTConstants.APPLICATION_JSON);

			LOG.info("##### Verifying HTTP response code...");
			int actualHTTPResponseCode = clientResponse.getStatus();
			String sExpectedHTTPResponseCode = data.get(EXPECTED_HTTP_RESPONSE_CODE);
			int expectedResponseCode = Integer.parseInt( sExpectedHTTPResponseCode );
			String msg = String.format(BAD_RESPONSE_CODE_FMT, expectedResponseCode, actualHTTPResponseCode);
			restActions.assertTrue(expectedResponseCode == actualHTTPResponseCode, msg);
			
			String response = clientResponse.getEntity(String.class);
			LOG.info("##### Got the response body: " + response);
			restActions.assertTrue(response != null, RESPONSE_IS_NULL);
			
			verifyResponse( data, restActions, response );
			
		} finally {
			teardownAutomationTest(context, testCaseName);
			LOG.info("##### Done!");
		}
	}
	
	/**
	 * Helper method to verify the response contains the expected data.
	 * 
	 * @param data  The Test Data from the JSON input file.
	 * @param restActions  The RESTActions object used by the testMain method.
	 * @param response  The Response in String form obtained by the GET method.
	 */
	protected void verifyResponse( Hashtable<String,String> data, RESTActions restActions, String response ) {
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
	 * Helper method to build the URL used to call the API.
	 * 
	 * @param data  The Test Data from the JSON input file.
	 * @return The URL in string form.
	 */
	private String buildURL( Hashtable<String, String> data ) {
        String sURL = "http://" + BackOfficeGlobals.ENV.NIS_HOST + ":" + BackOfficeGlobals.ENV.NIS_PORT
                + "/nis/nwapi/v2/customer/" + data.get("CUSTOMER_ID")
                + "?returnCustomerInfo=" + data.get("RETURN_CUSTOMER_INFO");
        return sURL;
	}
}
