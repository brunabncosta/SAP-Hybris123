/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.b2bacceleratorservices.strategies.impl;

import de.hybris.platform.b2bacceleratorservices.strategies.B2BApprovalProcessLookUpStrategy;
import de.hybris.platform.store.BaseStoreModel;

import java.util.Map;

import org.springframework.beans.factory.annotation.Required;


/**
 * @deprecated Since 6.0. Use {@link de.hybris.platform.b2b.strategies.impl.DefaultB2BApprovalProcessLookUpStrategy}
 *             instead.
 *
 */
@Deprecated(since = "6.0", forRemoval = true)
public class DefaultB2BApprovalProcessLookUpStrategy implements B2BApprovalProcessLookUpStrategy
{

	private Map<String, String> processes;

	@Override
	public Map<String, String> getProcesses(final BaseStoreModel store)
	{
		//TODO: pull the list from the baseStore.
		return processes;
	}

	@Required
	public void setProcesses(final Map<String, String> processes)
	{
		this.processes = processes;
	}
}
