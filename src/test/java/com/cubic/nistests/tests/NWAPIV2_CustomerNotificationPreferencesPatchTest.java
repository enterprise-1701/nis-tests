package com.cubic.nistests.tests;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Hashtable;

import org.testng.ITestContext;
import org.testng.annotations.Test;

import com.cubic.accelerators.RESTActions;
import com.cubic.backoffice.constants.BackOfficeGlobals;
import com.cubic.backoffice.enums.BackOfficeEnums.BackOfficeComponent;
import com.cubic.backoffice.utils.BackOfficeUtils;
import com.cubic.database.DataBaseUtil;
import com.cubic.nisjava.constants.AppConstants;
import com.cubic.nisjava.dataproviders.NISDataProviderSource;
import com.cubic.nisjava.utils.NISUtils;
import com.sun.jersey.api.client.ClientResponse;

/**
 * This test class will call the PATCH
 * http://10.252.1.21:8201/nis/nwapi/v2/customer/AEEA73E8-A9B1-4266-9D2A-6113DDA6EEC9/notificationpreferences
 * API
 * 
 * @author 203402
 *
 */
public class NWAPIV2_CustomerNotificationPreferencesPatchTest extends NWAPIV2_PatchBase {

	private static final String CUSTOMER_ID = "CUSTOMER_ID";
	private static final String CONTACT_ID = "CONTACT_ID";
	
	/**
	 * SQL to get the latest NewCustomer row from the ALERT_HIST_SUMMARY_V View
	 */
	private static final String SQL_QUERY =  "select * from ALERT_HIST_SUMMARY_V where CREATE_TIME_DTM = (select max(CREATE_TIME_DTM) from ALERT_HIST_SUMMARY_V) and rownum = 1";	
	
	/**
	 * Test method to call the PATCH
	 * http://10.252.1.21:8201/nis/nwapi/v2/customer/AEEA73E8-A9B1-4266-9D2A-6113DDA6EEC9/notificationpreferences
	 * API.
	 * 
	 * testRailId = 1141781
	 * 
	 * @param context  The TestNG Context object
	 * @param data  The Test Data from the JSON input file
	 * @throws Throwable  Thrown if something goes wrong
	 */
	@Test(dataProvider = AppConstants.DATA_PROVIDER, dataProviderClass = NISDataProviderSource.class)
	public void patchNotificationPreference(ITestContext context, Hashtable<String, String> data) throws Throwable {
		
		BackOfficeGlobals.ENV.setEnvironmentVariables();
		DataBaseUtil dbUtils = BackOfficeUtils.setDBConn(BackOfficeComponent.CNG);
		String customerId = dbUtils.getStringQuery(SQL_QUERY, CUSTOMER_ID, 1);
		data.put(CUSTOMER_ID, customerId);
		
		String contactId = dbUtils.getStringQuery(SQL_QUERY, CONTACT_ID, 1);
		data.put(CONTACT_ID, contactId);
		
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
	protected void verifyResponse(Hashtable<String, String> data, RESTActions restActions, ClientResponse clientResponse) {
		LOG.info("##### Verifying HTTP response code...");
		int actualHTTPResponseCode = clientResponse.getStatus();
		String sExpectedHTTPResponseCode = data.get(EXPECTED_HTTP_RESPONSE_CODE);
		int expectedResponseCode = Integer.parseInt( sExpectedHTTPResponseCode );
		String msg = String.format(BAD_RESPONSE_CODE_FMT, expectedResponseCode, actualHTTPResponseCode);
		restActions.assertTrue(expectedResponseCode == actualHTTPResponseCode, msg);
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
		pw.println("    \"customer-id\":\"" + data.get(CUSTOMER_ID) + "\"," );
		pw.println("    \"contacts\":[");
		pw.println("         {");
		pw.println("             \"contactId\":\"" + data.get(CONTACT_ID) + "\"," );
		pw.println("             \"preferences\":[");
		pw.println("                  {");
		pw.println("                      \"notificationType\":\"DisassociatedMediaBillingInfoRemoved\"," );
		pw.println("                      \"optIn\":true," );
		pw.println("                      \"channels\":[");
		pw.println("                          {");
		pw.println("                              \"channel\":\"EMAIL\"," );
		pw.println("                              \"enabled\":true" );
		pw.println("                          }");
		pw.println("                      ]");
		pw.println("                  }");
		pw.println("              ]");
		pw.println("         }");
		pw.println("    ]");
		pw.println("}");
		return sw.toString();
	}
}
