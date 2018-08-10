package com.cubic.nistests.tests;

import java.util.Hashtable;

import org.testng.ITestContext;
import org.testng.annotations.Test;

import com.cubic.nisjava.constants.AppConstants;
import com.cubic.nisjava.dataproviders.NISDataProviderSource;

/**
 * This test class calls the API
 * 
 * @see POST http://10.252.1.21:8201/nis/nwapi/v2/customer/<customerId>/contact/<contactId>/eula/accept
 * 
 * with a Request Body that contains a non-existent Locale.
 * <pre>
 * {
 *   "eulaId":"100",
 *   "locale":"zz"
 * }
 * </pre>
 * This is done to elicit error message errors.general.record.not.found for locale.
 *  
 * @author 203402
 *
 */
public class NWAPIV2_CustomerContactEULAAccept_BadLocaleTest extends NWAPIV2_CustomerContactEULAAccept_BadParameterBase {
    
	/**
	 * This @Test method calls the API 
	 * 
	 * @see POST http://10.252.1.21:8201/nis/nwapi/v2/customer/<customerId>/contact/<contactId>/eula/accept
	 * 
	 * with a Request Body that contains a non-existent Locale.
	 * <pre>
	 * {
	 *   "eulaId":"100",
	 *   "locale":"zz"
	 * }
	 * </pre>
	 * This is done to elicit error message errors.general.record.not.found for locale.
	 * 
	 * testRailId = 1205015
	 * 
	 * @param context  The TestNG context object
	 * @param data  Test data from the JSON input file
	 * @throws Throwable  Thrown if something goes wrong
	 */
	@Test(dataProvider = AppConstants.DATA_PROVIDER, dataProviderClass = NISDataProviderSource.class)
	public void customerContactEULAAccept_badLocale(ITestContext context, Hashtable<String, String> data) throws Throwable {
		testMain( context, data );
	}

}
