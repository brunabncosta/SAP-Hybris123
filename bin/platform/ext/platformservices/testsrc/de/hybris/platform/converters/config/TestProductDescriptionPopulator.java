/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.converters.config;

import de.hybris.platform.converters.Populator;
import de.hybris.platform.converters.data.TestProductData;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;


/**
 * Product populator for testing purposes. Only populates the product description.
 */
public class TestProductDescriptionPopulator implements Populator<ProductModel, TestProductData>
{
	@Override
	public void populate(final ProductModel source, final TestProductData target) throws ConversionException
	{
		target.setDescription(source.getDescription());
	}

}
