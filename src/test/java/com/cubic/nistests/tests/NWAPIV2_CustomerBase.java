package com.cubic.nistests.tests;

import com.cubic.accelerators.RESTEngine;

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

}
