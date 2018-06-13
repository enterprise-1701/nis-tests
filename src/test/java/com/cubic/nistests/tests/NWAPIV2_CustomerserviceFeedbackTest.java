package com.cubic.nistests.tests;

import java.io.PrintWriter;
import java.io.StringWriter;
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
import com.cubic.nisjava.constants.AppConstants;
import com.cubic.nisjava.dataproviders.NISDataProviderSource;
import com.sun.jersey.api.client.ClientResponse;

/**
 * This test class calls the customerservice/feedback API.
 * 
 * @author 203402
 *
 */
public class NWAPIV2_CustomerserviceFeedbackTest extends RESTEngine {

	private final Logger LOG = Logger.getLogger(this.getClass().getName());
	
	/**
	 * This @Test method will call the customerservice/feedback API.
	 * By default, the HTTP response code returned is 500 Internal
	 * Server Error, unless the NIS SETTINGS table is updated. The
	 * default settings preclude the operation of the feedback
	 * mechanism, which is only implemented for MSD, not CMS. 
	 * 
	 * testRailId = 958036
	 * 
	 * @param context  The TestNG Context object
	 * @param data  The Test Data, from the JSON input file
	 * @throws Throwable  Thrown if something goes wrong
	 */
	@Test(dataProvider = AppConstants.DATA_PROVIDER, dataProviderClass = NISDataProviderSource.class)
	public void customerFeedback(ITestContext context, Hashtable<String, String> data) throws Throwable {
		String testCaseName = data.get("TestCase_Description");
		LOG.info("##### Starting Test Case " + testCaseName);
		
		try {
			LOG.info("##### Setting up automation test...");
			BackOfficeGlobals.ENV.setEnvironmentVariables();
			RESTActions restActions = setupAutomationTest(context, testCaseName);
			
            LOG.info("##### Creating GET request headers");
			Hashtable<String,String> headerTable = BackOfficeUtils.createRESTHeader(RESTConstants.APPLICATION_JSON);
			
			String sURL = buildFeedbackURL();
	        LOG.info("##### Built URL: " + sURL);
	        
			String sPrevalidateRequestBody = buildFeedbackRequestBody();            
	        
			LOG.info("##### Making HTTP request to prevalidate the credentials...");
			ClientResponse clientResponse = restActions.postClientResponse(
					sURL, sPrevalidateRequestBody, headerTable, null,
					RESTConstants.APPLICATION_JSON);

			LOG.info("##### Verifying HTTP response code...");
			int status = clientResponse.getStatus();
			String msg = "WRONG HTTP RESPONSE CODE - EXPECTED 500, FOUND " + status;
			restActions.assertTrue(status == HttpURLConnection.HTTP_INTERNAL_ERROR, msg);
			
		} finally {
			teardownAutomationTest(context, testCaseName);
			LOG.info("##### Done!");
		}
	}
	
	/**
	 * Helper method to build the customerservice/feedback URL.
	 * 
	 * @return  The customerservice/feedback URL, in String form
	 */
	private String buildFeedbackURL() {
        String sURL = "http://" + BackOfficeGlobals.ENV.NIS_HOST + ":" + BackOfficeGlobals.ENV.NIS_PORT
                + "/nis/nwapi/v2/customerservice/feedback";
		return sURL;
	}
	
	/**
	 * Helper method to build the customerservice/feedback request body.
	 * 
	 * @return the customerservice/feedback request body, as a String
	 */
	private String buildFeedbackRequestBody() {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter( sw );
		pw.println("{");
		pw.println("\"feedbackType\":\"GeneralFeedback\",");
		pw.println("\"feedbackMessage\":\"Test Feedback\",");
		pw.println("\"unregisteredEmail\":\"ankit.pandya@cubic.com\",");
		pw.println("\"firstName\":\"Ankit\",");
		pw.println("\"lastName\":\"Pandya\",");
		pw.println("\"referenceNumber\":\"1234567890\"");
		pw.println("}");
		return sw.toString();
	}
}
