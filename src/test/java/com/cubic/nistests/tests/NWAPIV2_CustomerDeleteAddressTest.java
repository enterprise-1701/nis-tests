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
import com.cubic.nisjava.apiobjects.WSCustomerPostAddressResponse;
import com.cubic.nisjava.constants.AppConstants;
import com.cubic.nisjava.dataproviders.NISDataProviderSource;
import com.cubic.nisjava.utils.NISUtils;

/**
 * This test class calls the DELETE http://10.252.1.21:8201/nis/nwapi/v2/customer/<customer-id>/address/<address-id> API.
 * 
 * @author 203402
 *
 */
public class NWAPIV2_CustomerDeleteAddressTest extends NWAPIV2_CustomerBase {
	
    private static final String CLASS_NAME = MethodHandles.lookup().lookupClass().getSimpleName();
    private static final Logger LOG = Logger.getLogger(CLASS_NAME);	
	
	/**
	 * This @Test method calls the DELETE Address API.
	 * 
	 * @see DELETE http://10.252.1.21:8201/nis/nwapi/v2/customer/<customer-id>/address/<address-id>
	 * 
	 * <b>Please note that this method fails due to CCBO-13129.</b>
	 * 
	 * testRailId = 978109
	 * 
	 * @param context  The TestNG Context object.
	 * @param data	The Test Data from the JSON input file.
	 * @throws Throwable  Thrown if something goes wrong.
	 */
	@Test(dataProvider = AppConstants.DATA_PROVIDER, dataProviderClass = NISDataProviderSource.class)
	public void deleteCustomerAddress(ITestContext context, Hashtable<String, String> data) throws Throwable {
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
			
			LOG.info("##### Get a Security Question");
			String securityQuestion = NISUtils.securityQuestion(restActions, headerTable);
			
			LOG.info("##### Call the Create Customer API");
			WSCreateCustomerResponse customerResponse = NISUtils.createCustomer(restActions, headerTable, username, password, securityQuestion);
			String expectedCustomerId = customerResponse.getCustomerId();
			
			LOG.info("##### Call the Complete Registration API");
			NISUtils.completeRegistration(restActions, headerTable, expectedCustomerId);
			
			LOG.info("##### Call the Post Address API");
			WSCustomerPostAddressResponse postAddressResponse = NISUtils.addCustomerAddress(restActions, data, headerTable, expectedCustomerId);
			
			LOG.info("##### Call the DELETE Address API");
			NISUtils.deleteCustomerAddress( restActions, data, headerTable, expectedCustomerId, postAddressResponse.getAddressId() );
		
		} catch (Exception e) {
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
