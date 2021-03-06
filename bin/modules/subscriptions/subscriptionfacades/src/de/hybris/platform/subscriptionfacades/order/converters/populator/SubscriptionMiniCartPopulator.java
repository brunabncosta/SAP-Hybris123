/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.subscriptionfacades.order.converters.populator;

import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNullStandardMessage;


/**
 * Subscription mini-cart populator.
 */
public class SubscriptionMiniCartPopulator extends AbstractSubscriptionOrderPopulator<CartModel, CartData>
{
	// Concrete implementation of the SubscriptionMiniCartPopulator that should be used for further customizations

	@Override
	public void populate(@Nullable final CartModel source, @Nonnull final CartData target) throws ConversionException
	{
		validateParameterNotNullStandardMessage("target", target);

		if (source == null)
		{
			target.setTotalPrice(createZeroPrice());
			target.setDeliveryCost(null);
			target.setSubTotal(createZeroPrice());
			target.setTotalItems(0);
			target.setTotalUnitCount(Integer.valueOf(0));
		}
		else
		{
			if (source.getBillingTime() == null)
			{
				// compatibility mode: do not perform the subscription specific populator tasks
				return;
			}

			super.populate(source, target);
		}
	}

}
