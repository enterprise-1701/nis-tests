package com.cubic.nistests.tests;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import org.apache.log4j.Logger;
import org.testng.ITestContext;
import org.testng.annotations.Test;

import com.cubic.accelerators.RESTActions;
import com.cubic.accelerators.RESTConstants;
import com.cubic.accelerators.RESTEngine;
import com.cubic.backoffice.constants.BackOfficeGlobals;
import com.cubic.backoffice.utils.BackOfficeUtils;
import com.cubic.nisjava.constants.AppConstants;
import com.cubic.nisjava.dataproviders.NISDataProviderSource;
import com.google.gson.Gson;
import com.sun.jersey.api.client.ClientResponse;
import com.cubic.nisjava.apiobjects.WSSecurityQuestionList;
import com.cubic.nisjava.apiobjects.WSSecurityQuestion;

/**
 * This test class will call the customerservice/securityquestions API 
 * in NIS and show the response, which is a list of security questions.
 * 
 * @author 203402
 *
 */
public class SecurityQuestionsTest extends RESTEngine {

	private static final String EXPECTED_QUESTION_1 = "EXPECTED_QUESTION_1";
	private static final String EXPECTED_QUESTION_2 = "EXPECTED_QUESTION_2";
	private static final String EXPECTED_VALUE_1 = "EXPECTED_VALUE_1";
	private static final String EXPECTED_VALUE_2 = "EXPECTED_VALUE_2";
	private static final String RESPONSE_IS_NULL = "RESPONSE IS NULL BUT SHOULD NOT BE NULL";
	private static final String WRONG_HTTP_CODE = "WRONG HTTP RESPONSE CODE - EXPECTED 200, FOUND ";
	
	private final Logger LOG = Logger.getLogger(this.getClass().getName());

	/**
	 * This test method will call the customerservice/securityquestions API
	 * in NIS and show the response, which is a list of security questions.
	 * 
	 * testRailId = 697931
	 * 
	 * @param context  The TestNG context object.
	 * @param data  The test data.
	 * @throws Throwable  Thrown if something goes wrong.
	 */
	@Test(dataProvider = AppConstants.DATA_PROVIDER, dataProviderClass = NISDataProviderSource.class)
	public void getSecurityQuestions(ITestContext context, Hashtable<String, String> data) throws Throwable {
		String testCaseName = data.get("TestCase_Description");
		LOG.info("##### Starting Test Case " + testCaseName);
		
		try {
			LOG.info("##### Setting up automation test...");
			BackOfficeGlobals.ENV.setEnvironmentVariables();
			RESTActions restActions = setupAutomationTest(context, testCaseName);
			
			String sURL = buildURL();
            LOG.info("##### Built URL: " + sURL);
            
            LOG.info("Creating GET request headers");
			Hashtable<String,String> headerTable = BackOfficeUtils.createRESTHeader(RESTConstants.APPLICATION_JSON);
			Hashtable<String, String> urlQueryParameters = new Hashtable<String, String>();
			
			LOG.info("##### Making HTTP request to get the EULA...");
			ClientResponse clientResponse = restActions.getClientResponse(sURL, headerTable, urlQueryParameters,
					RESTConstants.APPLICATION_JSON);

			LOG.info("##### Verifying HTTP response code...");
			int status = clientResponse.getStatus();
			String msg = WRONG_HTTP_CODE + status;
			restActions.assertTrue(status == HttpURLConnection.HTTP_OK, msg);
			
			String response = clientResponse.getEntity(String.class);
			LOG.info("##### Got the response body");
			restActions.assertTrue(response != null, RESPONSE_IS_NULL);
			LOG.info( response );
			
			WSSecurityQuestionList expectedQuestionList = buildExpectedTestData( data );
			
			LOG.info("##### Parse the actual response...");
			Gson gson = new Gson();
			WSSecurityQuestionList actualQuestionList = gson.fromJson(response, WSSecurityQuestionList.class);
			
			LOG.info("##### Compare the expected test data with the actual test data");
			boolean bEqual = expectedQuestionList.equals( actualQuestionList );
			restActions.assertTrue(bEqual, WSSecurityQuestionList.getErrorMessage());			
			
		} finally {
			teardownAutomationTest(context, testCaseName);
			LOG.info("##### Done!");
		}
	}
	
	/**
	 * Helper method to build the Expected test data.
	 * 
	 * @param data  The Test Data from the JSON input file.
	 * @return  An initialized WSSecurityQuestionList object.
	 */
	private WSSecurityQuestionList buildExpectedTestData( Hashtable<String,String> data) {
		WSSecurityQuestionList result = new WSSecurityQuestionList();
		List<WSSecurityQuestion> questionList = new ArrayList<WSSecurityQuestion>();
		
		WSSecurityQuestion q1 = new WSSecurityQuestion();
		q1.setName(data.get(EXPECTED_QUESTION_1));
		String sAnswer1 = data.get(EXPECTED_VALUE_1);
		Integer answer1 = Integer.valueOf(sAnswer1);
		q1.setValue(answer1);
		questionList.add(q1);

		WSSecurityQuestion q2 = new WSSecurityQuestion();
		q2.setName(data.get(EXPECTED_QUESTION_2));
		String sAnswer2 = data.get(EXPECTED_VALUE_2);
		Integer answer2 = Integer.valueOf(sAnswer2);
		q2.setValue(answer2);
		questionList.add(q2);
		
		result.setSecurityQuestions(questionList);
		return result;
	}
	
	/**
	 * Build the URL used to get the security questions list.
	 * 
	 * @return The URL in string format.
	 */
	private String buildURL() {
        return "http://" + BackOfficeGlobals.ENV.NIS_HOST + ":" + BackOfficeGlobals.ENV.NIS_PORT
                + "/nis/nwapi/v2/customerservice/securityquestions";
	}
}
