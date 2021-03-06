/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.commerceservices.search.solrfacetsearch.provider.entity;

import de.hybris.platform.variants.model.VariantValueCategoryModel;
import de.hybris.platform.category.CategoryService;
import de.hybris.platform.category.model.CategoryModel;

import java.util.Comparator;
import java.util.LinkedList;

import org.springframework.beans.factory.annotation.Required;


/**
 * Comparator for type {@link VariantValueCategoryModel}.
 */
public class VariantValueCategoryModelPriorityComparator implements Comparator<VariantValueCategoryModel>
{
	private CategoryService categoryService;

	@Override
	public int compare(final VariantValueCategoryModel variantValueCategory1, final VariantValueCategoryModel variantValueCategory2)
	{
		final LinkedList<CategoryModel> pathToRoot1 = getPathToRoot(variantValueCategory1);
		final LinkedList<CategoryModel> pathToRoot2 = getPathToRoot(variantValueCategory2);

		return Integer.compare(pathToRoot1.size(), pathToRoot2.size());
	}

	protected LinkedList<CategoryModel> getPathToRoot(final VariantValueCategoryModel variantValueCategory)
	{
		final LinkedList<CategoryModel> pathToRoot = new LinkedList<>(getCategoryService().getPathForCategory(variantValueCategory));

		while (!getCategoryService().isRoot(pathToRoot.get(0)))
		{
			pathToRoot.addAll(0, getCategoryService().getPathForCategory(pathToRoot.get(0)));
		}

		return pathToRoot;
	}

	public CategoryService getCategoryService()
	{
		return categoryService;
	}

	@Required
	public void setCategoryService(final CategoryService categoryService)
	{
		this.categoryService = categoryService;
	}
}
