package de.unipassau.im.ontoint.proposals;

import java.io.Serializable;

/**
 * A feature of a given context.  Features will be extracted from the context
 * within the {@link CompletionProposalComputer} instance and stored by the
 * {@link Classifier} instance used.
 *
 * @author Philipp Nolte
 */
public final class ContextFeature implements Serializable, IWeightedFeature {

    /**
     * SUID.
     */
    private static final long serialVersionUID = -2040479825727826019L;

    /**
     * Features can be of different type.
     */
    public static enum Feature {

        /**
         * A basic String found in the context.
         */
        ENVIRONMENTSTRING

    }

    /**
     * The feature weights.
     */
    public static float[] FeatureWeight = new float[] {

        /**
         * ENVIRONMENTSTRING
         */
        1.0f
    };

    /**
     * The type of this feature.
     */
    private ContextFeature.Feature type;

    /**
     * The value of this feature.
     */
    private Serializable value;

    /**
     * Constructs a new context feature of given type and with given value.
     *
     * @param featureType the type of the feature
     * @param featureValue the value of the feature
     */
    public ContextFeature(final ContextFeature.Feature featureType,
            final Serializable featureValue) {
        this.type = featureType;
        this.value = featureValue;
    }

    /**
     * {@inheritDoc}
     */
    public int hashCode() {
        return this.value.hashCode() * this.type.hashCode();
    }

    /**
     * {@inheritDoc}
     */
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final ContextFeature other = (ContextFeature) obj;
        if (type != other.type)
            return false;
        if (value == null) {
            if (other.value != null)
                return false;
        } else if (!value.equals(other.value))
            return false;
        return true;
    }

    /**
     * Gets the feature's type.
     *
     * @return the type
     */
    public ContextFeature.Feature getFeatureType() {
        return this.type;
    }

    /**
     * Gets the feature's value.
     *
     * @return the value
     */
    public Serializable getValue() {
        return this.value;
    }

    /**
     * {@inheritDoc}
     */
    public float getWeight() {
        return ContextFeature.FeatureWeight[this.getFeatureType().ordinal()];
    }

}
