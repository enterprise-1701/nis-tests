package com.cubic.nistests.tests;

import java.util.Hashtable;
import java.util.StringTokenizer;

import org.testng.ITestContext;
import org.testng.annotations.Test;

import com.cubic.accelerators.RESTActions;
import com.cubic.backoffice.constants.BackOfficeGlobals;
import com.cubic.backoffice.enums.BackOfficeEnums.BackOfficeComponent;
import com.cubic.backoffice.utils.BackOfficeUtils;
import com.cubic.database.DataBaseUtil;
import com.cubic.logutils.Log4jUtil;
import com.cubic.nisjava.constants.AppConstants;
import com.cubic.nisjava.dataproviders.NISDataProviderSource;

/**
 * <p>
 * This test class will call the URL
 * </p><p>
 * GET http://10.252.1.21:8201/nis/nwapi/v2/transitaccount/440000169613/subsystem/ABP/travelhistory/taps?sortBy=<criterion>
 * </p><p>
 * where <criterion> is one of these values: transactionDateTime.desc,
 * routeNumber.asc, travelMode.asc, status.asc, tripId.desc, tapId.asc,
 * zone.asc, and operator.desc.  If an unrecognized criterion value is
 * appended to the URL, the API returns Bad Request.  Since only
 * valid criteria values are appended, the test verifies that the
 * HTTP Response Code is 200.
 * </p>
 * @author 203402
 *
 */
public class NWAPIV2_TransitaccountSubsystemTravelhistoryTapsGoodSortByTest extends NWAPIV2_GetBase {

	private static final String TRANSIT_ACCOUNT_ID = "TRANSIT_ACCOUNT_ID";
	private static final String SUBSYSTEM_ENUM = "SUBSYSTEM_ENUM";
	private static final String SORTBY_VALUES = "SORTBY_VALUES";
	private static final String COMMA = ",";	
	
	protected StringTokenizer sortByValues;	
	
	/**
	 * <p>
	 * Test that each of the expected SortBy values can be appended to the URL.
	 * </p><p>
	 * testRailId = 824960
	 * </p>
	 * @param context  The TestNG Context object
	 * @param data  The Test Data from the JSON input file
	 * @throws Throwable  Thrown if something goes wrong
	 */
	@Test(dataProvider = AppConstants.DATA_PROVIDER, dataProviderClass = NISDataProviderSource.class)
	public void testSortByValue(ITestContext context, Hashtable<String, String> data) throws Throwable {
		setupBuildURL( context, data );
		while( sortByValues.hasMoreTokens() ) {
			testMain( context, data );
		}
	}
	
	/**
	 * Do nothing to verify the response content (we'll just test the HTTP Response Code).
	 */
	@Override
	protected void verifyResponse(Hashtable<String, String> data, RESTActions restActions, String response) {
		// deliberately empty
	}
	
	/** 
	 * BuildURL will get called repeatedly, returning a different URL each time.
	 * This method sets up the difference, which is ?sortBy=transactionDateTime.desc,
	 * ?sortBy=routeNumber.asc, ?sortBy=travelMode.asc, etc.
	 * 
	 * @throws Exception 
	 */
	protected void setupBuildURL(ITestContext context, Hashtable<String, String> data) throws Exception {
		DataBaseUtil dbUtils = null;
		try {
			BackOfficeGlobals.ENV.setEnvironmentVariables();
			
			String sSortBy = data.get(SORTBY_VALUES);
			sortByValues = new StringTokenizer(sSortBy, COMMA);

			dbUtils = BackOfficeUtils.setDBConn(BackOfficeComponent.ABP);
			long transitAccountId = dbUtils.getLongQuery("SELECT * FROM ABP_MAIN.TRANSIT_ACCOUNT", 
					TRANSIT_ACCOUNT_ID, 1);
			data.put(TRANSIT_ACCOUNT_ID, "" + transitAccountId);
		} catch (Exception e) {
			String testCaseName = data.get("TestCase_Description");
			RESTActions restActions = setupAutomationTest(context, testCaseName);
			restActions.failureReport("setup", "Setup failure occurred: " + e);
			teardownAutomationTest(context, testCaseName);
			LOG.error(Log4jUtil.getStackTrace(e));
			throw new RuntimeException(e);
		}
		finally {
            try {
                if (dbUtils != null && dbUtils.connection != null) {
                    LOG.info("##### DB Connection not closed, CLOSING ##########\n");
                    dbUtils.closeConnection();
                }
            }
            catch (Exception e) {
                LOG.error(Log4jUtil.getStackTrace(e));
            }   
		}
	}
	
	/**
	 * This Helper method builds the URL to invoke the API with.
	 * Each time the @Test method calls testMain, testMain will
	 * call buildURL, each time getting a different value of the
	 * ?sortBy= URL parameter.
	 */
	@Override
	protected String buildURL(Hashtable<String, String> data) {
        String sURL = "http://" + BackOfficeGlobals.ENV.NIS_HOST
        		+ ":" + BackOfficeGlobals.ENV.NIS_PORT
                + "/nis/nwapi/v2/transitaccount/"
                + data.get(TRANSIT_ACCOUNT_ID) + "/subsystem/"
        		+ data.get(SUBSYSTEM_ENUM) + "/travelhistory/taps?sortBy="
        		+ sortByValues.nextToken();
		return sURL;
	}

}
