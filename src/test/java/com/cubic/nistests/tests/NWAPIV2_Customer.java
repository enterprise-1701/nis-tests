package com.cubic.nistests.tests;

import java.util.Hashtable;
import java.util.UUID;

import org.testng.ITestContext;
import org.testng.annotations.Test;

import java.lang.invoke.MethodHandles;

import org.apache.log4j.Logger;

import com.cubic.accelerators.RESTActions;
import com.cubic.accelerators.RESTConstants;
import com.cubic.backoffice.constants.BackOfficeGlobals;
import com.cubic.backoffice.utils.BackOfficeUtils;
import com.cubic.nisjava.constants.AppConstants;
import com.cubic.nisjava.dataproviders.NISDataProviderSource;
import com.cubic.nisjava.utils.NISUtils;

/**
 * A test class that contains an @Test method to
 * call the Create Customer API.
 * 
 * @author 203402
 */
public class NWAPIV2_Customer extends NWAPIV2_CustomerBase {

    private static final String CLASS_NAME = MethodHandles.lookup().lookupClass().getSimpleName();
    private static final Logger LOG = Logger.getLogger(CLASS_NAME);	
	
	/**
	 * An @Test method to call the Create Customer API.
	 * 
	 * testRailId = 956903
	 * 
	 * @param context  The TestNG Context object
	 * @param data	The Test Data from the JSON input file.
	 * @throws Throwable  Thrown if something goes wrong.
	 */
	@Test(dataProvider = AppConstants.DATA_PROVIDER, dataProviderClass = NISDataProviderSource.class)
	public void createCustomer(ITestContext context, Hashtable<String, String> data) throws Throwable {
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
			
			LOG.info("##### call the Create Customer API");
			NISUtils.createCustomer(restActions, headerTable, username, password, securityQuestion);
		} finally {
			teardownAutomationTest(context, testCaseName);
			LOG.info("##### Done!");
		}
	}
}
