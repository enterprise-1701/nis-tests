package com.cubic.nistests.tests;

import java.util.Hashtable;
import java.util.List;

import org.testng.ITestContext;
import org.testng.annotations.Test;

import com.cubic.accelerators.RESTActions;
import com.cubic.backoffice.constants.BackOfficeGlobals;
import com.cubic.nisjava.apiobjects.WSShippingType;
import com.cubic.nisjava.apiobjects.WSShippingTypesList;
import com.cubic.nisjava.constants.AppConstants;
import com.cubic.nisjava.dataproviders.NISDataProviderSource;
import com.google.gson.Gson;

/**
 * This test class calls the GET http://10.252.1.21:8201/nis/nwapi/v2/customerservice/shippingtypes API.
 * 
 * @author 203402
 *
 */
public class NWAPIV2_CustomerServiceShippingTypesTest extends NWAPIV2_GetBase {
	
	private static final String SHIPPING_CONTAINER_IS_NULL_MSG = "SHIPPING TYPES CONTAINER IS NULL BUT SHOULD NOT BE";
	private static final String SHIPPING_TYPES_LIST_IS_NULL_MSG = "SHIPPING TYPES LIST IS NULL BUT SHOULD NOT BE";
	private static final String SHIPPING_TYPE_NAME_IS_LENGTH_ZERO = "SHIPPING TYPE NAME IS LENGTH ZERO!";
	private static final String SHIPPING_TYPE_DESCRIPTION_IS_LENGTH_ZERO = "SHIPPING TYPE DESCRIPTION IS LENGTH ZERO!";
	
	/**
	 * This @Test method will call the customerservice/feedback API. 
	 * 
	 * testRailId = 933957
	 * 
	 * @param context  The TestNG Context object
	 * @param data  The Test Data, from the JSON input file
	 * @throws Throwable  Thrown if something goes wrong
	 */
	@Test(dataProvider = AppConstants.DATA_PROVIDER, dataProviderClass = NISDataProviderSource.class)
	public void getShippingTypes(ITestContext context, Hashtable<String, String> data) throws Throwable {
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
	protected void verifyResponse( Hashtable<String,String> data, RESTActions restActions, String response ) {
		Gson gson = new Gson();
		
		LOG.info("##### Parsing the response content...");
		WSShippingTypesList shippingTypesContainer = gson.fromJson(response, WSShippingTypesList.class);
		
		LOG.info("##### Testing the response content...");
		restActions.assertTrue(shippingTypesContainer != null, SHIPPING_CONTAINER_IS_NULL_MSG);
		
		if ( null == shippingTypesContainer ) return;
		
		List<WSShippingType> shippingTypesList = shippingTypesContainer.getShippingTypes();
		
		restActions.assertTrue(shippingTypesList != null, SHIPPING_TYPES_LIST_IS_NULL_MSG);
		
		if ( null == shippingTypesList ) return;
		
		for ( WSShippingType shippingType : shippingTypesList ) {
			restActions.assertTrue(shippingType.getName().length() > 0, 
					SHIPPING_TYPE_NAME_IS_LENGTH_ZERO);
			
			restActions.assertTrue(shippingType.getDescription().length() > 0, 
					SHIPPING_TYPE_DESCRIPTION_IS_LENGTH_ZERO);
		}
	}
	
	/**
	 * Helper method to build the customerservice/shippingtypes URL.
	 * 
	 * @return  The customerservice/shippingtypes URL, in String form
	 */
	@Override
	protected String buildURL( Hashtable<String,String> data ) {
        String sURL = "http://" + BackOfficeGlobals.ENV.NIS_HOST + ":" + BackOfficeGlobals.ENV.NIS_PORT
                + "/nis/nwapi/v2/customerservice/shippingtypes";
		return sURL;
	}
}
