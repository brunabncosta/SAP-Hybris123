/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.commercefacades.order.converters.populator;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.order.data.DeliveryModeData;
import de.hybris.platform.commerceservices.util.ConverterFactory;
import de.hybris.platform.converters.impl.AbstractPopulatingConverter;
import de.hybris.platform.core.model.order.delivery.DeliveryModeModel;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


@UnitTest
public class DeliveryModePopulatorTest
{

	private final AbstractPopulatingConverter<DeliveryModeModel, DeliveryModeData> deliveryModeConverter = new ConverterFactory<DeliveryModeModel, DeliveryModeData, DeliveryModePopulator>()
			.create(DeliveryModeData.class, new DeliveryModePopulator());

	@Before
	public void setUp()
	{
		//Do Nothing
	}

	@Test
	public void testConvert()
	{
		final DeliveryModeModel deliveryModeModel = mock(DeliveryModeModel.class);
		given(deliveryModeModel.getCode()).willReturn("code");
		given(deliveryModeModel.getName()).willReturn("name");
		given(deliveryModeModel.getDescription()).willReturn("desc");
		final DeliveryModeData deliveryModeData = deliveryModeConverter.convert(deliveryModeModel);
		Assert.assertEquals("code", deliveryModeData.getCode());
		Assert.assertEquals("name", deliveryModeData.getName());
		Assert.assertEquals("desc", deliveryModeData.getDescription());
	}
}
