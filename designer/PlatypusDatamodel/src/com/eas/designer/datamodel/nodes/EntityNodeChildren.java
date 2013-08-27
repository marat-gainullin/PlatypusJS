/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.datamodel.nodes;

import com.bearsoft.rowset.metadata.Field;
import com.bearsoft.rowset.metadata.Fields;
import com.bearsoft.rowset.utils.CollectionListener;
import com.eas.client.model.Entity;
import com.eas.designer.datamodel.nodes.EntityNodeChildren.EntityFieldKey;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.openide.awt.UndoRedo;
import org.openide.nodes.Children;
import org.openide.util.Lookup;

/**
 *
 * @author vv
 */
public abstract class EntityNodeChildren<T> extends Children.Keys<T> implements PropertyChangeListener {

    protected Entity entity;
    protected Set<T> keys = new HashSet<>();
    private Fields tempFields;
    protected CollectionListener<Fields, Field> fieldsListener = new FieldsToNodesNotifier();
    protected Lookup lookup;
    protected UndoRedo.Manager undoRedo;

    public EntityNodeChildren(Entity anEnity, UndoRedo.Manager aUndoReciever, Lookup aLookup) {
        super();
        entity = anEnity;
        lookup = aLookup;
        undoRedo = aUndoReciever;
        setFields(entity.getFields());
        entity.getModel().getChangeSupport().addPropertyChangeListener("client", this);
    }

    protected abstract T createKey(Field aField);

    protected Set<T> computeKeys() {
        keys.clear();
        try {
            if (entity.getQuery() != null) {
                for (int i = 1; i <= entity.getQuery().getParameters().getParametersCount(); i++) {
                    keys.add(createKey(entity.getQuery().getParameters().get(i)));
                }
            }
        } catch (Exception ex) {
            Logger.getLogger(EntityNodeChildren.class.getName()).log(Level.SEVERE, "Error getting Query.", ex);
        }
        Fields fields = entity.getFields();
        if (fields != null) {
            for (int i = 1; i <= entity.getFields().getFieldsCount(); i++) {
                keys.add(createKey(entity.getFields().get(i)));
            }
        }
        return keys;
    }

    @Override
    protected void addNotify() {
        setKeys(computeKeys());
    }

    @Override
    protected void removeNotify() {
        entity.getModel().getChangeSupport().removePropertyChangeListener(this);
        setFields(null);
        setKeys(Collections.EMPTY_SET);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if ("client".equalsIgnoreCase(evt.getPropertyName())) {
            setFields(entity.getFields());
            setKeys(computeKeys());
        }
    }

    private void setFields(Fields aFields) {
        if (tempFields != null) {
            tempFields.getCollectionSupport().removeListener(fieldsListener);
        }
        tempFields = aFields;
        if (tempFields != null) {
            tempFields.getCollectionSupport().addListener(fieldsListener);
        }
    }

    protected class FieldsToNodesNotifier implements CollectionListener<Fields, Field> {

        @Override
        public void added(Fields c, Field v) {
            keys.add(createKey(v));
            setKeys(keys);
        }

        @Override
        public void added(Fields c, Collection<Field> clctn) {
            setKeys(computeKeys());
        }

        @Override
        public void removed(Fields c, Field v) {
            keys.remove(createKey(v));
            setKeys(keys);
        }

        @Override
        public void removed(Fields c, Collection<Field> clctn) {
            setKeys(computeKeys());
        }

        @Override
        public void cleared(Fields c) {
            setKeys(computeKeys());
        }

        @Override
        public void reodered(Fields c) {
            setKeys(computeKeys());
        }
    }

    public static class EntityFieldKey {

        public final Field field;

        public EntityFieldKey(Field aField) {
            field = aField;
        }

        @Override
        public int hashCode() {
            return System.identityHashCode(field);
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final EntityFieldKey other = (EntityFieldKey) obj;
            return other.field == field;
        }
    }
}
