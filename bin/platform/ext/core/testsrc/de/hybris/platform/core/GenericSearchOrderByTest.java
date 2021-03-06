/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.core;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.testframework.Assert;

import org.junit.Test;


/**
 * Unit tests for {@link GenericSearchOrderBy}.
 */
@UnitTest
public class GenericSearchOrderByTest
{

	private static final GenericSearchField ANY_SEARCH_FIELD = new GenericSearchField("TEST");
	private static final GenericSearchField OTHER_SEARCH_FIELD = new GenericSearchField("TEST_OTHER");

	@Test
	public void sameSearchFieldShouldBeEqual()
	{
		final GenericSearchOrderBy order1 = new GenericSearchOrderBy(ANY_SEARCH_FIELD, true);
		final GenericSearchOrderBy order2 = new GenericSearchOrderBy(ANY_SEARCH_FIELD, true);

		Assert.assertEquals(order1, order2);
	}

	@Test
	public void differentSearchFieldsShouldNotBeEqual()
	{
		final GenericSearchOrderBy order1 = new GenericSearchOrderBy(ANY_SEARCH_FIELD, true);
		final GenericSearchOrderBy order2 = new GenericSearchOrderBy(OTHER_SEARCH_FIELD, true);

		Assert.assertNotEquals(order1, order2);
	}

}
