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
import com.cubic.nisjava.apiobjects.WSAddress_;
import com.cubic.nisjava.apiobjects.WSCreateCustomerResponse;
import com.cubic.nisjava.apiobjects.WSCustomerInfoContainer;
import com.cubic.nisjava.constants.AppConstants;
import com.cubic.nisjava.dataproviders.NISDataProviderSource;

/**
 * This test class will call the Get Customer Address API.
 * 
 * @see GET http://10.252.1.21:8201/nis/nwapi/v2/customer/<customer-id>/address/<address-id>
 * 
 * @author 203402
 *
 */
public class NWAPIV2_CustomerGetAddressTest extends NWAPIV2_CustomerBase {

    private static final String CLASS_NAME = MethodHandles.lookup().lookupClass().getSimpleName();
    private static final Logger LOG = Logger.getLogger(CLASS_NAME);	
	
	/**
	 * This @Test method calls the GET Address API.
	 * 
	 * testRailId = 978003
	 * 
	 * @param context  The TestNG Context object
	 * @param data	The Test Data from the JSON input file
	 * @throws Throwable  Thrown if something goes wrong
	 */
	@Test(dataProvider = AppConstants.DATA_PROVIDER, dataProviderClass = NISDataProviderSource.class)
	public void getCustomerAddress(ITestContext context, Hashtable<String, String> data) throws Throwable {
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
			WSCreateCustomerResponse customerResponse = createCustomer(restActions, headerTable, username, password, securityQuestion);
			String expectedCustomerId = customerResponse.getCustomerId();
					
			LOG.info("##### Call the Complete Registration API");
			completeRegistration(restActions, headerTable, expectedCustomerId);
			
			LOG.info("##### Call the GET Customer API");
			WSCustomerInfoContainer customerInfoContainer = 
					getCustomer( restActions, headerTable, expectedCustomerId );
			
			WSAddress_ expectedAddress = customerInfoContainer.getCustomerInfo().getAddresses().get(0);
			
			LOG.info("##### Call the GET Address API");
			getCustomerAddress( restActions, data, headerTable, expectedCustomerId, expectedAddress );
		
		} catch( Exception e ) {
			RESTActions restActions = setupAutomationTest(context, testCaseName);
			restActions.failureReport("setup", "Exception: " + e);
			teardownAutomationTest(context, testCaseName);
			LOG.error(Log4jUtil.getStackTrace(e));
			throw new RuntimeException(e);
		} finally {
			teardownAutomationTest(context, testCaseName);
			LOG.info("##### Done!");
		}
	}
}
