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
import com.google.gson.Gson;
import com.sun.jersey.api.client.ClientResponse;
import com.cubic.nisjava.apiobjects.WSPhoneTypeList;
import com.cubic.nisjava.apiobjects.WSPhoneType;

/**
 * This test case verifies the operation of the NIS Get Phone Types API.
 * 
 * @author 203402
 *
 */
public class PhoneTypesTest extends RESTEngine {

	private final Logger LOG = Logger.getLogger(this.getClass().getName());	
	
	/**
	 * Get the Phone Types.
	 * 
	 * testRailId = 685823
	 * 
	 * @param context  The TestNG context object reference
	 * @param data  The Test Data
	 * @throws Throwable  Thrown if something went wrong.
	 */
	@Test(dataProvider = AppConstants.DATA_PROVIDER, dataProviderClass = NISDataProviderSource.class)
	public void getPhoneTypes(ITestContext context, Hashtable<String, String> data) throws Throwable {
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
			
			LOG.info("##### Making HTTP request to get the Phone Types...");
			ClientResponse clientResponse = restActions.getClientResponse(sURL, headerTable, urlQueryParameters,
					RESTConstants.APPLICATION_JSON);

			LOG.info("##### Verifying HTTP response code...");
			int status = clientResponse.getStatus();
			String msg = "WRONG HTTP RESPONSE CODE - EXPECTED 200, FOUND " + status;
			restActions.assertTrue(status == HttpURLConnection.HTTP_OK, msg);
			
			String response = clientResponse.getEntity(String.class);
			LOG.info("##### Got the response body");
			restActions.assertTrue(response != null, "RESPONSE IS NULL BUT SHOULD NOT BE NULL");
			LOG.info( response );
				
			LOG.info("##### Build expected Test Data...");
			WSPhoneTypeList expectedPhoneTypeList = buildExpectedTestData( data );
			
			LOG.info("##### Parse the actual response...");
			Gson gson = new Gson();
			WSPhoneTypeList actualPhoneTypeList = gson.fromJson(response, WSPhoneTypeList.class);
			
			LOG.info("##### Compare the expected test data with the actual test data");
			boolean bEqual = expectedPhoneTypeList.equals( actualPhoneTypeList );
			restActions.assertTrue(bEqual, WSPhoneTypeList.getErrorMessage());			
			
		} finally {
			teardownAutomationTest(context, testCaseName);
			LOG.info("##### Done!");
		}
	}
	
	/**
	 * Build the URL used to get the phone type list.
	 * 
	 * @return The URL in string format.
	 */
	private String buildURL() {
        return "http://" + BackOfficeGlobals.ENV.NIS_HOST + ":" + BackOfficeGlobals.ENV.NIS_PORT
                + "/nis/nwapi/v2/customerservice/phonetypes";
	}
	
	/**
	 * Helper method to build the Expected test data.
	 * 
	 * @param data  The test data from the JSON input file.
	 * @return  The expected test data as a WSPhoneTypeList object.
	 */
	private WSPhoneTypeList buildExpectedTestData( Hashtable<String,String> data) {
		WSPhoneTypeList result = new WSPhoneTypeList();
		List<WSPhoneType> phoneTypeList = new ArrayList<WSPhoneType>();
		
		WSPhoneType home = new WSPhoneType();
		home.setKey( data.get("PHONETYPE_KEY_H") );
		home.setValue(data.get("PHONETYPE_VAL_H"));
		phoneTypeList.add( home );		
	
		WSPhoneType fax = new WSPhoneType();
		fax.setKey(data.get("PHONETYPE_KEY_F"));
		fax.setValue(data.get("PHONETYPE_VAL_F"));
		phoneTypeList.add( fax );
		
		WSPhoneType mobile = new WSPhoneType();
		mobile.setKey(data.get("PHONETYPE_KEY_M"));
		mobile.setValue(data.get("PHONETYPE_VAL_M"));
		phoneTypeList.add( mobile );		
		
		WSPhoneType work = new WSPhoneType();
		work.setKey(data.get("PHONETYPE_KEY_W"));
		work.setValue(data.get("PHONETYPE_VAL_W"));
		phoneTypeList.add( work );
		
		WSPhoneType daytime = new WSPhoneType();
		daytime.setKey(data.get("PHONETYPE_KEY_D"));
		daytime.setValue(data.get("PHONETYPE_VAL_D"));
		phoneTypeList.add( daytime );
		
		WSPhoneType evening = new WSPhoneType();
		evening.setKey(data.get("PHONETYPE_KEY_E"));
		evening.setValue(data.get("PHONETYPE_VAL_E"));
		phoneTypeList.add( evening );
		
		WSPhoneType pager = new WSPhoneType();
		pager.setKey(data.get("PHONETYPE_KEY_P"));
		pager.setValue(data.get("PHONETYPE_VAL_P"));
		phoneTypeList.add( pager );
		
		WSPhoneType other = new WSPhoneType();
		other.setKey(data.get("PHONETYPE_KEY_O"));
		other.setValue(data.get("PHONETYPE_VAL_O"));
		phoneTypeList.add( other );
		
		result.setPhoneTypes(phoneTypeList);
		return result;
	}
}
