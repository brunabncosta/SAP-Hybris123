/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.  All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.promotionengineservices.promotionengine.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.catalog.model.classification.ClassificationSystemVersionModel;
import de.hybris.platform.category.CategoryService;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.promotions.model.PromotionResultModel;
import de.hybris.platform.ruleengineservices.rule.data.RuleParameterData;

import java.util.Arrays;
import java.util.Collection;
import java.util.Locale;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;


@UnitTest
public class DefaultCategoryResolutionStrategyTest
{


	private static final String CATEGORY_CODE = "234";
	private static final String CATEGORY_NAME = "The Category";
	private static final String CLASSIFICATION_NAME = "The Classification";

	@Rule
	public final ExpectedException expectedException = ExpectedException.none(); //NOPMD

	@InjectMocks
	private DefaultCategoryResolutionStrategy strategy;

	@Mock
	private PromotionResultModel promotionResult;

	@Mock
	private RuleParameterData data;

	@Mock
	private CategoryService categoryService;

	@Mock
	private CategoryModel category1;

	@Mock
	private CatalogVersionModel catalogVersion;

	@Mock
	private CategoryModel category2;

	@Mock
	private ClassificationSystemVersionModel classificationSystemVersion;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void testNullDataValue()
	{
		// given
		BDDMockito.given(data.getValue()).willReturn(null);

		//when
		final String result = strategy.getValue(data, promotionResult, Locale.US);

		// then
		Assert.assertNull(result);
	}

	@Test
	public void testWrongDataValueType()
	{
		// given
		BDDMockito.given(data.getValue()).willReturn(Integer.valueOf(2));

		//expect
		expectedException.expect(ClassCastException.class);

		//when
		strategy.getValue(data, promotionResult, Locale.US);

	}

	@Test
	public void testCategoryResolutionWithFiltering()
	{
		//given
		final Collection<CategoryModel> categories = Arrays.asList(category1, category2);
		BDDMockito.given(data.getValue()).willReturn(CATEGORY_CODE);
		BDDMockito.given(category1.getName()).willReturn(CATEGORY_NAME);
		BDDMockito.given(category1.getCatalogVersion()).willReturn(catalogVersion);
		BDDMockito.given(category2.getName()).willReturn(CLASSIFICATION_NAME);
		BDDMockito.given(category2.getCatalogVersion()).willReturn(classificationSystemVersion);
		BDDMockito.given(categoryService.getCategoriesForCode(Mockito.anyString())).willReturn(categories);

		//when
		final String result = strategy.getValue(data, promotionResult, Locale.US);

		//then
		Assert.assertEquals(CATEGORY_NAME, result);

	}

}
