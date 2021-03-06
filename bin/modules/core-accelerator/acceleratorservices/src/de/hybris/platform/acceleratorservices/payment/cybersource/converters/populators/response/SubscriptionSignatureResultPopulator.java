/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.acceleratorservices.payment.cybersource.converters.populators.response;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;

import de.hybris.platform.acceleratorservices.payment.data.CreateSubscriptionResult;
import de.hybris.platform.acceleratorservices.payment.data.SubscriptionSignatureData;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import java.util.Map;


public class SubscriptionSignatureResultPopulator extends AbstractResultPopulator<Map<String, String>, CreateSubscriptionResult>
{
	@Override
	public void populate(final Map<String, String> source, final CreateSubscriptionResult target) throws ConversionException
	{
		validateParameterNotNull(source, "Parameter [Map<String, String>] source cannot be null");
		validateParameterNotNull(target, "Parameter [CreateSubscriptionResult] target cannot be null");

		final SubscriptionSignatureData data = new SubscriptionSignatureData();
		data.setRecurringSubscriptionInfoAmount(getBigDecimalForString(source.get("recurringSubscriptionInfo_amount")));
		data.setRecurringSubscriptionInfoAmountPublicSignature(source.get("recurringSubscriptionInfo_amountPublicSignature"));
		data.setRecurringSubscriptionInfoAutomaticRenew(Boolean.valueOf(source.get("recurringSubscriptionInfo_automaticRenew")));
		data.setRecurringSubscriptionInfoAutomaticRenewPublicSignature(source
				.get("recurringSubscriptionInfo_automaticRenewPublicSignature"));
		data.setRecurringSubscriptionInfoFrequency(source.get("recurringSubscriptionInfo_frequency"));
		data.setRecurringSubscriptionInfoFrequencyPublicSignature(source.get("recurringSubscriptionInfo_frequencyPublicSignature"));
		data.setRecurringSubscriptionInfoNumberOfPayments(getIntegerForString(source
				.get("recurringSubscriptionInfo_numberOfPayments")));
		data.setRecurringSubscriptionInfoNumberOfPaymentsPublicSignature(source
				.get("recurringSubscriptionInfo_numberOfPaymentsPublicSignature"));
		data.setRecurringSubscriptionInfoStartDate(source.get("recurringSubscriptionInfo_startDate"));
		data.setRecurringSubscriptionInfoStartDatePublicSignature(source.get("recurringSubscriptionInfo_startDatePublicSignature"));

		target.setSubscriptionSignatureData(data);
	}
}
