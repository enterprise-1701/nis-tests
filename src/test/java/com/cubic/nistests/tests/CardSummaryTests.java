package com.cubic.nistests.tests;

import java.net.HttpURLConnection;
import java.util.Hashtable;

import org.apache.log4j.Logger;
import org.testng.ITestContext;
import org.testng.annotations.Test;

import com.cubic.nisjava.apiobjects.WSCreateSessionResponse;
import com.cubic.nisjava.constants.AppConstants;
import com.cubic.nisjava.constants.NISGlobals;
import com.cubic.nisjava.dataproviders.NISDataProviderNEXTLINK_v1;
import com.cubic.nisjava.utils.NextLinkBase;
import com.cubic.nisjava.apiobjects.WSCardSummaryResponse;
import com.cubic.vpos.trm.TrmCommands;
import com.cubic.vpos.trm.TrmResponses;
import com.cubic.vpos.trm.mock.MockRISCardReader;
import com.cubic.vpos.trm.mock.MockedTerminalProxy;
import com.google.gson.Gson;
import com.sun.jersey.api.client.ClientResponse;
import com.cubic.accelerators.RESTActions;
import com.cubic.accelerators.RESTConstants;

/**
 * Get a Card/Summary 
 * 
 * @author 203402
 *
 */
public class CardSummaryTests extends NextLinkBase {

	private final Logger LOG = Logger.getLogger(this.getClass().getName());

	/**
	 * Get a series of Card/Summary response.
	 * 
	 * testRailId: 428535
	 * 
	 * @param context  The TestNG context object
	 * @param data
	 * @throws Throwable
	 */
	@Test(dataProvider = AppConstants.DATA_PROVIDER, dataProviderClass = NISDataProviderNEXTLINK_v1.class)
	public void cardSummaryTest(ITestContext context, Hashtable<String, String> data) throws Throwable {
		String testCaseName = data.get("TestCase_Description");
		LOG.info("TestCase_Description=" + testCaseName);

		try {
			RESTActions restActions = setupAutomationTest(context, testCaseName);

			// get headers
			Hashtable<String, String> headers = buildHeaders(data);
			// build create session URL
			String createSessionURL = buildCreateSessionURL(data);

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

			// == do the card/summary operations
			headers.put("x-cub-sessionid", sessionId);

			// build the card/summary URL
			String sCardSummaryURL = buildCardSummaryURL(data);

			// prepare the terminal commands
			TrmCommands trmCommands = TrmCommands.fromString(trmResponsesStr);

			// use the RIS card for Miami
			MockedTerminalProxy mockedTerminalProxy = new MockedTerminalProxy(new MockRISCardReader());
			mockedTerminalProxy.selectCard();

			// execute terminal commands to get the terminal responses
			TrmResponses trmResponses = mockedTerminalProxy.executeCommandsOnReader(trmCommands);
			trmResponsesStr = trmResponses.toString();

			String cardSummaryRequestBody = buildCardSummaryRequestBody(trmResponsesStr);

			clientResponse = restActions.postClientResponse(sCardSummaryURL, cardSummaryRequestBody, headers,
					urlQueryParameters, RESTConstants.APPLICATION_JSON);

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

				cardSummaryRequestBody = buildCardSummaryRequestBody(trmResponsesStr);

				clientResponse = restActions.postClientResponse(sCardSummaryURL, cardSummaryRequestBody, headers,
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

				// } while( n++ < 5 );
			} while ("CONTINUE".equals(responseObj.getResult().getResponseCode()));

			// assert the fare card summary
			if ("OK".equals(responseObj.getResult().getResponseCode())) {
				WSCardSummaryResponse responseObj2 = gson.fromJson(response, WSCardSummaryResponse.class);

				LOG.info("Serial number=" + responseObj2.getSerialNumber());

				LOG.info("Result=" + responseObj2.getHdr().getResult());

				LOG.info("Card summary=" + responseObj2.getCardSummary());

				restActions.assertTrue(null != responseObj2.getCardSummary(),
						"Card Summary IS NULL BUT IT SHOULD NOT BE");

				if (null != responseObj2.getCardSummary()) {
					restActions.assertTrue(null != responseObj2.getCardSummary().getRiderClassId(),
							"Rider Class Id IS NULL BUT IT SHOULD NOT BE");
					restActions.assertTrue(null != responseObj2.getCardSummary().getExpirationDateTime(),
							"Expiration Date Time IS NULL BUT IT SHOULD NOT BE");
					restActions.assertTrue(null != responseObj2.getCardSummary().getSvRecurringAmount(),
							"Sv Recurring Amount IS NULL BUT IT SHOULD NOT BE");
					restActions.assertTrue(null != responseObj2.getCardSummary().getSvAutoloadSetup(),
							"Sv Autoload Setup IS NULL BUT IT SHOULD NOT BE");
					restActions.assertTrue(null != responseObj2.getCardSummary().getTotalPasses(),
							"Total Passes IS NULL BUT IT SHOULD NOT BE");
					restActions.assertTrue(null != responseObj2.getCardSummary().getCscRegisteredFlag(),
							"Csc Registered Flag IS NULL BUT IT SHOULD NOT BE");
					restActions.assertTrue(null != responseObj2.getCardSummary().getMaxAddValue(),
							"Max Add Value IS NULL BUT IT SHOULD NOT BE");
					restActions.assertTrue(null != responseObj2.getCardSummary().getMaxCancelValue(),
							"Max Cancel Value IS NULL BUT IT SHOULD NOT BE");
					restActions.assertTrue(null != responseObj2.getCardSummary().getTestFlag(),
							"Test Flag IS NULL BUT IT SHOULD NOT BE");
				}
			}

			// close session with vpos/NL
			String deleteSesssionURL = buildDeleteSessionURL(data);

			trmResponsesStr = responseObj.getResult().getTerminalCommands();
			LOG.info("terminalCommands=" + trmResponsesStr);

			trmCommands = TrmCommands.fromString(trmResponsesStr);

			// execute terminal commands to get the terminal responses
			trmResponses = mockedTerminalProxy.executeCommandsOnReader(trmCommands);
			trmResponsesStr = trmResponses.toString();

			String deleteSessionRequestBody = buildCardSummaryRequestBody("EA+ADQICAABqBwAAAQAAkQA=");

			LOG.info("Delete session request body=" + deleteSessionRequestBody);

			deleteClientResponse(deleteSesssionURL, deleteSessionRequestBody, headers);

			// assert that vpos/NL responds with OK from the close request
			responseObj = gson.fromJson(response, WSCreateSessionResponse.class);

			restActions.assertTrue("OK".equals(responseObj.getResult().getResponseCode()),
					"Response Code IN NOT 'OK' BUT IT SHOULD BE");

			restActions.assertTrue("Successful".equals(responseObj.getHdr().getResult()),
					"Result IS NOT 'Successful' BUT IT SHOULD BE");

		} finally {
			teardownAutomationTest(context, testCaseName);
		}
	}

	/**
	 * Build the URL of the Card Summary endpoint.
	 * 
	 * @param data
	 *            The Test Data
	 * @return the URL of the Card Summary endpoint
	 */
	private String buildCardSummaryURL(Hashtable<String, String> data) {
		String host = data.get(NISGlobals.NIS_HOST_NAME);
		String sfmt = data.get(NISGlobals.NIS_CARD_SUMMARY_URL_NAME);
		String sURL = String.format(sfmt, host);
		return sURL;
	}

}
