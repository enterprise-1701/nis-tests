package com.cubic.nistests.tests;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import org.apache.log4j.Logger;
import org.testng.ITestContext;
import org.testng.annotations.Test;

import com.cubic.accelerators.RESTActions;
import com.cubic.accelerators.RESTConstants;
import com.cubic.accelerators.RESTEngine;
import com.cubic.backoffice.constants.BackOfficeGlobals;
import com.cubic.backoffice.utils.BackOfficeUtils;
import com.cubic.nisjava.constants.AppConstants;
import com.cubic.nisjava.dataproviders.NISDataProviderSource;
import com.sun.jersey.api.client.ClientResponse;
import com.google.gson.Gson;
import com.cubic.nisjava.apiobjects.WSNameSuffix;
import com.cubic.nisjava.apiobjects.WSNameSuffixList;

/**
 * This test class implements the NameSuffixes test case.
 * 
 * @author 203402
 *
 */
public class NameSuffixesTest extends RESTEngine {

	private final Logger LOG = Logger.getLogger(this.getClass().getName());
	
	/**
	 * Get a list of name Suffixes, e.g., "Esquire".
	 * 
	 * testRailId = 685746
	 * 
	 * @param context  The TestNG context object reference.
	 * @param data  The Test Data
	 * @throws Throwable  Thrown if something goes wrong.
	 */
	@Test(dataProvider = AppConstants.DATA_PROVIDER, dataProviderClass = NISDataProviderSource.class)
	public void getNameSuffixes(ITestContext context, Hashtable<String, String> data) throws Throwable {
		String testCaseName = data.get("TestCase_Description");
		LOG.info("##### Starting Test Case " + testCaseName);
		
		try {
			LOG.info("##### Setting up automation test...");
			BackOfficeGlobals.ENV.setEnvironmentVariables();
			RESTActions restActions = setupAutomationTest(context, testCaseName);
			
			String sURL = buildURL();
            LOG.info("##### Built URL: " + sURL);
            
            LOG.info("Creating GET request headers");
			Hashtable<String,String> headerTable = BackOfficeUtils.createRESTHeader(RESTConstants.APPLICATION_JSON);
			Hashtable<String, String> urlQueryParameters = new Hashtable<String, String>();
			
			LOG.info("##### Making HTTP request to get the EULA...");
			ClientResponse clientResponse = restActions.getClientResponse(sURL, headerTable, urlQueryParameters,
					RESTConstants.APPLICATION_JSON);

			LOG.info("##### Verifying HTTP response code...");
			int status = clientResponse.getStatus();
			String msg = "WRONG HTTP RESPONSE CODE - EXPECTED 200, FOUND " + status;
			restActions.assertTrue(status == HttpURLConnection.HTTP_OK, msg);
			
			String response = clientResponse.getEntity(String.class);
			LOG.info("##### Got the response body");
			LOG.info( response );
			restActions.assertTrue(response != null, "RESPONSE IS NULL BUT SHOULD NOT BE NULL");	
	
			LOG.info("##### Build expected Test Data...");
			WSNameSuffixList nameSuffixExpected = buildExpectedTestData( data );
			
			LOG.info("##### Parse the actual response...");
			Gson gson = new Gson();
			WSNameSuffixList nameSuffixActual = gson.fromJson(response, WSNameSuffixList.class);
			
			LOG.info("##### Compare the expected test data with the actual test data");
			boolean bEqual = nameSuffixExpected.equals( nameSuffixActual );
			restActions.assertTrue(bEqual, WSNameSuffixList.getErrorMessage());
			
		} finally {
			teardownAutomationTest(context, testCaseName);
			LOG.info("##### Done!");
		}
	}
	
	/**
	 * Build the URL used to get the name suffix list.
	 * 
	 * @return The URL in string format.
	 */
	private String buildURL() {
        return "http://" + BackOfficeGlobals.ENV.NIS_HOST + ":" + BackOfficeGlobals.ENV.NIS_PORT
                + "/nis/nwapi/v2/customerservice/namesuffixes";
	}
	
	/**
	 * Helper method to build the Expected test data.
	 * 
	 * @param data  The test data from the JSON input file.
	 * @return  The expected test data as a WSNameSuffixList object.
	 */
	private WSNameSuffixList buildExpectedTestData( Hashtable<String,String> data ) {
		List<WSNameSuffix> nameSuffixList = new ArrayList<WSNameSuffix>();
		
		WSNameSuffix junior = new WSNameSuffix();
		junior.setKey( data.get("NAMESUFFIXES_KEY_1") );
		junior.setValue( data.get("NAMESUFFIXES_VALUE_1"));
		nameSuffixList.add( junior );
		
		WSNameSuffix senior = new WSNameSuffix();
		senior.setKey( data.get("NAMESUFFIXES_KEY_2") );
		senior.setValue( data.get("NAMESUFFIXES_VALUE_2"));
		nameSuffixList.add( senior );
		
		WSNameSuffix esquire = new WSNameSuffix();
		esquire.setKey( data.get("NAMESUFFIXES_KEY_3") );
		esquire.setValue( data.get("NAMESUFFIXES_VALUE_3"));
		nameSuffixList.add( esquire );
		
		WSNameSuffixList result = new WSNameSuffixList();
		result.setNameSuffixes(nameSuffixList);
		return result;
	}
}
