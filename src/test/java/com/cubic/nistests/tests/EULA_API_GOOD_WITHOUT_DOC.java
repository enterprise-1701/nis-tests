package com.cubic.nistests.tests;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import org.testng.ITestContext;
import org.testng.annotations.Test;

import com.cubic.nisjava.apiobjects.WSCXSInfo;
import com.cubic.nisjava.apiobjects.WSCXSLocale;
import com.cubic.nisjava.apiobjects.WSInfoDocument;
import com.cubic.nisjava.constants.AppConstants;
import com.cubic.nisjava.dataproviders.NISDataProviderSource;

public class EULA_API_GOOD_WITHOUT_DOC extends EULA_API_GOOD {

	/**
	 * Lookup the EULA Info from NIS by eula-id and locale.
	 * 
	 * testRailId: 698330
	 * 
	 * @param context 	The TestNG context reference.
	 * @param data		The test data.
	 * @throws Throwable Thrown if something goes wrong.
	 */
	@Test(dataProvider = AppConstants.DATA_PROVIDER, dataProviderClass = NISDataProviderSource.class)
	public void getEULA(ITestContext context, Hashtable<String, String> data) throws Throwable {
		mainTest( context, data );
	}	
	
	
	/**
	 * Helper method to build a WSInfoDocument instance from info
	 * stored in the JSON test data file.
	 * 
	 * @param data  The test data read from the JSON file.
	 * @return A WSInfoDocument object built from the test data.
	 */
	@Override
	public WSInfoDocument buildExpectedInfoDoc( Hashtable<String,String> data ) {
		WSInfoDocument infoDoc = new WSInfoDocument();
		
		WSCXSLocale cxsLocale = new WSCXSLocale();
		cxsLocale.setCxsLanguage(data.get(LOC_LANGUAGE));
		String sLocaleId = data.get(LOC_LOCALE_ID);
		Integer oLocaleId = Integer.valueOf(sLocaleId);
		cxsLocale.setCxsLocaleId(oLocaleId);
		cxsLocale.setCxsLocaleTag(data.get(LOC_LOCALE_TAG));
		
		// no Document info
		infoDoc.setDocument(null);
		
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
