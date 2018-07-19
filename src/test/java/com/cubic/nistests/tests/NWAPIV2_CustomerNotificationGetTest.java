package com.cubic.nistests.tests;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Hashtable;
import java.util.List;

import org.testng.ITestContext;
import org.testng.annotations.Test;

import com.cubic.accelerators.RESTActions;
import com.cubic.backoffice.constants.BackOfficeGlobals;
import com.cubic.backoffice.enums.BackOfficeEnums.BackOfficeComponent;
import com.cubic.backoffice.utils.BackOfficeUtils;
import com.cubic.database.DataBaseUtil;
import com.cubic.nisjava.apiobjects.WSNotification;
import com.cubic.nisjava.apiobjects.WSNotificationList;
import com.cubic.nisjava.constants.AppConstants;
import com.cubic.nisjava.dataproviders.NISDataProviderSource;
import com.cubic.nisjava.utils.NISUtils;
import com.google.gson.Gson;

public class NWAPIV2_CustomerNotificationGetTest extends NWAPIV2_PostBase {

	private static final String CUSTOMER_ID = "CUSTOMER_ID";
	private static final String ALERT_ID = "ALERT_ID";
	private static final String CONTACT_ID = "CONTACT_ID";
	private static final String CONTACT_NAME = "CONTACT_NAME";
	private static final String RECIPIENT = "RECIPIENT";
	
	/**
	 * SQL to get the latest NewCustomer row from the ALERT_HIST_SUMMARY_V View
	 */
	private static final String SQL_QUERY =  "select * from ALERT_HIST_SUMMARY_V where CREATE_TIME_DTM = (select max(CREATE_TIME_DTM) from ALERT_HIST_SUMMARY_V) and rownum = 1";
	
	/**
	 * Test method to call the POST
	 * http://10.252.1.21:8201/nis/nwapi/v2/customer/BD334982-A08A-E811-80CC-000D3A36F32A/notification/get
	 * API.
	 * 
	 * testRailId = 1041078
	 * 
	 * @param context  The TestNG Context object
	 * @param data  The Test Data from the JSON input file
	 * @throws Throwable  Thrown if something goes wrong
	 */
	@Test(dataProvider = AppConstants.DATA_PROVIDER, dataProviderClass = NISDataProviderSource.class)
	public void getNotification(ITestContext context, Hashtable<String, String> data) throws Throwable {
		
		BackOfficeGlobals.ENV.setEnvironmentVariables();
		DataBaseUtil dbUtils = BackOfficeUtils.setDBConn(BackOfficeComponent.CNG);
		String customerId = dbUtils.getStringQuery(SQL_QUERY, CUSTOMER_ID, 1);
		data.put(CUSTOMER_ID, customerId);
		
		String alertId = dbUtils.getStringQuery(SQL_QUERY, ALERT_ID, 1);
		data.put(ALERT_ID, alertId);
		
		String contactId = dbUtils.getStringQuery(SQL_QUERY, CONTACT_ID, 1);
		data.put(CONTACT_ID, contactId);
		
		String contactName = dbUtils.getStringQuery(SQL_QUERY, CONTACT_NAME, 1);
		data.put(CONTACT_NAME, contactName);
		
		String email = dbUtils.getStringQuery(SQL_QUERY, RECIPIENT, 1);
		data.put(RECIPIENT, email);
		
		testMain(context, data);
	}	
	
	@Override
	protected void verifyResponse(Hashtable<String, String> data, RESTActions restActions, String response) {
		Gson gson = new Gson();
		
		LOG.info("##### Parsing the response content...");
		WSNotificationList wsNotificationList = gson.fromJson(response, WSNotificationList.class);
		
		restActions.assertTrue( wsNotificationList != null, "NOTIFICATION CONTAINER IS NULL BUT SHOULD NOT BE");
		if ( null == wsNotificationList ) return;
		List<WSNotification> notifications = wsNotificationList.getNotifications();
		restActions.assertTrue( notifications != null, "NOTIFICATION LIST IS NULL BUT SHOULD NOT BE");
		if ( null == notifications ) return;
		
		if ( notifications.size() > 0 ) {
			WSNotification notification = notifications.get(0);
			
			String sExpectedNotificationId = data.get( ALERT_ID );
			Integer expectedNotificationId = Integer.valueOf(sExpectedNotificationId);
			Integer actualNotificationId = notification.getNotificationId();
			restActions.assertTrue(expectedNotificationId.equals(actualNotificationId), 
					String.format("BAD NOTIFICATION ID - EXPECTED %s FOUND %s", 
							expectedNotificationId, actualNotificationId ) );
			
			String expectedContactId = data.get(CONTACT_ID);
			String actualContactId = notification.getContactId();
			restActions.assertTrue(expectedContactId.equals(actualContactId), 
					String.format("BAD CONTACT ID - EXPECTED %s FOUND %s", 
							expectedContactId, actualContactId ) );
			
			String expectedContactName = data.get(CONTACT_NAME);
			String actualContactName = notification.getContactName();
			restActions.assertTrue(expectedContactName.equals(actualContactName), 
					String.format("BAD CONTACT NAME - EXPECTED %s FOUND %s", 
							expectedContactName, actualContactName ) );
			
			String expectedRecipient = data.get(RECIPIENT);
			String actualRecipient = notification.getRecipient();
			restActions.assertTrue(expectedRecipient.equals(actualRecipient), 
					String.format("BAD RECIPIENT - EXPECTED %s FOUND %s", 
							expectedRecipient, actualRecipient ) );
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
                + "/notification/get";
		return sURL;
	}

	/**
	 * Helper method to build the request Body.
	 * 
	 * @param data The Test Data from the JSON input file.
	 * @return  The Request Body in String form.
	 */
	@Override
	protected String buildRequestBody(Hashtable<String, String> data) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter( sw );
		pw.println("{");
		pw.println( "  \"customer-Id\":\"" + data.get(CUSTOMER_ID) + "\"");
		pw.println("}");
		return sw.toString();
	}

}
