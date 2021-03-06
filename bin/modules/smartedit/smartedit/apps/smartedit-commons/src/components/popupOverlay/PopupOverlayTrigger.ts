/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
export enum PopoverTrigger {
    Hover = 'hover',
    Click = 'click'
}

export type PopupOverlayTrigger = PopoverTrigger | boolean | string;
