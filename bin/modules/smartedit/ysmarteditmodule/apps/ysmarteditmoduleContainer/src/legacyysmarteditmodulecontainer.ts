/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { IFeatureService, SeModule } from 'smarteditcommons';
/**
 * @ngdoc overview
 * @name ysmarteditmoduleContainer
 * @description
 * Placeholder for documentation
 */
@SeModule({
    imports: ['smarteditServicesModule', 'abAnalyticsToolbarItemModule'],
    initialize: (featureService: IFeatureService) => {
        'ngInject';
        /// /////////////////////////////////////////////////
        // Create Toolbar Item
        /// /////////////////////////////////////////////////
        // Create the toolbar item as a feature.
        featureService.addToolbarItem({
            toolbarId: 'smartEditPerspectiveToolbar',
            key: 'abAnalyticsToolbarItem',
            type: 'HYBRID_ACTION',
            nameI18nKey: 'ab.analytics.toolbar.item.name',
            priority: 2,
            section: 'left',
            iconClassName: 'icon-message-information se-toolbar-menu-ddlb--button__icon',
            include: 'abAnalyticsToolbarItemWrapperTemplate.html'
        });
    }
})
export class YSmarteditModuleContainer {}
