/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.util.StringByStringTokenizer;

import org.junit.Test;


/**
 * Testcase for the StringByStringTokenizerTest.
 */
@UnitTest
public class StringByStringTokenizerTest
{
	@Test
	public void testNextToken() throws Exception
	{
		StringByStringTokenizer tokenizer = new StringByStringTokenizer("blubblahblub", "blah", false);
		assertEquals("First token must be \"blub\"", "blub", tokenizer.nextToken());
		assertEquals("Second token must be \"blub\"", "blub", tokenizer.nextToken());
		assertFalse("Cannot have more tokens", tokenizer.hasMoreTokens());

		tokenizer = new StringByStringTokenizer("blubblahblub", "blah", true);
		assertEquals("First token must be \"blub\"", "blub", tokenizer.nextToken());
		assertEquals("Second token must be \"blah\"", "blah", tokenizer.nextToken());
		assertEquals("Third token must be \"blub\"", "blub", tokenizer.nextToken());
		assertFalse("Cannot have more tokens", tokenizer.hasMoreTokens());

		tokenizer = new StringByStringTokenizer("blahblubblah", "blah", false);
		assertEquals("First token must be \"blub\"", "blub", tokenizer.nextToken());
		assertFalse("Cannot have more tokens", tokenizer.hasMoreTokens());

		tokenizer = new StringByStringTokenizer("blahblahblubblahblah", "blah", false);
		assertEquals("First token must be \"blub\"", "blub", tokenizer.nextToken());
		assertFalse("Cannot have more tokens", tokenizer.hasMoreTokens());
	}

	@Test
	public void testHasMoreTokens() throws Exception
	{
		final StringByStringTokenizer tokenizer = new StringByStringTokenizer("blahblahblah", "blah", false);
		assertFalse("Cannot have tokens", tokenizer.hasMoreTokens());
	}

}
