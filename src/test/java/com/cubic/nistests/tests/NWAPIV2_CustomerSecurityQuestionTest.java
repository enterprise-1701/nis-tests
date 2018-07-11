package com.cubic.nistests.tests;

import java.util.Hashtable;
import java.util.List;
import java.util.UUID;

import org.testng.ITestContext;
import org.testng.annotations.Test;

import com.cubic.accelerators.RESTActions;
import com.cubic.accelerators.RESTConstants;
import com.cubic.backoffice.constants.BackOfficeGlobals;
import com.cubic.backoffice.utils.BackOfficeUtils;
import com.cubic.nisjava.apiobjects.WSSecurityQuestions;
import com.cubic.nisjava.constants.AppConstants;
import com.cubic.nisjava.dataproviders.NISDataProviderSource;
import com.google.gson.Gson;

/**
 * Get a Customer's Security Question using the username.
 *  
 * @author 203402
 *
 */
public class NWAPIV2_CustomerSecurityQuestionTest extends NWAPIV2_CustomerBase {

	/**
	 * Get a Customer's Security Question using the username.
	 * 
	 * testRailId = 960977
	 * 
	 * @param context  The TestNG Context object.
	 * @param data  The Test Data from the JSON input file.
	 * @throws Throwable  Thrown if something goes wrong.
	 */
	@Test(dataProvider = AppConstants.DATA_PROVIDER, dataProviderClass = NISDataProviderSource.class)
	public void securityQuestion(ITestContext context, Hashtable<String, String> data) throws Throwable {
		String testCaseName = data.get("TestCase_Description");
		LOG.info("##### Starting Test Case " + testCaseName);
		
		try {
			LOG.info("##### Setting up automation test...");
			BackOfficeGlobals.ENV.setEnvironmentVariables();
			RESTActions restActions = setupAutomationTest(context, testCaseName);
			
            LOG.info("##### Creating GET request headers");
			Hashtable<String,String> headerTable = BackOfficeUtils.createRESTHeader(RESTConstants.APPLICATION_JSON);			
			
			String uniqueID = UUID.randomUUID().toString();
			String username = uniqueID + "@test.com";
			String password = "Pas5word!";
			
			LOG.info("##### Call the Prevalidate API");
			prevalidate(restActions, headerTable, username, password);
			
			LOG.info("##### Get all Security Questions from the system");
			String expectedSecurityQuestion = securityQuestion(restActions, headerTable);
			
			LOG.info("##### Call the Create Customer API");
			String expectedCustomerId = createCustomer(restActions, headerTable, username, password, expectedSecurityQuestion);
			
			LOG.info("##### Call the Complete Registration API");
			completeRegistration(restActions, headerTable, expectedCustomerId);
			
			LOG.info("##### Get the user's Security Question");
			String response = securityQuestion( restActions, data, headerTable, username );
			verifyResponse( restActions, response, expectedSecurityQuestion );
		} finally {
			teardownAutomationTest(context, testCaseName);
			LOG.info("##### Done!");
		}
	}
	
	/**
	 * Helper method to verify the response, to parse the response and
	 * get the actual value of the Security Question from it, then compare
	 * this value with the expectedSecurityQuestion value that is passed in.
	 * 
	 * @param restActions  The RESTActions object used by the @Test method.
	 * @param response  The API response in String form.
	 * @param expectedSecurityQuestion  The expected Security Question.
	 */
	protected void verifyResponse(RESTActions restActions, String response, String expectedSecurityQuestion) {
		LOG.info("##### Verifying the response...");
		
		Gson gson = new Gson();
		WSSecurityQuestions securityQuestionList = gson.fromJson(response, WSSecurityQuestions.class);
		
		restActions.assertTrue(securityQuestionList != null, 
				"securityQuestionList IS NULL BUT IT SHOULD NOT BE");
		
		List<String> securityQuestions = securityQuestionList.getSecurityQuestions();

		restActions.assertTrue(securityQuestions != null, 
				"securityQuestions IS NULL BUT IT SHOULD NOT BE");		
		
		restActions.assertTrue(securityQuestions.size() > 0, 
				"securityQuestions HAS SIZE == 0 BUT IT SHOULD NOT HAVE");
		
		String actualSecurityQuestion = securityQuestions.get(0);
		
		restActions.assertTrue(actualSecurityQuestion != null, 
				"actualSecurityQuestion IS NULL BUT IT SHOULD NOT BE");	
		
		restActions.assertTrue(expectedSecurityQuestion.equals(actualSecurityQuestion), 
				String.format("BAD SECURITY QUESTION - EXPECTED '%s' FOUND '%s'", 
						expectedSecurityQuestion, actualSecurityQuestion));
	}
	
}
