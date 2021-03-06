/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.subscriptionservices.subscription.impl;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.basecommerce.util.BaseCommerceBaseTest;
import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.commerceservices.enums.SalesApplication;
import de.hybris.platform.commerceservices.order.CommerceCartModification;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.commerceservices.order.CommerceCheckoutService;
import de.hybris.platform.commerceservices.service.data.CommerceCheckoutParameter;
import de.hybris.platform.core.model.order.CartEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.OrderEntryModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.product.UnitModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.impex.constants.ImpExConstants;
import de.hybris.platform.jalo.CoreBasicDataCreator;
import de.hybris.platform.jalo.JaloSession;
import de.hybris.platform.jalo.order.price.PriceFactory;
import de.hybris.platform.order.InvalidCartException;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.product.UnitService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.site.BaseSiteService;
import de.hybris.platform.subscriptionservices.subscription.SubscriptionCommerceCartService;
import de.hybris.platform.testframework.Transactional;
import de.hybris.platform.util.AppendSpringConfiguration;
import de.hybris.platform.util.Config;

import java.util.Collection;
import java.util.Collections;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


/**
 * Integration test suite for {@link CommerceCheckoutService}.
 */
@IntegrationTest
@AppendSpringConfiguration("classpath:subscriptionservices-spring-test-context.xml")
@Transactional
public class DefaultSubscriptionCommerceCheckoutServiceIntegrationTest extends BaseCommerceBaseTest
{
	private static final Logger LOG = Logger.getLogger(DefaultSubscriptionCommerceCheckoutServiceIntegrationTest.class);
	private static final String TEST_BASESITE_UID = "testSite";

	@Resource
	private UserService userService;

	@Resource
	private CatalogVersionService catalogVersionService;

	@Resource
	private ProductService productService;

	@Resource
	private UnitService unitService;

	@Resource
	private SubscriptionCommerceCartService subscriptionCommerceCartService;

	@Resource
	private CommerceCheckoutService defaultCommerceCheckoutService;

	@Resource
	private BaseSiteService baseSiteService;

	@Resource
	private ModelService modelService;

	@Resource
	private PriceFactory testPriceFactory;

	private PriceFactory defaultPriceFactory;

	private ProductModel galaxyNexus;
	private ProductModel planStandard1Y;
	private CartModel telcoMasterCart;
	private UnitModel unitModel;

	@Before
	public void setUp() throws Exception
	{
		// final Create data for tests
		LOG.info("Creating data for DefaultSubscriptionCommerceCheckoutServiceIntegrationTest ..");

		defaultPriceFactory = JaloSession.getCurrentSession().getPriceFactory();
		JaloSession.getCurrentSession().setPriceFactory(testPriceFactory);

		userService.setCurrentUser(userService.getAdminUser());
		final long startTime = System.currentTimeMillis();
		new CoreBasicDataCreator().createEssentialData(Collections.EMPTY_MAP, null);
		// importing test csv
		final String legacyModeBackup = Config.getParameter(ImpExConstants.Params.LEGACY_MODE_KEY);
		LOG.info("Existing value for " + ImpExConstants.Params.LEGACY_MODE_KEY + " :" + legacyModeBackup);
		Config.setParameter(ImpExConstants.Params.LEGACY_MODE_KEY, "true");
		importCsv("/commerceservices/test/testCommerceCart.csv", "utf-8");
		Config.setParameter(ImpExConstants.Params.LEGACY_MODE_KEY, "false");
		importCsv("/subscriptionservices/test/testSubscriptionCommerceCartService.impex", "utf-8");
		Config.setParameter(ImpExConstants.Params.LEGACY_MODE_KEY, legacyModeBackup);

		LOG.info("Finished data for DefaultSubscriptionCommerceCheckoutServiceIntegrationTest "
				+ (System.currentTimeMillis() - startTime) + "ms");

		baseSiteService.setCurrentBaseSite(baseSiteService.getBaseSiteForUID(TEST_BASESITE_UID), false);
		catalogVersionService.setSessionCatalogVersion("testCatalog", "Online");

		modelService.detachAll();

		final UserModel telco = userService.getUserForUID("telco");
		final Collection<CartModel> cartModels = telco.getCarts();
		assertEquals("", 1, cartModels.size());
		telcoMasterCart = cartModels.iterator().next();
		unitModel = unitService.getUnitForCode("pieces");

		galaxyNexus = productService.getProductForCode("GALAXY_NEXUS");
		planStandard1Y = productService.getProductForCode("PLAN_STANDARD_1Y");
	}

	@Test
	public void testPlaceOrder() throws CommerceCartModificationException, InvalidCartException
	{
		final CommerceCartModification modProduct = subscriptionCommerceCartService.addToCart(telcoMasterCart, galaxyNexus, 1,
				unitModel, false);
		assertNotNull("", modProduct);
		final CommerceCartModification modPlan = subscriptionCommerceCartService.addToCart(telcoMasterCart, planStandard1Y, 1,
				unitModel, false);
		assertNotNull("", modPlan);

		assertEquals("", 1, telcoMasterCart.getChildren().size());
		assertEquals("", 2, telcoMasterCart.getEntries().size());
		final CartModel childCart = (CartModel) telcoMasterCart.getChildren().iterator().next();

		final CommerceCheckoutParameter parameter = new CommerceCheckoutParameter();
		parameter.setEnableHooks(true);
		parameter.setCart(telcoMasterCart);
		parameter.setSalesApplication(SalesApplication.WEB);

		final OrderModel masterOrder = defaultCommerceCheckoutService.placeOrder(parameter).getOrder();

		assertEquals("", 1, masterOrder.getChildren().size());
		assertEquals("", 2, masterOrder.getEntries().size());
		final OrderModel childOrder = (OrderModel) masterOrder.getChildren().iterator().next();

		assertEquals("", masterOrder, childOrder.getParent());
		assertTrue("", masterOrder.getChildren().contains(childOrder));
		assertEquals("", masterOrder.getDate(), childOrder.getDate());

		compareCartToOrder(telcoMasterCart, masterOrder, true);
		compareCartToOrder(childCart, childOrder, false);
	}

	@Test(expected = InvalidCartException.class)
	public void testPlaceOrderWithChildCart() throws CommerceCartModificationException, InvalidCartException
	{
		final CommerceCartModification modProduct = subscriptionCommerceCartService.addToCart(telcoMasterCart, galaxyNexus, 1,
				unitModel, false);
		assertNotNull("", modProduct);
		final CommerceCartModification modPlan = subscriptionCommerceCartService.addToCart(telcoMasterCart, planStandard1Y, 1,
				unitModel, false);
		assertNotNull("", modPlan);

		assertEquals("", 1, telcoMasterCart.getChildren().size());
		assertEquals("", 2, telcoMasterCart.getEntries().size());
		final CartModel childCart = (CartModel) telcoMasterCart.getChildren().iterator().next();

		//assigning child cart for placing the order
		final CommerceCheckoutParameter parameter = new CommerceCheckoutParameter();
		parameter.setEnableHooks(true);
		parameter.setCart(childCart);
		parameter.setSalesApplication(SalesApplication.WEB);

		defaultCommerceCheckoutService.placeOrder(parameter);
	}

	private void compareCartToOrder(final CartModel cartModel, final OrderModel orderModel, final boolean isMasterCart)
	{
		assertEquals("", cartModel.getCalculated(), orderModel.getCalculated());
		assertEquals("", cartModel.getTotalPrice(), orderModel.getTotalPrice());
		assertEquals("", cartModel.getSubtotal(), orderModel.getSubtotal());
		assertEquals("", cartModel.getTotalDiscounts(), orderModel.getTotalDiscounts());
		assertEquals("", cartModel.getTotalTax(), orderModel.getTotalTax());
		assertEquals("", cartModel.getUser(), orderModel.getUser());
		if (isMasterCart)
		{
			assertEquals("", 2, cartModel.getEntries().size());
		}
		else
		{
			assertEquals("", 1, cartModel.getEntries().size());
		}
		assertEquals("", cartModel.getEntries().size(), orderModel.getEntries().size());
		assertEquals("", cartModel.getBillingTime(), orderModel.getBillingTime());
		assertEquals("", cartModel.getChildren().size(), orderModel.getChildren().size());
		assertEquals("", cartModel.getEntries().size(), orderModel.getEntries().size());

		final CartEntryModel cartEntry = (CartEntryModel) cartModel.getEntries().iterator().next();
		final OrderEntryModel orderEntry = (OrderEntryModel) orderModel.getEntries().iterator().next();

		assertEquals("", cartEntry.getProduct(), orderEntry.getProduct());
		assertEquals("", cartEntry.getCalculated(), orderEntry.getCalculated());
		assertEquals("", cartEntry.getTotalPrice(), orderEntry.getTotalPrice());
		assertEquals("", cartEntry.getBasePrice(), orderEntry.getBasePrice());
		assertEquals("", cartEntry.getQuantity(), orderEntry.getQuantity());
		assertEquals("", cartEntry.getEntryNumber(), orderEntry.getEntryNumber());
		assertEquals("", cartEntry.getOrder().getBillingTime(), orderEntry.getOrder().getBillingTime());
		if (isMasterCart)
		{
			assertNull("", orderEntry.getMasterEntry());
		}
		else
		{
			assertNotNull("", orderEntry.getMasterEntry());
			assertEquals("", orderEntry.getProduct(), orderEntry.getMasterEntry().getProduct());
			assertEquals("", orderEntry.getQuantity(), orderEntry.getMasterEntry().getQuantity());
		}
	}

	@After
	public void tearDown()
	{
		JaloSession.getCurrentSession().setPriceFactory(defaultPriceFactory);
	}
}
