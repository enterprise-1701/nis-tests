package com.cubic.nistests.tests;

import java.net.HttpURLConnection;
import java.util.Hashtable;

import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.annotations.Test;

import com.cubic.nisjava.constants.AppConstants;
import com.cubic.nisjava.dataproviders.VNHNISDataProviderSource;
import com.cubic.accelerators.RESTActions;
import com.cubic.accelerators.RESTEngine;
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
	/**
	 * This test method looks up the Notification History of a test account in NIS.
	 * 
	 * @param context Reference to testNG's ITestContext object.
	 * @param data The Test Date.
	 * @throws Throwable Thrown if something goes wrong.
	 */
	@Test(dataProvider = AppConstants.DATA_PROVIDER, dataProviderClass = VNHNISDataProviderSource.class)
	public void getNotificationDetail(ITestContext context, Hashtable<String, String> data) throws Throwable
	{
		String testCaseName = data.get("TestCase_Description");
		try
		{
			if (GenericConstants.RUN_MODE_YES.equals(data.get(GenericConstants.RUN_MODE))) 
			{
				// Make HTTP GET request to obtain Notification Detail				
				RESTActions restActions = setupAutomationTest(context, testCaseName);
				ClientResponse clientResponse = HttpActionsUtil.getNotificationDetailByNotificationId(data, restActions);
				
				// Verify HTTP response code
				int status = clientResponse.getStatus();
				String msg = "WRONG HTTP RESPONSE CODE - EXPECTED 200, FOUND " + status;
				Assert.assertEquals( HttpURLConnection.HTTP_OK, status, msg );
				
                if (HttpURLConnection.HTTP_OK == clientResponse.getStatus()) {
                    restActions.successReport("Expecting 200 response code", "Received 200 response code");
                } else {
                    restActions.failureReport("Expecting 200 response code",
                                                  "Received  +clientResponse.getStatus()  +response code");
                }
				
				// Test response
				verifyResponse( data, clientResponse.getEntity( String.class ) );
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
	private void verifyResponse( Hashtable<String, String> data, String response )
	{
		Assert.assertTrue(response != null, "RESPONSE IS NULL");		
		
		Gson gson = new Gson();
		WSNotificationDetail notDetail =  gson.fromJson(response, WSNotificationDetail.class);
		
		// Verify the Notification Id
		String actualNotificationId = notDetail.getNotificationId().toString();
		String expectNotificationId = data.get("NOTIFICATION_ID");
		Assert.assertEquals(actualNotificationId, expectNotificationId);
		
		// Verify the Contact info
		WSContact actualContact = notDetail.getContact();
		WSContact expectContact = new WSContact();
		expectContact.setContactId(data.get("CONTACT_ID"));
		expectContact.setFirstName(data.get("FIRST_NAME"));
		expectContact.setLastName(data.get("LAST_NAME"));
		expectContact.setEmail(data.get("EMAIL_ADDRESS"));
		expectContact.setSmsPhone(data.get("SMS_PHONE"));
		expectContact.setMandatory(Boolean.valueOf(data.get("MANDATORY")));
		
		boolean equal = expectContact.equals(actualContact);
		Assert.assertTrue( equal, WSContact.getDiff() );
		
		// Verify the Greetings of the Message and Alt Message
		boolean contains = notDetail.getMessage().contains("Dear 4gPmgyr1RqkDvv0y 4gPmgyr1RqkDvv0y,");
		Assert.assertTrue( contains, "BAD MESSAGE: GREETING NOT FOUND" );
		
		contains = notDetail.getAlternateMessage().contains("Dear 4gPmgyr1RqkDvv0y 4gPmgyr1RqkDvv0y,");
		Assert.assertTrue( contains, "BAD ALTERNATE MESSAGE: GREETING NOT FOUND" );
	}
}

