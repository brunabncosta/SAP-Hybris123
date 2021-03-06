/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.personalizationyprofile.mapper.affinity.impl;

import de.hybris.platform.personalizationyprofile.mapper.affinity.CxConsumptionLayerAffinityStrategy;
import de.hybris.platform.personalizationyprofile.yaas.Affinity;

import java.math.BigDecimal;
import java.util.Optional;


/**
 * Returns recent score as current affinity.
 *
 * @deprecated since 2011, Profile structure has changed and code is no longer valid. Mappers which have used that
 *             strategy are deprecated
 */
@Deprecated(since = "2011", forRemoval = true)
public class CxConsumptionLayerCurrentAffinityStrategy implements CxConsumptionLayerAffinityStrategy
{
	@Override
	public BigDecimal extract(final Affinity affinity)
	{
		return Optional.ofNullable(affinity)//
				.map(Affinity::getRecentScore)//
				.orElse(BigDecimal.ZERO);
	}

}
