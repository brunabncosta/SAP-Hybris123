/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { CommonModule } from '@angular/common';
import { CUSTOM_ELEMENTS_SCHEMA, NgModule } from '@angular/core';
import { PopoverModule } from '@fundamental-ngx/core';
import { TranslateModule } from '@ngx-translate/core';

import {
    HeaderLanguageDropdownComponent,
    SeGenericEditorModule,
    TooltipModule
} from 'smarteditcommons';
import { ExperienceSelectorButtonComponent } from '../../services/ExperienceSelectorButtonComponent/ExperienceSelectorButtonComponent';
import { InflectionPointSelectorComponent } from '../../services/inflectionPointSelectorWidget/InflectionPointSelectorComponent';
import { ExperienceSelectorComponent } from '../experienceSelector/ExperienceSelectorComponent';
import { ShortcutLinkComponent } from '../shortcutLink/ShortcutLinkComponent';
import { UserAccountComponent } from '../userAccount/UserAccountComponent';
import { DeviceSupportWrapperComponent } from './DeviceSupportWrapperComponent';
import { ExperienceSelectorWrapperComponent } from './ExperienceSelectorWrapperComponent';
import { LogoComponent } from './LogoComponent';

@NgModule({
    schemas: [CUSTOM_ELEMENTS_SCHEMA],
    imports: [
        PopoverModule,
        CommonModule,
        SeGenericEditorModule,
        TranslateModule.forChild(),
        TooltipModule
    ],
    declarations: [
        HeaderLanguageDropdownComponent,
        ShortcutLinkComponent,
        UserAccountComponent,
        InflectionPointSelectorComponent,
        ExperienceSelectorButtonComponent,
        ExperienceSelectorComponent,
        DeviceSupportWrapperComponent,
        ExperienceSelectorWrapperComponent,
        LogoComponent
    ],
    entryComponents: [
        HeaderLanguageDropdownComponent,
        UserAccountComponent,
        ShortcutLinkComponent,
        InflectionPointSelectorComponent,
        ExperienceSelectorButtonComponent,
        ExperienceSelectorComponent,
        DeviceSupportWrapperComponent,
        ExperienceSelectorWrapperComponent,
        LogoComponent
    ],
    exports: [
        HeaderLanguageDropdownComponent,
        UserAccountComponent,
        ShortcutLinkComponent,
        InflectionPointSelectorComponent,
        ExperienceSelectorButtonComponent,
        ExperienceSelectorComponent,
        DeviceSupportWrapperComponent,
        ExperienceSelectorWrapperComponent,
        LogoComponent
    ]
})
export class TopToolbarsModule {}
