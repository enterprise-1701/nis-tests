package com.cubic.nistests.tests;

import org.apache.log4j.Logger;

import java.net.HttpURLConnection;
import java.util.Hashtable;
import java.util.List;

import org.testng.ITestContext;
import org.testng.annotations.Test;

import com.cubic.accelerators.RESTActions;
import com.cubic.accelerators.RESTConstants;
import com.cubic.accelerators.RESTEngine;
import com.cubic.backoffice.utils.BackOfficeUtils;
import com.cubic.nisjava.constants.AppConstants;
import com.cubic.nisjava.dataproviders.NISDataProviderNEXTLINK_v1;
import com.sun.jersey.api.client.ClientResponse;
import com.google.gson.Gson;
import com.cubic.nisjava.apiobjects.WSCatalogContainer;
import com.cubic.nisjava.apiobjects.WSProduct;
import com.cubic.nisjava.apiobjects.WSStoredValue;

public class ProductCatalogNewTests extends RESTEngine {

	private final Logger LOG = Logger.getLogger(this.getClass().getName());

	/**
	 * Get the New Product Catalog.
	 * 
	 * testRailId: 428539
	 * 
	 * @param context
	 *            The TestNG context object
	 * @param data
	 *            The test data
	 * @throws Throwable
	 *             Thrown if something goes wrong
	 */
	@Test(dataProvider = AppConstants.DATA_PROVIDER, dataProviderClass = NISDataProviderNEXTLINK_v1.class)
	public void getNewProductCatalog(ITestContext context, Hashtable<String, String> data) throws Throwable {
		String testCaseName = data.get("TestCase_Description");
		LOG.info("TestCase_Description=" + testCaseName);

		try {
			LOG.info("##### Setting up automation test");
			RESTActions restActions = setupAutomationTest(context, testCaseName);

			LOG.info("##### Getting REST Headers");
			Hashtable<String, String> headers = BackOfficeUtils.createRESTHeader(RESTConstants.APPLICATION_JSON);

			String newProdCatFmt = data.get("PRODUCT_CATALOG_URL");
			String newProdCatURL = String.format(newProdCatFmt, data.get("HOST"), data.get("PORT"));

			Hashtable<String, String> urlQueryParameters = new Hashtable<String, String>();

			LOG.info("##### Calling Product Catalog URL");
			ClientResponse clientResponse = restActions.getClientResponse(newProdCatURL, headers, urlQueryParameters,
					RESTConstants.APPLICATION_JSON);

			// Verify HTTP response code
			int status = clientResponse.getStatus();
			String msg = "WRONG HTTP RESPONSE CODE - EXPECTED 200, FOUND " + status;
			restActions.assertTrue(status == HttpURLConnection.HTTP_OK, msg);

			String response = clientResponse.getEntity(String.class);
			LOG.info("##### Listing response...");
			LOG.info(response);

			Gson gson = new Gson();

			LOG.info("##### Parsing Catalog Response");
			WSCatalogContainer catResponse = gson.fromJson(response, WSCatalogContainer.class);

			LOG.info("##### Testing top-level items");
			restActions.assertTrue("Successful".equals(catResponse.getHdr().getResult()),
					"HDR RESULT IS NOT 'Successful' BUT IT SHOULD BE");

			restActions.assertTrue(null != catResponse.getHdr().getUid(), "HDR RESULT IS null BUT IT SHOULD NOT BE");

			restActions.assertTrue(catResponse.getCatalog().getMaxAddValue() != null,
					"MAX ADD VALUE IS NULL BUT IT SHOULD NOT BE");

			restActions.assertTrue(catResponse.getCatalog().getMaxAddValue() > 0,
					"MAX ADD VALUE IS =< ZERO BUT IT SHOULD NOT BE");

			LOG.info("##### Testing stored value list");
			List<WSStoredValue> storedValueList = catResponse.getCatalog().getStoredValue();
			for (WSStoredValue storedValue : storedValueList) {
				restActions.assertTrue(storedValue.getName() != null, "STORED VALUE IS NULL BUT IT SHOULD NOT BE");

				restActions.assertTrue(storedValue.getAmount() != null, "AMOUNT IS NULL BUT IT SHOULD NOT BE");

				restActions.assertTrue(storedValue.getAmount() > 0, "AMOUNT IS =< ZERO BUT IT SHOULD NOT BE");

				List<String> loadTypeList = storedValue.getLoadType();
				if (null != loadTypeList) {
					restActions.assertTrue(loadTypeList.size() == 0, "LOAD TYPE LIST HAS SIZE > 0 BUT IT SHOULD NOT");
				}
			}

			LOG.info("##### Testing Catalog Products");
			for (WSProduct product : catResponse.getCatalog().getProduct()) {
				restActions.assertTrue(product.getFareInstrumentId() != null,
						"FARE INSTRUMENT ID IS NULL BUT IT SHOULD NOT BE");

				restActions.assertTrue(product.getFareInstrumentId() > 0,
						"FARE INSTRUMENT ID IS =< ZERO BUT IT SHOULD NOT BE");

				restActions.assertTrue(product.getName() != null, "NAME IS NULL BUT IT SHOULD NOT BE");

				restActions.assertTrue(product.getName().length() > 0, "NAME IS NULL BUT IT SHOULD NOT BE");

				restActions.assertTrue(product.getNameShort() != null, "NAME SHORT IS NULL BUT IT SHOULD NOT BE");

				restActions.assertTrue(product.getNameShort().length() > 0, "NAME SHORT IS NULL BUT IT SHOULD NOT BE");

				restActions.assertTrue(product.getSupportsAutoload() != null,
						"SUPPORTS AUTOLOAD IS NULL BUT IT SHOULD NOT BE");

				restActions.assertTrue(product.getSupportsAutoload() == false,
						"SUPPORTS AUTOLOAD IS NOT FALSE BUT IT SHOULD BE");

				restActions.assertTrue(product.getPrice() != null, "PRICE IS NULL BUT IT SHOULD NOT BE");

				restActions.assertTrue(product.getPrice() > 0, "PRICE IS =< ZERO BUT IT SHOULD NOT BE");
			}
		} finally {
			teardownAutomationTest(context, testCaseName);
			LOG.info("##### Done!");
		}
	}
}
