package com.cubic.nistests.tests;

import java.net.HttpURLConnection;
import java.util.Hashtable;

import org.apache.log4j.Logger;
import org.testng.ITestContext;
import org.testng.annotations.Test;

import com.cubic.accelerators.RESTActions;
import com.cubic.accelerators.RESTConstants;
import com.cubic.accelerators.RESTEngine;
import com.cubic.backoffice.constants.BackOfficeGlobals;
import com.cubic.backoffice.utils.BackOfficeUtils;
import com.cubic.nisjava.apiobjects.WSCountryList;
import com.cubic.nisjava.apiobjects.WSCountry;
import com.cubic.nisjava.apiobjects.WSState;
import com.cubic.nisjava.constants.AppConstants;
import com.cubic.nisjava.dataproviders.NISDataProviderSource;
import com.google.gson.Gson;
import com.sun.jersey.api.client.ClientResponse;

/**
 * This test class will get a List of countries from the NIS
 * customerservice/countries API.
 * 
 * @author 203402
 *
 */
public class CountriesTest extends RESTEngine {

	private final Logger LOG = Logger.getLogger(this.getClass().getName());

	/**
	 * Get a list of countries.
	 * 
	 * testRailId = 686679
	 * 
	 * @param context The TestNG context object reference.
	 * @param data  The Test Data.
	 * @throws Throwable  Thrown if something goes wrong.
	 */
	@Test(dataProvider = AppConstants.DATA_PROVIDER, dataProviderClass = NISDataProviderSource.class)
	public void getCountries(ITestContext context, Hashtable<String, String> data) throws Throwable {
		String testCaseName = data.get("TestCase_Description");
		LOG.info("##### Starting Test Case " + testCaseName);

		try {
			LOG.info("##### Setting up automation test...");
			BackOfficeGlobals.ENV.setEnvironmentVariables();
			RESTActions restActions = setupAutomationTest(context, testCaseName);

			String sURL = buildURL();
			LOG.info("##### Built URL: " + sURL);

			LOG.info("Creating GET request headers");
			Hashtable<String, String> headerTable = BackOfficeUtils.createRESTHeader(RESTConstants.APPLICATION_JSON);
			Hashtable<String, String> urlQueryParameters = new Hashtable<String, String>();

			LOG.info("##### Making HTTP request to get the Name Titles...");
			ClientResponse clientResponse = restActions.getClientResponse(sURL, headerTable, urlQueryParameters,
					RESTConstants.APPLICATION_JSON);

			LOG.info("##### Verifying HTTP response code...");
			int status = clientResponse.getStatus();
			String msg = "WRONG HTTP RESPONSE CODE - EXPECTED 200, FOUND " + status;
			restActions.assertTrue(status == HttpURLConnection.HTTP_OK, msg);

			String response = clientResponse.getEntity(String.class);
			LOG.info("##### Got the response body");
			restActions.assertTrue(response != null, "RESPONSE IS NULL BUT SHOULD NOT BE NULL");
			LOG.info(response);

			verifyResponse( restActions, data, response );
			
		} finally {
			teardownAutomationTest(context, testCaseName);
			LOG.info("##### Done!");
		}
	}

	/**
	 * Helper method to verify the the list of countries.
	 * 
	 * @param restActions  The RestActions object used by this class.
	 * @param data  The Test Data.
	 * @param response  The API response in String form.
	 */
	private void verifyResponse(RESTActions restActions, Hashtable<String, String> data, String response) {
		String msg = "";
		LOG.info("##### Parse the actual response...");
		Gson gson = new Gson();
		WSCountryList countryListActual = gson.fromJson(response, WSCountryList.class);
		boolean bFoundCountry = false;
		LOG.info("##### Test the list of countries...");

		String expectedStateKey = data.get("EXPECTED_STATE_KEY");
		String expectedStateVal = data.get("EXPECTED_STATE_VALUE");
		String sExpectedNumStates = data.get("EXPECTED_NUM_US_STATES");
		String sExpectedCountryName = data.get("EXPECTED_COUNTRY_NAME");
		String sExpectedCountryDesc = data.get("EXPECTED_COUNTRY_DESC");
		for (WSCountry country : countryListActual.getCountries()) {

			if (sExpectedCountryName.equals(country.getCountry())) {
				bFoundCountry = true;

				String expectedDescription = sExpectedCountryDesc;
				String actualDescription = country.getDescription();
				boolean bDesc = expectedDescription.equals(actualDescription);
				if (!bDesc) {
					msg = String.format("BAD DESCRIPTION: EXPECTED %s FOUND %s", expectedDescription,
							actualDescription);
				}
				restActions.assertTrue(bDesc, msg);

				int exectedNumStates = Integer.valueOf(sExpectedNumStates);
				int actualNumStates = country.getStates().size();
				boolean bNumStates = exectedNumStates == actualNumStates;
				if (!bNumStates) {
					msg = String.format("BAD STATELIST SIZE: EXPECTED %s FOUND %s", exectedNumStates, actualNumStates);
				}
				restActions.assertTrue(bNumStates, msg);

				for (WSState state : country.getStates()) {
					if (expectedStateKey.equals(state.getKey())) {

						String actualState = state.getValue();
						boolean bState = expectedStateVal.equals(actualState);
						if (!bState) {
							msg = String.format("BAD STATE NAME: EXPECTED %s FOUND %s", expectedStateVal, actualState);
						}
						restActions.assertTrue(bState, msg);
						break;
					}
				}
				break;
			}
		}
		restActions.assertTrue(bFoundCountry, "COUNTRY WAS NOT FOUND");
	}

	/**
	 * Build the URL used to get the Countries list.
	 * 
	 * @return The URL in string format.
	 */
	private String buildURL() {
		return "http://" + BackOfficeGlobals.ENV.NIS_HOST + ":" + BackOfficeGlobals.ENV.NIS_PORT
				+ "/nis/nwapi/v2/customerservice/countries";
	}

}
