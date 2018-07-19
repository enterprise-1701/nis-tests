package com.cubic.nistests.tests;

import java.util.Hashtable;

import org.testng.ITestContext;
import org.testng.annotations.Test;

import com.cubic.accelerators.RESTActions;
import com.cubic.backoffice.constants.BackOfficeGlobals;
import com.cubic.backoffice.enums.BackOfficeEnums.BackOfficeComponent;
import com.cubic.backoffice.utils.BackOfficeUtils;
import com.cubic.database.DataBaseUtil;
import com.cubic.nisjava.apiobjects.WSXCubHdrJSON;
import com.cubic.nisjava.constants.AppConstants;
import com.cubic.nisjava.dataproviders.NISDataProviderSource;
import com.cubic.nisjava.utils.NISUtils;
import com.google.gson.Gson;

/**
 * This test calls the URL http://10.252.1.21:8201/nis/nwapi/v2/customer/957/notification/1580
 * where the notificationId doesn't match the customerId.  This will generate the response
 * "fieldName":"notification-id","errorKey":"errors.general.record.not.found".
 * 
 * @author 203402
 *
 */
public class NWAPIV2_CustomerNotification_BadNotificationIdTest extends NWAPIV2_GetBase {

	private static final String CUSTOMER_ID = "CUSTOMER_ID";
	private static final String ALERT_ID = "ALERT_ID";
	private static final String EXPECTED_RESULT = "EXPECTED_RESULT";
	private static final String RESULT_FMT = "WRONG RESULT - EXPECTED %s FOUND %s";
	private static final String BAD_UID_NULL_FMT = "UID IS NULL BUT SHOULD NOT BE";
	private static final String BAD_UID_LENGTH_0_MSG = "BAD UID HAS LENGTH ZERO";
	private static final String EXPECTED_FIELDNAME = "EXPECTED_FIELDNAME";
	private static final String BAD_FIELD_NAME_FMT = "BAD FIELD NAME - EXPECTED %s FOUND %s";
	private static final String EXPECTED_ERROR_KEY = "EXPECTED_ERROR_KEY";
	private static final String BAD_ERROR_KEY_FMT = "BAD ERROR KEY - EXPECTED %s FOUND %s";
	
	/**
	 * SQL to get the latest NewCustomer row from the ALERT_HIST_SUMMARY_V View
	 */
	private static final String SQL_QUERY = "select * from ALERT_HIST_SUMMARY_V where rownum = 1";	
	
	/**
	 * This test calls the URL http://10.252.1.21:8201/nis/nwapi/v2/customer/957/notification/1580
	 * where the notificationId doesn't match the customerId.  This will generate the response
	 * "fieldName":"notification-id","errorKey":"errors.general.record.not.found".
	 * 
	 * testRailId = 1040744
	 * 
	 * @param context  The TestNG Context object
	 * @param data  Test Data from the JSON input file
	 * @throws Throwable  Thrown if something goes wrong
	 */
	@Test(dataProvider = AppConstants.DATA_PROVIDER, dataProviderClass = NISDataProviderSource.class)
	public void getNotification(ITestContext context, Hashtable<String, String> data) throws Throwable {
		
		BackOfficeGlobals.ENV.setEnvironmentVariables();
		DataBaseUtil dbUtils = BackOfficeUtils.setDBConn(BackOfficeComponent.CNG);
		String customerId = dbUtils.getStringQuery(SQL_QUERY, CUSTOMER_ID, 1);
		data.put(CUSTOMER_ID, customerId);
		
		String alertId = dbUtils.getStringQuery(SQL_QUERY, ALERT_ID, 1);
		
		// add a 0 to the alertId so it doesn't match any more
		data.put(ALERT_ID, alertId + "0");
		
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
		WSXCubHdrJSON errorMsg = gson.fromJson(response, WSXCubHdrJSON.class);            
        
		LOG.info("##### Testing the response content...");
		String expectedResult = data.get(EXPECTED_RESULT);
		String actualResult = errorMsg.getHdr().getResult();
		restActions.assertTrue(expectedResult.equals(actualResult),
				String.format(RESULT_FMT, expectedResult, actualResult));
		
		String uid = errorMsg.getHdr().getUid();
		restActions.assertTrue(uid != null, BAD_UID_NULL_FMT);
		
		if ( uid != null ) {
			restActions.assertTrue(uid.length() > 0, BAD_UID_LENGTH_0_MSG);
		}
		
		String actualFieldName = errorMsg.getHdr().getFieldName();
		String expectedFieldName = data.get(EXPECTED_FIELDNAME);
		restActions.assertTrue(expectedFieldName.equals(actualFieldName),
				String.format(BAD_FIELD_NAME_FMT, expectedFieldName, actualFieldName));
		
		String expectedErrorKey = data.get(EXPECTED_ERROR_KEY);
		String actualErrorKey = errorMsg.getHdr().getErrorKey();
		restActions.assertTrue(expectedErrorKey.equals(actualErrorKey),
				String.format(BAD_ERROR_KEY_FMT, expectedErrorKey, actualErrorKey));
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
