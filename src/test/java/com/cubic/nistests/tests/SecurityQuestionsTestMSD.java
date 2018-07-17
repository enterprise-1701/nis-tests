package com.cubic.nistests.tests;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import org.testng.ITestContext;
import org.testng.annotations.Test;

import com.cubic.nisjava.apiobjects.WSSecurityQuestionList;
import com.cubic.nisjava.constants.AppConstants;
import com.cubic.nisjava.dataproviders.NISDataProviderSource;
import com.cubic.nisjava.apiobjects.WSSecurityQuestion;

/**
 * The MSD flavor of the SecurityQuestionsTest class.  
 * To be used when NIS is using MSD as its CRM component.
 * 
 * @author 203402
 *
 */
public class SecurityQuestionsTestMSD extends SecurityQuestionsTest {	
	
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
		mainTest( context, data );
	}
	
	/**
	 * Helper method to build the Expected test data.
	 * 
	 * @param data  The Test Data from the JSON input file.
	 * @return  An initialized WSSecurityQuestionList object.
	 */
	@Override
	protected WSSecurityQuestionList buildExpectedTestData( Hashtable<String,String> data) {
		WSSecurityQuestionList result = new WSSecurityQuestionList();
		
		List<WSSecurityQuestion> questionList = new ArrayList<WSSecurityQuestion>();
		
		WSSecurityQuestion q1 = new WSSecurityQuestion();
		q1.setName(data.get(EXPECTED_QUESTION_1));
		String sAnswer1 = data.get(EXPECTED_VALUE_1);
		q1.setValue(sAnswer1);
		questionList.add(q1);

		WSSecurityQuestion q2 = new WSSecurityQuestion();
		q2.setName(data.get(EXPECTED_QUESTION_2));
		String sAnswer2 = data.get(EXPECTED_VALUE_2);
		q2.setValue(sAnswer2);
		questionList.add(q2);
		
		WSSecurityQuestion q3 = new WSSecurityQuestion();
		q3.setName(data.get(EXPECTED_QUESTION_3));
		String sAnswer3 = data.get(EXPECTED_VALUE_3);
		q3.setValue(sAnswer3);
		questionList.add(q3);
		
		WSSecurityQuestion q4 = new WSSecurityQuestion();
		q4.setName(data.get(EXPECTED_QUESTION_4));
		String sAnswer4 = data.get(EXPECTED_VALUE_4);
		q4.setValue(sAnswer4);
		questionList.add(q4);
		
		WSSecurityQuestion q5 = new WSSecurityQuestion();
		q5.setName(data.get(EXPECTED_QUESTION_5));
		String sAnswer5 = data.get(EXPECTED_VALUE_5);
		q5.setValue(sAnswer5);
		questionList.add(q5);
		
		WSSecurityQuestion q6 = new WSSecurityQuestion();
		q6.setName(data.get(EXPECTED_QUESTION_6));
		String sAnswer6 = data.get(EXPECTED_VALUE_6);
		q6.setValue(sAnswer6);
		questionList.add(q6);
		
		result.setSecurityQuestions(questionList);
		
		return result;
	}
}
