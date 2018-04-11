package com.cubic.nistests.tests;

import java.net.HttpURLConnection;
import java.util.Hashtable;

import org.apache.log4j.Logger;
import org.testng.ITestContext;
import org.testng.annotations.Test;

import com.cubic.accelerators.RESTActions;
import com.cubic.accelerators.RESTConstants;
import com.cubic.nisjava.constants.AppConstants;
import com.cubic.nisjava.dataproviders.NISDataProviderNEXTLINK_v1;
import com.cubic.nisjava.utils.NextLinkBase;
import com.cubic.nisjava.apiobjects.WSCatalogContainer;
import com.cubic.nisjava.apiobjects.WSProduct;
import com.cubic.nisjava.apiobjects.WSStoredValue;
import com.cubic.vpos.trm.TrmCommands;
import com.cubic.vpos.trm.TrmResponses;
import com.cubic.vpos.trm.mock.MockRISCardReader;
import com.cubic.vpos.trm.mock.MockedTerminalProxy;
import com.cubic.nisjava.apiobjects.WSCreateSessionResponse;
import com.google.gson.Gson;
import com.sun.jersey.api.client.ClientResponse;

public class ProductCatalogTests extends NextLinkBase {

	private final Logger LOG = Logger.getLogger(this.getClass().getName());

	/**
	 * Get the Product Catalog for a given card.
	 * 
	 * testRailId: 428537
	 * 
	 * @param context
	 *            The TestNG context object
	 * @param data
	 *            The test data
	 * @throws Throwable
	 *             Thrown if something goes wrong
	 */
	@Test(dataProvider = AppConstants.DATA_PROVIDER, dataProviderClass = NISDataProviderNEXTLINK_v1.class)
	public void getProductCatalog(ITestContext context, Hashtable<String, String> data) throws Throwable {
		String testCaseName = data.get("TestCase_Description");
		LOG.info("TestCase_Description=" + testCaseName);

		try {
			javax.net.ssl.HttpsURLConnection.setDefaultHostnameVerifier(
					new javax.net.ssl.HostnameVerifier(){

					    public boolean verify(String hostname,
					            javax.net.ssl.SSLSession sslSession) {
					        return true;
					    }
					});			
			
			// get headers
			Hashtable<String, String> headers = buildHeaders(data);

			// build create session URL
			String createSessionURL = buildCreateSessionURL(data);

			RESTActions restActions = setupAutomationTest(context, testCaseName);

			Hashtable<String, String> urlQueryParameters = new Hashtable<String, String>();

			// get create session
			ClientResponse clientResponse = restActions.getClientResponse(createSessionURL, headers, urlQueryParameters,
					RESTConstants.APPLICATION_JSON);

			// Verify HTTP response code
			int status = clientResponse.getStatus();
			String msg = "WRONG HTTP RESPONSE CODE - EXPECTED 200, FOUND " + status;
			restActions.assertTrue(status == HttpURLConnection.HTTP_OK, msg);

			String response = clientResponse.getEntity(String.class);
			LOG.info(response);

			Gson gson = new Gson();

			// parse the JSON String response into a List
			WSCreateSessionResponse responseObj = gson.fromJson(response, WSCreateSessionResponse.class);

			// verify the Create Session operation was Successful
			restActions.assertTrue("Successful".equals(responseObj.getHdr().getResult()),
					"Hdr Result IS NOT 'Successful' BUT IT SHOULD BE");

			String sessionId = responseObj.getSessionId();
			LOG.info("sessionId=" + sessionId);

			String trmResponsesStr = responseObj.getResult().getTerminalCommands();
			LOG.info("terminalCommands=" + trmResponsesStr);

			// do the card/summary operations
			headers.put("x-cub-sessionid", sessionId);

			// build the product/catalog URL
			String prodCatHost = data.get("HOST");
			String prodCatURLFmt = data.get("PRODUCT_CATALOG_URL");
			String surl = String.format(prodCatURLFmt, prodCatHost);

			// prepare the terminal commands
			TrmCommands trmCommands = TrmCommands.fromString(trmResponsesStr);

			// use the RIS card for Miami
			MockedTerminalProxy mockedTerminalProxy = new MockedTerminalProxy(new MockRISCardReader());
			mockedTerminalProxy.selectCard();

			// execute terminal commands to get the terminal responses
			TrmResponses trmResponses = mockedTerminalProxy.executeCommandsOnReader(trmCommands);
			trmResponsesStr = trmResponses.toString();

			LOG.info("trmResponsesStr=" + trmResponsesStr);

			// ==

			String format = "{ \"terminalResponses\":\"%s\" }";
			String productCatalogRequestBody = String.format(format, trmResponsesStr);

			clientResponse = restActions.postClientResponse(surl, productCatalogRequestBody, headers,
					urlQueryParameters, RESTConstants.APPLICATION_JSON);

			// Verify HTTP response code
			status = clientResponse.getStatus();
			msg = "WRONG HTTP RESPONSE CODE - EXPECTED 200, FOUND " + status;
			restActions.assertTrue(status == HttpURLConnection.HTTP_OK, msg);

			response = clientResponse.getEntity(String.class);
			LOG.info(response);

			responseObj = gson.fromJson(response, WSCreateSessionResponse.class);

			restActions.assertTrue("CONTINUE".equals(responseObj.getResult().getResponseCode()),
					"Response Code IS NOT 'CONTINUE' BUT IT SHOULD BE");
			trmResponsesStr = responseObj.getResult().getTerminalCommands();
			LOG.info("terminalCommands=" + trmResponsesStr);

			do {

				trmCommands = TrmCommands.fromString(trmResponsesStr);

				// execute terminal commands to get the terminal responses
				trmResponses = mockedTerminalProxy.executeCommandsOnReader(trmCommands);
				trmResponsesStr = trmResponses.toString();

				productCatalogRequestBody = String.format(format, trmResponsesStr);

				clientResponse = restActions.postClientResponse(surl, productCatalogRequestBody, headers,
						urlQueryParameters, RESTConstants.APPLICATION_JSON);

				status = clientResponse.getStatus();
				msg = "WRONG HTTP RESPONSE CODE - EXPECTED 200, FOUND " + status;
				restActions.assertTrue(status == HttpURLConnection.HTTP_OK, msg);

				response = clientResponse.getEntity(String.class);
				LOG.info(response);
				responseObj = gson.fromJson(response, WSCreateSessionResponse.class);

				if ("OK".equals(responseObj.getResult().getResponseCode())) {
					break;
				}

				trmResponsesStr = responseObj.getResult().getTerminalCommands();
				LOG.info("terminalCommands=" + trmResponsesStr);

			} while ("CONTINUE".equals(responseObj.getResult().getResponseCode()));

			if ("OK".equals(responseObj.getResult().getResponseCode())) {
				LOG.info("ResponseCode is OK");

				// com.cubic.qa.apiobjects.WSCatalogContainer
				WSCatalogContainer catResponse = gson.fromJson(response, WSCatalogContainer.class);

				restActions.assertTrue("Successful".equals(catResponse.getHdr().getResult()),
						"HDR RESULT IS NOT 'Successful' BUT IT SHOULD BE");

				restActions.assertTrue(null != catResponse.getHdr().getUid(),
						"HDR RESULT IS null BUT IT SHOULD NOT BE");

				restActions.assertTrue("".equals(catResponse.getResult().getTerminalCommands()),
						"RESULT TERMINAL COMMANDS IS NOT EMPTY STRING, BUT IT SHOULD BE");
				
				restActions.assertTrue(null != catResponse.getCatalog().getMaxAddValue(),
						"CATALOG MAX ADD VALUE IS NULL BUT IT SHOULD NOT BE");
				
				restActions.assertTrue(null != catResponse.getCatalog().getStoredValue(),
						"CATALOG STORED VALUE IS NULL BUT IT SHOULD NOT BE");
				
				for (WSStoredValue stored : catResponse.getCatalog().getStoredValue()) {
					restActions.assertTrue(null != stored.getName(), "STORED VALUE NAME IS NULL BUT IT SHOULD NOT BE");
					
					restActions.assertTrue(stored.getAmount() > 0,
							"STORED VALUE AMOUNT IS NOT GREATER THAN ZERO BUT IT SHOULD BE");

					restActions.assertTrue(stored.getPageNumber() > 0,
							"STORED VALUE PAGE NUMBER IS NOT GREATER THAN ZERO BUT IT SHOULD BE");

					restActions.assertTrue(stored.getButtonNumber() > 0,
							"STORED VALUE BUTTON NUMBER IS NOT GREATER THAN ZERO BUT IT SHOULD BE");
					
					restActions.assertTrue(null != stored.getLoadType(),
							"STORED VALUE LOAD TYPE IS NULL BUT IT SHOULD NOT BE");
				}
				
				restActions.assertTrue(null != catResponse.getCatalog().getProduct(),
						"CATALOG PRODUCT VALUE IS NULL BUT IT SHOULD NOT BE");
				
				for (WSProduct product : catResponse.getCatalog().getProduct()) {
					restActions.assertTrue(product.getFareInstrumentId() > 0,
							"PRODUCT FARE INSTRUMENT ID IS NOT GREATER THAN ZERO BUT IT SHOULD BE");

					restActions.assertTrue(null != product.getName(), "PRODUCT NAME IS NULL BUT IT SHOULD NOT BE");

					restActions.assertTrue(null != product.getNameShort(),
							"PRODUCT NAME SHORT IS NULL BUT IT SHOULD NOT BE");

					restActions.assertTrue(null != product.getSupportsAutoload(),
							"PRODUCT SUPPORTS AUTOLOAD IS NULL BUT IT SHOULD NOT BE");

					restActions.assertTrue(product.getPrice() > 0,
							"PRODUCT PRICE IS NOT GREATER THAN ZERO BUT IT SHOULD BE");
					
					restActions.assertTrue(product.getPageNumber() > 0,
							"PRODUCT PAGE NUMBER IS NOT GREATER THAN ZERO BUT IT SHOULD BE");

					restActions.assertTrue(product.getButtonNumber() > 0,
							"PRODUCT BUTTON NUMBER IS NOT GREATER THAN ZERO BUT IT SHOULD BE");
				}
			}

			// close session with vpos/NL
			String deleteSesssionURL = buildDeleteSessionURL(data);

			String deleteSessionRequestBody = buildCardSummaryRequestBody("EA+ADQICAABqBwAAAQAAkQA=");

			LOG.info("requestBody=" + deleteSessionRequestBody);

			response = deleteClientResponse(deleteSesssionURL, deleteSessionRequestBody, headers);

			responseObj = gson.fromJson(response, WSCreateSessionResponse.class);

			restActions.assertTrue("OK".equals(responseObj.getResult().getResponseCode()),
					"Response Code IN NOT 'OK' BUT IT SHOULD BE");
			restActions.assertTrue("Successful".equals(responseObj.getHdr().getResult()),
					"Result IS NOT 'Successful' BUT IT SHOULD BE");

		} finally {
			teardownAutomationTest(context, testCaseName);
		}
	}
}
