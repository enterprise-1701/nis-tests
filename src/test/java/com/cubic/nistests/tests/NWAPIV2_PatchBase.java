package com.cubic.nistests.tests;

import java.util.Hashtable;

import org.apache.log4j.Logger;
import org.testng.ITestContext;

import com.cubic.accelerators.RESTActions;
import com.cubic.accelerators.RESTConstants;
import com.cubic.accelerators.RESTEngine;
import com.cubic.backoffice.constants.BackOfficeGlobals;
import com.cubic.backoffice.utils.BackOfficeUtils;
import com.cubic.logutils.Log4jUtil;
import com.sun.jersey.api.client.ClientResponse;

/**
 * This test class contains a method that calls restActions.postClientResponse.
 * 
 * @author 203402
 *
 */
public abstract class NWAPIV2_PatchBase extends RESTEngine {
	
	protected static final String TESTCASE_DESCRIPTION = "TestCase_Description";
	protected static final String EXPECTED_HTTP_RESPONSE_CODE = "EXPECTED_HTTP_RESPONSE_CODE";	
	protected static final String BAD_RESPONSE_CODE_FMT = "WRONG HTTP RESPONSE CODE - EXPECTED %s, FOUND %s";
	protected static final String RESPONSE_IS_NULL = "RESPONSE IS NULL BUT SHOULD NOT BE NULL";
	protected static final Logger LOG = Logger.getLogger(NWAPIV2_PatchBase.class.getName());
	
	/**
	 * The method that calls restActions.patchClientResponse.
	 * 
	 * @param context  The TestNG Context object
	 * @param data The Test Data read from the JSON input file
	 * @throws Throwable Thrown if something goes wrong
	 */
	public void testMain(ITestContext context, Hashtable<String, String> data) throws Throwable {
		String testCaseName = data.get( TESTCASE_DESCRIPTION );
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
			
			String requestBody = this.buildRequestBody(data);
			
			LOG.info("##### Making HTTP request to get the EULA...");
			ClientResponse clientResponse = restActions.patchClientResponse(
					sURL, requestBody, headerTable, urlQueryParameters, RESTConstants.APPLICATION_JSON);

			verifyResponse( data, restActions, clientResponse );
		
		} catch( Exception e ) {	
			restActions = setupAutomationTest(context, testCaseName);
			restActions.failureReport("setup", "Setup failure occurred: " + e);
			teardownAutomationTest(context, testCaseName);
			LOG.error(Log4jUtil.getStackTrace(e));
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
	protected abstract void verifyResponse( Hashtable<String,String> data, RESTActions restActions, ClientResponse clientResponse );	
	
	/**
	 * Helper method to build the URL used to call the API.
	 * 
	 * @param data  The Test Data from the JSON input file.
	 * @return The URL in string form.
	 */
	protected abstract String buildURL( Hashtable<String,String> data );
	
	/**
	 * Helper method to build the request Body.
	 * 
	 * @param data The Test Data from the JSON input file.
	 * @return  The Request Body in String form.
	 */
	protected abstract String buildRequestBody( Hashtable<String,String> data );
}
