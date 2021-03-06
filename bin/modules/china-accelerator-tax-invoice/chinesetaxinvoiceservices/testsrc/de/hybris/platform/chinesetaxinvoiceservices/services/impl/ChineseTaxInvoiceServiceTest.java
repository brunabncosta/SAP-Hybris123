/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.chinesetaxinvoiceservices.services.impl;

import static org.mockito.BDDMockito.given;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.chinesetaxinvoiceservices.daos.TaxInvoiceDao;
import de.hybris.platform.chinesetaxinvoiceservices.model.TaxInvoiceModel;
import de.hybris.platform.servicelayer.model.ModelService;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


@UnitTest
public class ChineseTaxInvoiceServiceTest
{

	private ChineseTaxInvoiceService service;

	@Mock
	private TaxInvoiceDao taxInvoiceDao;
	@Mock
	private ModelService modelService;

	@Mock
	private TaxInvoiceModel invoiceModel;

	private static final String INVOICE_CODE = "00001";

	@Before
	public void prepare()
	{
		MockitoAnnotations.initMocks(this);

		service = new ChineseTaxInvoiceService();
		service.setTaxInvoiceDao(taxInvoiceDao);
		service.setModelService(modelService);

		given(taxInvoiceDao.findInvoiceByCode(INVOICE_CODE)).willReturn(invoiceModel);
	}

	@Test
	public void testGetTaxInvoiceForCode()
	{
		final TaxInvoiceModel result = service.getTaxInvoiceForCode(INVOICE_CODE);
		Assert.assertEquals(invoiceModel, result);
	}

	@Test
	public void testCreateTaxInvoiceForExisting()
	{
		given(service.getTaxInvoiceForCode(INVOICE_CODE)).willReturn(invoiceModel);
		final TaxInvoiceModel result = service.createTaxInvoice(INVOICE_CODE);
		Assert.assertEquals(invoiceModel, result);
	}

	@Test
	public void testCreateTaxInvoiceForNew()
	{
		given(modelService.create(TaxInvoiceModel.class)).willReturn(invoiceModel);
		final TaxInvoiceModel result = service.createTaxInvoice(INVOICE_CODE);
		Assert.assertEquals(invoiceModel, result);
	}
}
