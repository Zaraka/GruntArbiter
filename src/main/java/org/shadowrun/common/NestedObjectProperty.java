package org.shadowrun.common;

import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.beans.value.WeakChangeListener;

import java.util.function.Function;

/**
 * A Property which is unidirectionally or bidirectionally bound to a property of a property. What that means:
 * You have a property A. The object contained in that property A has itself a property B.
 * If you want to bind to B, you have the problem, that if property A changes, your binding
 * is invalid, but you don't recognize it until you have a change listener to A and rebind to
 * the new B'. Exactly that does that class for you.
 *
 * @param <T> the type of the property itself
 * @param <D> the type of the property, that delivers the property to bind against
 */
public class NestedObjectProperty<T, D> extends SimpleObjectProperty<T> {
    /**
     * The binding partner of this instance
     */
    private Property<T> boundProperty;

    /**
     * Creates a new instance
     *
     * @param dependandProperty the property that delivers the property to bind against
     * @param getFunc           a {@link java.util.function.Function} of D and ObjectProperty of T that returns the property to bind against
     * @param bidirectional     boolean flag, if the binding should be bidirectional or unidirectional
     */
    public NestedObjectProperty(ObservableValue<D> dependandProperty, final Function<D, Property<T>> getFunc,
                                boolean bidirectional) {
        bindDependandProperty(dependandProperty.getValue(), getFunc, bidirectional);

        dependandProperty.addListener(new WeakChangeListener<>((observable, oldValue, newValue) -> {
            if (boundProperty != null) {
                if (bidirectional) {
                    unbindBidirectional(boundProperty);
                } else {
                    unbind();
                }
                boundProperty = null;
            }
            bindDependandProperty(newValue, getFunc, bidirectional);
        }));
    }

    /**
     * Binds this instance to the dependant property
     *
     * @param newValue      D
     * @param getFunc       Function<D and ObjectProperty of T
     * @param bidirectional boolean flag, if the binding should be bidirectional or unidirectional
     */
    private void bindDependandProperty(D newValue, Function<D, Property<T>> getFunc, boolean bidirectional) {
        if (newValue != null) {
            Property<T> newProp = getFunc.apply(newValue);
            boundProperty = newProp;
            if (newProp != null) {
                if (bidirectional) {
                    bindBidirectional(newProp);
                } else {
                    bind(newProp);
                }
            } else {
                set(null);
            }
        } else {
            set(null);
        }
    }
}