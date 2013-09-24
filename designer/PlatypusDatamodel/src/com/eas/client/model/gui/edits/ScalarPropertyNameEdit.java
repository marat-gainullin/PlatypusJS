/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.model.gui.edits;

import com.eas.client.model.application.ReferenceRelation;

/**
 *
 * @author mg
 */
public class ScalarPropertyNameEdit extends DatamodelEdit {

    protected ReferenceRelation<?> relation;
    protected String oldValue;
    protected String newValue;

    public ScalarPropertyNameEdit(ReferenceRelation<?> aRelation, String aOldValue, String aNewValue) {
        super();
        relation = aRelation;
        oldValue = aOldValue;
        newValue = aNewValue;
    }

    @Override
    protected void redoWork() {
        relation.setScalarPropertyName(newValue);
    }

    @Override
    protected void undoWork() {
        relation.setScalarPropertyName(oldValue);
    }
}
