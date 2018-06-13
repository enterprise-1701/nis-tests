package com.cubic.nistests.tests;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.util.Hashtable;
import java.util.List;

import org.apache.log4j.Logger;

import com.cubic.accelerators.RESTActions;
import com.cubic.accelerators.RESTConstants;
import com.cubic.accelerators.RESTEngine;
import com.cubic.backoffice.constants.BackOfficeGlobals;
import com.cubic.nisjava.apiobjects.WSCompleteRegistrationResponse;
import com.cubic.nisjava.apiobjects.WSCreateCustomerResponse;
import com.cubic.nisjava.apiobjects.WSCustomerInfo;
import com.cubic.nisjava.apiobjects.WSCustomerInfoContainer;
import com.cubic.nisjava.apiobjects.WSHdr;
import com.cubic.nisjava.apiobjects.WSPrevalidateResponse;
import com.cubic.nisjava.apiobjects.WSSecurityQuestion;
import com.cubic.nisjava.apiobjects.WSSecurityQuestionList;
import com.google.gson.Gson;
import com.sun.jersey.api.client.ClientResponse;

public class NWAPIV2_CustomerBase extends RESTEngine {

	protected static final String EXPECTED_HTTP_RESPONSE_CODE = "EXPECTED_HTTP_RESPONSE_CODE";	
	protected static final String BAD_RESPONSE_CODE_FMT = "WRONG HTTP RESPONSE CODE - EXPECTED %s, FOUND %s";
	protected static final String RESPONSE_IS_NULL = "RESPONSE IS NULL BUT SHOULD NOT BE NULL";	
	
	protected final Logger LOG = Logger.getLogger(this.getClass().getName());
	
	/**
	 * Test step 1: call the prevalidate API
	 * 
	 * @param restActions  The RESTActions object used by the test
	 * @param headerTable  The HTTP Headers
	 * @param username	The username
	 * @param password	The password
	 * @throws Throwable  Thrown if something goes wrong
	 */
	protected void prevalidate(RESTActions restActions, Hashtable<String,String> headerTable, String username, String password) throws Throwable {
		String sURL = buildPrevalidateURL();
        LOG.info("##### Built URL: " + sURL);
        
		String sPrevalidateRequestBody = buildPrevalidateRequestBody(username,password);            
        
		LOG.info("##### Making HTTP request to prevalidate the credentials...");
		ClientResponse clientResponse = restActions.postClientResponse(
				sURL, sPrevalidateRequestBody, headerTable, null,
				RESTConstants.APPLICATION_JSON);

		LOG.info("##### Verifying HTTP response code...");
		int status = clientResponse.getStatus();
		String msg = "WRONG HTTP RESPONSE CODE - EXPECTED 200, FOUND " + status;
		restActions.assertTrue(status == HttpURLConnection.HTTP_OK, msg);
		
		String response = clientResponse.getEntity(String.class);
		LOG.info("##### Got the response body: " + response);
		restActions.assertTrue(response != null, "RESPONSE IS NULL BUT SHOULD NOT BE NULL");	
		
		LOG.info("##### Parse the actual response...");
		Gson gson = new Gson();
		WSPrevalidateResponse prevalidateResponse = gson.fromJson(response, WSPrevalidateResponse.class);
		restActions.assertTrue( prevalidateResponse.getIsUsernameValid(), 
				"IsUsernameValid SHOULD BE TRUE BUT IT IS NOT" );
		restActions.assertTrue( prevalidateResponse.getIsPasswordValid(),
				"IsPasswordValid SHOULD BE TRUE BUT IT IS NOT" );
	}
	
	/**
	 * Test Step 2: get Security Question
	 * 
	 * @param restActions  The RESTActions object used by the test
	 * @param headerTable  The HTTP Headers
	 * @return  The security question
	 */
	protected String securityQuestion(RESTActions restActions, Hashtable<String,String> headerTable) {
		String securityQuestionsURL = buildSecurityQuestionsURL();
		ClientResponse clientResponse = restActions.getClientResponse(securityQuestionsURL, headerTable, null,
				RESTConstants.APPLICATION_JSON);

		LOG.info("##### Verifying HTTP response code...");
		int status = clientResponse.getStatus();
		String msg = "WRONG HTTP RESPONSE CODE - EXPECTED 200, FOUND " + status;
		restActions.assertTrue(status == HttpURLConnection.HTTP_OK, msg);
		
		String response = clientResponse.getEntity(String.class);
		LOG.info("##### Got the response body");
		restActions.assertTrue(response != null, "RESPONSE IS NULL BUT SHOULD NOT BE NULL");
		LOG.info( response );
		
		Gson gson = new Gson();
		WSSecurityQuestionList actualQuestionList = gson.fromJson(response, WSSecurityQuestionList.class);
		List<WSSecurityQuestion> securityQuestionList = actualQuestionList.getSecurityQuestions();
		WSSecurityQuestion securityQuestion = securityQuestionList.get(0);
		
		return securityQuestion.getName();
	}	
	
	/**
	 * Test step 3: call the Create Customer API
	 * 
	 * @param restActions  The RESTActions object used by the test
	 * @param headerTable  The HTTP Headers
	 * @param username  The username
	 * @param password	The password
	 * @param securityQuestion  The security question
	 * @return  The customer Id
	 * @throws Throwable  Thrown if something goes wrong
	 */
	protected String createCustomer(RESTActions restActions, Hashtable<String,String> headerTable, String username, String password, String securityQuestion ) throws Throwable {
		String createCustomerURL = buildCreateCustomerURL();
		LOG.info("##### Built URL: " + createCustomerURL);
		String createCustomerRequestBody = buildCreateCustomerRequestBody( username, password, securityQuestion );
		
		ClientResponse clientResponse = restActions.postClientResponse(
				createCustomerURL, createCustomerRequestBody, headerTable, null,
				RESTConstants.APPLICATION_JSON);
		
		LOG.info("##### Verifying HTTP response code...");
		int status = clientResponse.getStatus();
		String msg = "WRONG HTTP RESPONSE CODE - EXPECTED 200, FOUND " + status;
		restActions.assertTrue(status == HttpURLConnection.HTTP_OK, msg);
		
		String response = clientResponse.getEntity(String.class);
		LOG.info("##### Got the response body");
		restActions.assertTrue(response != null, "RESPONSE IS NULL BUT SHOULD NOT BE NULL");
		LOG.info( response );
		
		Gson gson = new Gson();
		WSCreateCustomerResponse createCustomerResponse = gson.fromJson(response, WSCreateCustomerResponse.class);
		restActions.assertTrue(createCustomerResponse != null, "createCustomerResponse IS NULL BUT SHOULD NOT BE NULL");
		String customerId = createCustomerResponse.getCustomerId();
		restActions.assertTrue(customerId != null, "customerId IS NULL BUT SHOULD NOT BE NULL");
		
		String contactId = createCustomerResponse.getContactId();
		restActions.assertTrue(contactId != null, "contactId IS NULL BUT SHOULD NOT BE NULL");
		
		Integer oneAccountId = createCustomerResponse.getOneAccountId();
		restActions.assertTrue(oneAccountId != null, "oneAccountId IS NULL BUT SHOULD NOT BE NULL");
		
		return customerId;
	}
	
	
	/**
	 * Test step 4: call the completeregistration API
	 * 
	 * @param restActions  The RESTActions object used by the test
	 * @param headerTable  The HTTP Headers
	 * @param customerIdn  The customer Id
	 * @throws Throwable  Thrown if something goes wrong
	 */
	protected void completeRegistration(RESTActions restActions, Hashtable<String,String> headerTable, String customerId) throws Throwable {

		String completeRegistrationURL = buildCompleteRegistrationURL( customerId );
		
		ClientResponse clientResponse = restActions.postClientResponse(
				completeRegistrationURL, "", headerTable, null,
				RESTConstants.APPLICATION_JSON);

		LOG.info("##### Verifying HTTP response code...");
		int status = clientResponse.getStatus();
		String msg = "WRONG HTTP RESPONSE CODE - EXPECTED 200, FOUND " + status;
		restActions.assertTrue(status == HttpURLConnection.HTTP_OK, msg);
		
		String response = clientResponse.getEntity(String.class);
		LOG.info("##### Got the response body");
		restActions.assertTrue(response != null, "RESPONSE IS NULL BUT SHOULD NOT BE NULL");
		LOG.info( response );
		
		Gson gson = new Gson();
		WSCompleteRegistrationResponse completeRegistrationResponse =
				gson.fromJson(response, WSCompleteRegistrationResponse.class);
		
		restActions.assertTrue(completeRegistrationResponse != null, 
				"completeRegistrationResponse IS NULL BUT IT SHOULD NOT BE");
		if ( null == completeRegistrationResponse ) {
			return;
		}
		
		WSHdr hdr = completeRegistrationResponse.getHdr();
		restActions.assertTrue(completeRegistrationResponse != null, 
				"hdr IS NULL BUT IT SHOULD NOT BE");			
		if ( null == hdr ) {
			return;
		}
		
		restActions.assertTrue( hdr.getUid() != null, 
				"UID IS NULL BUT SHOULD NOT BE");
		
		restActions.assertTrue( "Successful".equals( hdr.getResult()), 
				String.format("BAD RESULT: EXPCTED 'Successful' FOUND '%s'", hdr.getResult() ) );
		
		Integer oneAccountId = completeRegistrationResponse.getOneAccountId();
		restActions.assertTrue( oneAccountId != null, 
				"oneAccountId IS NULL BUT SHOULD NOT BE");
	}	
	
	/**
	 * Helper method to build the Prevalidate URL.
	 * 
	 * @return the Prevalidate URL in String form
	 */
	protected String buildPrevalidateURL() {
		return "http://" + BackOfficeGlobals.ENV.NIS_HOST + ":" + BackOfficeGlobals.ENV.NIS_PORT
                + "/nis/nwapi/v2/customer/credentials/prevalidate";
	}
	
	/**
	 * Helper method to build the Prevalidate request body.
	 * 
	 * @param username  The username to use
	 * @param password  The password to use
	 * @return  the Prevalidate request body
	 */
	protected String buildPrevalidateRequestBody( String username, String password ) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter( sw );
		pw.println("{");
		pw.println("\"username\":\"" + username + "\",");
		pw.println("\"password\":\"" + password + "\"");
		pw.println("}");
		return sw.toString();
	}	
	
	/**
	 * Build the URL used to get the security questions list.
	 * 
	 * @return The URL in string format.
	 */
	protected String buildSecurityQuestionsURL() {
        return "http://" + BackOfficeGlobals.ENV.NIS_HOST + ":" + BackOfficeGlobals.ENV.NIS_PORT
                + "/nis/nwapi/v2/customerservice/securityquestions";
	}
	
	/**
	 * Helper method to build the Create Customer URL.
	 * 
	 * @return the Create Customer URL in String form
	 */
	protected String buildCreateCustomerURL() {
        return "http://" + BackOfficeGlobals.ENV.NIS_HOST + ":" + BackOfficeGlobals.ENV.NIS_PORT
                + "/nis/nwapi/v2/customer";
	}	
	
	/**
	 * Helper method to build the Create Customer request body.
	 * 
	 * @param username  The username value
	 * @param password  The password value
	 * @param securityQuestion  The security question
	 * @return the Create Customer request body in String form
	 */
	protected String buildCreateCustomerRequestBody(String username, String password, String securityQuestion) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter( sw );
		pw.println("{");
		pw.println("    \"customerType\":\"Traveler\",");
		pw.println("    \"contact\": {");
		pw.println("        \"contactType\":\"Primary\",");
		pw.println("        \"name\": {");
		pw.println("            \"firstName\":\"Monika\",");
		pw.println("            \"middleInitial\":null,");
		pw.println("            \"lastName\":\"Mest\",");
		pw.println("            \"nameSuffixId\":null,");
		pw.println("            \"title\":null");
		pw.println("        },");
		pw.println("        \"address\": {");
		pw.println("            \"address1\":\"123 broadway\",");
		pw.println("            \"address2\":null,");
		pw.println("            \"city\":\"san diego\",");
		pw.println("            \"postalCode\":\"92122\",");
		pw.println("            \"state\":\"CA\",");
		pw.println("            \"country\":\"US\"");
		pw.println("        },");
		pw.println("        \"addressId\":null,");
		pw.println("        \"phone\": [");
		pw.println("           {");
		pw.println("              \"number\":\"8596140263\",");
		pw.println("              \"type\":\"M\",");
		pw.println("              \"country\":\"US\",");
		pw.println("              \"displayNumber\":null");		
		pw.println("           }");
		pw.println("        ],");
		pw.println("        \"email\":\"" + username + "\",");
		pw.println("        \"dateOfBirth\":\"1980-01-02\",");
		pw.println("        \"personalIdentifierInfo\": {");
		pw.println("            \"personalIdentifier\":\"1\",");
		pw.println("            \"personalIdentifierType\":\"DriversLicense\"");
		pw.println("        },");
		pw.println("        \"username\":\"" + username + "\",");
		pw.println("        \"password\":\"" + password + "\",");
		pw.println("        \"pin\":\"1234\",");
		pw.println("        \"securityQAs\": [ {");
		pw.println("            \"securityQuestion\":\"" + securityQuestion + "\",");
		pw.println("            \"securityAnswer\":\"blue\"");
		pw.println("        } ]");
		pw.println("    }");
		pw.println("}");
		return sw.toString();
	}
	
	/**
	 * Helper method to build the /completeregistration URL.
	 * 
	 * @param customerId  The customer-id to add to the URL
	 * @return  The URL in String form
	 */
	protected String buildCompleteRegistrationURL( String customerId ) {
        return "http://" + BackOfficeGlobals.ENV.NIS_HOST + ":" + BackOfficeGlobals.ENV.NIS_PORT
                + "/nis/nwapi/v2/customer/" + customerId + "/completeregistration";
	}
	
	/**
	 * Helper method to build the URL used to call the 'Get Customer' API.
	 * 
	 * @param data  The Test Data from the JSON input file.
	 * @return The URL in string form.
	 */
	protected String buildGetCustomerURL( String customerId ) {
        String sURL = "http://" + BackOfficeGlobals.ENV.NIS_HOST + ":" + BackOfficeGlobals.ENV.NIS_PORT
                + "/nis/nwapi/v2/customer/" + customerId + "?returnCustomerInfo=true";
		return sURL;
	}
	
	/**
	 * Call the GET Customer API.
	 * 
	 * @param restActions  The RESTActions object used by the test.
	 * @param headerTable  HTTP Headers 
	 * @param expectedCustomerId  The expected Customer Id.
	 */
	protected void getCustomer( RESTActions restActions, Hashtable<String,String> headerTable, String expectedCustomerId ) {
		String sURL = buildGetCustomerURL(expectedCustomerId);	
		LOG.info("##### Built URL: " + sURL);
		
		LOG.info("##### Making HTTP request to get the EULA...");
		ClientResponse clientResponse = restActions.getClientResponse(sURL, headerTable, null,
				RESTConstants.APPLICATION_JSON);

		LOG.info("##### Verifying HTTP response code...");
		int actualHTTPResponseCode = clientResponse.getStatus();
		int expectedResponseCode = 200;
		String msg = String.format(BAD_RESPONSE_CODE_FMT, expectedResponseCode, actualHTTPResponseCode);
		restActions.assertTrue(expectedResponseCode == actualHTTPResponseCode, msg);
		
		String response = clientResponse.getEntity(String.class);
		LOG.info("##### Got the response body:");
		LOG.info( response );
		restActions.assertTrue(response != null, RESPONSE_IS_NULL);
		
		Gson gson = new Gson();
		WSCustomerInfoContainer customerInfoContainer =
				gson.fromJson(response, WSCustomerInfoContainer.class);
		
		restActions.assertTrue(customerInfoContainer != null, 
				"customerInfoContainer IS NULL BUT IT SHOULD NOT BE");
		
		if ( null == customerInfoContainer ) {
			return;
		}
		
		WSCustomerInfo customerInfo = customerInfoContainer.getCustomerInfo();
		
		restActions.assertTrue(customerInfo != null, 
				"customerInfo IS NULL BUT IT SHOULD NOT BE");			
		
		if ( null == customerInfo ) {
			return;
		}
		
		String actualCustomerId = customerInfo.getCustomerId();
		restActions.assertTrue(expectedCustomerId.equals(actualCustomerId),
				String.format(
						"BAD CUSTOMER ID - EXPECTED %s, FOUND %s",
						expectedCustomerId, actualCustomerId));
	}
}
