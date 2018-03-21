package com.cubic.nistests.tests;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.testng.annotations.Factory;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;

import com.cubic.genericutils.FileUtil;
import com.cubic.genericutils.GenericConstants;
import com.cubic.testrail.TestRailUtil;

public class TestScript_Driver {
	private static Hashtable<String , String> propTable = GenericConstants.GENERIC_FW_CONFIG_PROPERTIES;
	private String testRailProjectID;
	private String testRailSuiteID;
	@SuppressWarnings("unchecked")
	@Factory
	@Parameters({"projectID","suiteID"})
	public Object[] factoryMethod(@Optional String projectID,@Optional String suiteID) throws Exception {
		if((projectID!=null) && (!projectID.equals("%projectID%")) && (!projectID.equals("${ProjectID}")) ){
			testRailProjectID=projectID;
		}else if(propTable.get("Test_Rail_Project_ID")!=null) {
			testRailProjectID=propTable.get("Test_Rail_Project_ID");
		}
		if((suiteID!=null) && (!suiteID.equals("%suiteID%")) && (!suiteID.equals("${SuitID}"))&& (!suiteID.equals("${SuiteID}"))){
			testRailSuiteID=suiteID;
		}else if(propTable.get("Test_Rail_Suite_ID")!=null){
			testRailSuiteID=propTable.get("Test_Rail_Suite_ID");
		}
		JSONArray automationRefernceClasses = new JSONArray();
		JSONArray testRailCaseIDs = new JSONArray();
		JSONObject MyData=TestRailUtil.updateTestClassList(testRailProjectID, testRailSuiteID);
		automationRefernceClasses=(JSONArray) MyData.get("TestClasses");
		testRailCaseIDs = (JSONArray) MyData.get("TestRailCaseIDs");
		JSONParser parser = new JSONParser();
		String testRunPostRequestJSON = FileUtil.readFile(GenericConstants.TEST_CASES_TO_BE_EXECUTED_JSON_FILE_PATH+GenericConstants.TEST_RAIL_TEST_RUN_TEMPLATE_JSON);
		Object obj = parser.parse(testRunPostRequestJSON);
		JSONObject jsonObject = (JSONObject) obj;
		jsonObject.put("case_ids", testRailCaseIDs);
		String Data;
		List<Object> list = new ArrayList<Object>();
		for (int i=0;i<automationRefernceClasses.size();i++) {		 		
			Object classObj;
			try {
				Data = this.getClass().getPackage()+"."+automationRefernceClasses.get(i).toString();
				classObj = Class.forName(Data.split(" ")[1]).newInstance();
				list.add(classObj);
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}		 		
		}
		try (FileWriter file = new FileWriter(GenericConstants.TEST_CASES_TO_BE_EXECUTED_JSON_FILE_PATH+GenericConstants.TEST_RAIL_TEST_RUN_TEMPLATE_JSON)) {

			file.write(jsonObject.toJSONString());
			file.flush();

		} catch (IOException e) {
			e.printStackTrace();
		}
		Object[] data = list.toArray();
		return data;
	} 
}
