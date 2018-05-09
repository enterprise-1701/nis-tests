package com.cubic.nistests.tests;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import org.apache.log4j.Logger;
import org.testng.ITestContext;

import com.cubic.accelerators.RESTActions;
import com.cubic.accelerators.RESTConstants;
import com.cubic.accelerators.RESTEngine;
import com.cubic.backoffice.constants.BackOfficeGlobals;
import com.cubic.backoffice.utils.BackOfficeUtils;
import com.cubic.nisjava.apiobjects.WSCXSDocument;
import com.cubic.nisjava.apiobjects.WSCXSInfo;
import com.cubic.nisjava.apiobjects.WSCXSLocale;
import com.cubic.nisjava.apiobjects.WSInfoDocument;
import com.google.gson.Gson;
import com.sun.jersey.api.client.ClientResponse;

public class EULA_API_GOOD extends RESTEngine {

	protected final Logger LOG = Logger.getLogger(this.getClass().getName());
	protected static final String LOC_LANGUAGE = "LOC_LANGUAGE";
	protected static final String LOC_LOCALE_ID = "LOC_LOCALE_ID";
	protected static final String LOC_LOCALE_TAG = "LOC_LOCALE_TAG";
	protected static final String DOC_EULA_ID = "DOC_EULA_ID";
	protected static final String DOC_EULA_DOCUMENT_ID = "DOC_EULA_DOCUMENT_ID";
	protected static final String DOC_FORMAT = "DOC_FORMAT";
	protected static final String DOC_FILENAME = "DOC_FILENAME";
	protected static final String DOC_DOCUMENT = "DOC_DOCUMENT";
	protected static final String INFO_EULA_ID = "INFO_EULA_ID";
	protected static final String INFO_EULA_TYPE = "INFO_EULA_TYPE";
	protected static final String INFO_NAME = "INFO_NAME";
	protected static final String INFO_NOTICE_DATE = "INFO_NOTICE_DATE";
	protected static final String INFO_EFFECTIVE_DATE = "INFO_EFFECTIVE_DATE";
	protected static final String INFO_PUBLISH_DATE = "INFO_PUBLISH_DATE";
	protected static final String INFO_EXPLICIT = "INFO_EXPLICIT";
	protected static final String INFO_FEATURE = "INFO_FEATURE";
	protected static final String INFO_CHANNEL = "INFO_CHANNEL";
	protected static final String INFO_USER_TYPE = "INFO_USER_TYPE";
	protected static final String INFO_STATUS = "INFO_STATUS";
	protected static final String INFO_ARCHIVE = "INFO_ARCHIVE";
	protected static final String INFO_AUTHOR_ID = "INFO_AUTHOR_ID";
	protected static final String INFO_FEATURE_DESC = "INFO_FEATURE_DESC";
	protected static final String INFO_CHANNEL_DESC = "INFO_CHANNEL_DESC";
	protected static final String INFO_USER_TYPE_DESC = "INFO_USER_TYPE_DESC";
	protected static final String INFO_EULA_TYPE_DESC = "INFO_EULA_TYPE_DESC";
	protected static final String INFO_CREATE_DTM = "INFO_CREATE_DTM";
	protected static final String INFO_PUBLISHER_ID = "INFO_PUBLISHER_ID";
	protected static final String RESPONSE_IS_NULL = "RESPONSE IS NULL BUT SHOULD NOT BE NULL";
	protected static final String BAD_RESPONSE_CODE = "WRONG HTTP RESPONSE CODE - EXPECTED 200, FOUND ";
	protected static final String RETURN_DOCUMENT = "RETURN_DOCUMENT";
	
	public void mainTest(ITestContext context, Hashtable<String, String> data) throws Throwable {
		String testCaseName = data.get("TestCase_Description");
		LOG.info("##### Starting Test Case " + testCaseName);
		
		try {
			LOG.info("##### Setting up automation test...");
			BackOfficeGlobals.ENV.setEnvironmentVariables();
			RESTActions restActions = setupAutomationTest(context, testCaseName);
			
            String sURL = "http://" + BackOfficeGlobals.ENV.NIS_HOST + ":" + BackOfficeGlobals.ENV.NIS_PORT
                    + "/nis/csapi/v1/eula/" + data.get(INFO_EULA_ID)
                    + "/" + data.get(LOC_LOCALE_TAG)
                    + "?returnDocument=" + data.get(RETURN_DOCUMENT);
            LOG.info("##### Built URL: " + sURL);
            
            LOG.info("Creating GET request headers");
			Hashtable<String,String> headerTable = BackOfficeUtils.createRESTHeader(RESTConstants.APPLICATION_JSON);
			Hashtable<String, String> urlQueryParameters = new Hashtable<String, String>();
			
			LOG.info("##### Making HTTP request to get the EULA...");
			ClientResponse clientResponse = restActions.getClientResponse(sURL, headerTable, urlQueryParameters,
					RESTConstants.APPLICATION_JSON);

			LOG.info("##### Verifying HTTP response code...");
			int status = clientResponse.getStatus();
			String msg = BAD_RESPONSE_CODE + status;
			restActions.assertTrue(status == HttpURLConnection.HTTP_OK, msg);
			
			String response = clientResponse.getEntity(String.class);
			LOG.info("##### Got the response body");
			restActions.assertTrue(response != null, RESPONSE_IS_NULL);
			
			Gson gson = new Gson();
			LOG.info("##### Building the Expected WSInfoDocument test data...");
			WSInfoDocument expectInfoDoc = buildExpectedInfoDoc( data );
			
			LOG.info("##### Parsing the Actual WSInfoDocument test data from the API response...");
			WSInfoDocument actualInfoDoc = gson.fromJson(response, WSInfoDocument.class);
			
			LOG.info("##### Comparing the Expected test data with the Actual test data...");
			boolean bEquals = expectInfoDoc.equals( actualInfoDoc );
			String errorMessage = WSInfoDocument.getErrorMessage();
			restActions.assertTrue(bEquals, errorMessage);
			
		} finally {
			teardownAutomationTest(context, testCaseName);
			LOG.info("##### Done!");
		}
	}
	
	/**
	 * Helper method to build a WSInfoDocument instance from info
	 * stored in the JSON test data file.
	 * 
	 * @param data  The test data read from the JSON file.
	 * @return A WSInfoDocument object built from the test data.
	 */
	public WSInfoDocument buildExpectedInfoDoc( Hashtable<String,String> data ) {
		WSInfoDocument infoDoc = new WSInfoDocument();
		
		WSCXSLocale cxsLocale = new WSCXSLocale();
		cxsLocale.setCxsLanguage(data.get(LOC_LANGUAGE));
		String sLocaleId = data.get(LOC_LOCALE_ID);
		Integer oLocaleId = Integer.valueOf(sLocaleId);
		cxsLocale.setCxsLocaleId(oLocaleId);
		cxsLocale.setCxsLocaleTag(data.get(LOC_LOCALE_TAG));
		
		WSCXSDocument cxsDocument = new WSCXSDocument();
		String sDocEulaId = data.get(DOC_EULA_ID);
		Integer oDocEulaId = Integer.valueOf(sDocEulaId);
		cxsDocument.setEulaId(oDocEulaId);
		
		String sDocEulaDocumentId = data.get(DOC_EULA_DOCUMENT_ID);
		Integer oDocEluaDocumentId = Integer.valueOf(sDocEulaDocumentId);
		cxsDocument.setEulaDocumentId(oDocEluaDocumentId);
		cxsDocument.setDocument(data.get(DOC_DOCUMENT));			
		cxsDocument.setFormat(data.get(DOC_FORMAT));
		cxsDocument.setLocale(cxsLocale);
		cxsDocument.setFileName(data.get(DOC_FILENAME));
		infoDoc.setDocument(cxsDocument);
		
		WSCXSInfo cxsInfo = new WSCXSInfo();
		String sInfoEulaId = data.get(INFO_EULA_ID);
		Integer oInfoEulaId = Integer.valueOf(sInfoEulaId);
		cxsInfo.setEulaId(oInfoEulaId);
		cxsInfo.setEulaType(data.get(INFO_EULA_TYPE));
		cxsInfo.setName(data.get(INFO_NAME));
		cxsInfo.setNoticeDate(data.get(INFO_NOTICE_DATE));
		cxsInfo.setEffectiveDate(data.get(INFO_EFFECTIVE_DATE));
		cxsInfo.setPublishDate(data.get(INFO_PUBLISH_DATE));
		String sInfoExplicit = data.get(INFO_EXPLICIT);
		Boolean oInfoExplicit = Boolean.valueOf(sInfoExplicit);
		cxsInfo.setExplicit(oInfoExplicit);
		cxsInfo.setFeature(data.get(INFO_FEATURE));
		cxsInfo.setChannel(data.get(INFO_CHANNEL));
		cxsInfo.setUserType(data.get(INFO_USER_TYPE));
		cxsInfo.setStatus(data.get(INFO_STATUS));
		
		String sInfoArchive = data.get(INFO_ARCHIVE);
		Boolean oInfoArchive = Boolean.valueOf(sInfoArchive);
		cxsInfo.setArchive(oInfoArchive);
		cxsInfo.setAuthorId(data.get(INFO_AUTHOR_ID));
		List<Object> localeInfo = new ArrayList<Object>();
		cxsInfo.setLocaleInfo(localeInfo);
		cxsInfo.setFeatureDescription(data.get(INFO_FEATURE_DESC));
		cxsInfo.setChannelDescription(data.get(INFO_CHANNEL_DESC));
		cxsInfo.setUserTypeDescription(data.get(INFO_USER_TYPE_DESC));
		cxsInfo.setEulaTypeDescription(data.get(INFO_EULA_TYPE_DESC));
		cxsInfo.setCreateDtm(data.get(INFO_CREATE_DTM));
		cxsInfo.setPublisherId(data.get(INFO_PUBLISHER_ID));
		infoDoc.setInfo(cxsInfo);								  
		
		return infoDoc;
	}
}
