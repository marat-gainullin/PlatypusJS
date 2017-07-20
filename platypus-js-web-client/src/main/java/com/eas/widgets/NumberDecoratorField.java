package com.eas.widgets;

import com.eas.core.Utils;
import com.eas.core.XElement;
import com.eas.ui.CommonResources;
import com.eas.ui.HasNumberValue;
import com.eas.ui.Widget;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.BrowserEvents;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Style;

/**
 *
 * @author mg
 */
public class NumberDecoratorField extends ValueDecoratorField {

    protected Element left = Document.get().createDivElement();
    protected Element right = Document.get().createDivElement();

    public NumberDecoratorField() {
        super(Utils.isMobile() ? new NumberField() : new FormattedField(FormattedField.NUMBER));
        decorated.element.classList.add("spin-field");
        element.setClassName("form-control");
        left.setClassName("spin-left");
        left.getStyle().display ='inline-block');
        left.getStyle().top =0+ 'px');
        left.getStyle().height =100 + '%');
        left.getStyle().position = 'relative';
        // FireFox hides this without such setting because of place in DOM.
        // Place in DOM is after input element because of FireFox's float elements behaviour.
        left.getStyle().setZIndex(1);

        right.classList.add("spin-right");
        right.getStyle().display ='inline-block');
        right.getStyle().top =0+ 'px');
        right.getStyle().height =100 + '%');
        right.getStyle().position = 'relative';
        // FireFox hides this without such setting because of place in DOM.
        // Place in DOM is after input element because of FireFox's float elements behaviour.
        right.getStyle().setZIndex(1);

        CommonResources.INSTANCE.commons().ensureInjected();
        left.classList.add(CommonResources.INSTANCE.commons().unselectable());
        right.classList.add(CommonResources.INSTANCE.commons().unselectable());

        left.<XElement>cast().addEventListener(BrowserEvents.CLICK, new XElement.NativeHandler() {

            @Override
            public void on(NativeEvent event) {
                if (!isReadonly()) {
                    decrement();
                }
            }
        });
        right.<XElement>cast().addEventListener(BrowserEvents.CLICK, new XElement.NativeHandler() {

            @Override
            public void on(NativeEvent event) {
                if (!isReadonly()) {
                    increment();
                }
            }
        });
    }

    public void increment() {
        ((HasNumberValue) decorated).increment();
    }

    public void decrement() {
        ((HasNumberValue) decorated).decrement();
    }

    public Double getMin() {
        return ((HasNumberValue) decorated).getMin();
    }

    public void setMin(Double aValue) {
        ((HasNumberValue) decorated).setMin(aValue);
    }

    public Double getMax() {
        return ((HasNumberValue) decorated).getMax();
    }

    public void setMax(Double aValue) {
        ((HasNumberValue) decorated).setMax(aValue);
    }

    public Double getStep() {
        return ((HasNumberValue) decorated).getStep();
    }

    public void setStep(Double aValue) {
        ((HasNumberValue) decorated).setStep(aValue);
    }

    @Override
    protected void publish(JavaScriptObject aValue) {
        publish(this, aValue);
    }

    private native static void publish(Widget aWidget, JavaScriptObject aPublished)/*-{
        aPublished.redraw = function(){
            aWidget.@com.eas.bound.ModelSpin::rebind()();
        };
        Object.defineProperty(aPublished, "emptyText", {
            get : function() {
                return aWidget.@com.eas.ui.HasEmptyText::getEmptyText()();
            },
            set : function(aValue) {
                aWidget.@com.eas.ui.HasEmptyText::setEmptyText(Ljava/lang/String;)(aValue != null ? '' + aValue : null);
            }
        });
        Object.defineProperty(aPublished, "value", {
            get : function() {
                var v = aWidget.@com.eas.bound.ModelSpin::getValue()();
                if (v != null) {
                    return v.@java.lang.Number::doubleValue()();
                } else
                    return null;
            },
            set : function(aValue) {
                if (aValue != null) {
                    var v = aValue * 1;
                    var d = @java.lang.Double::new(D)(v);
                    aWidget.@com.eas.bound.ModelSpin::setValue(Ljava/lang/Double;Z)(d, true);
                } else {
                    aWidget.@com.eas.bound.ModelSpin::setValue(Ljava/lang/Double;Z)(null, true);
                }
            }
        });
        Object.defineProperty(aPublished, "text", {
            get : function() {
                return aWidget.@com.eas.bound.ModelSpin::getText()();
            },
            set : function(aValue) {
                aWidget.@com.eas.bound.ModelSpin::setText(Ljava/lang/String;)(aValue != null ? aValue + '' : '');
            }
        });
        Object.defineProperty(aPublished, "min", {
            get : function() {
                var v = aWidget.@com.eas.bound.ModelSpin::getMin()();
                if (v != null) {
                    return v.@java.lang.Number::doubleValue()();
                } else
                    return null;
            },
            set : function(aValue) {
                if (aValue != null) {
                    var v = aValue * 1;
                    var d = @java.lang.Double::new(D)(v);
                    aWidget.@com.eas.bound.ModelSpin::setMin(Ljava/lang/Double;)(d);
                } else {
                    aWidget.@com.eas.bound.ModelSpin::setMin(Ljava/lang/Double;)(null);
                }
            }
        });
        Object.defineProperty(aPublished, "max", {
            get : function() {
                var v = aWidget.@com.eas.bound.ModelSpin::getMax()();
                if (v != null) {
                    return v.@java.lang.Number::doubleValue()();
                } else
                    return null;
            },
            set : function(aValue) {
                if (aValue != null) {
                    var v = aValue * 1;
                    var d = @java.lang.Double::new(D)(v);
                    aWidget.@com.eas.bound.ModelSpin::setMax(Ljava/lang/Double;)(d);
                } else {
                    aWidget.@com.eas.bound.ModelSpin::setMax(Ljava/lang/Double;)(null);
                }
            }
        });
        Object.defineProperty(aPublished, "step", {
            get : function() {
                var v = aWidget.@com.eas.bound.ModelSpin::getStep()();
                if (v != null) {
                    return v.@java.lang.Number::doubleValue()();
                } else
                    return null;
            },
            set : function(aValue) {
                if (aValue != null) {
                    var v = aValue * 1;
                    var d = @java.lang.Double::new(D)(v);
                    aWidget.@com.eas.bound.ModelSpin::setStep(Ljava/lang/Double;)(d);
                } else {
                    aWidget.@com.eas.bound.ModelSpin::setStep(Ljava/lang/Double;)(null);
                }
            }
        });
    }-*/;
}
