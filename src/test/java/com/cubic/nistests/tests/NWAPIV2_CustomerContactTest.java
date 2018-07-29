package com.cubic.nistests.tests;

import java.util.Hashtable;
import java.util.UUID;

import org.apache.log4j.Logger;
import org.testng.ITestContext;
import org.testng.annotations.Test;

import java.lang.invoke.MethodHandles;

import com.cubic.accelerators.RESTActions;
import com.cubic.accelerators.RESTConstants;
import com.cubic.backoffice.constants.BackOfficeGlobals;
import com.cubic.backoffice.utils.BackOfficeUtils;
import com.cubic.logutils.Log4jUtil;
import com.cubic.nisjava.apiobjects.WSContactContainer;
import com.cubic.nisjava.apiobjects.WSCustomerContact;
import com.cubic.nisjava.apiobjects.WSCreateCustomerResponse;
import com.cubic.nisjava.constants.AppConstants;
import com.cubic.nisjava.dataproviders.NISDataProviderSource;
import com.cubic.nisjava.utils.NISUtils;
import com.google.gson.Gson;

/**
 * This test class calls the GET Contact API.
 * 
 * GET http://10.252.1.21:8201/nis/nwapi/v2/customer/2B911170-4690-E811-80CC-000D3A36F32A/contact/33911170-4690-E811-80CC-000D3A36F32A
 * 
 * @author 203402
 *
 */
public class NWAPIV2_CustomerContactTest extends NWAPIV2_CustomerBase {

	private static final String CLASS_NAME = MethodHandles.lookup().lookupClass().getSimpleName();
    private static final Logger LOG = Logger.getLogger(CLASS_NAME);	
	
	/**
	 * This @Test method calls the GET Contact API.
	 * 
	 * testRailId = 1142251
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
			Hashtable<String,String> headerTable = BackOfficeUtils.createRESTHeader(RESTConstants.APPLICATION_JSON);			
			
			String uniqueID = UUID.randomUUID().toString();
			String username = uniqueID + "@test.com";
			String password = "Pas5word!";
			
			LOG.info("##### Call the Prevalidate API");
			NISUtils.prevalidate(restActions, headerTable, username, password);
			
			LOG.info("##### Get a Security Question");
			String securityQuestion = NISUtils.securityQuestion(restActions, headerTable);
			
			LOG.info("##### Call the Create Customer API");
			WSCreateCustomerResponse customerResponse = NISUtils.createCustomer(restActions, headerTable, username, password, securityQuestion);
			String expectedCustomerId = customerResponse.getCustomerId();
			String expectedContactId = customerResponse.getContactId();
			data.put(CUSTOMER_ID, expectedCustomerId);
			data.put(CONTACT_ID, expectedContactId);
					
			LOG.info("##### Call the Complete Registration API");
			NISUtils.completeRegistration(restActions, headerTable, expectedCustomerId);
			
			LOG.info("##### Call the GET Contact API");
			String response = NISUtils.getContact(restActions, headerTable, data);
	
			LOG.info("##### Parsing the response content...");
			Gson gson = new Gson();
			WSContactContainer wsContactContainer = gson.fromJson(response, WSContactContainer.class);
			
			restActions.assertTrue(wsContactContainer != null, "wsContactContainer IS NULL BUT SHOULD NOT BE");
			if ( wsContactContainer == null ) return;	
			WSCustomerContact contact = wsContactContainer.getContact();	
			restActions.assertTrue(contact != null, "contact IS NULL BUT SHOULD NOT BE");	
			if ( contact == null ) return;
			
			String actualContactId = contact.getContactId();
			restActions.assertTrue(actualContactId != null, "actualContactId IS NULL BUT SHOULD NOT BE");
			restActions.assertTrue(expectedContactId.equals(actualContactId),
					String.format("BAD CONTACT ID - EXPECTED %s FOUND %s", 
							expectedContactId, actualContactId));
			
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
