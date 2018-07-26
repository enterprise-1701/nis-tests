package com.cubic.nistests.tests;

import java.util.Hashtable;
import java.util.UUID;

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
import com.google.gson.Gson;

/**
 * This test class calls the API
 * 
 * GET http://10.252.1.21:8201/nis/nwapi/v2/customer/4846DEB2-5C90-E811-80CC-000D3A36F32A/contact/F57A450A-5A90-E811-80CC-DEADBEEF0000
 * 
 * to elicit HTTP Response Code 404 and Error Key value "errors.general.record.not.found"
 * because there is no Contact with Id F57A450A-5A90-E811-80CC-DEADBEEF0000.
 * 
 * @author 203402
 *
 */
public class NWAPIV2_CustomerContact_InvalidContactIdTest extends NWAPIV2_CustomerBase {

	/**
	 * This @Test method calls the API
	 * 
	 * GET http://10.252.1.21:8201/nis/nwapi/v2/customer/4846DEB2-5C90-E811-80CC-000D3A36F32A/contact/F57A450A-5A90-E811-80CC-DEADBEEF0000
	 * 
	 * to elicit HTTP Response Code 404 and Error Key value "errors.general.record.not.found"
	 * because there is no Contact with Id F57A450A-5A90-E811-80CC-DEADBEEF0000.
	 * 
	 * @param context  The TestNG context object.
	 * @param data  The Test Data from the JSON file.
	 * @throws Throwable  Thrown if something went wrong.
	 */
	@Test(dataProvider = AppConstants.DATA_PROVIDER, dataProviderClass = NISDataProviderSource.class)
	public void getContact(ITestContext context, Hashtable<String, String> data) throws Throwable {
		String testCaseName = data.get("TestCase_Description");
		LOG.info("##### Starting Test Case " + testCaseName);
		RESTActions restActions = setupAutomationTest(context, testCaseName);
		try {
			LOG.info("##### Setting up automation test...");
			BackOfficeGlobals.ENV.setEnvironmentVariables();
			
            LOG.info("##### Creating GET request headers");
			Hashtable<String,String> headerTable = BackOfficeUtils.createRESTHeader(RESTConstants.APPLICATION_JSON);			
			
			String uniqueID = UUID.randomUUID().toString();
			String username = uniqueID + "@test.com";
			String password = "Pas5word!";
			
			LOG.info("##### Call the Prevalidate API");
			prevalidate(restActions, headerTable, username, password);
			
			LOG.info("##### Get a Security Question");
			String securityQuestion = securityQuestion(restActions, headerTable);
			
			LOG.info("##### Call the Create Customer API");
			WSCreateCustomerResponse customerResponse = createCustomer(restActions, headerTable, username, password, securityQuestion);
			String expectedCustomerId = customerResponse.getCustomerId();
			data.put(CUSTOMER_ID, expectedCustomerId);
					
			LOG.info("##### Call the Complete Registration API");
			completeRegistration(restActions, headerTable, expectedCustomerId);
			
			LOG.info("##### Call the GET Contact API");
			String response = getContact(restActions, headerTable, data);
	
			LOG.info("##### Parsing the response content...");
			Gson gson = new Gson();
			WSXCubHdrJSON errorMsg = gson.fromJson(response, WSXCubHdrJSON.class);            
	        
			LOG.info("##### Testing the response content...");
			String expectedResult = data.get(EXPECTED_RESULT);
			String actualResult = errorMsg.getHdr().getResult();
			restActions.assertTrue(expectedResult.equals(actualResult),
					String.format(RESULT_FMT, expectedResult, actualResult));
			
			String expectedFieldName = data.get(EXPECTED_FIELDNAME);
			String actualFieldName = errorMsg.getHdr().getFieldName();
			restActions.assertTrue(expectedFieldName.equals(actualFieldName),
					String.format(BAD_FIELD_NAME_FMT, expectedFieldName, actualFieldName));
			
			String expectedErrorKey = data.get(EXPECTED_ERROR_KEY);
			String actualErrorKey = errorMsg.getHdr().getErrorKey();
			restActions.assertTrue(expectedErrorKey.equals(actualErrorKey),
					String.format(BAD_ERROR_KEY_FMT, expectedErrorKey, actualErrorKey));
			
		} catch( Exception e ) {
			restActions.failureReport("setup", "Setup failure occurred: " + e);
			teardownAutomationTest(context, testCaseName);
			LOG.error(Log4jUtil.getStackTrace(e));
			throw new RuntimeException(e);
		} finally {
			teardownAutomationTest(context, testCaseName);
			LOG.info("##### Done!");
		}	
	}
}
