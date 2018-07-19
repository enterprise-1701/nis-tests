package com.cubic.nistests.tests;

import java.util.Hashtable;

import org.testng.ITestContext;
import org.testng.annotations.Test;

import com.cubic.accelerators.RESTActions;
import com.cubic.backoffice.constants.BackOfficeGlobals;
import com.cubic.backoffice.enums.BackOfficeEnums.BackOfficeComponent;
import com.cubic.backoffice.utils.BackOfficeUtils;
import com.cubic.database.DataBaseUtil;
import com.cubic.nisjava.apiobjects.WSContact;
import com.cubic.nisjava.apiobjects.WSNotificationDetail;
import com.cubic.nisjava.constants.AppConstants;
import com.cubic.nisjava.dataproviders.NISDataProviderSource;
import com.cubic.nisjava.utils.NISUtils;
import com.google.gson.Gson;

/**
 * This test class will call the GET
 * http://10.252.1.21:8201/nis/nwapi/v2/customer/BD334982-A08A-E811-80CC-000D3A36F32A/notification/46053
 * API.
 * 
 * @author 203402
 *
 */
public class NWAPIV2_CustomerNotificationTest extends NWAPIV2_GetBase {

	private static final String CUSTOMER_ID = "CUSTOMER_ID";
	private static final String ALERT_ID = "ALERT_ID";
	private static final String CONTACT_ID = "CONTACT_ID";
	private static final String CONTACT_NAME = "CONTACT_NAME";
	private static final String RECIPIENT = "RECIPIENT";
	private static final String NOTIFICATION_DETAIL_IS_NULL_MSG = "NOTIFICATION DETAIL IS NULL BUT SHOULD NOT BE";
	private static final String CONTACT_IS_NULL_MSG = "CONTACT IS NULL BUT SHOULD NOT BE";
	private static final String WRONG_CONTACT_ID_FMT = "WRONG CONTACT_ID - EXPECTED %s FOUND %s";
	private static final String SPACE = " ";
	private static final String WRONG_FIRST_NAME_FMT = "WRONG FIRST NAME - EXPECTED %s FOUND %s";
	private static final String WRONG_LAST_NAME_FMT = "WRONG LAST NAME - EXPECTED %s FOUND %s";
	private static final String WRONG_RECIPIENT_FMT = "WRONG RECIPIENT - EXPECTED %s FOUND %s";
	
	/**
	 * SQL to get the latest NewCustomer row from the ALERT_HIST_SUMMARY_V View
	 */
	private static final String SQL_QUERY =  "select * from ALERT_HIST_SUMMARY_V where CREATE_TIME_DTM = (select max(CREATE_TIME_DTM) from ALERT_HIST_SUMMARY_V) and rownum = 1";
	
	/**
	 * Test method to call the GET
	 * http://10.252.1.21:8201/nis/nwapi/v2/customer/BD334982-A08A-E811-80CC-000D3A36F32A/notification/46053
	 * API.
	 * 
	 * testRailId = 1040718
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
		WSNotificationDetail wsNotificationDetail = gson.fromJson(response, WSNotificationDetail.class);            
        
		LOG.info("##### Testing the response content...");
		restActions.assertTrue(wsNotificationDetail != null,NOTIFICATION_DETAIL_IS_NULL_MSG);
		
		if ( null == wsNotificationDetail ) return;
		WSContact contact = wsNotificationDetail.getContact();
		restActions.assertTrue(contact != null,CONTACT_IS_NULL_MSG);
		if ( null == contact ) return;
	
		String expectedContactId = data.get(CONTACT_ID);
		String actualContactId = contact.getContactId();
		restActions.assertTrue(expectedContactId.equals(actualContactId),
				String.format(WRONG_CONTACT_ID_FMT,expectedContactId, actualContactId));
		
		String expectedContactName = data.get(CONTACT_NAME);
		String[] aExpectedContactName = expectedContactName.split(SPACE);
		String expectedFirstName = aExpectedContactName[0];
		String actualFirstName = contact.getFirstName();
		restActions.assertTrue(expectedFirstName.equals(actualFirstName),
				String.format(WRONG_FIRST_NAME_FMT,expectedFirstName, actualFirstName));
		
		String expectedLastName = aExpectedContactName[1];
		String actualLastName = contact.getLastName();
		restActions.assertTrue(expectedLastName.equals(actualLastName),
				String.format(WRONG_LAST_NAME_FMT,expectedLastName, actualLastName));
		
		String expectedEmail = data.get(RECIPIENT);
		String actualEmail = contact.getEmail();
		restActions.assertTrue(expectedEmail.equals(actualEmail),
				String.format(WRONG_RECIPIENT_FMT,expectedEmail, actualEmail));
	}

	/**
	 * Helper method to build the URL used to call the API.
	 * 
	 * @param data  The Test Data from the JSON input file.
	 * @return The URL in string form.
	 */
	@Override
	protected String buildURL(Hashtable<String, String> data) {
        String sURL = NISUtils.getURL("false", 
        		BackOfficeGlobals.ENV.NIS_HOST, BackOfficeGlobals.ENV.NIS_PORT)
                + "/nis/nwapi/v2/customer/" + data.get(CUSTOMER_ID)
                + "/notification/" + data.get(ALERT_ID);
		return sURL;
	}

}
