package com.cubic.nistests.tests;

import java.net.HttpURLConnection;
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
import com.cubic.nisjava.apiobjects.WSFinanciallyResponsibleOperator;
import com.cubic.nisjava.apiobjects.WSReasonCode;
import com.cubic.nisjava.apiobjects.WSReasonCodeType;
import com.cubic.nisjava.apiobjects.WSReasonCodeTypeList;
import com.cubic.nisjava.constants.AppConstants;
import com.cubic.nisjava.dataproviders.NISDataProviderSource;
import com.google.gson.Gson;
import com.sun.jersey.api.client.ClientResponse;

/**
 * This test class will verify that NIS will return a list of Reason Codes.
 * 
 * @author 203402
 *
 */
public class ReasonCodesTest extends RESTEngine {

	private final Logger LOG = Logger.getLogger(this.getClass().getName());
	
	private static final String EXPECTED_REASON_CODE_TYPE_NAME = "EXPECTED_REASON_CODE_TYPE_NAME";
	private static final String EXPECTED_REASON_CODE_TYPE_DESC = "EXPECTED_REASON_CODE_TYPE_DESC";
	private static final String EXPECTED_REASON_CODE_DESC = "EXPECTED_REASON_CODE_DESC";
	private static final String EXPECTED_REASON_CODE_ACTIVE = "EXPECTED_REASON_CODE_ACTIVE";
	private static final String EXPECTED_REASON_CODE_MIN_AMOUNT = "EXPECTED_REASON_CODE_MIN_AMOUNT";
	private static final String EXPECTED_REASON_CODE_MAX_AMOUNT = "EXPECTED_REASON_CODE_MAX_AMOUNT";
	private static final String EXPECTED_NOTES_MANDATORY_FLAG = "EXPECTED_NOTES_MANDATORY_FLAG";
	private static final String EXPECTED_FINANCIALLY_RESPONSIBLE_OPERATOR_ID = "EXPECTED_FINANCIALLY_RESPONSIBLE_OPERATOR_ID";
	private static final String EXPECTED_FINANCIALLY_RESPONSIBLE_OPERATOR_DESC = "EXPECTED_FINANCIALLY_RESPONSIBLE_OPERATOR_DESC";
	
	private static final String BAD_REASON_CODE_TYPE_NAME = "BAD ReasonCodeTypeName - EXPECTED %s ACTUAL %s";
	private static final String BAD_REASON_CODE_TYPE_DESC = "BAD ReasonCodeTypeDesc - EXPECTED %s ACTUAL %s";
	private static final String BAD_REASON_CODE_ID = "BAD ReasonCodeId - EXPECTED %s ACTUAL %s";
	private static final String BAD_REASON_CODE_DESC = "BAD ReasonCodeDesc - EXPECTED %s ACTUAL %s";
	private static final String BAD_REASON_CODE_ACTIVE = "BAD ReasonCodeActive - EXPECTED %s ACTUAL %s";
	private static final String BAD_REASON_CODE_MIN_AMOUNT = "BAD ReasonCodeActiveMinAmount - EXPECTED %s ACTUAL %s";
	private static final String BAD_REASON_CODE_MAX_AMOUNT = "BAD ReasonCodeActiveMaxAmount - EXPECTED %s ACTUAL %s";
	private static final String BAD_NOTES_MANDATORY_FLAG = "BAD NotesMandatoryFlag - EXPECTED %s ACTUAL %s";
	private static final String BAD_FINANCIALLY_RESPONSIBLE_OPERATOR_ID = "BAD FinanciallyResponsibleOperatorId - EXPECTED %s ACTUAL %s";
	private static final String BAD_FINANCIALLY_RESPONSIBLE_OPERATOR_DESC = "BAD FinanciallyResponsibleOperatorDesc - EXPECTED %s ACTUAL %s";
	
	private static final String REASON_CODE_TYPE_LIST_IS_NULL = "WSReasonCodeTypeList REFERENCE IS NULL BUT IT SHOULD NOT BE";
	private static final String REASON_CODE_TYPE_LIST_HAS_SIZE_ZERO = "ReasonCodeTypes LIST HAS SIZE == 0 BUT IT SHOULD BE > 0";
	private static final String REASON_CODE_TYPE_REFERENCE_IS_NULL = "WSReasonCodeType REFERENCE IS NULL BUT IT SHOULD NOT BE";
	private static final String REASON_CODE_LIST_HAS_SIZE_ZERO = "ReasonCode LIST HAS SIZE == 0 BUT IT SHOULD BE > 0";
	private static final String FINANCIALLY_RESPONSIBLE_OPERAOR_LIST_IS_NULL = "List<WSFinanciallyResponsibleOperator> REFERENCE IS NULL BUT IT SHOULD NOT BE";	
	
	/**
	 * Get a list of Reason Codes.
	 * 
	 * testRailId = 
	 * 
	 * @param context  The TestNG content object.
	 * @param data  The Test Data.
	 * @throws Throwable  Thrown if something goes wrong.
	 */
	@Test(dataProvider = AppConstants.DATA_PROVIDER, dataProviderClass = NISDataProviderSource.class)
	public void getReasonCodes(ITestContext context, Hashtable<String, String> data) throws Throwable {
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
			String msg = "WRONG HTTP RESPONSE CODE - EXPECTED 200, FOUND " + status;
			restActions.assertTrue(status == HttpURLConnection.HTTP_OK, msg);
			
			String response = clientResponse.getEntity(String.class);
			LOG.info("##### Got the response body");
			LOG.info( response );
			restActions.assertTrue(response != null, "RESPONSE IS NULL BUT SHOULD NOT BE NULL");	
			
			verifyResponse( restActions, data, response );
			
		} finally {
			teardownAutomationTest(context, testCaseName);
			LOG.info("##### Done!");
		}
	}
	
	/**
	 * Build the URL used to get the Reason Code list.
	 * 
	 * @return The URL in string format.
	 */
	private String buildURL() {
        return "http://" + BackOfficeGlobals.ENV.NIS_HOST + ":" + BackOfficeGlobals.ENV.NIS_PORT
                + "/nis/nwapi/v2/customerservice/reasoncodes";
	}
	
	/**
	 * Helper method to verify the response from the API.
	 * 
	 * @param restActions  The Rest Actions object used to report assertions.
	 * @param data The Test Data.
	 * @param response  The API response, in String form.
	 */
	private void verifyResponse( RESTActions restActions, Hashtable<String,String> data, String response ) {
		LOG.info("##### Test the actual response...");
		Gson gson = new Gson();
		WSReasonCodeTypeList actualReasonCodeTypeList = gson.fromJson(response, WSReasonCodeTypeList.class);
		
		restActions.assertTrue(null != actualReasonCodeTypeList, REASON_CODE_TYPE_LIST_IS_NULL);
		if (null != actualReasonCodeTypeList) {
			
			restActions.assertTrue(actualReasonCodeTypeList.getReasonCodeTypes().size() > 0,
					REASON_CODE_TYPE_LIST_HAS_SIZE_ZERO );
			
			WSReasonCodeType reasonTypeCode = actualReasonCodeTypeList.getReasonCodeTypes().get(0);
			
			restActions.assertTrue(null != reasonTypeCode, REASON_CODE_TYPE_REFERENCE_IS_NULL);
			
			if (null != reasonTypeCode) {
				LOG.info("##### Test the Reason Code Type Name...");
				String expectedReasonCodeTypeName = data.get( EXPECTED_REASON_CODE_TYPE_NAME );
				String actualReasonCodeTypeName = reasonTypeCode.getName();
				
				restActions.assertTrue(expectedReasonCodeTypeName.equals(actualReasonCodeTypeName),
						String.format(BAD_REASON_CODE_TYPE_NAME, expectedReasonCodeTypeName, actualReasonCodeTypeName));
				
				LOG.info("##### Test the Reason Code Type Description...");
				String expectedReasonCodeTypeDesc = data.get( EXPECTED_REASON_CODE_TYPE_DESC );
				String actualReasonCodeTypeDesc = reasonTypeCode.getDescription();
				
				restActions.assertTrue(expectedReasonCodeTypeDesc.equals(actualReasonCodeTypeDesc),
						String.format(BAD_REASON_CODE_TYPE_DESC, expectedReasonCodeTypeDesc, actualReasonCodeTypeDesc));
				
				restActions.assertTrue(reasonTypeCode.getReasonCodes().size() > 0, REASON_CODE_LIST_HAS_SIZE_ZERO);
				
				WSReasonCode reasonCode = reasonTypeCode.getReasonCodes().get(0);
				if (null != reasonCode) {
					LOG.info("##### Test the Reason Code Id...");
					int expectedReasonCodeId = 1;
					int actualReasonCodeId = reasonCode.getReasonCodeId();
					
					restActions.assertTrue(expectedReasonCodeId == actualReasonCodeId, 
							String.format(BAD_REASON_CODE_ID, expectedReasonCodeId, actualReasonCodeId));
					
					LOG.info("##### Test the Reason Code Description...");
					String expectedReasonCodeDesc = data.get(EXPECTED_REASON_CODE_DESC);
					String actualReasonCodeDesc = reasonCode.getDescription();
					
					restActions.assertTrue(expectedReasonCodeDesc.equals(actualReasonCodeDesc),
							String.format(BAD_REASON_CODE_DESC, expectedReasonCodeDesc, actualReasonCodeDesc));
					
					LOG.info("##### Test the Reason Code Active flag...");
					String sExpectedReasonCodeActive = data.get(EXPECTED_REASON_CODE_ACTIVE);
					Boolean expectedReasonCodeActive = Boolean.valueOf(sExpectedReasonCodeActive);
					Boolean actualReasonCodeActive = reasonCode.getActive();
					
					restActions.assertTrue(expectedReasonCodeActive.equals(actualReasonCodeActive),
							String.format(BAD_REASON_CODE_ACTIVE, expectedReasonCodeActive, actualReasonCodeActive));
					
					LOG.info("##### Test the Reason Code Min Amount...");
					String sExpectedReasonCodeMinAmount = data.get(EXPECTED_REASON_CODE_MIN_AMOUNT);
					Integer expectedReasonCodeMinAmount = Integer.valueOf(sExpectedReasonCodeMinAmount);
					Integer actualReasonCodeMinAmount = reasonCode.getMinAmount();
					
					restActions.assertTrue(expectedReasonCodeMinAmount.equals(actualReasonCodeMinAmount), 
							String.format(BAD_REASON_CODE_MIN_AMOUNT,
									expectedReasonCodeMinAmount, actualReasonCodeMinAmount));
					
					LOG.info("##### Test the Reason Code Max Amount...");
					String sExpectedReasonCodeMaxAmount = data.get(EXPECTED_REASON_CODE_MAX_AMOUNT);
					Integer expectedReasonCodeMaxAmount = Integer.valueOf(sExpectedReasonCodeMaxAmount);
					Integer actualReasonCodeMaxAmount = reasonCode.getMaxAmount();
					
					restActions.assertTrue( expectedReasonCodeMaxAmount.equals(actualReasonCodeMaxAmount), 
							String.format(BAD_REASON_CODE_MAX_AMOUNT,
									expectedReasonCodeMaxAmount, actualReasonCodeMaxAmount));
					
					LOG.info("##### Test the Notes Mandatory Flag...");
					String sExpectedNotesMandatoryFlag = data.get(EXPECTED_NOTES_MANDATORY_FLAG);
					Boolean expectedNotesManadatoryFlag = Boolean.valueOf(sExpectedNotesMandatoryFlag);
					Boolean actualNotesMandatoryFlag = reasonCode.getNotesMandatoryFlag();
					
					restActions.assertTrue(expectedNotesManadatoryFlag.equals(actualNotesMandatoryFlag), 
							String.format(BAD_NOTES_MANDATORY_FLAG,
									expectedNotesManadatoryFlag, actualNotesMandatoryFlag));
					
					List<WSFinanciallyResponsibleOperator> finResponseOpList = reasonCode.getFinanciallyResponsibleOperators();
					
					restActions.assertTrue(null != finResponseOpList, FINANCIALLY_RESPONSIBLE_OPERAOR_LIST_IS_NULL);
					
					if (null != finResponseOpList) {
						WSFinanciallyResponsibleOperator finResponsibleOp = finResponseOpList.get(0);
						
						LOG.info("##### Test the Financially Responsible Operator Id...");
						String sExpectedFinResOpId = data.get(EXPECTED_FINANCIALLY_RESPONSIBLE_OPERATOR_ID);
						Integer expectedFinResOpId = Integer.valueOf(sExpectedFinResOpId);
						Integer actualFinResOpId = finResponsibleOp.getFinanciallyResponsibleOperatorId();
						
						restActions.assertTrue(expectedFinResOpId.equals(actualFinResOpId), 
								String.format(BAD_FINANCIALLY_RESPONSIBLE_OPERATOR_ID,
										expectedFinResOpId, actualFinResOpId));
						
						LOG.info("##### Test the Financially Responsible Operator Description...");
						String expectedFinResOpDesc = data.get(EXPECTED_FINANCIALLY_RESPONSIBLE_OPERATOR_DESC);
						String actualFinResOpDesc = finResponsibleOp.getFinanciallyResponsibleOperatorDescription();
						
						restActions.assertTrue(expectedFinResOpDesc.equals(actualFinResOpDesc), 
								String.format(BAD_FINANCIALLY_RESPONSIBLE_OPERATOR_DESC,
										expectedFinResOpDesc, actualFinResOpDesc));
					}
				}
			}
		}
	}
}
