/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.customercouponservices.thread.factory;

import de.hybris.platform.core.Registry;
import de.hybris.platform.core.Tenant;
import de.hybris.platform.core.threadregistry.RegistrableThread;
import de.hybris.platform.jalo.JaloSession;
import de.hybris.platform.notificationservices.thread.factory.NotificationThreadFactory;

import java.util.concurrent.ThreadFactory;



/**
 * Creates threads in the current tenant
 * 
 * @deprecated since 1905 Use {@link NotificationThreadFactory} instead.
 */
@Deprecated(since = "1905", forRemoval= true )
public class CouponNotificationThreadFactory implements ThreadFactory
{
	private final Tenant currentTenant;

	public CouponNotificationThreadFactory()
	{
		currentTenant = Registry.getCurrentTenant();
	}

	@Override
	public Thread newThread(final Runnable runnable)
	{
		return new RegistrableThread()
		{
			@Override
			public void internalRun()
			{
				try
				{
					Registry.setCurrentTenant(currentTenant);
					JaloSession.getCurrentSession().activate();
					runnable.run();
				}
				finally
				{
					JaloSession.getCurrentSession().close();
					JaloSession.deactivate();
					Registry.unsetCurrentTenant();
				}
			}
		};
	}

}
