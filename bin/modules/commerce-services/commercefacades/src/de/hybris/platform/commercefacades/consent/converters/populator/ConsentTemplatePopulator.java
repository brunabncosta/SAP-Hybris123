/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.commercefacades.consent.converters.populator;

import de.hybris.platform.commercefacades.consent.data.ConsentData;
import de.hybris.platform.commercefacades.consent.data.ConsentTemplateData;
import de.hybris.platform.commerceservices.consent.CommerceConsentService;
import de.hybris.platform.commerceservices.model.consent.ConsentModel;
import de.hybris.platform.commerceservices.model.consent.ConsentTemplateModel;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.user.UserService;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.Assert;


/**
 * Default populator that converts source {@link ConsentTemplateModel} to target {@link ConsentTemplateData}*
 */
public class ConsentTemplatePopulator implements Populator<ConsentTemplateModel, ConsentTemplateData>
{
	private UserService userService;

	private CommerceConsentService commerceConsentService;

	private Converter<ConsentModel, ConsentData> consentConverter;

	@Override
	public void populate(final ConsentTemplateModel source, final ConsentTemplateData target)
	{
		Assert.notNull(source, "Parameter source cannot be null.");
		Assert.notNull(target, "Parameter target cannot be null.");

		target.setId(source.getId());
		target.setName(source.getName());
		target.setDescription(source.getDescription());
		target.setVersion(source.getVersion());
		target.setExposed(source.isExposed());
		populateUserConsent(source, target);
	}

	protected void populateUserConsent(final ConsentTemplateModel source, final ConsentTemplateData target)
	{
		final UserModel userModel = getUserService().getCurrentUser();
		if (userModel instanceof CustomerModel)
		{
			final CustomerModel customer = (CustomerModel) userModel;
			Optional.ofNullable(getCommerceConsentService().getActiveConsent(customer, source))
					.ifPresent(consent -> target.setConsentData(getConsentConverter().convert(consent)));
		}
	}

	protected UserService getUserService()
	{
		return userService;
	}

	@Required
	public void setUserService(final UserService userService)
	{
		this.userService = userService;
	}

	protected CommerceConsentService getCommerceConsentService()
	{
		return commerceConsentService;
	}

	@Required
	public void setCommerceConsentService(final CommerceConsentService commerceConsentService)
	{
		this.commerceConsentService = commerceConsentService;
	}

	protected Converter<ConsentModel, ConsentData> getConsentConverter()
	{
		return consentConverter;
	}

	@Required
	public void setConsentConverter(final Converter<ConsentModel, ConsentData> consentConverter)
	{
		this.consentConverter = consentConverter;
	}
}
