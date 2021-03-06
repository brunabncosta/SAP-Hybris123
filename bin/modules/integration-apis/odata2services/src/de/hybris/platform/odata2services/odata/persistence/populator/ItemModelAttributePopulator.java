/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.odata2services.odata.persistence.populator;

import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.inboundservices.persistence.PersistenceContext;
import de.hybris.platform.inboundservices.persistence.populator.AbstractAttributePopulator;
import de.hybris.platform.integrationservices.model.TypeAttributeDescriptor;
import de.hybris.platform.inboundservices.persistence.populator.ContextReferencedItemModelService;

import org.springframework.beans.factory.annotation.Required;

/**
 * @deprecated use {@link de.hybris.platform.inboundservices.persistence.populator.ItemModelAttributePopulator}
 */
@Deprecated(since = "21.05.0-RC1", forRemoval = true)
public class ItemModelAttributePopulator extends AbstractAttributePopulator
{
    private ContextReferencedItemModelService contextReferencedItemModelService;

	@Override
	protected boolean isApplicable(final TypeAttributeDescriptor attribute, final PersistenceContext context)
	{
		return !attribute.isCollection() && !attribute.isPrimitive() && !attribute.isMap();
	}

    @Override
    protected void populateAttribute(final ItemModel item, final TypeAttributeDescriptor attribute,
                                     final PersistenceContext context)
    {
        final ItemModel referencedItem = getContextReferencedItemModelService().deriveReferencedItemModel(attribute,
                context.getReferencedContext(attribute));
        final Object referencedItemValue = attribute.getAttributeType().isEnumeration()
                ? getModelService().get(referencedItem.getPk())
                : referencedItem;
        attribute.accessor().setValue(item, referencedItemValue);
    }

    protected ContextReferencedItemModelService getContextReferencedItemModelService()
    {
        return contextReferencedItemModelService;
    }

    @Required
    public void setContextReferencedItemModelService(final ContextReferencedItemModelService service)
    {
        contextReferencedItemModelService = service;
    }
}
