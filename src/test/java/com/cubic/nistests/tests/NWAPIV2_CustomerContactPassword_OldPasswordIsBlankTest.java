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
 * This test class calls the Customer/Contact/Password API
 * 
 * POST http://10.252.1.21:8201/nis/nwapi/v2/customer/334C9BFE-499B-E811-80CC-000D3A36F32A/contact/3B4C9BFE-499B-E811-80CC-000D3A36F32A/password
 * 
 * with the following Request Body:
 * {
 *   "oldpassword":"",
 *   "newpassword": "Winter22mute!"
 * }
 * 
 * The old password is specified as "", in order to evoke an error message "Field is blank". 
 * 
 * @author 203402
 *
 */
public class NWAPIV2_CustomerContactPassword_OldPasswordIsBlankTest extends NWAPIV2_CustomerBase {
	
    private static final String CLASS_NAME = MethodHandles.lookup().lookupClass().getSimpleName();
    private static final Logger LOG = Logger.getLogger(CLASS_NAME);
    
	/**
	 * This @Test method calls the API
	 * 
	 * http://10.252.1.21:8201/nis/nwapi/v2/customer/<customer-id>/contact/<contact-id>/password
	 * 
	 * to try to change an existing password.  The old password is specified as "", in order to
	 * evoke an error message Old "Field is blank".
	 * 
	 * testRailId = 1203474
	 * 
	 * @param context
	 * @param data
	 * @throws Throwable
	 */
	@Test(dataProvider = AppConstants.DATA_PROVIDER, dataProviderClass = NISDataProviderSource.class)
	public void tryToResetWithABlankPassword(ITestContext context, Hashtable<String, String> data) throws Throwable {
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

			ClientResponse clientResponse = NISUtils.resetCustomerContactPassword(
					restActions,headerTable,customerId,contactId,data.get("OLD_PASSWORD"),data.get("NEW_PASSWORD"));
			
			LOG.info("##### Verifying HTTP response code to be 400");
			int status = clientResponse.getStatus();
			String msg = "WRONG HTTP RESPONSE CODE - EXPECTED 400, FOUND " + status;
			restActions.assertTrue(status == HttpURLConnection.HTTP_BAD_REQUEST, msg);
			
			String response = clientResponse.getEntity(String.class);
			LOG.info("##### Got the response body");
			restActions.assertTrue(response != null, "RESPONSE IS NULL BUT SHOULD NOT BE NULL");
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
			
			String actualFieldName = errorMsg.getHdr().getFieldName();
			String expectedFieldName = data.get(EXPECTED_FIELDNAME);
			restActions.assertTrue(expectedFieldName.equals(actualFieldName),
					String.format(BAD_FIELD_NAME_FMT, expectedFieldName, actualFieldName));
			
			String expectedErrorKey = data.get(EXPECTED_ERROR_KEY);
			String actualErrorKey = errorMsg.getHdr().getErrorKey();
			restActions.assertTrue(expectedErrorKey.equals(actualErrorKey),
					String.format(BAD_ERROR_KEY_FMT, expectedErrorKey, actualErrorKey));
			
		}  catch (Exception e) {
			RESTActions restActions = setupAutomationTest(context, testCaseName);
			restActions.failureReport("failure", "failure occurred: " + e);
			teardownAutomationTest(context, testCaseName);
			LOG.error(Log4jUtil.getStackTrace(e));
			throw new RuntimeException(e);	
		} finally {
			teardownAutomationTest(context, testCaseName);
			LOG.info("##### Done!");
		}
	}
}
