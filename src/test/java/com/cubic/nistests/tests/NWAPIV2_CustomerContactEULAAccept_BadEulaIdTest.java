package com.cubic.nistests.tests;

import java.util.Hashtable;

import org.testng.ITestContext;
import org.testng.annotations.Test;

import com.cubic.nisjava.constants.AppConstants;
import com.cubic.nisjava.dataproviders.NISDataProviderSource;

/**
 * 
 * This test class calls the API 
 * 
 * @see POST http://10.252.1.21:8201/nis/nwapi/v2/customer/<customerId>/contact/<contactId>/eula/accept
 * 
 * with a Request Body that contains a non-existent eulaID.
 * <pre>
 * {
 *   "eulaId":"9999",
 *   "locale":"en"
 * }
 * </pre>
 * This is done to elicit error message errors.general.record.not.found for eulaId.
 * 
 * @author 203402
 *
 */
public class NWAPIV2_CustomerContactEULAAccept_BadEulaIdTest 
	extends NWAPIV2_CustomerContactEULAAccept_BadParameterBase {
    
	/**
	 * This @Test method calls the API 
	 * 
	 * @see POST http://10.252.1.21:8201/nis/nwapi/v2/customer/<customerId>/contact/<contactId>/eula/accept
	 * 
	 * with a Request Body that contains a non-existent eulaID.
	 * <pre>
	 * {
	 *   "eulaId":"9999",
	 *   "locale":"en"
	 * }
	 * </pre>
	 * This is done to elicit error message errors.general.record.not.found for eulaId.
	 * 
	 * testRailId = 1205016
	 * 
	 * @param context  The TestNG context object
	 * @param data  The test data from the JSON input file
	 * @throws Throwable  Thrown if something goes wrong
	 */
	@Test(dataProvider = AppConstants.DATA_PROVIDER, dataProviderClass = NISDataProviderSource.class)
	public void customerContactEULAAccept_badEulaId(ITestContext context, Hashtable<String, String> data) throws Throwable {
		testMain( context, data );
	}
}
