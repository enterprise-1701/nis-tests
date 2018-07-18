package com.cubic.nistests.tests;

import java.util.Hashtable;
import java.util.List;

import org.testng.ITestContext;
import org.testng.annotations.Test;

import com.cubic.accelerators.RESTActions;
import com.cubic.backoffice.constants.BackOfficeGlobals;
import com.cubic.nisjava.apiobjects.WSChannelReqType;
import com.cubic.nisjava.apiobjects.WSNotificationType;
import com.cubic.nisjava.apiobjects.WSNotificationTypeContainer;
import com.cubic.nisjava.constants.AppConstants;
import com.cubic.nisjava.dataproviders.NISDataProviderSource;
import com.google.gson.Gson;

/**
 * This test class calls the
 * 
 * GET http://10.252.1.21:8201/nis/nwapi/v2/config/notificationtype
 * 
 * API.
 * 
 * @author 203402
 *
 */
public class NWAPIV2_ConfigNotificationTypeTest extends NWAPIV2_GetBase {

	private static final String NOTIFICATION_TYPE_CONTAINER_IS_NULL = "NOTIFICATION TYPE CONTAINER IS NULL BUT SHOULD NOT BE";
	private static final String NOTIFICATION_TYPE_LIST_IS_NULL = "NOTIFICATION TYPE LIST IS NULL BUT SHOULD NOT BE";
	private static final String NOTIFICATION_TYPE_IS_NULL = "NOTIFICATION TYPE IS NULL BUT SHOULD NOT BE"; 
	private static final String NOTIFICATION_DESC_IS_NULL = "NOTIFICATION DESCRIPTION IS NULL BUT SHOULD NOT BE";
	private static final String LONG_DESC_IS_NULL = "LONG DESCRIPTION IS NULL BUT SHOULD NOT BE";
	private static final String CHANNEL_REQTYPES_LIST_IS_NULL = "CHANNEL REQTYPES LIST IS NULL BUT SHOULD NOT BE";
	private static final String EXPECTED_CHANNEL = "EXPECTED_CHANNEL";
	private static final String BAD_CHANNEL_FMT = "BAD CHANNEL - EXPECTED %s FOUND %s";
	
	/**
	 * This @Test method will call the config/notificationchannel API. 
	 * 
	 * testRailId = 123456
	 * 
	 * @param context  The TestNG Context object
	 * @param data  The Test Data, from the JSON input file
	 * @throws Throwable  Thrown if something goes wrong
	 */
	@Test(dataProvider = AppConstants.DATA_PROVIDER, dataProviderClass = NISDataProviderSource.class)
	public void getNotificationTypes(ITestContext context, Hashtable<String, String> data) throws Throwable {
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
		WSNotificationTypeContainer notificationTypeContainer = gson.fromJson(response, WSNotificationTypeContainer.class);
		
		restActions.assertTrue(notificationTypeContainer != null, NOTIFICATION_TYPE_CONTAINER_IS_NULL);
		
		if ( null == notificationTypeContainer ) return;
		
		List<WSNotificationType> notificationTypeList = notificationTypeContainer.getNotificationTypes();
		restActions.assertTrue(notificationTypeList != null, NOTIFICATION_TYPE_LIST_IS_NULL);
		if ( null == notificationTypeList ) return;
		
		for ( WSNotificationType notificationType : notificationTypeList ) {
			restActions.assertTrue(notificationType.getNotificationType() != null, 
					NOTIFICATION_TYPE_IS_NULL);
			
			restActions.assertTrue(notificationType.getNotificationDescription() != null,
					NOTIFICATION_DESC_IS_NULL);
			
			restActions.assertTrue(notificationType.getLongDescription() != null,
					LONG_DESC_IS_NULL);
			
			List<WSChannelReqType> channelReqTypesList = notificationType.getChannelReqTypes();
			
			restActions.assertTrue(channelReqTypesList != null,
					CHANNEL_REQTYPES_LIST_IS_NULL);
			
			if ( null == channelReqTypesList ) continue;
			
			if ( channelReqTypesList.size() > 0 ) {
				WSChannelReqType channelReqType = channelReqTypesList.get(0);
				
				String expectedChannel = data.get(EXPECTED_CHANNEL);
				String actualChannel = channelReqType.getChannel();
				restActions.assertTrue(expectedChannel.equals(actualChannel),
						String.format(BAD_CHANNEL_FMT, expectedChannel, actualChannel));
			}
		}
	}
	
	/**
	 * Helper method to build the URL used to call the config/notificationtype API.
	 * 
	 * @param data  The Test Data from the JSON input file.
	 * @return The config/notificationtype URL in string form.
	 */
	@Override
	protected String buildURL(Hashtable<String, String> data) {
        String sURL = "http://" + BackOfficeGlobals.ENV.NIS_HOST + ":" + BackOfficeGlobals.ENV.NIS_PORT
                + "/nis/nwapi/v2/config/notificationtype";
		return sURL;
	}

}
