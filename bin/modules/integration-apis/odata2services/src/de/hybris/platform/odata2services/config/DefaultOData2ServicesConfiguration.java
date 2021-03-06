/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */

package de.hybris.platform.odata2services.config;

import de.hybris.platform.integrationservices.util.Log;
import de.hybris.platform.servicelayer.config.ConfigurationService;

import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;

import org.apache.commons.configuration.ConversionException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Required;

public class DefaultOData2ServicesConfiguration implements ODataServicesConfiguration
{
	private static final Logger LOG = Log.getLogger(DefaultOData2ServicesConfiguration.class);
	private static final int BATCH_LIMIT_FALLBACK_VALUE = 200;
	private static final String BATCH_LIMIT_PROPERTY_KEY = "odata2services.batch.limit";
	private static final int MAX_PAGE_SIZE_FALLBACK_VALUE = 1000;
	private static final int DEFAULT_PAGE_SIZE_FALLBACK_VALUE = 10;
	private static final String MAX_PAGE_SIZE_PROPERTY = "odata2services.page.size.max";
	private static final String DEFAULT_PAGE_SIZE_PROPERTY = "odata2services.page.size.default";
	private static final String EXPORTABLE_INTEGRATION_OBJECTS = "odata2services.exportable.integration.objects";
	private static final String DELIMITER = ",";

	private ConfigurationService configurationService;

	protected ConfigurationService getConfigurationService()
	{
		return configurationService;
	}

	@Required
	public void setConfigurationService(final ConfigurationService configurationService)
	{
		this.configurationService = configurationService;
	}

	/**
	 * Determines current setting of the maximum number of batches allowed in an inbound request body.
	 *
	 * @return number of batches that a single request may have.
	 */
	public int getBatchLimit()
	{
		return getIntegerValue(BATCH_LIMIT_PROPERTY_KEY, BATCH_LIMIT_FALLBACK_VALUE);
	}

	/**
	 * Specifies new limit on number of batches in a single inbound request.
	 *
	 * @param maxNumber max number of batches a single inbound request may have.
	 */
	public void setBatchLimit(final int maxNumber)
	{
		getConfigurationService().getConfiguration().setProperty(BATCH_LIMIT_PROPERTY_KEY, String.valueOf(maxNumber));
	}

	@Override
	public int getMaxPageSize()
	{
		return getIntegerValue(MAX_PAGE_SIZE_PROPERTY, MAX_PAGE_SIZE_FALLBACK_VALUE);
	}

	@Override
	public int getDefaultPageSize()
	{
		final int defaultValue = getIntegerValue(DEFAULT_PAGE_SIZE_PROPERTY, DEFAULT_PAGE_SIZE_FALLBACK_VALUE);
		final int maxPageSize = getMaxPageSize();
		if (defaultValue > maxPageSize)
		{
			LOG.warn("Default page size exceeds the maximum value of {}. Setting default page size configure property {} to the maximum value {}. To increase the maximum value, please configure property {}.", maxPageSize, DEFAULT_PAGE_SIZE_PROPERTY, maxPageSize, MAX_PAGE_SIZE_PROPERTY);
			getConfigurationService().getConfiguration().setProperty(DEFAULT_PAGE_SIZE_PROPERTY, Integer.toString(maxPageSize));
			return maxPageSize;
		}
		return defaultValue;
	}

	private int getIntegerValue(final String propertyKey, final int propertyFallbackValue)
	{
		try
		{
			final int limit = getConfigurationService().getConfiguration().getInt(propertyKey);
			if (limit <= 0)
			{
				LOG.warn("Property '{}' was not configured correctly. Using fallback value '{}'.", propertyKey, propertyFallbackValue);
				return propertyFallbackValue;
			}
			return limit;
		}
		catch (final NoSuchElementException | ConversionException e)
		{
			LOG.warn("Property '{}' was not configured or not configured correctly. Using fallback value '{}'.", propertyKey, propertyFallbackValue, e);
			return propertyFallbackValue;
		}
	}

	@Override
	public List<String> getExportableIntegrationObjects()
	{
		final String property = getConfigurationService().getConfiguration().getString(EXPORTABLE_INTEGRATION_OBJECTS);
		return StringUtils.isNotBlank(property) ? List.of(property.split(DELIMITER)) : Collections.emptyList();
	}

	@Override
	public void setExportableIntegrationObjects(final List<String> exportableIos)
	{
		getConfigurationService().getConfiguration().setProperty(EXPORTABLE_INTEGRATION_OBJECTS, String.join(DELIMITER, exportableIos));
	}

}
