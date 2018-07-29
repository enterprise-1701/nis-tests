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
import com.cubic.nisjava.apiobjects.WSCreateCustomerResponse;
import com.cubic.nisjava.constants.AppConstants;
import com.cubic.nisjava.dataproviders.NISDataProviderSource;
import com.cubic.nisjava.utils.NISUtils;

/**
 * Call a Customer's 'Username Forgot' API using the email address.
 * 
 * @author 203402
 *
 */
public class NWAPIV2_CustomerUsernameForgotTest extends NWAPIV2_CustomerBase {

    private static final String CLASS_NAME = MethodHandles.lookup().lookupClass().getSimpleName();
    private static final Logger LOG = Logger.getLogger(CLASS_NAME);	
	
	/**
	 * Call a Customer's 'Username Forgot' API using the email address.
	 * 
	 * testRailId = 960998
	 * 
	 * @param context  The TestNG Context object.
	 * @param data  The Test Data from the JSON input file.
	 * @throws Throwable  Thrown if something goes wrong.
	 */
	@Test(dataProvider = AppConstants.DATA_PROVIDER, dataProviderClass = NISDataProviderSource.class)
	public void usernameForgot(ITestContext context, Hashtable<String, String> data) throws Throwable {
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
			String email = username;
			String password = "Pas5word!";
			
			LOG.info("##### Call the Prevalidate API");
			NISUtils.prevalidate(restActions, headerTable, username, password);
			
			LOG.info("##### Get all Security Questions from the system");
			String expectedSecurityQuestion = NISUtils.securityQuestion(restActions, headerTable);
			
			LOG.info("##### Call the Create Customer API");
			WSCreateCustomerResponse customerResponse = NISUtils.createCustomer(restActions, headerTable, username, password, expectedSecurityQuestion);
			String expectedCustomerId = customerResponse.getCustomerId();
					
			LOG.info("##### Call the Complete Registration API");
			NISUtils.completeRegistration(restActions, headerTable, expectedCustomerId);
			
			LOG.info("##### Get the user's Username Forgot API");
			NISUtils.usernameForgot( restActions, data, headerTable, email );
		} finally {
			teardownAutomationTest(context, testCaseName);
			LOG.info("##### Done!");
		}
	}
}
