package com.cubic.nistests.tests;

import java.util.Hashtable;
import java.util.List;

import org.testng.ITestContext;
import org.testng.annotations.Test;
import com.cubic.accelerators.RESTActions;
import com.cubic.backoffice.constants.BackOfficeGlobals;
import com.cubic.nisjava.apiobjects.WSChannel;
import com.cubic.nisjava.apiobjects.WSChannelContainer;
import com.cubic.nisjava.constants.AppConstants;
import com.cubic.nisjava.dataproviders.NISDataProviderSource;
import com.google.gson.Gson;

/**
 * This test class calls the 
 * 
 * GET http://10.252.1.21:8201/nis/nwapi/v2/config/notificationchannel
 * 
 * API.
 * 
 * @author 203402
 *
 */
public class NWAPIV2_ConfigNotificationChannelTest extends NWAPIV2_GetBase {
	
	private static final String NOTIFICATION_CHANNEL_CONTAINER_IS_NULL = "NOTIFICATION CHANNEL CONTAINER IS NULL BUT SHOULD NOT BE";
	private static final String NOTIFICATION_CHANNEL_LIST_IS_NULL = "NOTIFICATION CHANNEL LIST IS NULL BUT SHOULD NOT BE";
	private static final String EXPECTED_CHANNEL_EMAIL_NAME = "EXPECTED_CHANNEL_EMAIL_NAME";
	private static final String EXPECTED_CHANNEL_EMAIL_ENABLED = "EXPECTED_CHANNEL_EMAIL_ENABLED";
	private static final String EXPECTED_CHANNEL_SMS_NAME = "EXPECTED_CHANNEL_SMS_NAME";
	private static final String EXPECTED_CHANNEL_SMS_ENABLED = "EXPECTED_CHANNEL_SMS_ENABLED";
	private static final String EXPECTED_CHANNEL_PUSHNOTIFICATION_NAME = "EXPECTED_CHANNEL_PUSHNOTIFICATION_NAME";
	private static final String EXPECTED_CHANNEL_PUSHNOTIFICATION_ENABLED = "EXPECTED_CHANNEL_PUSHNOTIFICATION_ENABLED";
	private static final String BAD_CHANNEL_NAME_FMT = "BAD CHANNEL NAME: EXPECTED %s FOUND %s";
	private static final String BAD_CHANNEL_ENABLED_FMT = "BAD CHANNEL ENABLED: EXPECTED %s FOUND %s";	
	
	/**
	 * This @Test method will call the config/notificationchannel API. 
	 * 
	 * testRailId = 951918
	 * 
	 * @param context  The TestNG Context object
	 * @param data  The Test Data, from the JSON input file
	 * @throws Throwable  Thrown if something goes wrong
	 */
	@Test(dataProvider = AppConstants.DATA_PROVIDER, dataProviderClass = NISDataProviderSource.class)
	public void getNotificationChannels(ITestContext context, Hashtable<String, String> data) throws Throwable {
		testMain(context, data);
	}
	
	/**
	 * Helper method to build the config/notificationchannel URL.
	 * 
	 * @return  The config/notificationchannel URL, in String form
	 */
	@Override
	protected String buildURL( Hashtable<String,String> data ) {
        String sURL = "http://" + BackOfficeGlobals.ENV.NIS_HOST + ":" + BackOfficeGlobals.ENV.NIS_PORT
                + "/nis/nwapi/v2/config/notificationchannel";
		return sURL;
	}
	
	/**
	 * Helper method to verify the response from the API.
	 * 
	 * @param data  The Test Data read from the JSON input file
	 * @param restActions  The RESTActions object created by the @Test method
	 * @param response  The response in String form
	 */
	@Override
	protected void verifyResponse( Hashtable<String,String> data, RESTActions restActions, String response ) {
		Gson gson = new Gson();
		
		LOG.info("##### Parsing the response content...");
		WSChannelContainer notificationChannelContainer = gson.fromJson(response, WSChannelContainer.class);
		
		restActions.assertTrue(notificationChannelContainer != null, 
				NOTIFICATION_CHANNEL_CONTAINER_IS_NULL);
		
		if ( null == notificationChannelContainer ) return;
		
		List<WSChannel> notificationChannelList = notificationChannelContainer.getChannels();
		
		restActions.assertTrue(notificationChannelList != null, 
				NOTIFICATION_CHANNEL_LIST_IS_NULL);
		
		if ( null == notificationChannelList ) return;

		if ( notificationChannelList.size() >= 1 ) {
			String expectedChannelName = data.get(EXPECTED_CHANNEL_EMAIL_NAME);
			String actualChannelName = notificationChannelList.get(0).getChannel();
			restActions.assertTrue(expectedChannelName.equals(actualChannelName), 
					String.format(BAD_CHANNEL_NAME_FMT,expectedChannelName,actualChannelName));
			
			Boolean expectedEnabled = Boolean.valueOf(data.get(EXPECTED_CHANNEL_EMAIL_ENABLED));
			Boolean actualEnabled = notificationChannelList.get(0).getEnabled();
			restActions.assertTrue(expectedEnabled.equals(actualEnabled),
					String.format(BAD_CHANNEL_ENABLED_FMT,expectedEnabled,actualEnabled));	
		}
		
		if ( notificationChannelList.size() >= 2 ) {
			String expectedChannelName = data.get(EXPECTED_CHANNEL_SMS_NAME);
			String actualChannelName = notificationChannelList.get(1).getChannel();
			restActions.assertTrue(expectedChannelName.equals(actualChannelName), 
					String.format(BAD_CHANNEL_NAME_FMT,expectedChannelName,actualChannelName));
			
			Boolean expectedEnabled = Boolean.valueOf(data.get(EXPECTED_CHANNEL_SMS_ENABLED));
			Boolean actualEnabled = notificationChannelList.get(1).getEnabled();
			restActions.assertTrue(expectedEnabled.equals(actualEnabled),
					String.format(BAD_CHANNEL_ENABLED_FMT,expectedEnabled,actualEnabled));
		}
		
		if ( notificationChannelList.size() >= 3 ) {
			String expectedChannelName = data.get(EXPECTED_CHANNEL_PUSHNOTIFICATION_NAME);
			String actualChannelName = notificationChannelList.get(2).getChannel();
			restActions.assertTrue(expectedChannelName.equals(actualChannelName), 
					String.format(BAD_CHANNEL_NAME_FMT,expectedChannelName,actualChannelName));
			
			Boolean expectedEnabled = Boolean.valueOf(data.get(EXPECTED_CHANNEL_PUSHNOTIFICATION_ENABLED));
			Boolean actualEnabled = notificationChannelList.get(2).getEnabled();
			restActions.assertTrue(expectedEnabled.equals(actualEnabled),
					String.format(BAD_CHANNEL_ENABLED_FMT,expectedEnabled,actualEnabled));
		}
	}
}
