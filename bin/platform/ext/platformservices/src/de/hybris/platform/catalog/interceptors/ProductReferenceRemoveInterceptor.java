/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.catalog.interceptors;

import de.hybris.platform.catalog.model.ProductReferenceModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.interceptor.InterceptorContext;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;
import de.hybris.platform.servicelayer.interceptor.PersistenceOperation;
import de.hybris.platform.servicelayer.interceptor.RemoveInterceptor;

import java.util.Date;


public class ProductReferenceRemoveInterceptor implements RemoveInterceptor<ProductReferenceModel>
{

	@Override
	public void onRemove(final ProductReferenceModel productReferenceModel, final InterceptorContext ctx)
			throws InterceptorException
	{

		if (!ctx.isNew(productReferenceModel))
		{
			final ProductModel source = productReferenceModel.getSource();
			if (source != null)
			{
				if (!ctx.getAllRegisteredElements().contains(source))
				{
					source.setModifiedtime(new Date());
					ctx.registerElementFor(source, PersistenceOperation.SAVE);
				}
			}
		}
	}
}
