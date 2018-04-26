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
import com.cubic.nisjava.apiobjects.WSXCubHdrJSON;
import com.cubic.nisjava.constants.AppConstants;
import com.cubic.nisjava.dataproviders.NISDataProviderSource;
import com.google.gson.Gson;
import com.sun.jersey.api.client.ClientResponse;

/**
 * This test class will try to Get a EULA from NIS using
 * a eulaId that doesn't exist in the EULA_INFO table in
 * the CXS Database.  Instead of returning a Document,
 * the response will contain a 'errors.general.record.not.found'
 * Error Key.
 * 
 * @author 203402
 *
 */
public class EULAWithBadEulaIdTest extends RESTEngine {

	private final Logger LOG = Logger.getLogger(this.getClass().getName());
	private static final String INFO_EULA_ID = "INFO_EULA_ID";
	private static final String LOC_LOCALE_TAG = "LOC_LOCALE_TAG";
	private static final String EULA_ID = "eulaId";
	private static final String RECORD_NOT_FOUND = "errors.general.record.not.found";
	private static final String DATA_VALIDATION_ERROR = "DataValidationError";
	private static final String DATA_VALIDATION_FMT = "BAD RESULT: EXPECTED '%s', FOUND '%s'";
	private static final String BAD_UI_NULL_FMT = "BAD UID: UID IS NULL BUT IT SHOULD NOT BE";
	private static final String BAD_UI_LENGTH_0_MSG = "BAD UID: UID HAS LENGTH 0";
	private static final String BAD_FIELD_NAME_FMT = "BAD FIELD NAME: EXPECTED '%s', FOUND '%s'";
	private static final String BAD_ERROR_KEY_FMT = "BAD ERROR KEY: EXPECTED '%s', FOUND '%s'";
	private static final String RESPONSE_IS_NULL = "RESPONSE IS NULL BUT SHOULD NOT BE NULL";
	private static final String BAD_RESPONSE_CODE = "WRONG HTTP RESPONSE CODE - EXPECTED 404, FOUND ";
	
	/**
	 * Try to get a EULA giving a non-existent EulaId
	 * in order to elicit a errors.general.record.not.found
	 * error.
	 * 
	 * testRailId: 633247
	 * 
	 * @param context 	The TestNG context reference.
	 * @param data		The test data.
	 * @throws Throwable Thrown if something goes wrong.
	 */
	@Test(dataProvider = AppConstants.DATA_PROVIDER, dataProviderClass = NISDataProviderSource.class)
	public void getEULA(ITestContext context, Hashtable<String, String> data) throws Throwable {
		String testCaseName = data.get("TestCase_Description");
		LOG.info("##### Starting Test Case " + testCaseName);
		
		try {
			LOG.info("##### Setting up automation test...");
			BackOfficeGlobals.ENV.setEnvironmentVariables();
			RESTActions restActions = setupAutomationTest(context, testCaseName);
			
            String sURL = "http://" + BackOfficeGlobals.ENV.NIS_HOST + ":" + BackOfficeGlobals.ENV.NIS_PORT
                    + "/nis/csapi/v1/eula/" + data.get(INFO_EULA_ID)
                    + "/" + data.get(LOC_LOCALE_TAG) + "?returnDocument=true";
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
			restActions.assertTrue(status == HttpURLConnection.HTTP_NOT_FOUND, msg);
			
			String response = clientResponse.getEntity(String.class);
			LOG.info("##### Got the response body: " + response);
			restActions.assertTrue(response != null, RESPONSE_IS_NULL);
			
			Gson gson = new Gson();
			
			LOG.info("##### Parsing the response content...");
			WSXCubHdrJSON errorMsg = gson.fromJson(response, WSXCubHdrJSON.class);
			
			LOG.info("##### Testing the response content...");
			String result = errorMsg.getHdr().getResult();
			restActions.assertTrue(DATA_VALIDATION_ERROR.equals(result),
					String.format(DATA_VALIDATION_FMT, DATA_VALIDATION_ERROR, result));
			
			String uid = errorMsg.getHdr().getUid();
			restActions.assertTrue(uid != null, BAD_UI_NULL_FMT);
			
			if ( uid != null ) {
				restActions.assertTrue(uid.length() > 0, BAD_UI_LENGTH_0_MSG);
			}
			
			String fieldName = errorMsg.getHdr().getFieldName();
			restActions.assertTrue(EULA_ID.equals(fieldName),
					String.format(BAD_FIELD_NAME_FMT, EULA_ID, fieldName));
			
			String errorKey = errorMsg.getHdr().getErrorKey();
			restActions.assertTrue(RECORD_NOT_FOUND.equals(errorKey),
					String.format(BAD_ERROR_KEY_FMT, RECORD_NOT_FOUND, errorKey));
			
		} finally {
			teardownAutomationTest(context, testCaseName);
			LOG.info("##### Done!");
		}
	}
}
