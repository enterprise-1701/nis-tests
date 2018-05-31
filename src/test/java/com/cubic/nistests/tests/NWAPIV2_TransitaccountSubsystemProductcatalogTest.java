package com.cubic.nistests.tests;

import java.util.Hashtable;
import java.util.List;

import org.testng.ITestContext;
import org.testng.annotations.Test;

import com.cubic.accelerators.RESTActions;
import com.cubic.backoffice.constants.BackOfficeGlobals;
import com.cubic.nisjava.apiobjects.WSTransitaccountProduct;
import com.cubic.nisjava.apiobjects.WSTransitaccountProductList;
import com.cubic.nisjava.constants.AppConstants;
import com.cubic.nisjava.dataproviders.NISDataProviderSource;
import com.google.gson.Gson;

/**
 * The test class that gets the Product Catalog from the URL
 * /nis/nwapi/v2/transitaccount/<transitaccount-id>/subsystem/productcatalog.
 * 
 * @author 203402
 *
 */
public class NWAPIV2_TransitaccountSubsystemProductcatalogTest extends NWAPIV2_GetBase {

	private static final String EXPECTED_PRODUCT_SKU = "EXPECTED_PRODUCT_SKU";
	private static final String EXPECTED_PRODUCT_TYPE_ID = "EXPECTED_PRODUCT_TYPE_ID";
	private static final String EXPECTED_PRODUCT_FAMILY_ID = "EXPECTED_PRODUCT_FAMILY_ID";
	private static final String EXPECTED_PRODUCT_NAME = "EXPECTED_PRODUCT_NAME";
	private static final String EXPECTED_VALIDITY_DURATION = "EXPECTED_VALIDITY_DURATION";
	private static final String EXPECTED_VALIDITY_DURATION_TYPE = "EXPECTED_VALIDITY_DURATION_TYPE";
	private static final String EXPECTED_PRICE = "EXPECTED_PRICE";
	private static final String EXPECTED_SUPPORTS_AUTOLOAD = "EXPECTED_SUPPORTS_AUTOLOAD";
	
	/**
	 * The @Test method that gets the Product Catalog from the
	 * /nis/nwapi/v2/transitaccount/<transitaccount-id>/subsystem/productcatalog
	 * API.
	 * 
	 * testRailId = 933962
	 * 
	 * @param context  The TestNG Context object
	 * @param data  The Test Data from the JSON input file
	 * @throws Throwable Thrown if something goes wrong
	 */
	@Test(dataProvider = AppConstants.DATA_PROVIDER, dataProviderClass = NISDataProviderSource.class)
	public void getProductCatalog(ITestContext context, Hashtable<String, String> data) throws Throwable {
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
	protected void verifyResponse(Hashtable<String, String> data, RESTActions restActions, String response) {
		Gson gson = new Gson();
		
		LOG.info("##### Parsing the response content...");
		WSTransitaccountProductList wsProductList = gson.fromJson(response, WSTransitaccountProductList.class);            
        
		LOG.info("##### Testing the response content...");
		
		restActions.assertTrue( wsProductList != null, 
				"WSTransitaccountProductList REFERENCE IS NULL BUT SHOULD NOT BE" );
		
		if ( null == wsProductList ) {
			return;
		}
		
		List<WSTransitaccountProduct> productList = wsProductList.getProducts();
		
		restActions.assertTrue( productList != null, 
				"Transitaccount Product List REFERENCE IS NULL BUT SHOULD NOT BE" );
		
		if ( null == productList ) {
			return;
		}
		
		restActions.assertTrue( productList.size() > 0,
				"Product List IS EMPTY BUT IT SHOULD NOT BE" );
		
		if ( productList.size() == 0 ) {
			return;
		}
		
		WSTransitaccountProduct expected = new WSTransitaccountProduct();
		expected.setProductSku(data.get(EXPECTED_PRODUCT_SKU));
		expected.setProductTypeId(data.get(EXPECTED_PRODUCT_TYPE_ID));
		expected.setProductFamilyId(data.get(EXPECTED_PRODUCT_FAMILY_ID));
		expected.setProductName(data.get(EXPECTED_PRODUCT_NAME));
		String sExpectedValidityDuration = data.get(EXPECTED_VALIDITY_DURATION);
		Integer expectedValidityDuration = Integer.valueOf(sExpectedValidityDuration);
		expected.setValidityDuration(expectedValidityDuration);
		expected.setValidityDurationType(data.get(EXPECTED_VALIDITY_DURATION_TYPE));
		String sExpectedPrice = data.get(EXPECTED_PRICE);
		Integer expectedPrice = Integer.valueOf(sExpectedPrice);
		expected.setPrice(expectedPrice);
		String sExpectedSupportsAutoload = data.get(EXPECTED_SUPPORTS_AUTOLOAD);
		Boolean expectedSupportsAutoload = Boolean.valueOf(sExpectedSupportsAutoload);
		expected.setSupportsAutoload(expectedSupportsAutoload);
		
		WSTransitaccountProduct actual = productList.get(0);
		boolean bEqual = expected.equals(actual);
		restActions.assertTrue( bEqual,WSTransitaccountProduct.getErrorMessage()); 
	}

	/**
	 * Helper method to build the URL used to call the API.
	 * 
	 * @param data  The Test Data from the JSON input file.
	 * @return The URL in string form.
	 */
	@Override
	protected String buildURL(Hashtable<String, String> data) {
        String sURL = "http://" + BackOfficeGlobals.ENV.NIS_HOST
        		+ ":" + BackOfficeGlobals.ENV.NIS_PORT
                + "/nis/nwapi/v2/transitaccount/"
                + data.get("TRANSIT_ACCOUNT_ID") + "/subsystem/"
        		+ data.get("SUBSYSTEM_ENUM") + "/productcatalog";
		return sURL;
	}

}
