package com.cubic.nistests.tests;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.util.Hashtable;
import java.util.List;
import java.util.UUID;

import org.apache.log4j.Logger;

import com.cubic.accelerators.RESTActions;
import com.cubic.accelerators.RESTConstants;
import com.cubic.accelerators.RESTEngine;
import com.cubic.backoffice.constants.BackOfficeGlobals;
import com.cubic.nisjava.apiobjects.WSAddressContainer;
import com.cubic.nisjava.apiobjects.WSAddress_;
import com.cubic.nisjava.apiobjects.WSCompleteRegistrationResponse;
import com.cubic.nisjava.apiobjects.WSContactContainer;
import com.cubic.nisjava.apiobjects.WSCreateCustomerResponse;
import com.cubic.nisjava.apiobjects.WSCustomerInfo;
import com.cubic.nisjava.apiobjects.WSCustomerInfoContainer;
import com.cubic.nisjava.apiobjects.WSCustomerPostAddressResponse;
import com.cubic.nisjava.apiobjects.WSDependencyLists;
import com.cubic.nisjava.apiobjects.WSHdr;
import com.cubic.nisjava.apiobjects.WSPrevalidateResponse;
import com.cubic.nisjava.apiobjects.WSSecurityQuestion;
import com.cubic.nisjava.apiobjects.WSSecurityQuestionList;
import com.cubic.nisjava.utils.NISUtils;
import com.google.gson.Gson;
import com.sun.jersey.api.client.ClientResponse;

public class NWAPIV2_CustomerBase extends RESTEngine {

	protected static final String EXPECTED_HTTP_RESPONSE_CODE = "EXPECTED_HTTP_RESPONSE_CODE";	
	protected static final String BAD_RESPONSE_CODE_FMT = "WRONG HTTP RESPONSE CODE - EXPECTED %s, FOUND %s";
	protected static final String RESPONSE_IS_NULL = "RESPONSE IS NULL BUT SHOULD NOT BE NULL";	
	protected static final String EXPECTED_RESULT = "EXPECTED_RESULT";	
	protected static final String RESULT_FMT = "BAD RESULT: EXPECTED '%s', FOUND '%s'";
	protected static final String BAD_UI_NULL_FMT = "BAD UID: UID IS NULL BUT IT SHOULD NOT BE";
	protected static final String BAD_UI_LENGTH_0_MSG = "BAD UID: UID HAS LENGTH 0";
	protected static final String EXPECTED_FIELDNAME = "EXPECTED_FIELDNAME";
	protected static final String BAD_ERROR_KEY_FMT = "BAD ERROR KEY: EXPECTED '%s', FOUND '%s'";
	protected static final String EXPECTED_ERROR_KEY = "EXPECTED_ERROR_KEY";
	protected static final String BAD_FIELD_NAME_FMT = "BAD FIELD NAME: EXPECTED '%s', FOUND '%s'";
	protected static final String CUSTOMER_ID = "CUSTOMER_ID";
	protected static final String CONTACT_ID = "CONTACT_ID";
	
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
		restActions.assertTrue(response != null, RESPONSE_IS_NULL);	
		
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
		restActions.assertTrue(response != null, RESPONSE_IS_NULL);
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
	protected WSCreateCustomerResponse createCustomer(RESTActions restActions, Hashtable<String,String> headerTable, String username, String password, String securityQuestion ) throws Throwable {
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
		restActions.assertTrue(response != null, RESPONSE_IS_NULL);
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
		
		return createCustomerResponse;
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
		restActions.assertTrue(response != null, RESPONSE_IS_NULL);
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
		String email = username;
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
		pw.println("        \"email\":\"" + email + "\",");
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
	protected WSCustomerInfoContainer getCustomer( RESTActions restActions, Hashtable<String,String> headerTable, String expectedCustomerId ) {
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
			return customerInfoContainer;
		}
		
		WSCustomerInfo customerInfo = customerInfoContainer.getCustomerInfo();
		
		restActions.assertTrue(customerInfo != null, 
				"customerInfo IS NULL BUT IT SHOULD NOT BE");			
		
		if ( null == customerInfo ) {
			return customerInfoContainer;
		}
		
		String actualCustomerId = customerInfo.getCustomerId();
		restActions.assertTrue(expectedCustomerId.equals(actualCustomerId),
				String.format(
						"BAD CUSTOMER ID - EXPECTED %s, FOUND %s",
						expectedCustomerId, actualCustomerId));
		
		return customerInfoContainer;
	}
	
	
	/**
	 * Build the URL used to get the security question.
	 * 
	 * @return The URL in string format.
	 */
	protected String buildSecurityQuestionURL() {
        return "http://" + BackOfficeGlobals.ENV.NIS_HOST + ":" + BackOfficeGlobals.ENV.NIS_PORT
                + "/nis/nwapi/v2/customer/securityquestion";
	}
	
	/**
	 * Helper method to build the Prevalidate request body.
	 * 
	 * @param username  The username to use
	 * @param password  The password to use
	 * @return  the Prevalidate request body
	 */
	protected String buildSecurityQuestionRequestBody( String username ) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter( sw );
		pw.println("{");
		pw.println("\"username\":\"" + username + "\"");
		pw.println("}");
		return sw.toString();
	}
	
	/**
	 * Helper method to lookup the Security Questions that belong to a username.
	 * 
	 * @param restActions  The RESTActions object used by the @Test method.
	 * @param data  The Test Data from the JSON input file
	 * @param headerTable  The HTTP Headers used to call the API.
	 * @param username  The username used to get the Security Questions.
	 * @return The response in String form.
	 * @throws Throwable  Thrown if something goes wrong.
	 */
	protected String securityQuestion(RESTActions restActions, Hashtable<String,String> data, Hashtable<String,String> headerTable, String username ) throws Throwable {

		String securityQuestionURL = buildSecurityQuestionURL();
		LOG.info("##### Built URL: " + securityQuestionURL);
		
		String requestBody = buildSecurityQuestionRequestBody( username );
		LOG.info("##### Built request body: " + requestBody);
		
		LOG.info("##### Making HTTP request to obtain the Security Question...");
		ClientResponse clientResponse = restActions.postClientResponse(
				securityQuestionURL, requestBody, headerTable, null,
				RESTConstants.APPLICATION_JSON);

		LOG.info("##### Verifying HTTP response code...");
		String sExpectedResponseCode = data.get("EXPECTED_RESPONSE_CODE");
		int iExpectedResponseCode = Integer.parseInt(sExpectedResponseCode);
		int iActualResponseCode = clientResponse.getStatus();
		String fmt = "WRONG HTTP RESPONSE CODE - EXPECTED %s, FOUND %s";
		String msg = String.format(fmt, iExpectedResponseCode, iActualResponseCode);
		restActions.assertTrue(iExpectedResponseCode == iActualResponseCode, msg);
		
		String response = clientResponse.getEntity(String.class);
		LOG.info("##### Got the response body");
		restActions.assertTrue(response != null, RESPONSE_IS_NULL);
		LOG.info( response );
		
		return response;
	}
	
	/**
	 * Build the URL used to call the username forgot API.
	 * 
	 * @return The URL in string format.
	 */
	protected String buildUsernameForgotURL() {
        return "http://" + BackOfficeGlobals.ENV.NIS_HOST + ":" + BackOfficeGlobals.ENV.NIS_PORT
                + "/nis/nwapi/v2/customer/username/forgot";
	}
	
	/**
	 * Helper method to build the 'Username Forgot' request body.
	 * 
	 * @param email  The email address to use
	 * @return  the 'Username Forgot' request body
	 */
	protected String buildUsernameForgotRequestBody( String email ) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter( sw );
		pw.println("{");
		pw.println("    \"email\":\"" + email + "\"");
		pw.println("}");
		return sw.toString();
	}	
	
	/**
	 * This is a Helper method to call the 'Username Forgot' API.
	 * 
	 * @param restActions  The RESTActions object used by the @Test method.
	 * @param data  The Test Data from the JSON input file.
	 * @param headerTable  The HTTP Headers using by the @Test method.
	 * @param email  The email address of the user we're searching for.
	 * @throws Throwable  Thrown if something goes wrong.
	 */
	protected ClientResponse usernameForgot(RESTActions restActions, Hashtable<String,String> data, Hashtable<String,String> headerTable, String email ) throws Throwable {

		String usernameForgotURL = buildUsernameForgotURL();
		LOG.info("##### Built URL: " + usernameForgotURL);
		
		String requestBody = buildUsernameForgotRequestBody( email );
		LOG.info("##### Built request body: " + requestBody);
		
		LOG.info("##### Making HTTP request to obtain the Username Forgot...");
		ClientResponse clientResponse = restActions.postClientResponse(
				usernameForgotURL, requestBody, headerTable, null,
				RESTConstants.APPLICATION_JSON);

		LOG.info("##### Verifying HTTP response code...");
		String sExpectedResponseCode = data.get("EXPECTED_RESPONSE_CODE");
		int iExpectedResponseCode = Integer.parseInt(sExpectedResponseCode);
		int iActualResponseCode = clientResponse.getStatus();
		String fmt = "WRONG HTTP RESPONSE CODE - EXPECTED %s, FOUND %s";
		String msg = String.format(fmt, iExpectedResponseCode, iActualResponseCode);
		restActions.assertTrue(iExpectedResponseCode == iActualResponseCode, msg);
		
		return clientResponse;
	}
	
	
	/**
	 * Helper method to build the URL used to call the 'Get Address' API.
	 * 
	 * @see GET http://10.252.1.21:8201/nis/nwapi/v2/customer/<customer-id>/address/<address-id>
	 * 
	 * @param data  The Test Data from the JSON input file.
	 * @return The URL in string form.
	 */
	protected String buildGetAddressURL( String customerId, String addressId ) {
        String sURL = "http://" + BackOfficeGlobals.ENV.NIS_HOST + ":" + BackOfficeGlobals.ENV.NIS_PORT
                + "/nis/nwapi/v2/customer/" + customerId + "/address/" + addressId;
		return sURL;
	}
	
	/**
	 * Call the GET Customer Address API.
	 * 
	 * @see GET http://10.252.1.21:8201/nis/nwapi/v2/customer/<customer-id>/address/<address-id>
	 * 
	 * @param restActions  The RESTActions object used by the test.
	 * @param headerTable  HTTP Headers 
	 * @param expectedCustomerId  The expected Customer Id.
	 */
	protected void getCustomerAddress( RESTActions restActions, Hashtable<String,String> data, Hashtable<String,String> headerTable, String expectedCustomerId, WSAddress_ expectedAddress ) {
		String sURL = buildGetAddressURL(expectedCustomerId, expectedAddress.getAddressId());	
		LOG.info("##### Built URL: " + sURL);
		
		LOG.info("##### Making HTTP request to get the Customer Address...");
		ClientResponse clientResponse = restActions.getClientResponse(sURL, headerTable, null,
				RESTConstants.APPLICATION_JSON);

		LOG.info("##### Verifying HTTP response code...");
		String sExpectedStatus = data.get(EXPECTED_HTTP_RESPONSE_CODE);
		int expectedStatus = Integer.parseInt(sExpectedStatus);
		int actualStatus = clientResponse.getStatus();
		String msg =  String.format(BAD_RESPONSE_CODE_FMT, expectedStatus, actualStatus);
		restActions.assertTrue(expectedStatus == actualStatus, msg);
		
		String response = clientResponse.getEntity(String.class);
		LOG.info("##### Got the response body:");
		LOG.info( response );
		restActions.assertTrue(response != null, RESPONSE_IS_NULL);
		
		LOG.info("##### Parsing the response body...");
		Gson gson = new Gson();
		WSAddressContainer addressContainer =
				gson.fromJson(response, WSAddressContainer.class);
		
		restActions.assertTrue(addressContainer != null, 
				"addressContainer IS NULL BUT IT SHOULD NOT BE NULL");
		
		if ( null == addressContainer ) {
			return;
		}
		
		restActions.assertTrue(addressContainer.getAddress() != null, 
				"addressContainer.getAddress() IS NULL BUT IT SHOULD NOT BE NULL");
		
		if ( null == addressContainer.getAddress() ) {
			return;
		}
		
		LOG.info("##### Comparing the expected Address with the actual Address...");
		String actualAddressId = addressContainer.getAddress().getAddressId();
		String expectedAddressId = expectedAddress.getAddressId();
		restActions.assertTrue(expectedAddressId.equals(actualAddressId), 
				String.format("BAD ADDRESS ID - EXPECTED %s FOUND %s", 
						expectedAddressId, actualAddressId));
		
		String actualAddress1 = addressContainer.getAddress().getAddress1();
		String expectedAddress1 = expectedAddress.getAddress1();
		restActions.assertTrue(expectedAddress1.equals(actualAddress1), 
				String.format("BAD ADDRESS 1 - EXPECTED %s FOUND %s", 
						expectedAddress1, actualAddress1));
		
		String actualCity = addressContainer.getAddress().getCity();
		String expectedCity = expectedAddress.getCity();
		restActions.assertTrue(expectedCity.equals(actualCity), 
				String.format("BAD CITY - EXPECTED %s FOUND %s", 
						expectedCity, actualCity));
		
		String actualState = addressContainer.getAddress().getState();
		String expectedState = expectedAddress.getState();
		restActions.assertTrue(expectedState.equals(actualState), 
				String.format("BAD STATE - EXPECTED %s FOUND %s", 
						expectedState, actualState));
		
		String actualPostalCode = addressContainer.getAddress().getPostalCode();
		String expectedPostalCode = expectedAddress.getPostalCode();
		restActions.assertTrue(expectedPostalCode.equals(actualPostalCode), 
				String.format("BAD PostalCode - EXPECTED %s FOUND %s", 
						expectedPostalCode, actualPostalCode));
		
		String actualCountry = addressContainer.getAddress().getCountry();
		String expectedCountry = expectedAddress.getCountry();
		restActions.assertTrue(expectedCountry.equals(actualCountry), 
				String.format("BAD Country - EXPECTED %s FOUND %s", 
						expectedCountry, actualCountry));
	}
	
	/**
	 * Helper method to build the URL used to call the 'Post Address' API.
	 * 
	 * @see POST http://10.252.1.21:8201/nis/nwapi/v2/customer/<customer-id>/address
	 * 
	 * @param data  The Test Data from the JSON input file.
	 * @return The URL in string form.
	 */
	protected String buildPostAddressURL( String customerId ) {
        String sURL = "http://" + BackOfficeGlobals.ENV.NIS_HOST + ":" + BackOfficeGlobals.ENV.NIS_PORT
                + "/nis/nwapi/v2/customer/" + customerId + "/address";
		return sURL;
	}
	
	/**
	 * Helper method to build the Request Body for the 'Post Address' API.
	 * 
	 * @return  The 'Post Address' request Body in String form.
	 */
	protected String buildPostAddressRequestBody() {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter( sw );
		pw.println("{");
		pw.println("    \"address\": {");
		pw.println("        \"address1\":\"11889 Ramsdell Court\",");
		pw.println("        \"address2\":null,");
		pw.println("        \"city\":\"San Diego\",");
		pw.println("        \"postalCode\":\"92131\",");
		pw.println("        \"state\":\"CA\",");
		pw.println("        \"country\":\"US\"");			
		pw.println("    }");
		pw.println("}");
		return sw.toString();
	}	

	/**
	 * Helper method to call the /customer/<customer-id>/address API.
	 * 
	 * @see POST http://10.252.1.21:8201/nis/nwapi/v2/customer/<customer-id>/address 
	 * 
	 * @param restActions  The RESTActions object created by the @Test method.
	 * @param data  The Test Data from the JSON input file.
	 * @param headerTable  The HTTP Headers created by the @Test method.
	 * @param expectedCustomerId  The customer Id used to build the URL.
	 * @return a WSCustomerPostAddressResponse object.
	 * @throws Throwable  Thrown if something goes wrong.
	 */
	protected WSCustomerPostAddressResponse addCustomerAddress(
			RESTActions restActions, Hashtable<String,String> data, 
			Hashtable<String,String> headerTable, String expectedCustomerId ) throws Throwable {
		String sURL = buildPostAddressURL(expectedCustomerId);	
		LOG.info("##### Built URL: " + sURL);
		
		String requestBody = buildPostAddressRequestBody();
		
		LOG.info("##### Making HTTP request to Add the address...");
		ClientResponse clientResponse = restActions.postClientResponse(sURL, requestBody, headerTable, null,
				RESTConstants.APPLICATION_JSON);

		LOG.info("##### Verifying HTTP response code...");
		String sExpectedStatus = data.get(EXPECTED_HTTP_RESPONSE_CODE);
		int expectedStatus = Integer.parseInt(sExpectedStatus);
		int actualStatus = clientResponse.getStatus();
		String msg =  String.format(BAD_RESPONSE_CODE_FMT, expectedStatus, actualStatus);
		restActions.assertTrue(expectedStatus == actualStatus, msg);
		
		String response = clientResponse.getEntity(String.class);
		LOG.info("##### Got the response body:");
		LOG.info( response );
		restActions.assertTrue(response != null, RESPONSE_IS_NULL);
		
		Gson gson = new Gson();
		WSCustomerPostAddressResponse postAddressResponse =
				gson.fromJson(response, WSCustomerPostAddressResponse.class);
		
		return postAddressResponse;
	}
	
	
	/**
	 * Build the URL of the customer/<customer-id>/address/<address-id> API.
	 * 
	 * @see PUT http://10.252.1.21:8201/nis/nwapi/v2/customer/4E5885AA-9179-E811-80CC-000D3A36F32A/address/7B5885AA-9179-E811-80CC-000D3A36F32A
	 * 
	 * @param customerId  The customer Id to use to build the URL.
	 * @param addressId  The address Id to use the build the URL.
	 * @return The URL in String form.
	 */
	protected String buildCustomerAddressURL(String customerId, String addressId) {
        String sURL = "http://" + BackOfficeGlobals.ENV.NIS_HOST + ":" + BackOfficeGlobals.ENV.NIS_PORT
                + "/nis/nwapi/v2/customer/" + customerId + "/address/" + addressId;
		return sURL;
	}
	
	
	/**
	 * This method will build the URL of the PUT customer/<customerId>/address/<addressId> API,
	 * then build a Request Body containing a Address elements, then PUT the request body to
	 * the URL.  Finally, it will verify that the HTTP Response Code is 204 No Content.
	 * 
	 * @see PUT http://10.252.1.21:8201/nis/nwapi/v2/customer/4E5885AA-9179-E811-80CC-000D3A36F32A/address/7B5885AA-9179-E811-80CC-000D3A36F32A
	 *
	 * @param restActions  The RESTActions object created in the @Test method.
	 * @param data  the Test Data read from the JSON input file.
	 * @param headerTable  Set of HTTP Headers to use.
	 * @param customerId  The customer Id to build the URL. 
	 * @param addressId  The address Id to build the URL.
	 * @throws Throwable  Thrown if something goes wrong.
	 */
	protected void updateCustomerAddress( RESTActions restActions, Hashtable<String,String> data, 
			Hashtable<String,String> headerTable, String customerId, String addressId ) throws Throwable {
		int iExpectedResponseCode = 204;
		
		String updateAddressURL = buildCustomerAddressURL( customerId, addressId );
		LOG.info("##### Built URL: " + updateAddressURL);
		
		String updateAddressRequestBody = buildPostAddressRequestBody();
		LOG.info("##### Built request body: " + updateAddressRequestBody);
		
		LOG.info("##### Making HTTP request to the PUT Address API...");
		ClientResponse clientResponse = restActions.putClientResponse(
				updateAddressURL, updateAddressRequestBody, headerTable, null,
				RESTConstants.APPLICATION_JSON);

		LOG.info("##### Verifying HTTP response code...");
		int iActualResponseCode = clientResponse.getStatus();
		String msg = String.format(BAD_RESPONSE_CODE_FMT, iExpectedResponseCode, iActualResponseCode);
		restActions.assertTrue(iExpectedResponseCode == iActualResponseCode, msg);
	}
	
	
	/**
	 * Helper method to build the customer/<customer-id>/address/<address-id>/dependencies URL.
	 * 
	 * @param customerId  The customer Id used to build the URL.
	 * @param addressId  The address Id used to build the URL.
	 * @return  The URL, in String form.
	 */
	protected String buildDependenciesURL(String customerId, String addressId) {
        String sURL = "http://" + BackOfficeGlobals.ENV.NIS_HOST + ":" + BackOfficeGlobals.ENV.NIS_PORT
                + "/nis/nwapi/v2/customer/" + customerId + "/address/" + addressId + "/dependencies";
		return sURL;
	}
	
	/**
	 * Helper method to call the customer/<customer-id>/address/<address-id>/dependencies URL.
	 * 
	 * @param restActions  The RESTActions object.
	 * @param data  Test Data from the JSON input file.
	 * @param headerTable The HTTP Headers.
	 * @param customerId  The customer Id used to build the URL.
	 * @param addressId  The address Id used to build the URL.
	 * @return A WSDependencyLists object, for testing.
	 */
	protected WSDependencyLists getContactDependencies( 
			RESTActions restActions, Hashtable<String,String> data, 
			Hashtable<String,String> headerTable, String customerId, String addressId ) {
		
		String contactDependenciesURL = buildDependenciesURL( customerId, addressId );
		LOG.info("##### Built URL: " + contactDependenciesURL);
		
		LOG.info("##### Making HTTP request to get the Customer Address...");
		ClientResponse clientResponse = restActions.getClientResponse(
				contactDependenciesURL, headerTable, null,
				RESTConstants.APPLICATION_JSON);

		LOG.info("##### Verifying HTTP response code...");
		String sExpectedStatus = data.get(EXPECTED_HTTP_RESPONSE_CODE);
		int expectedStatus = Integer.parseInt(sExpectedStatus);
		int actualStatus = clientResponse.getStatus();
		String msg =  String.format(BAD_RESPONSE_CODE_FMT, expectedStatus, actualStatus);
		restActions.assertTrue(expectedStatus == actualStatus, msg);
		
		String response = clientResponse.getEntity(String.class);
		LOG.info("##### Got the response body:");
		LOG.info( response );
		restActions.assertTrue(response != null, RESPONSE_IS_NULL);
		
		LOG.info("##### Parsing the response body...");
		Gson gson = new Gson();
		WSDependencyLists dependencyLists =
				gson.fromJson(response, WSDependencyLists.class);
		
		return dependencyLists;
	}
	
	
	/**
	 * Helper method to build the URL used to call the 'Delete Address' API.
	 * 
	 * @see DELETE http://10.252.1.21:8201/nis/nwapi/v2/customer/<customer-id>/address/<address-id>
	 * 
	 * @param data  The Test Data from the JSON input file.
	 * @return The URL in string form.
	 */
	protected String buildDeleteAddressURL( String customerId, String addressId ) {
        String sURL = "http://" + BackOfficeGlobals.ENV.NIS_HOST + ":" + BackOfficeGlobals.ENV.NIS_PORT
                + "/nis/nwapi/v2/customer/" + customerId + "/address/" + addressId;
		return sURL;
	}
	
	
	/**
	 * 
	 * Helper method to call the DELETE http://10.252.1.21:8201/nis/nwapi/v2/customer/<customer-id>/address/<address-id> URL.
	 * 
	 * Please note that this method fails due to CCBO-13129.
	 * 
	 * @param restActions  The RESTActions object created by the @Test method.
	 * @param data  The Test Data from the JSON input file.
	 * @param headerTable  A set of HTTP Headers suitable for calling NIS.
	 * @param expectedCustomerId  The expected value of the customer Id used for building the API's URL.
	 * @param expectedAddressId  The expected value of the address Id used for building the API's URL.
	 * @throws Throwable  Thrown if something goes wrong.
	 */
	protected void deleteCustomerAddress(
			RESTActions restActions, Hashtable<String,String> data, Hashtable<String,String>headerTable,
			String expectedCustomerId, String expectedAddressId ) throws Throwable {
		String sURL = buildDeleteAddressURL(expectedCustomerId, expectedAddressId);	
		LOG.info("##### Built URL: " + sURL);
		
		LOG.info("##### Making HTTP request to get the EULA...");
		ClientResponse clientResponse = restActions.deleteClientResponse(sURL, headerTable, null,
				RESTConstants.APPLICATION_JSON);

		LOG.info("##### Verifying HTTP response code...");
		String sExpectedStatus = data.get(EXPECTED_HTTP_RESPONSE_CODE);
		int expectedStatus = Integer.parseInt(sExpectedStatus);
		int actualStatus = clientResponse.getStatus();
		String msg =  String.format(BAD_RESPONSE_CODE_FMT, expectedStatus, actualStatus);
		restActions.assertTrue(expectedStatus == actualStatus, msg);
		
		String response = clientResponse.getEntity(String.class);
		LOG.info("##### Got the response body:");
		LOG.info( response );
		restActions.assertTrue(response != null, RESPONSE_IS_NULL);
	}
	
	/**
	 * Helper method to call the API
	 * 
	 * GET http://10.252.1.21:8201/nis/nwapi/v2/customer/2B911170-4690-E811-80CC-000D3A36F32A/contact/33911170-4690-E811-80CC-000D3A36F32A
	 * 
	 * @param restActions  The RESTActions object created by the calling @Test method
	 * @param headerTable  The set of Headers created by the calling @Test method
	 * @param customerId The CustomerId created by the calling @Test method
	 * @param contactId  The ContactId created by the calling @Test method
	 * @return A WSContactContainer object, initialized by parsing the HTTP Response.
	 * @throws Throwable  Thrown if something goes wrong
	 */
	protected String getContact(RESTActions restActions, Hashtable<String,String> headerTable,
			Hashtable<String,String> data ) throws Throwable {
		
		String customerId = data.get(CUSTOMER_ID);
		String contactId = data.get(CONTACT_ID);
		
		String getContactURL = buildGetContactURL( customerId, contactId );
		
		ClientResponse clientResponse = restActions.getClientResponse(
				getContactURL, headerTable, null, RESTConstants.APPLICATION_JSON);

		LOG.info("##### Verifying HTTP response code...");
		String sExpectedStatus = data.get(EXPECTED_HTTP_RESPONSE_CODE);
		int expectedStatus = Integer.parseInt(sExpectedStatus);
		int actualStatus = clientResponse.getStatus();
		String msg =  String.format(BAD_RESPONSE_CODE_FMT, expectedStatus, actualStatus);
		restActions.assertTrue(expectedStatus == actualStatus, msg);
		
		String response = clientResponse.getEntity(String.class);
		LOG.info("##### Got the response body");
		restActions.assertTrue(response != null, RESPONSE_IS_NULL);
		LOG.info( response );
		
		return response;
	}
	
	/**
	 * Helper method to build the Get Contact URL
	 * 
	 * @param customerId The customerId to use to build the URL
	 * @param contactId  The contactId to use to build the URL
	 * @return  The Get Contact URL in String form
	 */
	protected String buildGetContactURL( String customerId, String contactId ) {
        String sURL = NISUtils.getURL()
                + "/nis/nwapi/v2/customer/" + customerId
                + "/contact/" + contactId;
		return sURL;
	}

	/**
	 * Helper method call by the @Test method to build the URL of the
	 * PATCH customer/<customerid>/contact/<contact-id> API, and build
	 * the Request Body to send, then call the API.
	 * 
	 * @param restActions  The RESTActions object created by the @Test method.
	 * @param headerTable  The HTTP Headers used to send the message.
	 * @param data  The test Data from the JSON input file.
	 * @throws Throwable  Thrown if something went wrong.
	 */
	protected void patchContact(RESTActions restActions,Hashtable<String,String> headerTable,Hashtable<String, String> data) throws Throwable {
		String customerId = data.get(CUSTOMER_ID);
		String contactId = data.get(CONTACT_ID);
		
		String patchContactURL = buildGetContactURL( customerId, contactId );
        LOG.info("##### Built URL: " + patchContactURL);
        
		String patchContactRequestBody = buildPatchContactRequestBody();            
        
		LOG.info("##### Making HTTP request to prevalidate the credentials...");
		ClientResponse clientResponse = restActions.patchClientResponse(
				patchContactURL, patchContactRequestBody, headerTable, null,
				RESTConstants.APPLICATION_JSON);

		LOG.info("##### Verifying HTTP response code...");
		String sExpectedStatus = data.get(EXPECTED_HTTP_RESPONSE_CODE);
		int expectedStatus = Integer.parseInt(sExpectedStatus);
		int actualStatus = clientResponse.getStatus();
		String msg =  String.format(BAD_RESPONSE_CODE_FMT, expectedStatus, actualStatus);
		restActions.assertTrue(expectedStatus == actualStatus, msg);
	}
	
	/**
	 * Helper method to build the Request Body for the
	 * PATCH customer/<customerid>/contact/<contact-id>
	 * API.
	 * 
	 * @return  The Request Body in String form.
	 */
	protected String buildPatchContactRequestBody() {
		String uniqueID = UUID.randomUUID().toString();
		String username = uniqueID + "@cubic.com";	
		
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter( sw );
		pw.println("{");
		pw.println("    \"contactType\": \"Primary\",");
		pw.println("    \"username\":\"" + username + "\"");
		pw.println("}");
		return sw.toString();
	}	
}
