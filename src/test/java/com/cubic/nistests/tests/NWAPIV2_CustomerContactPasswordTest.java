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
import com.cubic.nisjava.constants.AppConstants;
import com.cubic.nisjava.dataproviders.NISDataProviderSource;
import com.cubic.nisjava.utils.NISUtils;
import com.sun.jersey.api.client.ClientResponse;

/**
 * This test class calls the API with the URL
 * 
 * http://10.252.1.21:8201/nis/nwapi/v2/customer/16796D51-369B-E811-80CC-000D3A36F32A/contact/1E796D51-369B-E811-80CC-000D3A36F32A/password
 * 
 * to reset an existing password.
 * 
 * @author 203402
 *
 */
public class NWAPIV2_CustomerContactPasswordTest extends NWAPIV2_CustomerBase {

    private static final String CLASS_NAME = MethodHandles.lookup().lookupClass().getSimpleName();
    private static final Logger LOG = Logger.getLogger(CLASS_NAME);
    
	/**
	 * This @Test method calls the API
	 * 
	 * http://10.252.1.21:8201/nis/nwapi/v2/customer/<customer-id>/contact/<contact-id>/password
	 * 
	 * to change an existing password.
	 * 
	 * testRailId = 1201014
	 * 
	 * @param context
	 * @param data
	 * @throws Throwable
	 */
	@Test(dataProvider = AppConstants.DATA_PROVIDER, dataProviderClass = NISDataProviderSource.class)
	public void resetCustomerContactPassword(ITestContext context, Hashtable<String, String> data) throws Throwable {
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
			String password = data.get("OLD_PASSWORD");
			
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
					restActions,headerTable,customerId,contactId,password,data.get("NEW_PASSWORD"));
			
			LOG.info("##### Verifying HTTP response code to be 204");
			int status = clientResponse.getStatus();
			String msg = "WRONG HTTP RESPONSE CODE - EXPECTED 204, FOUND " + status;
			restActions.assertTrue(status == HttpURLConnection.HTTP_NO_CONTENT, msg);
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
