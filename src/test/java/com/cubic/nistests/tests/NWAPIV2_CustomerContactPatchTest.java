package com.cubic.nistests.tests;

import java.lang.invoke.MethodHandles;
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
import com.cubic.nisjava.constants.AppConstants;
import com.cubic.nisjava.dataproviders.NISDataProviderSource;

/**
 * This test class calls the PATCH Customer Contact API:
 * 
 * PATCH http://10.252.1.21:8201/nis/nwapi/v2/customer/E4B71816-F990-E811-80CC-000D3A36F32A/contact/ECB71816-F990-E811-80CC-000D3A36F32A
 * 
 * @author 203402
 *
 */
public class NWAPIV2_CustomerContactPatchTest extends NWAPIV2_CustomerBase {

    private static final String CLASS_NAME = MethodHandles.lookup().lookupClass().getSimpleName();
    private static final Logger LOG = Logger.getLogger(CLASS_NAME);	
	
	/**
	 * This @Test method calls the PATCH customer/<customerid>/contact/<contact-id> API.
	 * 
	 * testRailId = 1142579
	 * 
	 * @param context  The TestNG Context object
	 * @param data	The Test Data from the JSON input file
	 * @throws Throwable  Thrown if something goes wrong
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
			Hashtable<String,String> headerTable = BackOfficeUtils.createRESTHeader(
					RESTConstants.APPLICATION_JSON);  
			
			String uniqueID = UUID.randomUUID().toString();
			String username = uniqueID + "@test.com";
			String password = "Pas5word!";
			
			LOG.info("##### Call the Prevalidate API");
			prevalidate(restActions, headerTable, username, password);
			
			LOG.info("##### Get a Security Question");
			String securityQuestion = securityQuestion(restActions, headerTable);
			
			LOG.info("##### Call the Create Customer API");
			WSCreateCustomerResponse customerResponse = createCustomer(
					restActions, headerTable, username, password, securityQuestion);
			String expectedCustomerId = customerResponse.getCustomerId();
			String expectedContactId = customerResponse.getContactId();
			data.put(CUSTOMER_ID, expectedCustomerId);
			data.put(CONTACT_ID, expectedContactId);
					
			LOG.info("##### Call the Complete Registration API");
			completeRegistration(restActions, headerTable, expectedCustomerId);
			
			LOG.info("##### Call the PATCH Contact API");
			patchContact(restActions, headerTable, data);
			
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
