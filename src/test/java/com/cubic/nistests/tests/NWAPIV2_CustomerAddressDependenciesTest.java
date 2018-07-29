package com.cubic.nistests.tests;

import java.lang.invoke.MethodHandles;
import java.util.Hashtable;
import java.util.List;
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
import com.cubic.nisjava.apiobjects.WSContactDependency;
import com.cubic.nisjava.apiobjects.WSCreateCustomerResponse;
import com.cubic.nisjava.apiobjects.WSCustomerInfoContainer;
import com.cubic.nisjava.apiobjects.WSDependencyLists;
import com.cubic.nisjava.constants.AppConstants;
import com.cubic.nisjava.dataproviders.NISDataProviderSource;
import com.cubic.nisjava.utils.NISUtils;

/**
 * Test class to call the customer/<customer-id>/address/<address-id>/dependencies URL.
 * 
 * @author 203402
 *
 */
public class NWAPIV2_CustomerAddressDependenciesTest extends NWAPIV2_CustomerBase {

    private static final String CLASS_NAME = MethodHandles.lookup().lookupClass().getSimpleName();
    private static final Logger LOG = Logger.getLogger(CLASS_NAME);	
	
	/**
	 * @Test method to call the customer/<customer-id>/address/<address-id>/dependencies URL.
	 * 
	 * testRailId = 978107
	 * 
	 * @param context  The TestNG Context object.
	 * @param data  The Test Data from the JSON input file.
	 * @throws Throwable  Thrown if something went wrong.
	 */
	@Test(dataProvider = AppConstants.DATA_PROVIDER, dataProviderClass = NISDataProviderSource.class)
	public void getContactDependencies(ITestContext context, Hashtable<String, String> data) throws Throwable {
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
			
			LOG.info("##### Call the GET Customer API");
			WSCustomerInfoContainer customerInfoContainer = 
					NISUtils.getCustomer( restActions, headerTable, expectedCustomerId );
			
			WSAddress_ expectedAddress = customerInfoContainer.getCustomerInfo().getAddresses().get(0);			
			
			String expectedAddressId = expectedAddress.getAddressId();
			
			LOG.info("##### Call the PUT Address API");
			WSDependencyLists dependencyLists = NISUtils.getContactDependencies( restActions, data, headerTable, expectedCustomerId, expectedAddressId );
			
			restActions.assertTrue( dependencyLists != null, 
					"dependencyLists IS NULL BUT IT SHOULD NOT BE NULL");
			
			if ( null == dependencyLists ) {
				return;
			}
			
			List<Object> fundingSourceDependencies = dependencyLists.getFundingSourceDependencies();
			
			restActions.assertTrue( fundingSourceDependencies != null, 
					"fundingSourceDependencies IS NULL BUT IT SHOULD NOT BE NULL");			
			
			List<WSContactDependency> contactDependencies = dependencyLists.getContactDependencies();
			
			restActions.assertTrue( contactDependencies != null, 
					"dependencyLists IS NULL BUT IT SHOULD NOT BE NULL");
			
			if ( null == contactDependencies ) {
				return;
			}
			
			restActions.assertTrue( contactDependencies.size() == 1, 
					String.format("BAD SIZE FOR contactDependencies - SHOULD BE SIZE 1 BUT IT IS SIZE %s",
							contactDependencies.size() ) );
			
			if ( contactDependencies.size() == 0 ) {
				return;
			}
			
			WSContactDependency contactDepen = contactDependencies.get(0);
			String actualAddressId = contactDepen.getAddress().getAddressId();
			restActions.assertTrue(expectedAddressId.equals(actualAddressId),
					String.format("BAD ADDRESS ID - EXPECTED %s FOUND %s",
							expectedAddressId, actualAddressId ) );
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
