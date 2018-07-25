package com.cubic.nistests.tests;

import java.util.Hashtable;
import java.util.List;

import org.testng.ITestContext;
import org.testng.annotations.Test;

import com.cubic.accelerators.RESTActions;
import com.cubic.backoffice.constants.BackOfficeGlobals;
import com.cubic.backoffice.enums.BackOfficeEnums.BackOfficeComponent;
import com.cubic.backoffice.utils.BackOfficeUtils;
import com.cubic.database.DataBaseUtil;
import com.cubic.nisjava.apiobjects.WSChannel;
import com.cubic.nisjava.apiobjects.WSContact;
import com.cubic.nisjava.apiobjects.WSPreference;
import com.cubic.nisjava.apiobjects.WSViewAccountNotificationPreferences;
import com.cubic.nisjava.constants.AppConstants;
import com.cubic.nisjava.dataproviders.NISDataProviderSource;
import com.cubic.nisjava.utils.NISUtils;
import com.google.gson.Gson;

/**
 * This test class will call the
 * http://10.252.1.21:8201/nis/nwapi/v2/customer/AEEA73E8-A9B1-4266-9D2A-6113DDA6EEC9/notificationpreferences
 * API.
 * 
 * @author 203402
 *
 */
public class NWAPIV2_CustomerNotificationPreferencesTest extends NWAPIV2_GetBase {

	private static final String CUSTOMER_ID = "CUSTOMER_ID";
	private static final String NOTIF_PREFS_IS_NULL_MSG = "NOTIFICATION PREFERENCES CONTAINER IS NULL BUT SHOULD NOT BE";
	private static final String CONTACTID_IS_NULL_MSG = "contactId IS NULL BUT SHOULD NOT BE";
	private static final String FIRSTNAME_IS_NULL_MSG = "firstName IS NULL BUT SHOULD NOT BE";
	private static final String LASTNAME_IS_NULL_MSG = "lastName IS NULL BUT SHOULD NOT BE";
	private static final String EMAIL_IS_NULL_MSG = "email IS NULL BUT SHOULD NOT BE";
	private static final String SMSPHONE_IS_NULL_MSG = "smsPhone IS NULL BUT SHOULD NOT BE";
	private static final String MANDATORY_IS_NULL_MSG = "mandatory IS NULL BUT SHOULD NOT BE";
	private static final String NOTIF_TYPE_IS_NULL_MSG = "notificationType IS NULL BUT SHOULD NOT BE";
	private static final String NOTIF_DESC_IS_NULL_MSG = "notificationDesc IS NULL BUT SHOULD NOT BE";
	private static final String EXPECTED_CHANNEL_EMAIL = "EXPECTED_CHANNEL_EMAIL";
	private static final String BAD_CHANNEL_FMT = "BAD CHANNEL - EXPECTED %s FOUND %s";
	
	/**
	 * SQL to get the latest NewCustomer row from the ALERT_HIST_SUMMARY_V View
	 */
	private static final String SQL_QUERY =  "select * from ALERT_HIST_SUMMARY_V where CREATE_TIME_DTM = (select max(CREATE_TIME_DTM) from ALERT_HIST_SUMMARY_V) and rownum = 1";
	
	/**
	 * Test method to call the GET
	 * http://10.252.1.21:8201/nis/nwapi/v2/customer/AEEA73E8-A9B1-4266-9D2A-6113DDA6EEC9/notificationpreferences
	 * API.
	 * 
	 * testRailId = 1094305
	 * 
	 * @param context  The TestNG Context object
	 * @param data  The Test Data from the JSON input file
	 * @throws Throwable  Thrown if something goes wrong
	 */
	@Test(dataProvider = AppConstants.DATA_PROVIDER, dataProviderClass = NISDataProviderSource.class)
	public void getNotificationPreferences(ITestContext context, Hashtable<String, String> data) throws Throwable {
		
		BackOfficeGlobals.ENV.setEnvironmentVariables();
		DataBaseUtil dbUtils = BackOfficeUtils.setDBConn(BackOfficeComponent.CNG);
		String customerId = dbUtils.getStringQuery(SQL_QUERY, CUSTOMER_ID, 1);
		data.put(CUSTOMER_ID, customerId);
		
		testMain(context, data);
	}	
	
	/**
	 * Helper method to verify the response from the API.
	 * 
	 * @param data  The Test Data read from the JSON input file
	 * @param restActions  The RESTActions object created by the @Test method
	 * @param response  The response in String form
	 */
	@Override
	protected void verifyResponse(Hashtable<String, String> data, RESTActions restActions, String response) { 
		Gson gson = new Gson();
		
		LOG.info("##### Parsing the response content...");
		WSViewAccountNotificationPreferences wsNotificationPrefs =
				gson.fromJson(response, WSViewAccountNotificationPreferences.class);
		
		LOG.info("##### Testing the response content...");
		restActions.assertTrue(wsNotificationPrefs != null, NOTIF_PREFS_IS_NULL_MSG);
		if ( null == wsNotificationPrefs ) return;
		
		List<WSContact> contactsList = wsNotificationPrefs.getContacts();
		if ( null == contactsList ) return;
		
		for ( WSContact contact : contactsList ) {
			String contactId = contact.getContactId();
			restActions.assertTrue(contactId != null,CONTACTID_IS_NULL_MSG);
			
			String firstName = contact.getFirstName();
			restActions.assertTrue(firstName != null,FIRSTNAME_IS_NULL_MSG);
			
			String lastName = contact.getLastName();
			restActions.assertTrue(lastName != null,LASTNAME_IS_NULL_MSG);
			
			String email = contact.getEmail();
			restActions.assertTrue(email != null,EMAIL_IS_NULL_MSG);
			
			String smsPhone = contact.getSmsPhone();
			restActions.assertTrue(smsPhone != null,SMSPHONE_IS_NULL_MSG);
			
			Boolean mandatory = contact.getMandatory();
			restActions.assertTrue(mandatory != null,MANDATORY_IS_NULL_MSG);
			
			List<WSPreference> prefList = contact.getPreferences();
			if ( null == prefList ) continue;
			
			for ( WSPreference pref : prefList ) {
				String notificationType = pref.getNotificationType();
				restActions.assertTrue(notificationType != null,NOTIF_TYPE_IS_NULL_MSG);
				
				String notificationDesc = pref.getNotificationDescription();
				restActions.assertTrue(notificationDesc != null,NOTIF_DESC_IS_NULL_MSG);
				
				List<WSChannel> channelList = pref.getChannels();
				if ( null == channelList ) continue;
				if ( channelList.size() == 0 ) continue;
				WSChannel channel = channelList.get(0);
				String expectedChannel = data.get(EXPECTED_CHANNEL_EMAIL);
				String actualChannel = channel.getChannel();
				restActions.assertTrue(expectedChannel.equals(actualChannel),
						String.format(BAD_CHANNEL_FMT,
								expectedChannel, actualChannel));
			}
		}
	}

	/**
	 * Helper method to build the URL used to call the API.
	 * 
	 * @param data  The Test Data from the JSON input file.
	 * @return The URL in string form.
	 */
	@Override
	protected String buildURL(Hashtable<String, String> data) {
        String sURL = NISUtils.getURL()
                + "/nis/nwapi/v2/customer/" + data.get(CUSTOMER_ID)
                + "/notificationpreferences";
		return sURL;
	}

}
