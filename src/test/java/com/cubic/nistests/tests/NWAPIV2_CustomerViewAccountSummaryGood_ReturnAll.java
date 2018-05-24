package com.cubic.nistests.tests;

import java.util.Hashtable;
import java.util.List;

import org.apache.log4j.Logger;
import org.testng.ITestContext;

import com.cubic.accelerators.RESTActions;
import com.cubic.accelerators.RESTConstants;
import com.cubic.accelerators.RESTEngine;
import com.cubic.backoffice.constants.BackOfficeGlobals;
import com.cubic.backoffice.utils.BackOfficeUtils;
import com.cubic.nisjava.apiobjects.WSCustomerInfo;
import com.cubic.nisjava.apiobjects.WSCustomerInfoContainer;
import com.cubic.nisjava.apiobjects.WSCustomerStatus;
import com.cubic.nisjava.apiobjects.WSFundingSource;
import com.cubic.nisjava.apiobjects.WSFundingSourceList;
import com.cubic.nisjava.apiobjects.WSOneAccountInfo;
import com.google.gson.Gson;
import com.sun.jersey.api.client.ClientResponse;

public abstract class NWAPIV2_CustomerViewAccountSummaryGood_ReturnAll extends RESTEngine {
	
	protected static final String CUSTOMER_ID = "CUSTOMER_ID";
	protected static final String CUSTOMER_TYPE = "CUSTOMER_TYPE";
	protected static final String CUSTOMER_STATUS = "CUSTOMER_STATUS";
	protected static final String CUSTOMER_INFO_REF_IS_NULL = "CUSTOMER INFO REFERENCE IS NULL BUT SHOULD NOT BE";
	protected static final String FUNDING_SOURCE_REF_IS_NULL = "FUNDING SOURCE REFERENCE IS NULL BUT SHOULD NOT BE";
	protected static final String ONE_ACCOUNT_REF_IS_NULL = "ONE ACCOUNT REFERENCE IS NULL BUT SHOULD NOT BE";
	protected static final String BAD_CUSTOMER_ID_FMT = "BAD CUSTOMER ID - EXPECTED %s FOUND %s";
	protected static final String BAD_CUSTOMER_TYPE_FMT = "BAD CUSTOMER TYPE - EXPECTED %s FOUND %s";
	protected static final String BAD_CUSTOMER_STATUS_FMT = "BAD CUSTOMER STATUS - EXPECTED %s FOUND %s";
	protected static final String EXPECTED_HTTP_RESPONSE_CODE = "EXPECTED_HTTP_RESPONSE_CODE";
	protected static final String RESPONSE_IS_NULL = "RESPONSE IS NULL BUT SHOULD NOT BE NULL";
	protected static final String BAD_RESPONSE_CODE_FMT = "WRONG HTTP RESPONSE CODE - EXPECTED %s, FOUND %s";	
	protected static final String RETURN_CUSTOMER_INFO = "RETURN_CUSTOMER_INFO";
	protected static final String RETURN_FUNDING_SOURCES = "RETURN_FUNDING_SOURCES";
	protected static final String RETURN_ONEACCOUNT_INFO = "RETURN_ONEACCOUNT_INFO";
	protected static final String EXPECTED_ONE_ACCOUNT_ID = "EXPECTED_ONE_ACCOUNT_ID";
	protected static final String BAD_ONE_ACCOUNT_ID_FMT = "BAD ONE ACCOUNT ID - EXPECTED %s FOUND %s";
	protected static final String EXPECTED_FUNDING_SOURCE_ID_LIST = "EXPECTED_FUNDING_SOURCE_ID_LIST";
	protected static final String FUNDING_SOURCE_LIST_IS_NULL = "FUNDING SOURCE LIST IS NULL BUT SHOULD NOT BE";
	protected static final String BAD_FUNDING_SOURCES_LIST_SIZE_FMT= "BAD FUNDING SOURCES LIST SIZE - EXPECTED %s FOUND %S";
	protected static final String BAD_FUNDING_SOURCE_ID_FMT = "BAD FUNDING SOURCE ID - EXPECTED %s FOUND %s";
	
	private final Logger LOG = Logger.getLogger(this.getClass().getName());		

	/**
	 * Try to get an Account Summary with a valid Customer Id.
	 * 
	 * testRailId: 687523
	 * 
	 * @param context  The TestNG context object.
	 * @param data  The Test Data from the JSON input file.
	 * @throws Throwable  Thrown if something goes wrong.
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
	 * Helper method to build the URL used to call the API.
	 * 
	 * @param data  The Test Data from the JSON input file.
	 * @return The URL in string form.
	 */
	protected String buildURL( Hashtable<String,String> data ) {
        String sURL = "http://" + BackOfficeGlobals.ENV.NIS_HOST + ":" + BackOfficeGlobals.ENV.NIS_PORT
                + "/nis/nwapi/v2/customer/" + data.get(CUSTOMER_ID)
                + "?returnCustomerInfo=" + data.get(RETURN_CUSTOMER_INFO)
                + "&returnFundingSources=" + data.get(RETURN_FUNDING_SOURCES)
                + "&returnOneAccountInfo=" + data.get(RETURN_ONEACCOUNT_INFO);
		return sURL;
	}
		
	/**
	 * Helper method to verify the response contains the expected data.
	 * 
	 * @param data  The Test Data from the JSON input file.
	 * @param restActions  The RESTActions object used by the testMain method.
	 * @param response  The Response in String form obtained by the GET method.
	 */
	protected void verifyResponse( Hashtable<String,String> data, RESTActions restActions, String response ) throws Exception {
		Gson gson = new Gson();
		
		LOG.info("##### Parsing the response content...");
		WSCustomerInfoContainer customerInfoContainer = gson.fromJson(response, WSCustomerInfoContainer.class);            
        
		LOG.info("##### Testing the response content...");
		
		// test that the Customer Info block is present
		WSCustomerInfo customerInfo = customerInfoContainer.getCustomerInfo();
		restActions.assertTrue( customerInfo != null, CUSTOMER_INFO_REF_IS_NULL );
		
		if ( customerInfo == null ) {
			return;
		}
		
		String expectedCustomerId = data.get(CUSTOMER_ID);
		String actualCustomerId = customerInfo.getCustomerId();
		restActions.assertTrue(expectedCustomerId.equals(actualCustomerId), 
				String.format(BAD_CUSTOMER_ID_FMT, 
						expectedCustomerId, actualCustomerId));
		
		String expectedCustomerType = data.get(CUSTOMER_TYPE);
		String actualCustomerType = customerInfo.getCustomerType();
		restActions.assertTrue(expectedCustomerType.equals(actualCustomerType), 
				String.format(BAD_CUSTOMER_TYPE_FMT, 
						expectedCustomerType, actualCustomerType));
		
		String expectedCustomerStatus = data.get(CUSTOMER_STATUS);
		WSCustomerStatus customerStatus = customerInfo.getCustomerStatus();
		if ( null == customerStatus )
			return;
		String actualCustomerStatus = customerStatus.getValue();
		restActions.assertTrue(expectedCustomerStatus.equals(actualCustomerStatus), 
				String.format(BAD_CUSTOMER_STATUS_FMT, 
						expectedCustomerStatus, actualCustomerStatus));
		
		// test that the Funding Source block is present
		WSFundingSourceList fundingSourceList = customerInfoContainer.getFundingSources();
		restActions.assertTrue( fundingSourceList != null, FUNDING_SOURCE_REF_IS_NULL );
		if ( fundingSourceList != null ) {
			
			String sExpectedFundingSourceIdList = data.get(EXPECTED_FUNDING_SOURCE_ID_LIST);
			String[] aExpectedFundingSourceIdList = sExpectedFundingSourceIdList.split(" ");
			
			List<WSFundingSource> fundingSources = fundingSourceList.getFundingSources();
			restActions.assertTrue( fundingSources != null, FUNDING_SOURCE_LIST_IS_NULL);
			
			if ( fundingSources != null ) {
				int expectedFundingSourcesLength = 0;
				// count the number of fundingSourceIds with non-zero length
				for ( int i = 0; i < aExpectedFundingSourceIdList.length; i++ ) {
					if ( aExpectedFundingSourceIdList[i].length() > 0 ) {
						expectedFundingSourcesLength++;
					}
				}
				int actualFoundingSourcesLength = fundingSources.size();
				
				restActions.assertTrue( expectedFundingSourcesLength == actualFoundingSourcesLength,
						String.format(BAD_FUNDING_SOURCES_LIST_SIZE_FMT, 
								expectedFundingSourcesLength, actualFoundingSourcesLength));
				
				if (expectedFundingSourcesLength == actualFoundingSourcesLength) {
					for (int i = 0; i < expectedFundingSourcesLength; i++) {
						Integer expectedFundingSourceId = Integer.valueOf(aExpectedFundingSourceIdList[i]);
						WSFundingSource actualFoundingSource = fundingSources.get(i);
						Integer actualFundingSourceId = actualFoundingSource.getFundingSourceId();

						restActions.assertTrue(expectedFundingSourceId.equals(actualFundingSourceId),
								String.format(BAD_FUNDING_SOURCE_ID_FMT,
										expectedFundingSourceId,actualFundingSourceId));
					}
				}
			}
		}
		
		// test that the One Account block is present
		WSOneAccountInfo oneAccountInfo = customerInfoContainer.getOneAccountInfo();
		restActions.assertTrue( oneAccountInfo != null, ONE_ACCOUNT_REF_IS_NULL );
		
		if ( oneAccountInfo != null ) {
			Integer actualOneAccountId = oneAccountInfo.getOneAccountId();
			String sExpectedOneAccountId = data.get(EXPECTED_ONE_ACCOUNT_ID);
			if ( sExpectedOneAccountId != null ) {
				Integer expectedOneAccountId = Integer.valueOf(sExpectedOneAccountId);
				restActions.assertTrue( expectedOneAccountId.equals(actualOneAccountId),
						String.format(BAD_ONE_ACCOUNT_ID_FMT, 
								expectedOneAccountId, actualOneAccountId));
			}
		}
	}
}
