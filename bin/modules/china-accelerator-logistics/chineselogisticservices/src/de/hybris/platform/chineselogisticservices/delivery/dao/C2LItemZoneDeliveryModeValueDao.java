/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.chineselogisticservices.delivery.dao;

import de.hybris.platform.core.model.c2l.C2LItemModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.delivery.DeliveryModeModel;
import de.hybris.platform.deliveryzone.model.ZoneDeliveryModeValueModel;
import de.hybris.platform.servicelayer.internal.dao.Dao;


/**
 * Looking up ZoneDeliveryModeValue in the database
 */
public interface C2LItemZoneDeliveryModeValueDao extends Dao
{
	/**
	 * Getting DeliveryModeValue by C2LItem
	 *
	 * @param c2LItem
	 * @param order
	 * @param deliveryMode
	 * @return Collection<DeliveryModeModel>
	 */
	ZoneDeliveryModeValueModel findDeliveryModeValueByC2LItem(final C2LItemModel c2LItem, AbstractOrderModel order,
			DeliveryModeModel deliveryMode);
}
