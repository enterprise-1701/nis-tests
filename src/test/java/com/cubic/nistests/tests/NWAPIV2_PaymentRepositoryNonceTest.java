package com.cubic.nistests.tests;

import java.util.Hashtable;

import org.testng.ITestContext;
import org.testng.annotations.Test;

import com.cubic.accelerators.RESTActions;
import com.cubic.backoffice.constants.BackOfficeGlobals;
import com.cubic.nisjava.constants.AppConstants;
import com.cubic.nisjava.dataproviders.NISDataProviderSource;
import com.google.gson.Gson;
import com.cubic.nisjava.apiobjects.WSNonce;

/**
 * This test class calls the /nis/nwapi/v2/payment/repository/nonce/
 * API and verifies that the response contains an integer with a
 * positive value.
 * 
 * @author 203402
 *
 */
public class NWAPIV2_PaymentRepositoryNonceTest extends NWAPIV2_GetBase {

	private static final String WSNONCE_IS_NULL = "REFERENCE TO WSNONCE OBJECT IS NULL BUT IT SHOULD NOT BE";
	private static final String NONCE_VALUE_IS_NULL = "NONCE VALUE IS NULL BUT IT SHOULD NOT BE";		
	
	/**
	 * This test method will call the /nis/nwapi/v2/payment/repository/nonce/
	 * API and verify that a nonce integer value is returned.
	 * 
	 * testRailId = 933964
	 * 
	 * @param context The TestNG context object.
	 * @param data  The Test Data read from the JSON input file.
	 * @throws Throwable  Thrown if something goes wrong.
	 */
	@Test(dataProvider = AppConstants.DATA_PROVIDER, dataProviderClass = NISDataProviderSource.class)
	public void getNonce(ITestContext context, Hashtable<String, String> data) throws Throwable {
		testMain( context, data );
	}
	
	/**
	 * Helper method to test the value of the nonce returned by the API
	 * is greater than zero.
	 * 
	 * @param data  The Test Data read from the JSON input file.
	 * @param restActions  The RESTActions object created in the test.
	 * @param response  The response in String form.
	 */
	@Override
	protected void verifyResponse( Hashtable<String,String> data, RESTActions restActions, String response ) {
		Gson gson = new Gson();
		
		LOG.info("##### Parsing the response content...");
		WSNonce wsNonce = gson.fromJson(response, WSNonce.class);            
        
		LOG.info("##### Testing the response content...");	
		restActions.assertTrue( null != wsNonce, WSNONCE_IS_NULL );
		if ( null != wsNonce ) {
			Long nonce = wsNonce.getNonce();
			restActions.assertTrue( null != nonce, NONCE_VALUE_IS_NULL );
		}
	}
	
	/**
	 * Helper method to build the URL used to call the API.
	 * 
	 * @param data  The Test Data from the JSON input file.
	 * @return The URL in string form.
	 */
	@Override
	protected String buildURL( Hashtable<String,String> data ) {
        String sURL = "http://" + BackOfficeGlobals.ENV.NIS_HOST + ":" + BackOfficeGlobals.ENV.NIS_PORT
                + "/nis/nwapi/v2/payment/repository/nonce/";
		return sURL;
	}
}
