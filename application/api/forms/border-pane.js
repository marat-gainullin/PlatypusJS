(function() {
    var javaClass = Java.type("com.eas.client.forms.api.containers.BorderPane");
    javaClass.setPublisher(function(aDelegate) {
        return new P.BorderPane(null, null, aDelegate);
    });
    
    /**
     * A container with Border Layout.
     * @param hgap the horizontal gap (optional).
     * @param vgap the vertical gap (optional).
     * @constructor BorderPane BorderPane
     */
    P.BorderPane = function (hgap, vgap) {

        var maxArgs = 2;
        var delegate = arguments.length > maxArgs ?
            arguments[maxArgs] : new javaClass(P.boxAsJava(hgap), P.boxAsJava(vgap));

        Object.defineProperty(this, "unwrap", {
            get: function() {
                return function() {
                    return delegate;
                };
            }
        });
        var invalidatable = null;
        delegate.setPublishedCollectionInvalidator(function() {
            invalidatable = null;
        });
        /**
         * The mouse <code>Cursor</code> over this component.
         * @property cursor
         * @memberOf BorderPane
         */
        Object.defineProperty(this, "cursor", {
            get: function() {
                var value = delegate.cursor;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.cursor = P.boxAsJava(aValue);
            }
        });

        /**
        * Mouse dragged event handler function.
         * @property onMouseDragged
         * @memberOf BorderPane
        */
        Object.defineProperty(this, "onMouseDragged", {
            get: function() {
                var value = delegate.onMouseDragged;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.onMouseDragged = P.boxAsJava(aValue);
            }
        });

        /**
         * Gets the parent of this component.
         * @property parent
         * @memberOf BorderPane
         */
        Object.defineProperty(this, "parent", {
            get: function() {
                var value = delegate.parent;
                return P.boxAsJs(value);
            }
        });

        /**
        * Mouse released event handler function.
         * @property onMouseReleased
         * @memberOf BorderPane
        */
        Object.defineProperty(this, "onMouseReleased", {
            get: function() {
                var value = delegate.onMouseReleased;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.onMouseReleased = P.boxAsJava(aValue);
            }
        });

        /**
        * Keyboard focus lost by the component event handler function.
         * @property onFocusLost
         * @memberOf BorderPane
        */
        Object.defineProperty(this, "onFocusLost", {
            get: function() {
                var value = delegate.onFocusLost;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.onFocusLost = P.boxAsJava(aValue);
            }
        });

        /**
        * Mouse pressed event handler function.
         * @property onMousePressed
         * @memberOf BorderPane
        */
        Object.defineProperty(this, "onMousePressed", {
            get: function() {
                var value = delegate.onMousePressed;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.onMousePressed = P.boxAsJava(aValue);
            }
        });

        /**
         * The foreground color of this component.
         * @property foreground
         * @memberOf BorderPane
         */
        Object.defineProperty(this, "foreground", {
            get: function() {
                var value = delegate.foreground;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.foreground = P.boxAsJava(aValue);
            }
        });

        /**
         * An error message of this component.
         * Validation procedure may set this property and subsequent focus lost event will clear it.
         * @property error
         * @memberOf BorderPane
         */
        Object.defineProperty(this, "error", {
            get: function() {
                var value = delegate.error;
                return P.boxAsJs(value);
            }
        });

        /**
        * Determines whether this component is enabled. An enabled component can respond to user input and generate events. Components are enabled initially by default.
         * @property enabled
         * @memberOf BorderPane
        */
        Object.defineProperty(this, "enabled", {
            get: function() {
                var value = delegate.enabled;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.enabled = P.boxAsJava(aValue);
            }
        });

        /**
        * Component moved event handler function.
         * @property onComponentMoved
         * @memberOf BorderPane
        */
        Object.defineProperty(this, "onComponentMoved", {
            get: function() {
                var value = delegate.onComponentMoved;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.onComponentMoved = P.boxAsJava(aValue);
            }
        });

        /**
        * Component added event hanler function.
         * @property onComponentAdded
         * @memberOf BorderPane
        */
        Object.defineProperty(this, "onComponentAdded", {
            get: function() {
                var value = delegate.onComponentAdded;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.onComponentAdded = P.boxAsJava(aValue);
            }
        });

        /**
         * <code>PopupMenu</code> that assigned for this component.
         * @property componentPopupMenu
         * @memberOf BorderPane
         */
        Object.defineProperty(this, "componentPopupMenu", {
            get: function() {
                var value = delegate.componentPopupMenu;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.componentPopupMenu = P.boxAsJava(aValue);
            }
        });

        /**
        * Vertical coordinate of the component.
         * @property top
         * @memberOf BorderPane
        */
        Object.defineProperty(this, "top", {
            get: function() {
                var value = delegate.top;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.top = P.boxAsJava(aValue);
            }
        });

        /**
        * Gets the container's children components.
         * @property children
         * @memberOf BorderPane
        */
        Object.defineProperty(this, "children", {
            get: function() {
                if (!invalidatable) {
                    var value = delegate.children;
                    invalidatable = P.boxAsJs(value);
                }
                return invalidatable;
            }
        });

        /**
        * Component resized event handler function.
         * @property onComponentResized
         * @memberOf BorderPane
        */
        Object.defineProperty(this, "onComponentResized", {
            get: function() {
                var value = delegate.onComponentResized;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.onComponentResized = P.boxAsJava(aValue);
            }
        });

        /**
        * Mouse entered over the component event handler function.
         * @property onMouseEntered
         * @memberOf BorderPane
        */
        Object.defineProperty(this, "onMouseEntered", {
            get: function() {
                var value = delegate.onMouseEntered;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.onMouseEntered = P.boxAsJava(aValue);
            }
        });

        /**
         * The tooltip string that has been set with.
         * @property toolTipText
         * @memberOf BorderPane
         */
        Object.defineProperty(this, "toolTipText", {
            get: function() {
                var value = delegate.toolTipText;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.toolTipText = P.boxAsJava(aValue);
            }
        });

        /**
        * Height of the component.
         * @property height
         * @memberOf BorderPane
        */
        Object.defineProperty(this, "height", {
            get: function() {
                var value = delegate.height;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.height = P.boxAsJava(aValue);
            }
        });

        /**
        * Native API. Returns low level html element. Applicable only in HTML5 client.
         * @property element
         * @memberOf BorderPane
        */
        Object.defineProperty(this, "element", {
            get: function() {
                var value = delegate.element;
                return P.boxAsJs(value);
            }
        });

        /**
        * Component shown event handler function.
         * @property onComponentShown
         * @memberOf BorderPane
        */
        Object.defineProperty(this, "onComponentShown", {
            get: function() {
                var value = delegate.onComponentShown;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.onComponentShown = P.boxAsJava(aValue);
            }
        });

        /**
        * Mouse moved event handler function.
         * @property onMouseMoved
         * @memberOf BorderPane
        */
        Object.defineProperty(this, "onMouseMoved", {
            get: function() {
                var value = delegate.onMouseMoved;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.onMouseMoved = P.boxAsJava(aValue);
            }
        });

        /**
        * The component added using HorizontalPosition.TOP constraint.
        * If no component at the container on this constraint then set to <code>null</code>.
         * @property topComponent
         * @memberOf BorderPane
        */
        Object.defineProperty(this, "topComponent", {
            get: function() {
                var value = delegate.topComponent;
                return P.boxAsJs(value);
            }
        });

        /**
         * True if this component is completely opaque.
         * @property opaque
         * @memberOf BorderPane
         */
        Object.defineProperty(this, "opaque", {
            get: function() {
                var value = delegate.opaque;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.opaque = P.boxAsJava(aValue);
            }
        });

        /**
         * Determines whether this component should be visible when its parent is visible.
         * @property visible
         * @memberOf BorderPane
         */
        Object.defineProperty(this, "visible", {
            get: function() {
                var value = delegate.visible;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.visible = P.boxAsJava(aValue);
            }
        });

        /**
        * The component added using HorizontalPosition.BOTTOM constraint.
        * If no component at the container on this constraint then set to <code>null</code>.
         * @property bottomComponent
         * @memberOf BorderPane
        */
        Object.defineProperty(this, "bottomComponent", {
            get: function() {
                var value = delegate.bottomComponent;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.bottomComponent = P.boxAsJava(aValue);
            }
        });

        /**
        * Component hidden event handler function.
         * @property onComponentHidden
         * @memberOf BorderPane
        */
        Object.defineProperty(this, "onComponentHidden", {
            get: function() {
                var value = delegate.onComponentHidden;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.onComponentHidden = P.boxAsJava(aValue);
            }
        });

        /**
         * Overrides the default focus traversal policy for this component's focus traversal cycle by unconditionally setting the specified component as the next component in the cycle, and this component as the specified component's previous component.
         * @property nextFocusableComponent
         * @memberOf BorderPane
         */
        Object.defineProperty(this, "nextFocusableComponent", {
            get: function() {
                var value = delegate.nextFocusableComponent;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.nextFocusableComponent = P.boxAsJava(aValue);
            }
        });

        /**
        * Gets the number of components in this panel.
         * @property count
         * @memberOf BorderPane
        */
        Object.defineProperty(this, "count", {
            get: function() {
                var value = delegate.count;
                return P.boxAsJs(value);
            }
        });

        /**
        * Main action performed event handler function.
         * @property onActionPerformed
         * @memberOf BorderPane
        */
        Object.defineProperty(this, "onActionPerformed", {
            get: function() {
                var value = delegate.onActionPerformed;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.onActionPerformed = P.boxAsJava(aValue);
            }
        });

        /**
        * Key released event handler function.
         * @property onKeyReleased
         * @memberOf BorderPane
        */
        Object.defineProperty(this, "onKeyReleased", {
            get: function() {
                var value = delegate.onKeyReleased;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.onKeyReleased = P.boxAsJava(aValue);
            }
        });

        /**
         * Determines whether this component may be focused.
         * @property focusable
         * @memberOf BorderPane
         */
        Object.defineProperty(this, "focusable", {
            get: function() {
                var value = delegate.focusable;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.focusable = P.boxAsJava(aValue);
            }
        });

        /**
        * Key typed event handler function.
         * @property onKeyTyped
         * @memberOf BorderPane
        */
        Object.defineProperty(this, "onKeyTyped", {
            get: function() {
                var value = delegate.onKeyTyped;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.onKeyTyped = P.boxAsJava(aValue);
            }
        });

        /**
        * The component added using HorizontalPosition.RIGHT constraint.
        * If no component at the container on this constraint then set to <code>null</code>.
         * @property rightComponent
         * @memberOf BorderPane
        */
        Object.defineProperty(this, "rightComponent", {
            get: function() {
                var value = delegate.rightComponent;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.rightComponent = P.boxAsJava(aValue);
            }
        });

        /**
        * Mouse wheel moved event handler function.
         * @property onMouseWheelMoved
         * @memberOf BorderPane
        */
        Object.defineProperty(this, "onMouseWheelMoved", {
            get: function() {
                var value = delegate.onMouseWheelMoved;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.onMouseWheelMoved = P.boxAsJava(aValue);
            }
        });

        /**
        * Component removed event handler function.
         * @property onComponentRemoved
         * @memberOf BorderPane
        */
        Object.defineProperty(this, "onComponentRemoved", {
            get: function() {
                var value = delegate.onComponentRemoved;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.onComponentRemoved = P.boxAsJava(aValue);
            }
        });

        /**
        * The component added using HorizontalPosition.LEFT constraint.
        * If no component at this constraint then set to <code>null</code>.
         * @property leftComponent
         * @memberOf BorderPane
        */
        Object.defineProperty(this, "leftComponent", {
            get: function() {
                var value = delegate.leftComponent;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.leftComponent = P.boxAsJava(aValue);
            }
        });

        /**
        * Native API. Returns low level swing component. Applicable only in J2SE swing client.
         * @property component
         * @memberOf BorderPane
        */
        Object.defineProperty(this, "component", {
            get: function() {
                var value = delegate.component;
                return P.boxAsJs(value);
            }
        });

        /**
        * Keyboard focus gained by the component event.
         * @property onFocusGained
         * @memberOf BorderPane
        */
        Object.defineProperty(this, "onFocusGained", {
            get: function() {
                var value = delegate.onFocusGained;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.onFocusGained = P.boxAsJava(aValue);
            }
        });

        /**
        * Horizontal coordinate of the component.
         * @property left
         * @memberOf BorderPane
        */
        Object.defineProperty(this, "left", {
            get: function() {
                var value = delegate.left;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.left = P.boxAsJava(aValue);
            }
        });

        /**
         * The background color of this component.
         * @property background
         * @memberOf BorderPane
         */
        Object.defineProperty(this, "background", {
            get: function() {
                var value = delegate.background;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.background = P.boxAsJava(aValue);
            }
        });

        /**
        * Mouse clicked event handler function.
         * @property onMouseClicked
         * @memberOf BorderPane
        */
        Object.defineProperty(this, "onMouseClicked", {
            get: function() {
                var value = delegate.onMouseClicked;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.onMouseClicked = P.boxAsJava(aValue);
            }
        });

        /**
        * Mouse exited over the component event handler function.
         * @property onMouseExited
         * @memberOf BorderPane
        */
        Object.defineProperty(this, "onMouseExited", {
            get: function() {
                var value = delegate.onMouseExited;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.onMouseExited = P.boxAsJava(aValue);
            }
        });

        /**
         * Gets name of this component.
         * @property name
         * @memberOf BorderPane
         */
        Object.defineProperty(this, "name", {
            get: function() {
                var value = delegate.name;
                return P.boxAsJs(value);
            }
        });

        /**
        * Width of the component.
         * @property width
         * @memberOf BorderPane
        */
        Object.defineProperty(this, "width", {
            get: function() {
                var value = delegate.width;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.width = P.boxAsJava(aValue);
            }
        });

        /**
        * The component added using HorizontalPosition.CENTER constraint.
        * If no component at the container on this constraint then set to <code>null</code>.
         * @property centerComponent
         * @memberOf BorderPane
        */
        Object.defineProperty(this, "centerComponent", {
            get: function() {
                var value = delegate.centerComponent;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.centerComponent = P.boxAsJava(aValue);
            }
        });

        /**
         * The font of this component.
         * @property font
         * @memberOf BorderPane
         */
        Object.defineProperty(this, "font", {
            get: function() {
                var value = delegate.font;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.font = P.boxAsJava(aValue);
            }
        });

        /**
        * Key pressed event handler function.
         * @property onKeyPressed
         * @memberOf BorderPane
        */
        Object.defineProperty(this, "onKeyPressed", {
            get: function() {
                var value = delegate.onKeyPressed;
                return P.boxAsJs(value);
            },
            set: function(aValue) {
                delegate.onKeyPressed = P.boxAsJava(aValue);
            }
        });

        /**
        * Appends the specified component to this container on the specified placement.
        * @param component the component to add.
        * @param place the placement in the container: <code>HorizontalPosition.LEFT</code>, <code>HorizontalPosition.CENTER</code>, <code>HorizontalPosition.RIGHT</code>, <code>VerticalPosition.TOP</code> or <code>VerticalPosition.BOTTOM</code> (optional).
        * @param size the size of the component by the provided place direction (optional).
         * @method add
         * @memberOf BorderPane
        */
        Object.defineProperty(this, "add", {
            get: function() {
                return function(component, place, size) {
                    var value = delegate.add(P.boxAsJava(component), P.boxAsJava(place), P.boxAsJava(size));
                    return P.boxAsJs(value);
                };
            }
        });

        /**
         * Gets the container's nth component.
         * @param index the component's index in the container
         * @return the child component
         * @method child
         * @memberOf BorderPane
        */
        Object.defineProperty(this, "child", {
            get: function() {
                return function(index) {
                    var value = delegate.child(P.boxAsJava(index));
                    return P.boxAsJs(value);
                };
            }
        });

        /**
        * Removes the specified component from this container.
        * @param component the component to remove
         * @method remove
         * @memberOf BorderPane
        */
        Object.defineProperty(this, "remove", {
            get: function() {
                return function(component) {
                    var value = delegate.remove(P.boxAsJava(component));
                    return P.boxAsJs(value);
                };
            }
        });

        /**
        * Removes all the components from this container.
         * @method clear
         * @memberOf BorderPane
        */
        Object.defineProperty(this, "clear", {
            get: function() {
                return function() {
                    var value = delegate.clear();
                    return P.boxAsJs(value);
                };
            }
        });

        /**
        * Tries to acquire focus for this component.
         * @method focus
         * @memberOf BorderPane
        */
        Object.defineProperty(this, "focus", {
            get: function() {
                return function() {
                    var value = delegate.focus();
                    return P.boxAsJs(value);
                };
            }
        });


        delegate.setPublished(this);
    };
})();