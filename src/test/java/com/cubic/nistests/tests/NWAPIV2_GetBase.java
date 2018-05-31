package com.cubic.nistests.tests;

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
 * This test class contains a method that calls restActions.getClientResponse.
 * 
 * @author 203402
 *
 */
public abstract class NWAPIV2_GetBase extends RESTEngine {

	protected static final String EXPECTED_HTTP_RESPONSE_CODE = "EXPECTED_HTTP_RESPONSE_CODE";	
	protected static final String BAD_RESPONSE_CODE_FMT = "WRONG HTTP RESPONSE CODE - EXPECTED %s, FOUND %s";
	protected static final String RESPONSE_IS_NULL = "RESPONSE IS NULL BUT SHOULD NOT BE NULL";
	
	protected final Logger LOG = Logger.getLogger(this.getClass().getName());
	
	/**
	 * The method that calls restActions.getClientResponse.
	 * 
	 * @param context  The TestNG Context object
	 * @param data The Test Data read from the JSON input file
	 * @throws Throwable Thrown if something goes wrong
	 */
	public void testMain(ITestContext context, Hashtable<String, String> data) throws Throwable {
		String testCaseName = data.get("TestCase_Description");
		LOG.info("##### Starting Test Case " + testCaseName);

		LOG.info("##### Setting up automation test...");
		BackOfficeGlobals.ENV.setEnvironmentVariables();
		RESTActions restActions = setupAutomationTest(context, testCaseName);		
		
		try {
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
		
		} catch( Exception e ) {	
			e.printStackTrace();
			restActions.failureReport("Exception", "Exception is " + e);
			throw new RuntimeException(e);
		} finally {
			teardownAutomationTest(context, testCaseName);
			LOG.info("##### Done!");
		}
	}
	
	/**
	 * Helper method to verify the response from the API.
	 * 
	 * @param data  The Test Data read from the JSON input file
	 * @param restActions  The RESTActions object created by the @Test method
	 * @param response  The response in String form
	 */
	protected abstract void verifyResponse( Hashtable<String,String> data, RESTActions restActions, String response );	
	
	/**
	 * Helper method to build the URL used to call the API.
	 * 
	 * @param data  The Test Data from the JSON input file.
	 * @return The URL in string form.
	 */
	protected abstract String buildURL( Hashtable<String,String> data );
}
