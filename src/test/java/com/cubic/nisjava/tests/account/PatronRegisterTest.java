package com.cubic.nisjava.tests.account;

import java.util.Hashtable;


import org.testng.ITestContext;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.cubic.accelerators.RESTActions;
import com.cubic.accelerators.RESTEngine;
import com.cubic.datadriven.TestDataUtil;
import com.cubic.nisjava.api.PatronRegisterPost;
import com.cubic.nisjava.constants.AppConstants;
import com.cubic.nisjava.dataproviders.NISPatronRegister;


/**
 * This test is for implementing the patron/register POST for Nextwave 1.1.
 *  * JIRA: SDPT1-843 
 *  * @author VijayaBhaskar Palem
 *
 */

public class PatronRegisterTest  extends RESTEngine {
	
	com.cubic.nisjava.api.PatronRegisterPost register = new com.cubic.nisjava.api.PatronRegisterPost();
	
	
	/*******  Patron Register -  Success  Scenario *******/
	@Test(dataProvider = AppConstants.DATA_PROVIDER, dataProviderClass = NISPatronRegister.class)
	public void patronRegister(ITestContext context,Hashtable<String, String> data) throws Throwable {
		
		String testCaseName = data.get("TestCase_Description_ValidData_C339774");
		RESTActions restActions = setupAutomationTest(context, testCaseName);
        
		
		try {
			
			if (data.get("RunMode").equals("Y")) {
				  PatronRegisterPost.createPatronAccount(data , restActions);
				 				                
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		} finally {
			teardownAutomationTest(context, testCaseName);

		}
	}
		//*******  Patron Register  -  Invalid User name *******//*
		@Test(dataProvider = AppConstants.DATA_PROVIDER, dataProviderClass = NISPatronRegister.class)
		public void patronRegisterWithinvalidUsername(ITestContext context,Hashtable<String, String> data) throws Throwable {
			
			String testCaseNameWithInvalidData = data.get("TestCase_Description_InvalidUsername_C339775");
			RESTActions restActions = setupAutomationTest(context, testCaseNameWithInvalidData);
	        
			
			try {
				if (data.get("RunMode").equals("Y")) {
					  PatronRegisterPost.verifyPatronAccountWithInvalidUsername(data, restActions);
	                  
				}
			} catch (Exception e) {
				e.printStackTrace();
				throw new RuntimeException(e);
			} finally {
				teardownAutomationTest(context, testCaseNameWithInvalidData);

			}
		}
		
		
		//*******  Patron Register -  password - Maximum length exceeded  *******//*
		@Test(dataProvider = AppConstants.DATA_PROVIDER, dataProviderClass = NISPatronRegister.class)
		public void patronRegisterWithPasswordMaxlength(ITestContext context,Hashtable<String, String> data) throws Throwable {
			
			String testCaseNameWithInvalidData = data.get("TestCase_Description_PasswordMaxlength_C360689");
			RESTActions restActions = setupAutomationTest(context, testCaseNameWithInvalidData);
	        
			
			try {
				if (data.get("RunMode").equals("Y")) {
					  PatronRegisterPost.verifyPatronAccountWithInvalidPassword(data, restActions);
	                  
				}
			} catch (Exception e) {
				e.printStackTrace();
				throw new RuntimeException(e);
			} finally {
				teardownAutomationTest(context, testCaseNameWithInvalidData);

			}
		}
		
		//*******  Patron Register -  Name - blank  *******//*
		@Test(dataProvider = AppConstants.DATA_PROVIDER, dataProviderClass = NISPatronRegister.class)
		public void patronRegisterWithNameBlank(ITestContext context,Hashtable<String, String> data) throws Throwable {
			
			String testCaseNameWithInvalidData = data.get("TestCase_Description_NameBlank_C360697");
			RESTActions restActions = setupAutomationTest(context, testCaseNameWithInvalidData);
	        
			
			try {
				if (data.get("RunMode").equals("Y")) {
					  PatronRegisterPost.verifyPatronAccountWithNameBlank(data, restActions);
	                  
				}
			} catch (Exception e) {
				e.printStackTrace();
				throw new RuntimeException(e);
			} finally {
				teardownAutomationTest(context, testCaseNameWithInvalidData);

			}
		}
		
		//*******  Patron Register -  First name - blank *******//*
		@Test(dataProvider = AppConstants.DATA_PROVIDER, dataProviderClass = NISPatronRegister.class)
		public void patronRegisterWithFirstNameBlank(ITestContext context,Hashtable<String, String> data) throws Throwable {
			
			String testCaseNameWithInvalidData = data.get("TestCase_Description_FirstNameBlank_C360698");
			RESTActions restActions = setupAutomationTest(context, testCaseNameWithInvalidData);
	        
			
			try {
				if (data.get("RunMode").equals("Y")) {
					 PatronRegisterPost.verifyPatronAccountWithFirstNameBlank(data, restActions);
	                  
				}
			} catch (Exception e) {
				e.printStackTrace();
				throw new RuntimeException(e);
			} finally {
				teardownAutomationTest(context, testCaseNameWithInvalidData);

			}
		}
		
		//*******  Patron Register -  First name - too long *******//*
		@Test(dataProvider = AppConstants.DATA_PROVIDER, dataProviderClass = NISPatronRegister.class)
		public void patronRegisterWithFirstNametoolang(ITestContext context,Hashtable<String, String> data) throws Throwable {
			
			String testCaseNameWithInvalidData = data.get("TestCase_Description_FirstNametoolang_C360700");
			RESTActions restActions = setupAutomationTest(context, testCaseNameWithInvalidData);
	        
			
			try {
				if (data.get("RunMode").equals("Y")) {
					  PatronRegisterPost.verifyPatronAccountWithFirstNametoolong(data, restActions);
	                  
				}
			} catch (Exception e) {
				e.printStackTrace();
				throw new RuntimeException(e);
			} finally {
				teardownAutomationTest(context, testCaseNameWithInvalidData);

			}
		}
		
		
		//*******  Patron Register -  Last name - blank *******//*
		@Test(dataProvider = AppConstants.DATA_PROVIDER, dataProviderClass = NISPatronRegister.class)
		public void patronRegisterWithLastNameBlank(ITestContext context,Hashtable<String, String> data) throws Throwable {
			
			String testCaseNameWithInvalidData = data.get("TestCase_Description_LastNameBlank_C360705");
			RESTActions restActions = setupAutomationTest(context, testCaseNameWithInvalidData);
	        
			
			try {
				if (data.get("RunMode").equals("Y")) {
					  PatronRegisterPost.verifyPatronAccountWithLastNameBlank(data, restActions);
	                  
				}
			} catch (Exception e) {
				e.printStackTrace();
				throw new RuntimeException(e);
			} finally {
				teardownAutomationTest(context, testCaseNameWithInvalidData);

			}
		}
		
			//*******  Patron Register -  Last name - too long *******//*
		@Test(dataProvider = AppConstants.DATA_PROVIDER, dataProviderClass = NISPatronRegister.class)
		public void patronRegisterWithLastNametoolang(ITestContext context,Hashtable<String, String> data) throws Throwable {
			
			String testCaseNameWithInvalidData = data.get("TestCase_Description_LastNametoolang_C360706");
			RESTActions restActions = setupAutomationTest(context, testCaseNameWithInvalidData);
	        
			
			try {
				if (data.get("RunMode").equals("Y")) {
					  PatronRegisterPost.verifyPatronAccountWithLastNametoolong(data, restActions);
	                  
				}
			} catch (Exception e) {
				e.printStackTrace();
				throw new RuntimeException(e);
			} finally {
				teardownAutomationTest(context, testCaseNameWithInvalidData);

			}
		}
			
		//*******  Patron Register - Address is Blank ******//*
		@Test(dataProvider = AppConstants.DATA_PROVIDER, dataProviderClass = NISPatronRegister.class)
		public void patronRegisterWithAddressBlank(ITestContext context,Hashtable<String, String> data) throws Throwable {
			
			String testCaseNameWithInvalidData = data.get("TestCase_Description_AddressBlank_C360709");
			RESTActions restActions = setupAutomationTest(context, testCaseNameWithInvalidData);
	        
			
			try {
				if (data.get("RunMode").equals("Y")) {
					  PatronRegisterPost.verifyPatronAccountWithAddressBlank(data, restActions);
	                  
				}
			} catch (Exception e) {
				e.printStackTrace();
				throw new RuntimeException(e);
			} finally {
				teardownAutomationTest(context, testCaseNameWithInvalidData);

			}
		}
		
		//*******  Patron Register - Address1 field is Blank ******//*
		@Test(dataProvider = AppConstants.DATA_PROVIDER, dataProviderClass = NISPatronRegister.class)
		public void patronRegisterWithAddress1FieldBlank(ITestContext context,Hashtable<String, String> data) throws Throwable {
			
			String testCaseNameWithInvalidData = data.get("TestCase_Description_Address1Blank_C360711");
			RESTActions restActions = setupAutomationTest(context, testCaseNameWithInvalidData);
	        
			
			try {
				if (data.get("RunMode").equals("Y")) {
					  PatronRegisterPost.verifyPatronAccountWithAddress1Blank(data, restActions);
	                  
				}
			} catch (Exception e) {
				e.printStackTrace();
				throw new RuntimeException(e);
			} finally {
				teardownAutomationTest(context, testCaseNameWithInvalidData);

			}
		}
		
		//*******  Patron Register - Address1 is too long ******//*
		@Test(dataProvider = AppConstants.DATA_PROVIDER, dataProviderClass = NISPatronRegister.class)
		public void patronRegisterWithAddress1toolong(ITestContext context,Hashtable<String, String> data) throws Throwable {
			
			String testCaseNameWithInvalidData = data.get("TestCase_Description_Address1toolong_C360712");
			RESTActions restActions = setupAutomationTest(context, testCaseNameWithInvalidData);
	        
			
			try {
				if (data.get("RunMode").equals("Y")) {
					  PatronRegisterPost.verifyPatronAccountWithAddress1toolong(data, restActions);
	                  
				}
			} catch (Exception e) {
				e.printStackTrace();
				throw new RuntimeException(e);
			} finally {
				teardownAutomationTest(context, testCaseNameWithInvalidData);

			}
		}
		
	//*******  Patron Register - Address2 is too long ******//*
		@Test(dataProvider = AppConstants.DATA_PROVIDER, dataProviderClass = NISPatronRegister.class)
		public void patronRegisterWithAddress2toolong(ITestContext context,Hashtable<String, String> data) throws Throwable {
			
			String testCaseNameWithInvalidData = data.get("TestCase_Description_Address2toolong_C360713");
			RESTActions restActions = setupAutomationTest(context, testCaseNameWithInvalidData);
	        
			
			try {
				if (data.get("RunMode").equals("Y")) {
					   PatronRegisterPost.verifyPatronAccountWithAddress2toolong(data, restActions);
	                  
				}
			} catch (Exception e) {
				e.printStackTrace();
				throw new RuntimeException(e);
			} finally {
				teardownAutomationTest(context, testCaseNameWithInvalidData);

			}
		}
		
		//*******  Patron Register - City is blank ******//*
		@Test(dataProvider = AppConstants.DATA_PROVIDER, dataProviderClass = NISPatronRegister.class)
		public void patronRegisterWithCityBlank(ITestContext context,Hashtable<String, String> data) throws Throwable {
			
			String testCaseNameWithInvalidData = data.get("TestCase_Description_CityBlank_C360715");
			RESTActions restActions = setupAutomationTest(context, testCaseNameWithInvalidData);
	        
			
			try {
				if (data.get("RunMode").equals("Y")) {
					  PatronRegisterPost.verifyPatronAccountWithCityBlank(data, restActions);
	                  
				}
			} catch (Exception e) {
				e.printStackTrace();
				throw new RuntimeException(e);
			} finally {
				teardownAutomationTest(context, testCaseNameWithInvalidData);

			}
		}
		
		//*******  Patron Register - City is too long ******//*
		@Test(dataProvider = AppConstants.DATA_PROVIDER, dataProviderClass = NISPatronRegister.class)
		public void patronRegisterWithCitytoolong(ITestContext context,Hashtable<String, String> data) throws Throwable {
			
			String testCaseNameWithInvalidData = data.get("TestCase_Description_Citytoolong_C360716");
			RESTActions restActions = setupAutomationTest(context, testCaseNameWithInvalidData);
	        
			
			try {
				if (data.get("RunMode").equals("Y")) {
					  PatronRegisterPost.verifyPatronAccountWithCitytoolong(data, restActions);
	                  
				}
			} catch (Exception e) {
				e.printStackTrace();
				throw new RuntimeException(e);
			} finally {
				teardownAutomationTest(context, testCaseNameWithInvalidData);

			}
		}
		
		//*******  Patron Register - State is Blank ******//*
		@Test(dataProvider = AppConstants.DATA_PROVIDER, dataProviderClass = NISPatronRegister.class)
		public void patronRegisterWithSateIdBlank(ITestContext context,Hashtable<String, String> data) throws Throwable {
			
			String testCaseNameWithInvalidData = data.get("TestCase_Description_StateIDBlank_C360719");
			RESTActions restActions = setupAutomationTest(context, testCaseNameWithInvalidData);
	        
			
			try {
				if (data.get("RunMode").equals("Y")) {
					  PatronRegisterPost.verifyPatronAccountWithStateBlank(data, restActions);
	                  
				}
			} catch (Exception e) {
				e.printStackTrace();
				throw new RuntimeException(e);
			} finally {
				teardownAutomationTest(context, testCaseNameWithInvalidData);

			}
		}

		//*******  Patron Register - Invalid State  ******//*
		@Test(dataProvider = AppConstants.DATA_PROVIDER, dataProviderClass = NISPatronRegister.class)
		public void patronRegisterWithInvalidSate(ITestContext context,Hashtable<String, String> data) throws Throwable {
			
			String testCaseNameWithInvalidData = data.get("TestCase_Description_InvalidState_C360720");
			RESTActions restActions = setupAutomationTest(context, testCaseNameWithInvalidData);
	        
			
			try {
				if (data.get("RunMode").equals("Y")) {
					  PatronRegisterPost.verifyPatronAccountWithInvalidState(data, restActions);
	                  
				}
			} catch (Exception e) {
				e.printStackTrace();
				throw new RuntimeException(e);
			} finally {
				teardownAutomationTest(context, testCaseNameWithInvalidData);

			}
		}
		
		//*******  Patron Register - Country blank  ******//*
		@Test(dataProvider = AppConstants.DATA_PROVIDER, dataProviderClass = NISPatronRegister.class)
		public void patronRegisterWithCountryBlank(ITestContext context,Hashtable<String, String> data) throws Throwable {
			
			String testCaseNameWithInvalidData = data.get("TestCase_Description_CountryBlank_C360722");
			RESTActions restActions = setupAutomationTest(context, testCaseNameWithInvalidData);
	        		
			try {
				if (data.get("RunMode").equals("Y")) {
					  PatronRegisterPost.verifyPatronAccountWithCountryBlank(data, restActions);
	                  
				}
			} catch (Exception e) {
				e.printStackTrace();
				throw new RuntimeException(e);
			} finally {
				teardownAutomationTest(context, testCaseNameWithInvalidData);

			}
		}

		//*******  Patron Register - Invalid CountryID  ******//*
		@Test(dataProvider = AppConstants.DATA_PROVIDER, dataProviderClass = NISPatronRegister.class)
		public void patronRegisterWithInvalidCountry(ITestContext context,Hashtable<String, String> data) throws Throwable {
			
			String testCaseNameWithInvalidData = data.get("TestCase_Description_InvalidCountry_C360723");
			RESTActions restActions = setupAutomationTest(context, testCaseNameWithInvalidData);
	        
			
			try {
				if (data.get("RunMode").equals("Y")) {
					  PatronRegisterPost.verifyPatronAccountWithInvalidCountryId(data, restActions);
	                  
				}
			} catch (Exception e) {
				e.printStackTrace();
				throw new RuntimeException(e);
			} finally {
				teardownAutomationTest(context, testCaseNameWithInvalidData);

			}
		}
		

		//*******  Patron Register - Postal Code is blank  ******//*
		@Test(dataProvider = AppConstants.DATA_PROVIDER, dataProviderClass = NISPatronRegister.class)
		public void patronRegisterWithPostalCodeBlank(ITestContext context,Hashtable<String, String> data) throws Throwable {
			
			String testCaseNameWithInvalidData = data.get("TestCase_Description_PostalCodeBlank_C360724");
			RESTActions restActions = setupAutomationTest(context, testCaseNameWithInvalidData);
	        
			
			try {
				if (data.get("RunMode").equals("Y")) {
					  PatronRegisterPost.verifyPatronAccountWithPostalCodeBlank(data, restActions);
	                  
				}
			} catch (Exception e) {
				e.printStackTrace();
				throw new RuntimeException(e);
			} finally {
				teardownAutomationTest(context, testCaseNameWithInvalidData);

			}
		}

		//*******  Patron Register - Invalid Postal Code  ******//*
		@Test(dataProvider = AppConstants.DATA_PROVIDER, dataProviderClass = NISPatronRegister.class)
		public void patronRegisterWithInvalidPostalCode(ITestContext context,Hashtable<String, String> data) throws Throwable {
			
			String testCaseNameWithInvalidData = data.get("TestCase_Description_InvalidPostalCode_C360725");
			RESTActions restActions = setupAutomationTest(context, testCaseNameWithInvalidData);
	        
			
			try {
				if (data.get("RunMode").equals("Y")) {
					  PatronRegisterPost.verifyPatronAccountWithInvalidPostalCode(data, restActions);
	                  
				}
			} catch (Exception e) {
				e.printStackTrace();
				throw new RuntimeException(e);
			} finally {
				teardownAutomationTest(context, testCaseNameWithInvalidData);

			}
		}

		//*******  Patron Register - Phone is Blank  ******//*
		@Test(dataProvider = AppConstants.DATA_PROVIDER, dataProviderClass = NISPatronRegister.class)
		public void patronRegisterWithPhoneBlank(ITestContext context,Hashtable<String, String> data) throws Throwable {
			
			String testCaseNameWithInvalidData = data.get("TestCase_Description_PhoneBlank_C360728");
			RESTActions restActions = setupAutomationTest(context, testCaseNameWithInvalidData);
	        
			
			try {
				if (data.get("RunMode").equals("Y")) {
					  PatronRegisterPost.verifyPatronAccountWithPhoneBlank(data, restActions);
	                  
				}
			} catch (Exception e) {
				e.printStackTrace();
				throw new RuntimeException(e);
			} finally {
				teardownAutomationTest(context, testCaseNameWithInvalidData);

			}
		}

		//*******  Patron Register - Phone is too long  ******//*
		@Test(dataProvider = AppConstants.DATA_PROVIDER, dataProviderClass = NISPatronRegister.class)
		public void patronRegisterWithPhonetoolong(ITestContext context,Hashtable<String, String> data) throws Throwable {
			
			String testCaseNameWithInvalidData = data.get("TestCase_Description_Phonetoolong_C360729");
			RESTActions restActions = setupAutomationTest(context, testCaseNameWithInvalidData);
	        
			
			try {
				if (data.get("RunMode").equals("Y")) {
					  PatronRegisterPost.verifyPatronAccountWithPhonetoolong(data, restActions);
	                  
				}
			} catch (Exception e) {
				e.printStackTrace();
				throw new RuntimeException(e);
			} finally {
				teardownAutomationTest(context, testCaseNameWithInvalidData);

			}
		}
		
		//*******  Patron Register - Phone number is blank  ******//*
		@Test(dataProvider = AppConstants.DATA_PROVIDER, dataProviderClass = NISPatronRegister.class)
		public void patronRegisterWithPhoneNumberBlank(ITestContext context,Hashtable<String, String> data) throws Throwable {
			
			String testCaseNameWithInvalidData = data.get("TestCase_Description_PhoneNumberBlank_C360730");
			RESTActions restActions = setupAutomationTest(context, testCaseNameWithInvalidData);
	        
			
			try {
				if (data.get("RunMode").equals("Y")) {
					  PatronRegisterPost.verifyPatronAccountWithPhoneNumberBlank(data, restActions);
	                  
				}
			} catch (Exception e) {
				e.printStackTrace();
				throw new RuntimeException(e);
			} finally {
				teardownAutomationTest(context, testCaseNameWithInvalidData);

			}
		}

		//*******  Patron Register - Invalid Phone number  ******//*
		@Test(dataProvider = AppConstants.DATA_PROVIDER, dataProviderClass = NISPatronRegister.class)
		public void patronRegisterWithInvalidPhoneNumber(ITestContext context,Hashtable<String, String> data) throws Throwable {
			
			String testCaseNameWithInvalidData = data.get("TestCase_Description_InvalidPhoneNumber_C360732");
			RESTActions restActions = setupAutomationTest(context, testCaseNameWithInvalidData);
	        
			
			try {
				if (data.get("RunMode").equals("Y")) {
					  PatronRegisterPost.verifyPatronAccountWithInvalidPhoneNumber(data, restActions);
	                  
				}
			} catch (Exception e) {
				e.printStackTrace();
				throw new RuntimeException(e);
			} finally {
				teardownAutomationTest(context, testCaseNameWithInvalidData);

			}
		}
	  
		//*******  Patron Register -  Phone Type Blank  ******//*
		@Test(dataProvider = AppConstants.DATA_PROVIDER, dataProviderClass = NISPatronRegister.class)
		public void patronRegisterWithPhoneTypeBlank(ITestContext context,Hashtable<String, String> data) throws Throwable {
			
			String testCaseNameWithInvalidData = data.get("TestCase_Description_PhoneTypeBlank_C360734");
			RESTActions restActions = setupAutomationTest(context, testCaseNameWithInvalidData);
	        
			
			try {
				if (data.get("RunMode").equals("Y")) {
					  PatronRegisterPost.verifyPatronAccountWithPhoneTypeBlank(data, restActions);
	                  
				}
			} catch (Exception e) {
				e.printStackTrace();
				throw new RuntimeException(e);
			} finally {
				teardownAutomationTest(context, testCaseNameWithInvalidData);

			}
		}

		//*******  Patron Register - Invalid Phone Type   ******//*
		@Test(dataProvider = AppConstants.DATA_PROVIDER, dataProviderClass = NISPatronRegister.class)
		public void patronRegisterWithInvalidPhoneType(ITestContext context,Hashtable<String, String> data) throws Throwable {
			
			String testCaseNameWithInvalidData = data.get("TestCase_Description_InvalidPhoneType_C360733");
			RESTActions restActions = setupAutomationTest(context, testCaseNameWithInvalidData);
	        
			
			try {
				if (data.get("RunMode").equals("Y")) {
					  PatronRegisterPost.verifyPatronAccountWithInvalidPhoneType(data, restActions);
	                  
				}
			} catch (Exception e) {
				e.printStackTrace();
				throw new RuntimeException(e);
			} finally {
				teardownAutomationTest(context, testCaseNameWithInvalidData);

			}
		}

		//*******  Patron Register - Email - too long   ******//*
		@Test(dataProvider = AppConstants.DATA_PROVIDER, dataProviderClass = NISPatronRegister.class)
		public void patronRegisterWithEmailtoolong(ITestContext context,Hashtable<String, String> data) throws Throwable {
			
			String testCaseNameWithInvalidData = data.get("TestCase_Description_Emailtoolong_C360739");
			RESTActions restActions = setupAutomationTest(context, testCaseNameWithInvalidData);
	        
			
			try {
				if (data.get("RunMode").equals("Y")) {
					  PatronRegisterPost.verifyPatronAccountWithEmailtoolong(data, restActions);
	                  
				}
			} catch (Exception e) {
				e.printStackTrace();
				throw new RuntimeException(e);
			} finally {
				teardownAutomationTest(context, testCaseNameWithInvalidData);

			}
		}
		
		//*******  Patron Register - Email - With Special characters   ******//*
		@Test(dataProvider = AppConstants.DATA_PROVIDER, dataProviderClass = NISPatronRegister.class)
		public void patronRegisterWithEmailWithSpecialChars(ITestContext context,Hashtable<String, String> data) throws Throwable {
			
			String testCaseNameWithInvalidData = data.get("TestCase_Description_EmailSpecialChars_C360741");
			RESTActions restActions = setupAutomationTest(context, testCaseNameWithInvalidData);
	        
			
			try {
				if (data.get("RunMode").equals("Y")) {
					  PatronRegisterPost.verifyPatronAccountWithEmailSpecialChars(data, restActions);
	                  
				}
			} catch (Exception e) {
				e.printStackTrace();
				throw new RuntimeException(e);
			} finally {
				teardownAutomationTest(context, testCaseNameWithInvalidData);

			}
		}
		
		//*******  Patron Register -  Email  Invalid Format -    ******//*
		@Test(dataProvider = AppConstants.DATA_PROVIDER, dataProviderClass = NISPatronRegister.class)
		public void patronRegisterWithEmailWithInvalidFormat(ITestContext context,Hashtable<String, String> data) throws Throwable {
			
			String testCaseNameWithInvalidData = data.get("TestCase_Description_EmailInvalidFormat_C360742");
			RESTActions restActions = setupAutomationTest(context, testCaseNameWithInvalidData);
	        
			
			try {
				if (data.get("RunMode").equals("Y")) {
					  PatronRegisterPost.verifyPatronAccountWithEmailInvalidFormat(data, restActions);
	                  
				}
			} catch (Exception e) {
				e.printStackTrace();
				throw new RuntimeException(e);
			} finally {
				teardownAutomationTest(context, testCaseNameWithInvalidData);

			}
		}
		
		//*******  Patron Register -  Security Answer too long   ******//*
		@Test(dataProvider = AppConstants.DATA_PROVIDER, dataProviderClass = NISPatronRegister.class)
		public void patronRegisterWithEmailWithSecurityAnswertoolong(ITestContext context,Hashtable<String, String> data) throws Throwable {
			
			String testCaseNameWithInvalidData = data.get("TestCase_Description_SecurityAnswertoolong_C360744");
			RESTActions restActions = setupAutomationTest(context, testCaseNameWithInvalidData);
	        
			
			try {
				if (data.get("RunMode").equals("Y")) {
					  PatronRegisterPost.verifyPatronAccountWithSecurityAnswertoolong(data, restActions);
	                  
				}
			} catch (Exception e) {
				e.printStackTrace();
				throw new RuntimeException(e);
			} finally {
				teardownAutomationTest(context, testCaseNameWithInvalidData);

			}
		}
		
		//*******  Patron Register -  IVR pin too long   ******//*
		@Test(dataProvider = AppConstants.DATA_PROVIDER, dataProviderClass = NISPatronRegister.class)
		public void patronRegisterWithEmailWithIVRPintoolong(ITestContext context,Hashtable<String, String> data) throws Throwable {
			
			String testCaseNameWithInvalidData = data.get("TestCase_Description_IVRPintoolong_C360746");
			RESTActions restActions = setupAutomationTest(context, testCaseNameWithInvalidData);
	        
			
			try {
				if (data.get("RunMode").equals("Y")) {
					  PatronRegisterPost.verifyPatronAccountWithIVRPINtoolong(data, restActions);
	                  
				}
			} catch (Exception e) {
				e.printStackTrace();
				throw new RuntimeException(e);
			} finally {
				teardownAutomationTest(context, testCaseNameWithInvalidData);

			}
		}
		
		//*******  Patron Register -  IVR pin too small   ******//*
		@Test(dataProvider = AppConstants.DATA_PROVIDER, dataProviderClass = NISPatronRegister.class)
		public void patronRegisterWithEmailWithIVRPintoosmall(ITestContext context,Hashtable<String, String> data) throws Throwable {
			
			String testCaseNameWithInvalidData = data.get("TestCase_Description_IVRPintoosmall_C360747");
			RESTActions restActions = setupAutomationTest(context, testCaseNameWithInvalidData);
	        
			
			try {
				if (data.get("RunMode").equals("Y")) {
					  PatronRegisterPost.verifyPatronAccountWithIVRPINtoosmall(data, restActions);
	                  
				}
			} catch (Exception e) {
				e.printStackTrace();
				throw new RuntimeException(e);
			} finally {
				teardownAutomationTest(context, testCaseNameWithInvalidData);

			}
		}
		
		//*******  Patron Register -  IVR pin Invalid   ******//*
		@Test(dataProvider = AppConstants.DATA_PROVIDER, dataProviderClass = NISPatronRegister.class)
		public void patronRegisterWithEmailWithIVRPinInvalid(ITestContext context,Hashtable<String, String> data) throws Throwable {
			
			String testCaseNameWithInvalidData = data.get("TestCase_Description_IVRPinInvalid_C360748");
			RESTActions restActions = setupAutomationTest(context, testCaseNameWithInvalidData);
	        
			
			try {
				if (data.get("RunMode").equals("Y")) {
					  PatronRegisterPost.verifyPatronAccountWithIVRPINInvalid(data, restActions);
	                  
				}
			} catch (Exception e) {
				e.printStackTrace();
				throw new RuntimeException(e);
			} finally {
				teardownAutomationTest(context, testCaseNameWithInvalidData);

			}
		}

	}

