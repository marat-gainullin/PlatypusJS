package com.eas.ui.events;

import com.eas.ui.Widget;

public class RemoveEvent extends Event {

    private final Widget widget;

    /**
     * Creates a new close event.
     *
     * @param aWidget the target
     */
    public RemoveEvent(Object aSource, Widget aWidget) {
        super(aSource);
        widget = aWidget;
    }

    /**
     * Gets the target.
     *
     * @return the target
     */
    public Widget getWidget() {
        return widget;
    }

}
