package com.cubic.nistests.tests;

import java.util.Hashtable;
import java.util.List;

import org.apache.log4j.Logger;

import com.cubic.accelerators.RESTActions;
import com.cubic.backoffice.constants.BackOfficeGlobals;
import com.cubic.nisjava.apiobjects.WSCustomerInfo;
import com.cubic.nisjava.apiobjects.WSCustomerInfoContainer;
import com.cubic.nisjava.apiobjects.WSCustomerStatus;
import com.cubic.nisjava.apiobjects.WSFundingSource;
import com.cubic.nisjava.apiobjects.WSFundingSourceList;
import com.cubic.nisjava.apiobjects.WSOneAccountInfo;
import com.google.gson.Gson;

public class NWAPIV2_CustomerViewAccountSummaryGood_ReturnSubset 
	extends NWAPIV2_CustomerViewAccountSummaryGood_ReturnAll {

	private static final String ONE_ACCOUNT_IS_NOT_NULL = "ONE ACCOUNT IS NOT NULL BUT IT SHOULD BE";	
	
	private final Logger LOG = Logger.getLogger(this.getClass().getName());		
	
	/**
	 * Helper method to build the URL used to call the API.
	 * 
	 * @param data  The Test Data from the JSON input file.
	 * @return The URL in string form.
	 */
	@Override
	protected String buildURL( Hashtable<String,String> data ) {
        String sURL = "http://" + BackOfficeGlobals.ENV.NIS_HOST + ":" + BackOfficeGlobals.ENV.NIS_PORT
                + "/nis/nwapi/v2/customer/" + data.get(CUSTOMER_ID)
                + "?returnCustomerInfo=" + data.get(RETURN_CUSTOMER_INFO)
                + "&returnFundingSources=" + data.get(RETURN_FUNDING_SOURCES);
		return sURL;
	}
	
	/**
	 * Helper method to verify the response contains the expected data.
	 * 
	 * @param data  The Test Data from the JSON input file.
	 * @param restActions  The RESTActions object used by the testMain method.
	 * @param response  The Response in String form obtained by the GET method.
	 */
	@Override
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
		
		// test that the One Account block is absent
		WSOneAccountInfo oneAccountInfo = customerInfoContainer.getOneAccountInfo();
		restActions.assertTrue( oneAccountInfo == null, ONE_ACCOUNT_IS_NOT_NULL );
	}

}

