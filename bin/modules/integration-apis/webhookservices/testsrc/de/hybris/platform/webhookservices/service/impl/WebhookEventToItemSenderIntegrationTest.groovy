/*
 *  Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.webhookservices.service.impl

import de.hybris.bootstrap.annotations.IntegrationTest
import de.hybris.platform.apiregistryservices.dto.EventSourceData
import de.hybris.platform.core.PK
import de.hybris.platform.integrationservices.IntegrationObjectModelBuilder
import de.hybris.platform.integrationservices.util.IntegrationTestUtil
import de.hybris.platform.integrationservices.util.impex.ModuleEssentialData
import de.hybris.platform.outboundservices.facade.OutboundServiceFacade
import de.hybris.platform.outboundservices.util.TestOutboundFacade
import de.hybris.platform.servicelayer.ServicelayerSpockSpecification
import de.hybris.platform.tx.AfterSaveEvent
import de.hybris.platform.webhookservices.WebhookConfigurationBuilder
import de.hybris.platform.webhookservices.event.ItemSavedEvent
import de.hybris.platform.webhookservices.util.WebhookServicesEssentialData
import org.junit.ClassRule
import org.junit.Rule
import org.junit.Test
import spock.lang.AutoCleanup
import spock.lang.Issue
import spock.lang.Shared

import javax.annotation.Resource
import java.time.Duration

import static de.hybris.platform.integrationservices.IntegrationObjectItemAttributeModelBuilder.integrationObjectItemAttribute
import static de.hybris.platform.integrationservices.IntegrationObjectItemModelBuilder.integrationObjectItem
import static de.hybris.platform.integrationservices.IntegrationObjectModelBuilder.integrationObject
import static de.hybris.platform.integrationservices.util.EventualCondition.eventualCondition
import static de.hybris.platform.integrationservices.util.IntegrationTestUtil.condition
import static de.hybris.platform.integrationservices.util.IntegrationTestUtil.createCatalogWithId
import static de.hybris.platform.outboundservices.ConsumedDestinationBuilder.consumedDestinationBuilder
import static de.hybris.platform.webhookservices.WebhookConfigurationBuilder.webhookConfiguration

@IntegrationTest
@Issue('https://jira.hybris.com/browse/STOUT-4552')
class WebhookEventToItemSenderIntegrationTest extends ServicelayerSpockSpecification {
    private static final String TEST_NAME = 'WebhookEventToItemSender'
    private static final String CATALOG_IO = "${TEST_NAME}_CatalogIO"
    private static final String CATALOG_ID = "${TEST_NAME}_Catalog"
    private static final String DESTINATION_1 = "${TEST_NAME}_Destination_1"
    private static final String DESTINATION_2 = "${TEST_NAME}_Destination_2"
    private static final Duration REASONABLE_TIME = Duration.ofSeconds(7)

    private OutboundServiceFacade originalOutboundServiceFacade
    @Resource
    private WebhookEventToItemSender webhookEventSender

    @Shared
    @ClassRule
    ModuleEssentialData essentialData = WebhookServicesEssentialData.webhookServicesEssentialData()
    @Shared
    @ClassRule
    IntegrationObjectModelBuilder io = integrationObject().withCode(CATALOG_IO)
            .withItem(integrationObjectItem().withCode('Catalog').root()
                    .withAttribute(integrationObjectItemAttribute().withName('id')))
    @Rule
    TestOutboundFacade mockOutboundServiceFacade = new TestOutboundFacade().respondWithCreated()
    @AutoCleanup('cleanup')
    WebhookConfigurationBuilder webhookBuilder = webhookConfiguration()
            .withIntegrationObject(CATALOG_IO)

    def setup() {
        originalOutboundServiceFacade = webhookEventSender.outboundServiceFacade
        webhookEventSender.outboundServiceFacade = mockOutboundServiceFacade
    }

    def cleanup() {
        webhookEventSender.outboundServiceFacade = originalOutboundServiceFacade
    }

    @Test
    def 'Catalog is sent to webhook after it is saved'() {
        given: 'WebhookConfiguration created for a Catalog item'
        def consumedDestination = consumedDestinationBuilder()
                .withId(DESTINATION_1)
                .withUrl("https://path/to/webhooks")
                .withDestinationTarget('webhookServices') // created in essential data
        webhookBuilder
            .withDestination(consumedDestination)
            .build()

        when: 'Catalog created'
        def catalog = createCatalogWithId CATALOG_ID

        then:
        condition().eventually {
            assert mockOutboundServiceFacade.invocations() == 1
        }

        cleanup:
        IntegrationTestUtil.remove catalog
    }

    @Test
    def 'No item is sent when the PK in the event is not found'() {
        given: 'WebhookConfiguration created for a Catalog item'
        def consumedDestination = consumedDestinationBuilder()
                .withId(DESTINATION_2)
                .withUrl("https://path/to/webhooks")
                .withDestinationTarget('webhookServices') // created in essential data
        webhookBuilder
                .withDestination(consumedDestination)
                .build()

        and: 'Event contains a Catalog PK that does not exist'
        final EventSourceData data = new EventSourceData()
        final int catalogTypeCode = 600
        final AfterSaveEvent afterSaveEvent = new AfterSaveEvent(PK.createCounterPK(catalogTypeCode), 4)
        final ItemSavedEvent webhookEvent = new ItemSavedEvent(afterSaveEvent)
        data.setEvent(webhookEvent)

        when:
        webhookEventSender.send(data)

        then: 'Catalog does not exist so the facade is not called'
        eventualCondition().within(REASONABLE_TIME).retains {
            assert mockOutboundServiceFacade.invocations() == 0
        }
    }
}
