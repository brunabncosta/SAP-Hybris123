/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.acceleratorfacades.ordergridform.converters.populators;

import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.commercefacades.product.data.CategoryData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.variants.model.VariantValueCategoryModel;

import org.springframework.util.Assert;


public class VariantValueParentCategoryPopulator implements Populator<CategoryModel, CategoryData>
{
	@Override
	public void populate(final CategoryModel source, final CategoryData target) throws ConversionException
	{
		Assert.notNull(source, "Parameter source cannot be null.");
		Assert.notNull(target, "Parameter target cannot be null.");

		if (source instanceof VariantValueCategoryModel)
		{
			final Integer sequence = ((VariantValueCategoryModel) source).getSequence();
			if (sequence != null)
			{
				target.setSequence(sequence.intValue());
			}
			target.setParentCategoryName(source.getSupercategories().get(0).getName());
		}
	}
}
