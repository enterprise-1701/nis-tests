package com.cubic.nistests.tests;

import java.net.HttpURLConnection;
import java.util.Hashtable;

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
import com.cubic.nisjava.apiobjects.WSLineItemsList;

/**
 * A test class that calls the One Account Balance History endpoint
 * to obtain a list of Line Items.
 *  
 * @author 203402
 *
 */
public class CSAPIV1_OneAccountBalanceHistory extends RESTEngine {

	private static final String RESPONSE_IS_NULL = "RESPONSE IS NULL BUT SHOULD NOT BE NULL";
	private static final String BAD_RESPONSE_CODE = "WRONG HTTP RESPONSE CODE - EXPECTED 200, FOUND ";	
	
	private final Logger LOG = Logger.getLogger(this.getClass().getName());	
	
	/**
	 * A test method that calls the One Account Balance History endpoint
	 * to obtain a list of Line Items.
	 * 
	 * testRailId = 687535
	 * 
	 * @param context  The TestNG context reference.
	 * @param data  The Test Data read from the JSON input file.
	 * @throws Throwable  Thrown if something goes wrong.
	 */
	@Test(dataProvider = AppConstants.DATA_PROVIDER, dataProviderClass = NISDataProviderSource.class)
	public void getOneAccountBalanceHistory(ITestContext context, Hashtable<String, String> data) throws Throwable {
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
			int status = clientResponse.getStatus();
			String msg = BAD_RESPONSE_CODE + status;
			restActions.assertTrue(status == HttpURLConnection.HTTP_OK, msg);
			
			String response = clientResponse.getEntity(String.class);
			LOG.info("##### Got the response body: " + response);
			restActions.assertTrue(response != null, RESPONSE_IS_NULL);            
            
			Gson gson = new Gson();
			
			LOG.info("##### Parsing the response content...");
			WSLineItemsList lineItemsList = gson.fromJson(response, WSLineItemsList.class);            
            
			LOG.info("##### Testing the response content...");
			Integer actualTotalCount = lineItemsList.getTotalCount();
			String sExpectedTotalCount = data.get("EXPECTED_TOTAL_COUNT");
			Integer expectedTotalCount = Integer.valueOf(sExpectedTotalCount);
			restActions.assertTrue(expectedTotalCount.equals(actualTotalCount),
					String.format("BAD TOTAL COUNT - EXPECTED %s, FOUND %s", 
							expectedTotalCount, actualTotalCount));
			
			int actualArraySize = lineItemsList.getLineItems().size();
			String sExpectedArraySize = data.get("EXPECTED_ARRAY_SIZE");
			int expectedArraySize = Integer.parseInt(sExpectedArraySize);
			restActions.assertTrue( expectedArraySize == actualArraySize,
					String.format("BAD ARRAY SIZE - EXPECTED %s, FOUND %s", 
							expectedArraySize, actualArraySize));	
		} finally {
			teardownAutomationTest(context, testCaseName);
			LOG.info("##### Done!");
		} 
	}
	
	/**
	 * Helper method to build the URL of the balance history endpoint.
	 * 
	 * @param data  The Test Data read from the JSON input file.
	 * @return  The URL of the balance history endpoint.
	 */
	private String buildURL( Hashtable<String,String> data ) {
        String sURL = "http://" + BackOfficeGlobals.ENV.NIS_HOST + ":" + BackOfficeGlobals.ENV.NIS_PORT
                + "/nis/csapi/v1/oneaccount/" + data.get("ONE_ACCOUNT_ID") + "/balancehistory"
                + "?startDateTime=" + data.get("START_DATE_TIME")
                + "&endDateTime=" + data.get("END_DATE_TIME")
                + "&viewType=" + data.get("VIEW_TYPE")
                + "&financialTxnType=" + data.get("FINANCIAL_TXN_TYPE")
                + "&sortBy=" + data.get("SORT_BY")
                + "&offset=" + data.get("OFFSET")
                + "&limit=" + data.get("LIMIT");
        return sURL;
	}
	
}
