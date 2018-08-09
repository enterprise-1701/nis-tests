package com.cubic.nistests.tests;

import java.lang.invoke.MethodHandles;
import java.net.HttpURLConnection;
import java.util.Hashtable;
import java.util.UUID;

import org.apache.log4j.Logger;
import org.testng.ITestContext;
import org.testng.annotations.Test;

import com.cubic.accelerators.RESTActions;
import com.cubic.accelerators.RESTConstants;
import com.cubic.backoffice.constants.BackOfficeGlobals;
import com.cubic.backoffice.utils.BackOfficeUtils;
import com.cubic.logutils.Log4jUtil;
import com.cubic.nisjava.apiobjects.WSCreateCustomerResponse;
import com.cubic.nisjava.apiobjects.WSXCubHdrJSON;
import com.cubic.nisjava.constants.AppConstants;
import com.cubic.nisjava.dataproviders.NISDataProviderSource;
import com.cubic.nisjava.utils.NISUtils;
import com.google.gson.Gson;
import com.sun.jersey.api.client.ClientResponse;

/**
 * A test class to call the NWAPI V2 /eula/accept API.
 * 
 * @see http://10.252.1.21:8201/nis/nwapi/v2/customer/8F941633-659B-E811-80CC-000D3A36F32A/contact/97941633-659B-E811-80CC-000D3A36F32A/eula/accept
 * 
 * @author 203402
 *
 */
public class NWAPIV2_CustomerContactEULAAcceptTest extends NWAPIV2_CustomerBase {

	private static final String EULA_ID = "EULA_ID";
	private static final String LOCALE = "LOCALE";
	private static final String FAILURE = "Failure:";
    private static final String CLASS_NAME = MethodHandles.lookup().lookupClass().getSimpleName();
    private static final Logger LOG = Logger.getLogger(CLASS_NAME);
    
    /**
     * An @Test method to call the NWAPI V2 /eula/accept API.
     * 
     * testRailId = 1205020
     * 
     * @param context  The TestNG context object
     * @param data  Test Data from the JSON input file
     * @throws Throwable  Thrown if something goes wrong
     */
	@Test(dataProvider = AppConstants.DATA_PROVIDER, dataProviderClass = NISDataProviderSource.class)
	public void customerContactEULAAccept(ITestContext context, Hashtable<String, String> data) throws Throwable {
		String testCaseName = data.get("TestCase_Description");
		LOG.info("##### Starting Test Case " + testCaseName);
		
		try {
			LOG.info("##### Setting up automation test...");
			BackOfficeGlobals.ENV.setEnvironmentVariables();
			RESTActions restActions = setupAutomationTest(context, testCaseName);
			
            LOG.info("##### Creating GET request headers");
			Hashtable<String,String> headerTable = BackOfficeUtils.createRESTHeader(RESTConstants.APPLICATION_JSON);			
			
			String uniqueID = UUID.randomUUID().toString();
			String username = uniqueID + "@test.com";
			String password = "Pas5word!";
			
			LOG.info("##### Call the Prevalidate API");
			NISUtils.prevalidate(restActions, headerTable, username, password);
			
			LOG.info("##### Get the Security Questions");
			String securityQuestion = NISUtils.securityQuestion(restActions, headerTable);
			
			LOG.info("##### Call the Create Customer API");
			WSCreateCustomerResponse customerResponse = NISUtils.createCustomer(restActions, headerTable, username, password, securityQuestion);
			String customerId = customerResponse.getCustomerId();
			String contactId = customerResponse.getContactId();
			
			LOG.info("##### Call the Complete Registration API");
			NISUtils.completeRegistration(restActions, headerTable, customerId);

			LOG.info("##### Call the EULA Accept API for the first time");
			ClientResponse clientResponse = NISUtils.customerContactEULAAccept(
					restActions,headerTable,customerId,contactId,data.get(EULA_ID),data.get(LOCALE));
			
			LOG.info("##### Verifying HTTP response code to be 204");
			int status = clientResponse.getStatus();
			String msg = String.format(BAD_RESPONSE_CODE_FMT, HttpURLConnection.HTTP_NO_CONTENT, status);
			restActions.assertTrue(status == HttpURLConnection.HTTP_NO_CONTENT, msg);
			
			LOG.info("##### Call the EULA Accept API for a second time");
			clientResponse = NISUtils.customerContactEULAAccept(
					restActions,headerTable,customerId,contactId,data.get(EULA_ID),data.get(LOCALE));
			
			LOG.info("##### Verifying HTTP response code to be 400");
			status = clientResponse.getStatus();
			msg = String.format(BAD_RESPONSE_CODE_FMT, HttpURLConnection.HTTP_BAD_REQUEST, status);
			restActions.assertTrue(status == HttpURLConnection.HTTP_BAD_REQUEST, msg);
			
			String response = clientResponse.getEntity(String.class);
			LOG.info("##### Got the response body");
			restActions.assertTrue(response != null, RESPONSE_IS_NULL);
			LOG.info( response );
			
			Gson gson = new Gson();
			
			LOG.info("##### Parsing the response content...");
			WSXCubHdrJSON errorMsg = gson.fromJson(response, WSXCubHdrJSON.class);            
	        
			LOG.info("##### Testing the response content...");
			String expectedResult = data.get(EXPECTED_RESULT);
			String actualResult = errorMsg.getHdr().getResult();
			restActions.assertTrue(expectedResult.equals(actualResult),
					String.format(RESULT_FMT, expectedResult, actualResult));
			
			String uid = errorMsg.getHdr().getUid();
			restActions.assertTrue(uid != null, BAD_UI_NULL_FMT);
			
			if ( uid != null ) {
				restActions.assertTrue(uid.length() > 0, BAD_UI_LENGTH_0_MSG);
			}
			
			String expectedErrorKey = data.get(EXPECTED_ERROR_KEY);
			String actualErrorKey = errorMsg.getHdr().getErrorKey();
			restActions.assertTrue(expectedErrorKey.equals(actualErrorKey),
					String.format(BAD_ERROR_KEY_FMT, expectedErrorKey, actualErrorKey));
			
		}  catch (Exception e) {
			RESTActions restActions = setupAutomationTest(context, testCaseName);
			restActions.failureReport(FAILURE, FAILURE + e);
			teardownAutomationTest(context, testCaseName);
			LOG.error(Log4jUtil.getStackTrace(e));
			throw new RuntimeException(e);	
		} finally {
			teardownAutomationTest(context, testCaseName);
			LOG.info("##### Done!");
		}
	}
}
