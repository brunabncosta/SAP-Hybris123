/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.order;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.core.model.order.payment.PaymentInfoModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.ServicelayerTransactionalBaseTest;
import de.hybris.platform.servicelayer.model.ModelService;

import javax.annotation.Resource;

import org.junit.Test;


@IntegrationTest
public class PaymentInfoServiceTest extends ServicelayerTransactionalBaseTest
{
	@Resource
	private PaymentInfoService paymentInfoService;

	@Resource
	private ModelService modelService;

	@Test
	public void testCreatePaymentInfoForUser()
	{
		final UserModel user = modelService.create(UserModel.class);
		user.setUid("testGroup");
		user.setName("Testgroup");

		final String paymentCode = "paymentCode";

		modelService.save(user);

		final PaymentInfoModel paymentInfo = paymentInfoService.createPaymentInfoForUser(user, paymentCode);

		assertNotNull("PaymentInfo is null.", paymentInfo);
		assertTrue("PaymentInfo is not new.", modelService.isNew(paymentInfo));
		assertEquals("User differs.", user, paymentInfo.getUser());
	}
}
