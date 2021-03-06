/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.xyformsbackoffice.widgets.yformbuilder;

import de.hybris.platform.xyformsservices.exception.YFormServiceException;
import de.hybris.platform.xyformsservices.form.YFormService;
import de.hybris.platform.xyformsservices.model.YFormDefinitionModel;

import java.net.MalformedURLException;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.URIEvent;
import org.zkoss.zul.Div;
import org.zkoss.zul.Iframe;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Messagebox.ClickEvent;
import org.zkoss.zul.Window;

import com.hybris.backoffice.widgets.notificationarea.event.NotificationEvent;
import com.hybris.backoffice.widgets.notificationarea.NotificationService;
import com.hybris.cockpitng.annotations.SocketEvent;
import com.hybris.cockpitng.util.DefaultWidgetController;



/**
 * Displays Form Builder inside containing widget
 */
public class YFormBuilderController extends DefaultWidgetController
{
	private static final Logger LOG = Logger.getLogger(YFormBuilderController.class);

	protected static final String ORBEON_APPLICATION_ID = "orbeon";
	protected static final String ORBEON_FORM_BUILDER_ID = "builder";

	protected static final String ORBEON_BUILDER_EDIT_ADDRESS = "/orbeon/fr/orbeon/builder/edit/";

	protected static final String YFORM_DEFINITION_SOCKET_IN = "yformDefinition";
	protected static final String YFORM_DEFINITION_SOCKET_OUT = "yformDefinition";

	public static final String GENERIC_YFORM_DEFINITION_ERROR = "notificationYFormDefinitionError";

	private Div div;

	@Resource(name = "yformService")
	private transient YFormService yformService;

	@Resource(name = "notificationService")
	private transient NotificationService notificationService;

	@Override
	public void initialize(final Component component)
	{
		super.initialize(component);

		div = (Div) component;
	}

	/**
	 * Gets the current widget's Window.
	 *
	 * @param component
	 */
	protected Window getWindow(final Component component)
	{
		Component c = component;
		while (c.getParent() != null)
		{
			if (c instanceof Window)
			{
				return (Window) c;
			}
			c = c.getParent();
		}
		return null;
	}

	/**
	 * Displays the Window that contains the Form Builder.
	 *
	 * @param yformDefinition
	 * @throws MalformedURLException
	 */
	@SocketEvent(socketId = YFORM_DEFINITION_SOCKET_IN)
	public void show(final YFormDefinitionModel yformDefinition) throws MalformedURLException
	{
		div.setWidth("100%");
		div.setHeight("100%");
		div.setStyle("position: relative");

		final Window window = this.getWindow(div);
		if (window != null)
		{
			window.setWidth(getWidgetSettings().getString("width"));
			window.setHeight(getWidgetSettings().getString("height"));
			window.setBorder("normal");
			window.setSizable(getWidgetSettings().getBoolean("sizable"));
			window.setClosable(getWidgetSettings().getBoolean("closable"));
			window.setDraggable(getWidgetSettings().getString("draggable"));
			window.setMaximizable(true);
		}
		else
		{
			LOG.error("There is no window associated to the current widget.");
			return;
		}

		window.addEventListener(Events.ON_CLOSE, new EventListener<Event>()
		{
			@Override
			public void onEvent(final Event event) throws Exception
			{
				// we show always the latest formDefinition
				try
				{
					final YFormDefinitionModel yform = yformService.getYFormDefinition(yformDefinition.getApplicationId(),
							yformDefinition.getFormId());
					sendOutput(YFORM_DEFINITION_SOCKET_OUT, yform);
				}
				catch (final YFormServiceException e)
				{
					notificationService.notifyUser(notificationService.getWidgetNotificationSource(getWidgetInstanceManager()), GENERIC_YFORM_DEFINITION_ERROR, NotificationEvent.Level.FAILURE, e);
					LOG.error(e, e);
				}
			}
		});

		final Iframe iframe = new Iframe();
		iframe.setWidth("100%");
		iframe.setHeight("100%");
		iframe.setScrolling("no");
		iframe.setStyle("overflow: hidden");

		iframe.setSrc(this.getFormBuilderURL(yformDefinition));

		div.appendChild(iframe);

		iframe.addEventListener("onURIChange", new EventListener<URIEvent>()
		{
			@Override
			public void onEvent(final URIEvent arg0) throws Exception
			{
				// User is not allowed to change provided URI, for security reasons.
				Messagebox.show(getLabel("change.url.not.allowed"), getLabel("title.error"), new Messagebox.Button[]
						{ Messagebox.Button.OK }, Messagebox.ERROR, new EventListener<ClickEvent>()
				{
					@Override
					public void onEvent(final ClickEvent arg0) throws Exception
					{
						window.onClose();
					}
				});
			}
		});
	}

	/**
	 * Creates the URL to call Orbeon Form Builder.
	 *
	 * @param yformDefinition
	 */
	protected String getFormBuilderURL(final YFormDefinitionModel yformDefinition) throws MalformedURLException
	{

		final ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
		if (requestAttributes == null)
		{
			throw new MalformedURLException("Incoming RequestContextHolder is invalid.");
		} else
		{
			final HttpServletRequest request = requestAttributes.getRequest();

			String url = request.getRequestURL().toString();
			String contextPath = request.getContextPath();
			url = url.substring(0, url.indexOf(contextPath)); // it shouldn't contain the "/"

			// this is to prevent a previous filter that could modify the current contextPath.
			contextPath = contextPath.indexOf('/', 1) > 0 ? contextPath.substring(0, contextPath.indexOf('/', 1)) : contextPath;
			url = url.endsWith("/") ? url.substring(0, url.length() - 1) : url;

			url = url + contextPath + ORBEON_BUILDER_EDIT_ADDRESS + yformDefinition.getDocumentId();
			LOG.debug(url);
			return url;
		}
	}
}
