/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.catalog.dynamic;

import de.hybris.platform.core.model.user.AddressModel;

import java.util.Collection;
import java.util.Iterator;


public class CompanyContactAddressesAttributeHandler extends AbstractCompanyAddressAttributeHandler
{
	@Override
	public void filterOutAddresses(final Collection<AddressModel> addresses)
	{
		for (final Iterator<AddressModel> it = addresses.iterator(); it.hasNext(); )
		{
			final AddressModel address = it.next();
			if (address.getContactAddress() == null || !address.getContactAddress())
			{
				it.remove();
			}
		}
	}
}
