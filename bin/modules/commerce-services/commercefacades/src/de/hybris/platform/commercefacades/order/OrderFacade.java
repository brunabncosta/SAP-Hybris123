/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.commercefacades.order;

import de.hybris.platform.commercefacades.order.data.OrderData;
import de.hybris.platform.commercefacades.order.data.OrderHistoryData;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.core.enums.OrderStatus;

import java.util.List;


/**
 * Order facade interface. An Order Facade should provide access to a user's order history and full details of an order.
 */
public interface OrderFacade
{
	/**
	 * Returns the detail of an Order.
	 *
	 * @param code
	 *           The code of the Order for which to retrieve the detail.
	 * @return The detail of the order with matching code
	 */
	OrderData getOrderDetailsForCode(String code);

	/**
	 * Gets the order details for GUID.
	 *
	 * @param guid
	 *           The guid of the Order for which to retrieve the detail.
	 * @return the order details for GUID
	 */
	OrderData getOrderDetailsForGUID(final String guid);

	/**
	 * Returns the order history of the current user for given statuses.
	 *
	 * @param statuses
	 *           array of order statuses to filter the results
	 * @return The order history of the current user.
	 */
	List<OrderHistoryData> getOrderHistoryForStatuses(OrderStatus... statuses);

	/**
	 * Returns the order history of the current user for given statuses.
	 *
	 * @param pageableData
	 *           paging information
	 * @param statuses
	 *           array of order statuses to filter the results
	 * @return The order history of the current user.
	 */
	SearchPageData<OrderHistoryData> getPagedOrderHistoryForStatuses(PageableData pageableData, OrderStatus... statuses);

	/**
	 * Returns the order details for order code.
	 *
	 * @param code
	 *           order code
	 * @return the order details for code without user
	 */
	OrderData getOrderDetailsForCodeWithoutUser(String code);
}
