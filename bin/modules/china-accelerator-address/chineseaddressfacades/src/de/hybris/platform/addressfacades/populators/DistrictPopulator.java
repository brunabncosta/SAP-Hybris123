/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.addressfacades.populators;

import de.hybris.platform.addressfacades.data.DistrictData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.c2l.C2LItemModel;
import de.hybris.platform.servicelayer.i18n.I18NService;

import javax.annotation.Resource;

import org.springframework.util.Assert;


public class DistrictPopulator implements Populator<C2LItemModel, DistrictData>
{
	@Resource(name = "i18NService")
	private I18NService i18NService;

	@Override
	public void populate(final C2LItemModel source, final DistrictData target)
	{
		Assert.notNull(source, "Parameter source cannot be null.");
		Assert.notNull(target, "Parameter target cannot be null.");

		target.setCode(source.getIsocode());
		target.setName(source.getName(i18NService.getCurrentLocale()));
	}

}
