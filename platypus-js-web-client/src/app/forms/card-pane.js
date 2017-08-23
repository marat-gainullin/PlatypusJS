define([
    '../id',
    '../extend',
    '../invoke',
    './container',
    './item-event'], function (
        Id,
        extend,
        Invoke,
        Container,
        SelectionEvent) {
    function Cards(hgap, vgap) {
        Container.call(this);

        var self = this;
        
        if (arguments.length < 2) {
            vgap = 0;
        }
        if (arguments.length < 1) {
            vgap = 0;
            hgap = 0;
        }

        this.element.style.overflow = 'hidden';
        this.element.style.position = 'relative';
        
        this.element.classList.add('p-cards');
        
        this.element.id = 'p-' + Id.generate();
        
        var style = document.createElement('style');
        self.element.appendChild(style);
        function formatChildren() {
            style.innerHTML =
                    'div#' + self.element.id + ' > .p-widget {' +
                    '}';
        }
        formatChildren();
        
        var cards = new Map();

        var visibleWidget;

        Object.defineProperty(this, 'hgap', {
            get: function () {
                return hgap;
            },
            set: function (aValue) {
                if (hgap !== aValue) {
                    hgap = aValue;
                    self.element.style.paddingLeft = self.element.style.paddingRight = hgap + 'px';
                }
            }
        });

        Object.defineProperty(this, 'vgap', {
            get: function () {
                return vgap;
            },
            set: function (aValue) {
                if (vgap !== aValue) {
                    vgap = aValue;
                    self.element.style.paddingTop = self.element.style.paddingBottom = vgap + 'px';
                }
            }
        });

        var superChild = this.child;
        function child(indexOrCard) {
            if (isNaN(indexOrCard)) {
                var card = indexOrCard;
                return cards.get(card);
            } else {
                var index = +indexOrCard;
                return superChild(index);
            }
        }
        Object.defineProperty(this, 'child', {
            get: function () {
                return child;
            }
        });

        var superAdd = this.add;
        function add(w, indexOrCard) {
            if (w) {
                if (w.parent === self)
                    throw 'A widget is already added to this container';
                var card;
                var index;
                if (arguments.length < 2) {
                    card = 'card-' + Id.generate();
                    index = self.count;
                } else {
                    if (isNaN(indexOrCard)) {
                        card = indexOrCard;
                        index = self.count;
                    } else {
                        card = 'card-' + Id.generate();
                        index = indexOrCard;
                    }
                }
                var old;
                if (cards.has(card)) {
                    old = superRemove(cards.get(card));
                }
                superAdd(w, index);
                cards.set(card, w);
                w['-platypus-ui-card'] = card;
                if (!visibleWidget) {
                    showWidget(w);
                }
                return {
                    card: card,
                    evicted: old
                };
            }
        }
        Object.defineProperty(this, 'add', {
            get: function () {
                return add;
            }
        });

        var superClear = this.clear;
        function clear() {
            visibleWidget = null;
            cards.clear();
            self.forEach(function(w){
                w.element.classList.remove('card-shown');
                w.element.classList.remove('card-hidden');
            });
            superClear();
        }
        Object.defineProperty(this, 'clear', {
            get: function () {
                return clear;
            }
        });

        var superRemove = this.remove;
        function remove(widgetOrIndexOrCard) {
            if(typeof widgetOrIndexOrCard === 'string')
                widgetOrIndexOrCard = cards.get(widgetOrIndexOrCard);
            var removed = superRemove(widgetOrIndexOrCard);
            if (removed) {
                removeCard(removed);
                if (visibleWidget === removed) {
                    visibleWidget = self.count === 0 ? null : superChild(0);
                }
            }
            return removed;
        }
        Object.defineProperty(this, 'remove', {
            get: function () {
                return remove;
            }
        });

        function removeCard(w) {
            w.element.classList.remove('card-shown');
            w.element.classList.remove('card-hidden');
            if (w && w['-platypus-ui-card']) {
                cards.delete(w['-platypus-ui-card']);
                delete w['-platypus-ui-card'];
            }
        }

        function showWidget(toBeShown) {
            var oldWidget = visibleWidget;
            visibleWidget = toBeShown;

            if (visibleWidget !== oldWidget) {
                visibleWidget.element.classList.add('card-shown');
                visibleWidget.element.classList.remove('card-hidden');

                if (oldWidget) {
                    oldWidget.element.classList.add('card-hidden');
                    oldWidget.element.classList.remove('card-shown');
                }
                fireSelected(visibleWidget);
            }
        }

        function fireSelected(aItem) {
            var event = new SelectionEvent(self, aItem);
            selectionHandlers.forEach(function (h) {
                Invoke.later(function(){
                    h(event);
                });
            });
        }

        function show(widgetOrCardName) {
            if (cards.has(widgetOrCardName)) {
                var toShow = cards.get(widgetOrCardName);
                showWidget(toShow);
            } else {
                showWidget(widgetOrCardName);
            }
        }

        Object.defineProperty(this, 'show', {
            get: function () {
                return show;
            }
        });

        Object.defineProperty(this, 'visibleWidget', {
            get: function () {
                return visibleWidget;
            }
        });

        var selectionHandlers = new Set();

        function addSelectionHandler(handler) {
            selectionHandlers.add(handler);
            return {
                removeHandler: function () {
                    selectionHandlers.delete(handler);
                }
            };
        }

        Object.defineProperty(this, 'addSelectionHandler', {
            get: function () {
                return addSelectionHandler;
            }
        });

        function ajustLeft(w, aValue) {
        }
        Object.defineProperty(this, 'ajustLeft', {
            get: function () {
                return ajustLeft;
            }
        });

        function ajustWidth(w, aValue) {
        }
        Object.defineProperty(this, 'ajustWidth', {
            get: function () {
                return ajustWidth;
            }
        });

        function ajustTop(w, aValue) {
        }
        Object.defineProperty(this, 'ajustTop', {
            get: function () {
                return ajustTop;
            }
        });
        function ajustHeight(w, aValue) {
        }
        Object.defineProperty(this, 'ajustHeight', {
            get: function () {
                return ajustHeight;
            }
        });
    }
    extend(Cards, Container);
    return Cards;
});