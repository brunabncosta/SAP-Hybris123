/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.util;

import static org.assertj.core.api.Assertions.assertThat;

import de.hybris.bootstrap.annotations.UnitTest;

import org.junit.Test;


@UnitTest
public class TokenUnitTest
{

	@Test
	public void shouldGenerateNotEmptyTokenWithHexDigitsOnly()
	{
		final Token givenToken = Token.generateNew();

		assertThat(givenToken).isNotNull();

		final String stringValue = givenToken.stringValue();
		assertThat(stringValue).isNotEmpty();
		assertThat("01234567890abcdef".toCharArray()).contains(stringValue.toCharArray());
	}

	@Test
	public void shouldVerifyValidToken()
	{
		final Token givenToken = Token.generateNew();

		assertThat(givenToken.verify(givenToken.stringValue())).isTrue();
	}

	@Test
	public void shouldNotVerifyInvalidToken()
	{
		final Token givenToken = Token.generateNew();

		assertThat(givenToken.verify(null)).isFalse();
		assertThat(givenToken.verify("")).isFalse();
		assertThat(givenToken.verify(" ")).isFalse();
		assertThat(givenToken.verify("123abc")).isFalse();
		assertThat(givenToken.verify("ZŁO")).isFalse();
	}

}
