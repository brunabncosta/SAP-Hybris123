/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.order.strategies.calculation.pdt.query;

import de.hybris.platform.europe1.jalo.PDTRowsQueryBuilder;
import de.hybris.platform.order.strategies.calculation.pdt.criteria.PDTCriteria;
import de.hybris.platform.order.strategies.calculation.pdt.repository.impl.GenericPDTRowRepository;


/**
 * The Interface for providing queries for price/discount/tax {@link GenericPDTRowRepository}.
 *
 * @param <CRITERIA> the generic type for price/discount/tax criteria.
 */
public interface PDTQueryProvider<CRITERIA extends PDTCriteria>
{

	/**
	 * Generates query for price/discount/tax criteria.
	 *
	 * @param criteria the price/discount/tax criteria.
	 * @return the price/discount/tax rows query builder with params.
	 */
	PDTRowsQueryBuilder.QueryWithParams query(CRITERIA criteria);
}
