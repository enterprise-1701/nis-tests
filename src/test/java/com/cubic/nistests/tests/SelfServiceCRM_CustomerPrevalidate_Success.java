package com.cubic.nistests.tests;

//import java.util.ArrayList;
import java.util.Hashtable;
import java.util.LinkedHashMap;
//import java.util.List;

import org.apache.log4j.Logger;
//import org.json.simple.JSONArray;
//import org.json.simple.JSONObject;
import org.testng.ITestContext;
import org.testng.annotations.Test;

//import com.cubic.backoffice.apiobjects.WSCustomerContact;
//import com.cubic.backoffice.apiobjects.WSErrors;
import com.cubic.backoffice.BaseTest.BaseTest;
//import com.cubic.backoffice.utils.BackOfficeUtils;
//import com.cubic.backoffice.utils.GenericConstantsUtils;

//import com.cubic.nisjava.api.CRMCustomerCredentialPrevalidatePOST;
//import com.cubic.nisjava.apiobjects.CRMCustomerCredentialPrevalidatePostObj;
//import com.cubic.nisjava.apiobjects.CRMCustomerCredentialPrevalidateRespObj;
//import com.cubic.nisjava.apiutils.CRMCustomerCredentialPrevalidateUtils;
import com.cubic.nisjava.constants.AppConstants;
//import com.cubic.nisjava.constants.NISObj;
import com.cubic.nisjava.dataproviders.NISDataProviderSource;
//import com.cubic.nisjava.testcaseobjects.SelfServiceCRM_CustomerPrevalidate_Object;
//import com.cubic.nisjava.testcaseutils.SelfServiceCRM_CustomerLogin_Utils;
//import com.cubic.nisjava.utils.APICommonUtils;
//import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;

import net.minidev.json.JSONArray;
//import net.minidev.json.JSONObject;

//import android.util.Log;

public class SelfServiceCRM_CustomerPrevalidate_Success extends BaseTest {

//	public WSCustomerContact myContact = new WSCustomerContact();
//	public CRMCustomerCredentialPrevalidatePostObj myPostObj = new CRMCustomerCredentialPrevalidatePostObj();
//	public CRMCustomerCredentialPrevalidateRespObj myExpectedRspObj = new CRMCustomerCredentialPrevalidateRespObj();
//	public CRMCustomerCredentialPrevalidateRespObj myActualRspObj = new CRMCustomerCredentialPrevalidateRespObj();
	
	@Test(dataProvider = AppConstants.DATA_PROVIDER_RETURN_ARRAYS_NO_TRIM, dataProviderClass = NISDataProviderSource.class)
	public void SelfServiceCRM_CustomerPrevalidate_Success_Test(ITestContext context, Hashtable<String, String> data) {
		// setup test data / information
		super.setupTestJsonStr(this.getClass().getName(), data);
		LOG = Logger.getLogger(this.getClass().getName());
		
		
		// ACCESSING data when it is STRING/STRING
//		String myStr;
//		myStr = data.get("usernameErrors");
//		LOG.info("***************  PRINTING MY KEY:  " + myStr);
		
		
		// ACCESSING data when it is STRING/OBJECT
		
		
		
		//JSONParser parser = new JSONParser();
		//JSONObject jsonObj = (JSONObject) parser.parse(myStr);
		//JSONArray jsonArr = new JSONArray();
		//JSONArray jsonArr = (JSONArray) jsonObj.get();
		LOG.info("************ STARTING");
		//Object document = Configuration.defaultConfiguration().jsonProvider().parse(myStr);
		Object document = data.get("usernameErrors");
		LOG.info("************* CREATED JSON GENERIC OBJECT");
		
		//if (JsonPath.read(document, "$") instance of JSONArray) {
		JSONArray jsonArr = (JSONArray) JsonPath.read(document, "$");
		LOG.info("************** CREATED JSON ARRAY");
		//jsonArr.get(0);
		LinkedHashMap<String, String> myJsonObj;
		LOG.info("************** CREATED NEW JSON OBJECT");
		myJsonObj = (LinkedHashMap<String, String>) jsonArr.get(0);
		LOG.info("****************** STORED NEW JSON OBJECT");
		String myKey;
		myKey = myJsonObj.get("key");
		LOG.info("***************  PRINTING MY KEY:  " + myKey);


		
		// DELETE
//		JSONObject myJsonObj = new JSONObject();
//		LOG.info("************** CREATED NEW JSON OBJECT");
//		myJsonObj = (JSONObject) JsonPath.read(document, "$");
//		LOG.info("****************** STORED NEW JSON OBJECT");
//		String myKey;
//		myKey = (String) myJsonObj.get("key");
//		LOG.info("***************  PRINTING MY KEY:  " + myKey);
		

//		LOG.info("###### Initializing test - classname:  " + fullClassName + "\r\n" + 
//				"######Initializing test - test name:  " + testCaseName);
//		status++;
//		// generate data properties from BaseTest
//		super.generateData(false, false, true);
//		status++;
//		
//		try {
//			if (GenericConstantsUtils.isRunModeYes(data)) {
//				LOG.info("Executing test preconditions:  " + testCaseName);
//				setRestActions(context, testCaseName);
//				// TEST SETUP/PRECONDITIONS
//				// Create customer object
//				BackOfficeUtils.createWSCustomerContact(data, false, false, myContact);
//				// Create Post object
//				CRMCustomerCredentialPrevalidateUtils.wsCustomerContact2PostObj(myContact, myPostObj);
//				// Create Expected Rsp object
//				
//				// Send API request
//				CRMCustomerCredentialPrevalidateUtils.sendReq(myPostObj, this.restActions, myActualRspObj);
//
//				// 
//				myTestObj.setActualPrevalidateRespObj(CRMCustomerCredentialPrevalidatePOST.sendReq(myTestObj.getContact().getUsername(), myTestObj.getContact().getPassword(), restActions));
//				// if isUsernameValid = false and isPasswordvalid = true --> then if usernameErrors = errors.general.value.duplicate
//				// create expected Response object
//				boolean myIsUsernameValid = false;
//				boolean myIsPasswordValid = true;
//				String myUsernameErrorKey = "errors.general.value.duplicate";
//				String myUsernameErrorMsg = "Value already exists";
//				WSErrors myUsernameError = new WSErrors();
//				myUsernameError.setKey(myUsernameErrorKey);
//				myUsernameError.setMessage(myUsernameErrorMsg);
//				List<WSErrors> myUsernameErrors = new ArrayList<WSErrors>();
//				myUsernameErrors.add(myUsernameError);
//				CRMCustomerCredentialPrevalidateRespObj myExpectedRsp = new CRMCustomerCredentialPrevalidateRespObj();
//				myExpectedRsp.setUsernameValid(myIsUsernameValid);
//				myExpectedRsp.setPasswordValid(myIsPasswordValid);
//				myExpectedRsp.setUsernameErrors(myUsernameErrors);
//				myTestObj.setExpectedPrevalidateRespObj(myExpectedRsp);
//				status++;
//				// TEST EXECUTION
//				boolean userLoginExists;
//				userLoginExists = SelfServiceCRM_CustomerLogin_Utils.loginExists(myTestObj);
//				status++;
//				// END OF EXECUTION
//			}
//		} catch (Exception e) {
//			String myMsg = null;
//			switch (status) {
//				case 0:		myMsg = "Error initializing test";
//							break;
//				case 1:		myMsg = "Error setting up test";
//							break;
//				case 2:		myMsg = "Error executing preconditions - SKIP";
//							break;
//				case 3:		myMsg = "Error executing test - FAIL";
//							break;
//				case 4:		myMsg = "Error finalizing test";
//							break;
//				default: 	myMsg = "Error executing test; error position undetermined";
//							break;
//			}
//			LOG.error(myMsg);
//			throw new RuntimeException(e);
//		} finally {
//			LOG.info("Cleaning up test:  " + testCaseName);
//		}
	}
	
}
