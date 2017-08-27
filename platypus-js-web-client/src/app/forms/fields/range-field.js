define([
    '../../extend',
    './number-field'], function (
        extend,
        NumberField) {
    function RangeField(shell) {
        var box = document.createElement('input');
        box.type = 'range';
        
        NumberField.call(this, box, shell);
        var self = this;

    }
    extend(RangeField, NumberField);
    return RangeField;
});
