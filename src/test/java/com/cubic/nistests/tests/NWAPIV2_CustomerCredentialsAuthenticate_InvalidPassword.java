package com.cubic.nistests.tests;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Hashtable;

import org.apache.log4j.Logger;
import org.testng.ITestContext;

import com.cubic.accelerators.RESTActions;
import com.cubic.accelerators.RESTConstants;
import com.cubic.accelerators.RESTEngine;
import com.cubic.backoffice.constants.BackOfficeGlobals;
import com.cubic.backoffice.utils.BackOfficeUtils;
import com.sun.jersey.api.client.ClientResponse;

/**
 * This test class will Call the customer/credentials/authenticate API with an invalid password. 
 * 
 * @author 203402
 *
 */
public abstract class NWAPIV2_CustomerCredentialsAuthenticate_InvalidPassword  extends RESTEngine {

	protected static final String BAD_ERROR_MESSAGE_FMT = "BAD ERROR MESSAGE - EXPECTED %s FOUND %s";
	protected static final String BAD_ERROR_KEY_FMT = "BAD ERROR KEY - EXPECTED %s FOUND %s";
	protected static final String BAD_AUTH_ERROR_LIST_SIZE_FMT = "BAD AUTH ERROR LIST SIZE - EXPECTED %s FOUND %s";
	protected static final String BAD_AUTH_CODE_FMT = "BAD AUTH CODE - EXPECTED %s FOUND %s";
	protected static final String EXPECTED_ERROR_MESSAGE = "EXPECTED_ERROR_MESSAGE";
	protected static final String EXPECTED_ERROR_KEY = "EXPECTED_ERROR_KEY";
	protected static final String EXPECTED_ARRAY_SIZE = "EXPECTED_ARRAY_SIZE";
	protected static final String EXPECTED_AUTH_CODE = "EXPECTED_AUTH_CODE";
	protected static final String EXPECTED_HTTP_RESPONSE_CODE = "EXPECTED_HTTP_RESPONSE_CODE";
	protected static final String RESPONSE_IS_NULL = "RESPONSE IS NULL BUT SHOULD NOT BE NULL";
	protected static final String BAD_RESPONSE_CODE_FMT = "WRONG HTTP RESPONSE CODE - EXPECTED %s, FOUND %s";
	protected static final String USERNAME = "USERNAME";
	protected static final String PASSWORD = "PASSWORD";
	
	protected final Logger LOG = Logger.getLogger(this.getClass().getName());		
	
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
			
			String requestBody = buildRequestBody( data );
			
			LOG.info("##### Making HTTP request to get the EULA...");
			ClientResponse clientResponse = restActions.postClientResponse(sURL, requestBody, headerTable, urlQueryParameters,
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
	 * http://10.252.1.21:8201/nis/nwapi/v2/customer/credentials/authenticate
	 * 
	 * @param data The test Data from the JSON input file.
	 * @return  The API's URL is String form.
	 */
	private String buildURL( Hashtable<String,String> data ) {
        String sURL = "http://" + BackOfficeGlobals.ENV.NIS_HOST + ":" + BackOfficeGlobals.ENV.NIS_PORT
                + "/nis/nwapi/v2/customer/credentials/authenticate";
		return sURL;
	}
	
	/**
	 * Helper method to build the Request Body for this API.
	 * 
	 * @param data  The test Data from the JSON input file.
	 * @return  The request Body in String form.
	 */
	private String buildRequestBody(Hashtable<String,String> data) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter( sw );
		pw.println( "{" );
		pw.println( "    \"username\":\"" + data.get(USERNAME) + "\"," );
		pw.println( "    \"password\":\"" + data.get(PASSWORD) + "\"" );
		pw.println( "}" );
		return sw.toString();
	}
	
	/**
	 * Helper method to test the response from the API.
	 * 
	 * @param data The test Data from the JSON input file.
	 * @param restActions  The RESTActions object used by the test method.
	 * @param response  The API response in String form.
	 */
	protected abstract void verifyResponse(Hashtable<String,String> data, RESTActions restActions, String response);
}
