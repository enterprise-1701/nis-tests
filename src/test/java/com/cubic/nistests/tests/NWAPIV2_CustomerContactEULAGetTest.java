package com.cubic.nistests.tests;

import java.lang.invoke.MethodHandles;
import java.net.HttpURLConnection;
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
import com.cubic.nisjava.apiobjects.WSCXSLocale;
import com.cubic.nisjava.apiobjects.WSCreateCustomerResponse;
import com.cubic.nisjava.apiobjects.WSEula;
import com.cubic.nisjava.apiobjects.WSEulaContainer;
import com.cubic.nisjava.constants.AppConstants;
import com.cubic.nisjava.dataproviders.NISDataProviderSource;
import com.cubic.nisjava.utils.NISUtils;
import com.google.gson.Gson;
import com.sun.jersey.api.client.ClientResponse;

/**
 * This Test class will call the API
 * 
 * GET http://10.252.1.21:8201/nis/nwapi/v2/customer/<customerId>/contact/<contactId>/eula
 * 
 * to get the EULA Info assoc'd with the given customerId/contactId.
 *  
 * @author 203402
 *
 */
public class NWAPIV2_CustomerContactEULAGetTest extends NWAPIV2_CustomerBase {

	private static final String EULA_ID = "EULA_ID";
	private static final String LOCALE = "LOCALE";
	private static final String BAD_EULA_FMT = "BAD EULA ID - EXPECTED %s FOUND %s";
	private static final String BAD_USER_FMT = "BAD USER ID - EXPECTED %s FOUND %s";
	private static final String BAD_SUB_USER_FMT = "BAD SUB USER ID - EXPECTED %s FOUND %s";
	private static final String BAD_LOCALE_FMT = "BAD LOCALE - EXPECTED %s FOUND %s";
	private static final String FAILURE = "failure:";
    private static final String CLASS_NAME = MethodHandles.lookup().lookupClass().getSimpleName();
    private static final Logger LOG = Logger.getLogger(CLASS_NAME);
    
    /**
     * This @Test method will call the API
     * 
     * GET http://10.252.1.21:8201/nis/nwapi/v2/customer/<customerId>/contact/<contactId>/eula
     * 
     * to get the EULA Info assoc'd with the given customerId/contactId.
     *  
     * testRailId = 1205014
     * 
     * @param context  The TestNG context object
     * @param data  The test data from the JSON input file
     * @throws Throwable  Thrown if something goes wrong
     */
	@Test(dataProvider = AppConstants.DATA_PROVIDER, dataProviderClass = NISDataProviderSource.class)
	public void customerContactEULAGet(ITestContext context, Hashtable<String, String> data) throws Throwable {
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
			
			LOG.info("##### Call the Create Customer API");
			WSCreateCustomerResponse customerResponse = NISUtils.createCustomer(restActions, headerTable, username, password, securityQuestion);
			String customerId = customerResponse.getCustomerId();
			String contactId = customerResponse.getContactId();
			
			LOG.info("##### Call the Complete Registration API");
			NISUtils.completeRegistration(restActions, headerTable, customerId);

			LOG.info("##### Call the EULA Accept API for the first time");
			ClientResponse clientResponse = NISUtils.customerContactEULAAccept(
					restActions,headerTable,customerId,contactId,data.get(EULA_ID),data.get(LOCALE));
			
			LOG.info("##### Verifying HTTP response code to be 204");
			int status = clientResponse.getStatus();
			String msg = String.format(BAD_RESPONSE_CODE_FMT, HttpURLConnection.HTTP_NO_CONTENT, status);
			restActions.assertTrue(status == HttpURLConnection.HTTP_NO_CONTENT, msg);
			
			LOG.info("##### call the Get EULA API");
			clientResponse = NISUtils.customerContactEULAGet( restActions, headerTable, customerId, contactId );
			
			LOG.info("##### Verifying HTTP response code to be 200");
			status = clientResponse.getStatus();
			msg = String.format(BAD_RESPONSE_CODE_FMT, HttpURLConnection.HTTP_OK, status);
			restActions.assertTrue(status == HttpURLConnection.HTTP_OK, msg);
			
			String response = clientResponse.getEntity(String.class);
			LOG.info("##### Got the response body");
			restActions.assertTrue(response != null, RESPONSE_IS_NULL);
			LOG.info( response );
			
			Gson gson = new Gson();
			
			LOG.info("##### Parsing the response content...");
			WSEulaContainer eulaContainer = gson.fromJson(response, WSEulaContainer.class);
			
			// protect ourselves from a Null Pointer Exception
			if ( null == eulaContainer ) return;
			List<WSEula> eulaList = eulaContainer.getList();
			if ( null == eulaList ) return;
			if ( eulaList.size() == 0 ) return;
			WSEula eula = eulaList.get(0);
			if ( eula == null ) return;
			
			LOG.info("##### Testing the EULA_ID value"); 
			String expectEulaId = data.get(EULA_ID);
			String actualEulaId = ""+eula.getEulaId();
			restActions.assertTrue(expectEulaId.equals(actualEulaId),
					String.format(BAD_EULA_FMT,expectEulaId,actualEulaId));
			
			LOG.info("##### Testing the USER ID value");
			String expectUserId = customerId;
			String actualUserId = eula.getUserId();
			restActions.assertTrue(expectUserId.equals(actualUserId),
					String.format(BAD_USER_FMT,expectUserId,actualUserId));
			
			LOG.info("##### Testing the SUB USER ID value");
			String expectSubuserId = contactId;
			String actualSubuserId = eula.getSubuserId1();
			restActions.assertTrue(expectSubuserId.equals(actualSubuserId),
					String.format(BAD_SUB_USER_FMT,expectSubuserId,actualSubuserId));
			
			WSCXSLocale cxsLocale = eula.getLocale();
			if ( null == cxsLocale ) return;
			
			LOG.info("##### Testing the LOCALE value");
			String actualLocale = cxsLocale.getCxsLocaleTag();
			String expectLocale = data.get(LOCALE);
			restActions.assertTrue(expectLocale.equals(actualLocale),
					String.format(BAD_LOCALE_FMT,expectLocale,actualLocale));
			
		}  catch (Exception e) {
			RESTActions restActions = setupAutomationTest(context, testCaseName);
			restActions.failureReport(FAILURE, FAILURE + e);
			teardownAutomationTest(context, testCaseName);
			LOG.error(Log4jUtil.getStackTrace(e));
			throw new RuntimeException(e);	
		} finally {
			teardownAutomationTest(context, testCaseName);
			LOG.info("##### Done!");
		}
	}
}
