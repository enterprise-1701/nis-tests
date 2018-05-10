package com.cubic.nistests.tests;

import java.util.Hashtable;

import org.testng.ITestContext;
import org.testng.annotations.Test;

import com.cubic.nisjava.constants.AppConstants;
import com.cubic.nisjava.dataproviders.NISDataProviderSource;

public class EULAAPIBadIdLocaleCombo extends EULAAPIBad {

	/**
	 * Try to Get a EULA using a non-existent combination of eulaId and locale,
	 * i.e., the eualId refers to a real document which doesn't have the given
	 * locale.
	 * 
	 * testRailId: 698268
	 * 
	 * There's a defect assoc'd with this Test Case
	 * 
	 *  @see https://jira.cts.cubic.cub/browse/CCBO-13057
	 * 
	 * @param context 	The TestNG context reference.
	 * @param data		The test data.
	 * @throws Throwable Thrown if something goes wrong.
	 */
	@Test(dataProvider = AppConstants.DATA_PROVIDER, dataProviderClass = NISDataProviderSource.class)
	public void tryToGetEULAWithBadIdAndLocaleCombination(ITestContext context, Hashtable<String, String> data) throws Throwable {
		mainTest( context, data );
	}
}
