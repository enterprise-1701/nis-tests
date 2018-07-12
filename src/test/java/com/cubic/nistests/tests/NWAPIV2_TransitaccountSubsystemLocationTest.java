package com.cubic.nistests.tests;

import java.util.Hashtable;
import java.util.List;

import org.testng.ITestContext;
import org.testng.annotations.Test;

import com.cubic.accelerators.RESTActions;
import com.cubic.backoffice.constants.BackOfficeGlobals;
import com.cubic.nisjava.apiobjects.WSLocation;
import com.cubic.nisjava.apiobjects.WSLocationList;
import com.cubic.nisjava.constants.AppConstants;
import com.cubic.nisjava.dataproviders.NISDataProviderSource;
import com.google.gson.Gson;

/**
 *  This test class defines the buildURL method to build the
 *  /nis/nwapi/v2/transitaccount/subsystem/ABP/location URL,
 *  and the verifyResponse method to verify the response from
 *  this API.
 *  
 * @author 203402
 *
 */
public class NWAPIV2_TransitaccountSubsystemLocationTest extends NWAPIV2_GetBase {
	
	private static final String EXPECTED_OPERATOR = "EXPECTED_OPERATOR";
	private static final String EXPECTED_OPERATOR_NAME = "EXPECTED_OPERATOR_NAME";
	private static final String EXPECTED_STOP_POINT = "EXPECTED_STOP_POINT";
	private static final String EXPECTED_STOP_POINT_DESC = "EXPECTED_STOP_POINT_DESC";
	private static final String EXPECTED_ROUTE_NUMBER = "EXPECTED_ROUTE_NUMBER";
	private static final String EXPECTED_ROUTE_DESC = "EXPECTED_ROUTE_DESC";
	private static final String EXPECTED_ZONE = "EXPECTED_ZONE";
	private static final String EXPECTED_ZONE_DESC = "EXPECTED_ZONE_DESC";
	private static final String EXPECTED_DIRECTION = "EXPECTED_DIRECTION";
	private static final String EXPECTED_DIRECTION_DESC = "EXPECTED_DIRECTION_DESC";
	private static final String EXPECTED_SECTOR = "EXPECTED_SECTOR";
	private static final String EXPECTED_SECTOR_DESC = "EXPECTED_SECTOR_DESC";
	private static final String EXPECTED_SERVICE_TYPE = "EXPECTED_SERVICE_TYPE";
	private static final String EXPECTED_SERVICE_TYPE_DESC = "EXPECTED_SERVICE_TYPE_DESC";
	
	/**
	 * The @Test method that calls the 
	 * /nis/nwapi/v2/transitaccount/subsystem/ABP/location API.
	 * 
	 * testRailId = 933963
	 * 
	 * @param context  The TestNG Context object
	 * @param data The Test Data read from the JSON input file
	 * @throws Throwable Thrown if something goes wrong
	 */
	@Test(dataProvider = AppConstants.DATA_PROVIDER, dataProviderClass = NISDataProviderSource.class)
	public void getLocation(ITestContext context, Hashtable<String, String> data) throws Throwable {
		testMain( context, data );
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
		WSLocationList wsLocationList = gson.fromJson(response, WSLocationList.class);            
        
		LOG.info("##### Testing the response content...");
		
		restActions.assertTrue( wsLocationList != null, 
				"WSLocationList REFERENCE IS NULL BUT SHOULD NOT BE" );
		
		if ( null == wsLocationList ) {
			return;
		}
		
		List<WSLocation> locationList = wsLocationList.getLocations();
		
		restActions.assertTrue( locationList != null, 
				"Location List REFERENCE IS NULL BUT SHOULD NOT BE" );
		
		if ( null == locationList ) {
			return;
		}
		
		restActions.assertTrue( locationList.size() > 0,
				"Location List IS EMPTY BUT IT SHOULD NOT BE" );
		
		if ( locationList.size() == 0 ) {
			return;
		}
		
		WSLocation expected = new WSLocation();
		expected.setOperator(data.get(EXPECTED_OPERATOR));
		expected.setOperatorName(data.get(EXPECTED_OPERATOR_NAME));
		expected.setStopPoint(data.get(EXPECTED_STOP_POINT));
		expected.setStopPointDesc(data.get(EXPECTED_STOP_POINT_DESC));
		expected.setRouteNumber(data.get(EXPECTED_ROUTE_NUMBER));
		expected.setRouteDesc(data.get(EXPECTED_ROUTE_DESC));
		expected.setZone(data.get(EXPECTED_ZONE));
		expected.setZoneDesc(data.get(EXPECTED_ZONE_DESC));
		expected.setDirection(data.get(EXPECTED_DIRECTION));
		expected.setDirectionDesc(data.get(EXPECTED_DIRECTION_DESC));
		expected.setSector(data.get(EXPECTED_SECTOR));
		expected.setSectorDesc(data.get(EXPECTED_SECTOR_DESC));
		expected.setServiceType(data.get(EXPECTED_SERVICE_TYPE));
		expected.setServiceTypeDesc(data.get(EXPECTED_SERVICE_TYPE_DESC));
		
		WSLocation actual = locationList.get(0);
		
		actual.setServiceTypeDesc(data.get(EXPECTED_SERVICE_TYPE_DESC));
		actual.setServiceType(data.get(EXPECTED_SERVICE_TYPE));
		actual.setDirectionDesc(data.get(EXPECTED_DIRECTION_DESC));
		actual.setDirection(data.get(EXPECTED_DIRECTION));
		actual.setRouteDesc(data.get(EXPECTED_ROUTE_DESC));
		actual.setRouteNumber(data.get(EXPECTED_ROUTE_NUMBER));
		
		boolean bEquals = expected.equals( actual );
		restActions.assertTrue(bEquals, WSLocation.getErrorMessage());
	}
	
	
	/**
	 * Helper method to build the URL used to call the API.
	 * 
	 * @param data  The Test Data from the JSON input file.
	 * @return The URL in string form.
	 */
	@Override
	protected String buildURL( Hashtable<String,String> data ) {
        String sURL = "http://" + BackOfficeGlobals.ENV.NIS_HOST
        		+ ":" + BackOfficeGlobals.ENV.NIS_PORT
                + "/nis/nwapi/v2/transitaccount/subsystem/"
        		+ data.get("SUBSCRIPTION_ENUM") + "/location";
		return sURL;
	}
}
