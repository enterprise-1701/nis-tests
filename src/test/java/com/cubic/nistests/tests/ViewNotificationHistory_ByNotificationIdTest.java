package com.cubic.nistests.tests;

import java.lang.invoke.MethodHandles;
import java.net.HttpURLConnection;
import java.util.Hashtable;

import org.apache.log4j.Logger;
import org.testng.ITestContext;
import org.testng.annotations.Test;

import com.cubic.nisjava.constants.AppConstants;
import com.cubic.nisjava.dataproviders.NISDataProviderSource;
import com.cubic.accelerators.RESTActions;
import com.cubic.accelerators.RESTEngine;
import com.cubic.backoffice.constants.BackOfficeGlobals;
import com.cubic.genericutils.GenericConstants;
import com.cubic.nisjava.utils.HttpActionsUtil;
import com.cubic.nisjava.apiobjects.WSContact;
import com.cubic.nisjava.apiobjects.WSNotificationDetail;
import com.google.gson.Gson;
import com.sun.jersey.api.client.ClientResponse;

/**
 * This test class looks up a Notification using the NotificationId.
 * 
 * JIRA: STA-1400
 * Ext. Ref. RSC-163
 * 
 * @author 203402
 *
 */
public class ViewNotificationHistory_ByNotificationIdTest extends RESTEngine 
{
    private static final String CLASS_NAME = MethodHandles.lookup().lookupClass().getSimpleName();
    private static final Logger LOG = Logger.getLogger(CLASS_NAME);		
	
	/**
	 * This test method looks up the Notification History of a test account in NIS.
	 * 
	 * @param context Reference to testNG's ITestContext object.
	 * @param data The Test Date.
	 * @throws Throwable Thrown if something goes wrong.
	 */
	@Test(dataProvider = AppConstants.DATA_PROVIDER, dataProviderClass = NISDataProviderSource.class)
	public void getNotificationDetail(ITestContext context, Hashtable<String, String> data) throws Throwable
	{
		String testCaseName = data.get("TestCase_Description");
		try
		{
			if (GenericConstants.RUN_MODE_YES.equals(data.get(GenericConstants.RUN_MODE))) 
			{
				// Make HTTP GET request to obtain Notification Detail
				LOG.info("##### Setting up test");
				BackOfficeGlobals.ENV.setEnvironmentVariables();
				RESTActions restActions = setupAutomationTest(context, testCaseName);
				
				LOG.info("##### Calling get Notification Detail API");
				ClientResponse clientResponse = HttpActionsUtil.getNotificationDetailByNotificationId(data, restActions);
				
				// Verify HTTP response code
				int status = clientResponse.getStatus();
				String msg = "WRONG HTTP RESPONSE CODE - EXPECTED 200, FOUND " + status;	
                if (HttpURLConnection.HTTP_OK == clientResponse.getStatus()) {
                    restActions.successReport("Expecting 200 response code", "Received 200 response code");
                } else {
                    restActions.failureReport("Expecting 200 response code", msg);
                }
                
				// Test response
				verifyResponse(restActions, data, clientResponse.getEntity(String.class));
			}
		}
		finally 
		{
			this.teardownAutomationTest(context, testCaseName);
		}
	}
	
	/**
	 * Helper method to verify the response from CNG.
	 * 
	 * @param data		The Test Data
	 * @param response	The Response, as a String
	 */
	private void verifyResponse( RESTActions restActions, Hashtable<String, String> data, String response )
	{
		restActions.assertTrue(response != null, "RESPONSE IS NULL");
		
		if ( response == null ) return;
		
		Gson gson = new Gson();
		LOG.info("##### Parsing response");
		WSNotificationDetail notDetail =  gson.fromJson(response, WSNotificationDetail.class);
		
		if ( notDetail == null ) return;
		
		LOG.info("##### Testing parsed response");
		// Verify the Notification Id
		String actualNotificationId = ""+notDetail.getNotificationId();
		String expectNotificationId = data.get("NOTIFICATION_ID");
		restActions.assertTrue(expectNotificationId.equals(actualNotificationId),
				String.format("WRONG NOTIFICATION ID - EXPECTED %s FOUND %s",
						expectNotificationId, actualNotificationId));
		
		// Verify the Contact info
		WSContact actualContact = notDetail.getContact();
		if ( null == actualContact ) return;		
		WSContact expectContact = new WSContact();
		expectContact.setContactId(data.get("CONTACT_ID"));
		expectContact.setFirstName(data.get("FIRST_NAME"));
		expectContact.setLastName(data.get("LAST_NAME"));
		expectContact.setEmail(data.get("EMAIL_ADDRESS"));
		expectContact.setSmsPhone(data.get("SMS_PHONE"));
		expectContact.setMandatory(Boolean.valueOf(data.get("MANDATORY")));
		
		boolean equal = expectContact.equals(actualContact);
		restActions.assertTrue( equal, WSContact.getDiff() );
		
		// Verify the Greetings of the Message and Alt Message
		boolean contains = notDetail.getMessage().contains("Dear 4gPmgyr1RqkDvv0y 4gPmgyr1RqkDvv0y,");
		restActions.assertTrue( contains, "BAD MESSAGE: GREETING NOT FOUND" );
		
		contains = notDetail.getAlternateMessage().contains("Dear 4gPmgyr1RqkDvv0y 4gPmgyr1RqkDvv0y,");
		restActions.assertTrue( contains, "BAD ALTERNATE MESSAGE: GREETING NOT FOUND" );
	}
}

