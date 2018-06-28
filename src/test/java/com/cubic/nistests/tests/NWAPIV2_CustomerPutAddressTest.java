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
import com.cubic.nisjava.apiobjects.WSCustomerPostAddressResponse;
import com.cubic.nisjava.constants.AppConstants;
import com.cubic.nisjava.dataproviders.NISDataProviderSource;

/**
 * This test class calls the customer/<customer-id>/address/<address-id> API.
 * 
 * @see PUT http://10.252.1.21:8201/nis/nwapi/v2/customer/4E5885AA-9179-E811-80CC-000D3A36F32A/address/7B5885AA-9179-E811-80CC-000D3A36F32A
 * 
 * @author 203402
 *
 */
public class NWAPIV2_CustomerPutAddressTest extends NWAPIV2_CustomerBase {
	
	/**
	 * This @Test method calls the PUT Address API and verifies the response is 204 No Content.
	 *
 	 * @see PUT http://10.252.1.21:8201/nis/nwapi/v2/customer/4E5885AA-9179-E811-80CC-000D3A36F32A/address/7B5885AA-9179-E811-80CC-000D3A36F32A
	 *
	 * testRailId = 978025
	 * 
	 * @param context  The TestNG Context object
	 * @param data	The Test Data from the JSON input file
	 * @throws Throwable  Thrown if something goes wrong
	 */
	@Test(dataProvider = AppConstants.DATA_PROVIDER, dataProviderClass = NISDataProviderSource.class)
	public void updateCustomerAddress(ITestContext context, Hashtable<String, String> data) throws Throwable {
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
			prevalidate(restActions, headerTable, username, password);
			
			LOG.info("##### Get a Security Question");
			String securityQuestion = securityQuestion(restActions, headerTable);
			
			LOG.info("##### Call the Create Customer API");
			String expectedCustomerId = createCustomer(restActions, headerTable, username, password, securityQuestion);
			
			LOG.info("##### Call the Complete Registration API");
			completeRegistration(restActions, headerTable, expectedCustomerId);
			
			LOG.info("##### Call the Post Address API");
			WSCustomerPostAddressResponse postAddressResponse = addCustomerAddress(restActions, data, headerTable, expectedCustomerId);
			
			LOG.info("##### Call the PUT Address API");
			updateCustomerAddress( restActions, data, headerTable, expectedCustomerId, postAddressResponse.getAddressId() );
		
		} catch (Exception e) {
			RESTActions restActions = setupAutomationTest(context, testCaseName);
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
