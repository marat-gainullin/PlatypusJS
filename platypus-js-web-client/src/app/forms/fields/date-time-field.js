define([
    '../../extend',
    '../i18n',
    './box-field'], function (
        extend,
        i18n,
        BoxField) {
    function DateTimeField(shell) {
        var box = document.createElement('input');
        box.type = 'datetime-local';
        
        BoxField.call(this, box, shell);
        var self = this;
        var value = null;

        function parse(source) {
            return new Date(source);
        }

        function format(date) {
            var formatted = new Date(-(new Date()).getTimezoneOffset() * 60000 + date.valueOf()).toJSON();
            var zi = formatted.indexOf('Z');
            if (zi > -1) {
                return formatted.substring(0, zi);
            } else {
                return formatted;
            }
        }

        function textChanged() {
            var oldValue = value;
            if (box.value !== '') {
                var parsed = parse(box.value);
                if (isNaN(parsed.valueOf())) {
                    self.error = i18n['not.a.datetime'] + '(' + box.value + ')';
                } else {
                    value = parsed;
                }
            } else {
                value = null;
            }
            if (value !== oldValue) {
                self.fireValueChanged(oldValue);
            }
        }

        Object.defineProperty(this, 'textChanged', {
            enumerable: false,
            get: function () {
                return textChanged;
            }
        });

        Object.defineProperty(this, 'text', {
            get: function () {
                return box.value;
            },
            set: function (aValue) {
                if (box.value !== aValue) {
                    box.value = aValue;
                    textChanged();
                }
            }
        });

        Object.defineProperty(this, 'value', {
            get: function () {
                return value;
            },
            set: function (aValue) {
                if (value !== aValue) {
                    var oldValue = value;
                    value = aValue;
                    box.value = value !== null ? format(value) : '';
                    self.fireValueChanged(oldValue);
                }
            }
        });
    }
    extend(DateTimeField, BoxField);
    return DateTimeField;
});
