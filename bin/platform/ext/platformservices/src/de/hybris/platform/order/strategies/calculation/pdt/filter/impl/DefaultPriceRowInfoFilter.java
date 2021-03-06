/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.order.strategies.calculation.pdt.filter.impl;

import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.europe1.enums.PriceRowChannel;
import de.hybris.platform.europe1.model.PriceRowModel;
import de.hybris.platform.order.strategies.calculation.pdt.criteria.PriceValueInfoCriteria;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;


public class DefaultPriceRowInfoFilter extends DefaultPriceRowValueFilter
{

	@Override
	public Collection<PriceRowModel> filter(final Collection<PriceRowModel> collection, final PriceValueInfoCriteria criteria)
	{
		if (collection.isEmpty())
		{
			return Collections.emptyList();
		}

		final CurrencyModel base = getBaseCurrencyModel(criteria.getCurrency());
		final List<PriceRowModel> ret = new ArrayList<>(collection);
		boolean hasChannelRowMatching = false;
		final PriceRowChannel channel = retrieveChannelStrategy.getChannel(null);

		for (final ListIterator<PriceRowModel> it = ret.listIterator(); it.hasNext(); )
		{
			final PriceRowValidator priceRowV = new PriceRowValidator(it.next());
			boolean isPriceRowInvalid = priceRowV.isCurrencyInvalid(criteria.getCurrency(), base);
			isPriceRowInvalid = isPriceRowInvalid || priceRowV.isDateRangeInvalid(criteria.getDate());
			isPriceRowInvalid = isPriceRowInvalid || priceRowV.isGiveAwayModeInvalid(criteria.isGiveAwayMode());
			isPriceRowInvalid = isPriceRowInvalid || priceRowV.isChannelInvalid(channel);
			if (isPriceRowInvalid)
			{
				it.remove();
				continue;
			}
			hasChannelRowMatching = setupHasChannelRowMatching(channel, hasChannelRowMatching, priceRowV);
		}
		// if there is a possibility to have price row without channel
		removePriceRowsWithoutChannel(ret, hasChannelRowMatching);
		return ret;
	}

}
