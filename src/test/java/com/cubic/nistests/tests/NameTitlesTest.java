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
import com.cubic.nisjava.apiobjects.WSNameTitleList;
import com.cubic.nisjava.apiobjects.WSNameTitle;

/**
 * This test case verifies the operation of the NIS Get Name Titles API.
 * 
 * @author 203402
 *
 */
public class NameTitlesTest extends RESTEngine {

	private final Logger LOG = Logger.getLogger(this.getClass().getName());
	
	/**
	 * Get the Name Titles list from NIS.
	 * 
	 * testRailId = 685849
	 * 
	 * @param context  The TestNG context object reference
	 * @param data  The Test Data
	 * @throws Throwable  Thrown if something went wrong.
	 */
	@Test(dataProvider = AppConstants.DATA_PROVIDER, dataProviderClass = NISDataProviderSource.class)
	public void getNameTitles(ITestContext context, Hashtable<String, String> data) throws Throwable {
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
			
			LOG.info("##### Making HTTP request to get the Name Titles...");
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
			WSNameTitleList expectedNameTitleList = buildExpectedTestData( data );
            
			LOG.info("##### Parse the actual response...");
			Gson gson = new Gson();
			WSNameTitleList actualNameTitleList = gson.fromJson(response, WSNameTitleList.class);
			
			LOG.info("##### Compare the expected test data with the actual test data");
			boolean bEqual = expectedNameTitleList.equals( actualNameTitleList );
			restActions.assertTrue(bEqual, WSNameTitleList.getErrorMessage());			
			
		} finally {
			teardownAutomationTest(context, testCaseName);
			LOG.info("##### Done!");
		}
	}
	
	/**
	 * Helper method to build the Expected test data.
	 * 
	 * @param data  The test data from the JSON input file.
	 * @return  The expected test data as a WSNameTitleList object.
	 */
	private WSNameTitleList buildExpectedTestData( Hashtable<String,String> data) {
		WSNameTitleList result = new WSNameTitleList();
		List<WSNameTitle> nameTitleList = new ArrayList<WSNameTitle>();
		
		WSNameTitle dame = new WSNameTitle();
		dame.setKey(data.get("NAMETITLE_KEY_DAME"));
		dame.setValue(data.get("NAMETITLE_VAL_DAME"));
		nameTitleList.add( dame );
		
		WSNameTitle dr = new WSNameTitle();
		dr.setKey(data.get("NAMETITLE_KEY_DR"));
		dr.setValue(data.get("NAMETITLE_VAL_DR"));
		nameTitleList.add( dr );		
		
		WSNameTitle lady = new WSNameTitle();
		lady.setKey(data.get("NAMETITLE_KEY_LADY"));
		lady.setValue(data.get("NAMETITLE_VAL_LADY"));
		nameTitleList.add( lady );
		
		WSNameTitle lord = new WSNameTitle();
		lord.setKey(data.get("NAMETITLE_KEY_LORD"));
		lord.setValue(data.get("NAMETITLE_VAL_LORD"));
		nameTitleList.add( lord );
		
		WSNameTitle miss = new WSNameTitle();
		miss.setKey(data.get("NAMETITLE_KEY_MISS"));
		miss.setValue(data.get("NAMETITLE_VAL_MISS"));
		nameTitleList.add( miss );
		
		WSNameTitle mr = new WSNameTitle();
		mr.setKey(data.get("NAMETITLE_KEY_MR"));
		mr.setValue(data.get("NAMETITLE_VAL_MR"));
		nameTitleList.add( mr );
		
		WSNameTitle mrs = new WSNameTitle();
		mrs.setKey(data.get("NAMETITLE_KEY_MRS"));
		mrs.setValue(data.get("NAMETITLE_VAL_MRS"));
		nameTitleList.add( mrs );
		
		WSNameTitle ms = new WSNameTitle();
		ms.setKey(data.get("NAMETITLE_KEY_MS"));
		ms.setValue(data.get("NAMETITLE_VAL_MS"));
		nameTitleList.add( ms );
		
		WSNameTitle prof = new WSNameTitle();
		prof.setKey(data.get("NAMETITLE_KEY_PROF"));
		prof.setValue(data.get("NAMETITLE_VAL_PROF"));
		nameTitleList.add( prof );
		
		WSNameTitle rev = new WSNameTitle();
		rev.setKey(data.get("NAMETITLE_KEY_REV"));
		rev.setValue(data.get("NAMETITLE_VAL_REV"));
		nameTitleList.add( rev );
		
		WSNameTitle sir = new WSNameTitle();
		sir.setKey(data.get("NAMETITLE_KEY_SIR"));
		sir.setValue(data.get("NAMETITLE_VAL_SIR"));
		nameTitleList.add( sir );		
		
		WSNameTitle sr = new WSNameTitle();
		sr.setKey(data.get("NAMETITLE_KEY_SR"));
		sr.setValue(data.get("NAMETITLE_VAL_SR"));
		nameTitleList.add( sr );		
		
		result.setNameTitles(nameTitleList);
		return result;
	}
	
	/**
	 * Build the URL used to get the Name Titles list.
	 * 
	 * @return The URL in string format.
	 */
	private String buildURL() {
        return "http://" + BackOfficeGlobals.ENV.NIS_HOST + ":" + BackOfficeGlobals.ENV.NIS_PORT
                + "/nis/nwapi/v2/customerservice/nametitles";
	}	
}
