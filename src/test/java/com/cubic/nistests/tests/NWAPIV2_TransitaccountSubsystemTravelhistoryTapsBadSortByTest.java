package com.cubic.nistests.tests;

import java.util.Hashtable;

import org.testng.ITestContext;
import org.testng.annotations.Test;

import com.cubic.accelerators.RESTActions;
import com.cubic.nisjava.apiobjects.WSXCubHdrJSON;
import com.cubic.nisjava.constants.AppConstants;
import com.cubic.nisjava.dataproviders.NISDataProviderSource;
import com.google.gson.Gson;

/**
 * <p>
 * This test class will call the URL
 * </p><p>
 * GET http://10.252.1.21:8201/nis/nwapi/v2/transitaccount/440000169613/subsystem/ABP/travelhistory/taps?sortBy=<criterion>
 * </p><p>
 * where <criterion> is one of these invalid values: startDateTime.asc, 
 * endDateTime.asc, route.desc.  If an unrecognized criterion value is
 * appended to the URL, the API returns Bad Request.  Since only
 * invalid criteria values are appended, the test verifies that the
 * HTTP Response Code is 400.
 * </p>
 * @author 203402
 *
 */
public class NWAPIV2_TransitaccountSubsystemTravelhistoryTapsBadSortByTest
	extends NWAPIV2_TransitaccountSubsystemTravelhistoryTapsGoodSortByTest {

	private static final String EXPECTED_RESULT = "EXPECTED_RESULT";	
	private static final String RESULT_FMT = "BAD RESULT: EXPECTED '%s', FOUND '%s'";
	private static final String BAD_UI_NULL_FMT = "BAD UID: UID IS NULL BUT IT SHOULD NOT BE";
	private static final String BAD_UI_LENGTH_0_MSG = "BAD UID: UID HAS LENGTH 0";
	private static final String EXPECTED_FIELDNAME = "EXPECTED_FIELDNAME";
	private static final String BAD_ERROR_KEY_FMT = "BAD ERROR KEY: EXPECTED '%s', FOUND '%s'";
	private static final String EXPECTED_ERROR_KEY = "EXPECTED_ERROR_KEY";
	private static final String BAD_FIELD_NAME_FMT = "BAD FIELD NAME: EXPECTED '%s', FOUND '%s'";
	
	/**
	 * <p>
	 * Test that each of the expected SortBy values can be appended to the URL.
	 * </p><p>
	 * testRailId = 824956
	 * </p>
	 * @param context  The TestNG Context object
	 * @param data  The Test Data from the JSON input file
	 * @throws Throwable  Thrown if something goes wrong
	 */
	@Override
	@Test(dataProvider = AppConstants.DATA_PROVIDER, dataProviderClass = NISDataProviderSource.class)
	public void testSortByValue(ITestContext context, Hashtable<String, String> data) throws Throwable {
		setupBuildURL( context, data );
		while( sortByValues.hasMoreTokens() ) {
			testMain( context, data );
		}
	}	
	
	/**
	 * Helper method to verify the response from the API.
	 * 
	 * @param data - the Test Data from the JSON input file
	 * @param restActions - the RESTActions object created by the test
	 * @param response - The response in String form
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
		restActions.assertTrue(uid != null, BAD_UI_NULL_FMT);
		
		if ( uid != null ) {
			restActions.assertTrue(uid.length() > 0, BAD_UI_LENGTH_0_MSG);
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
}
