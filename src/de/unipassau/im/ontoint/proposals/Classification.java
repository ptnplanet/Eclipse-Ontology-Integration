package de.unipassau.im.ontoint.proposals;

import java.io.Serializable;
import java.util.Collection;

/**
 * A basic wrapper reflecting a classification.  It will store both featureset
 * and resulting classification.
 *
 * @author Philipp Nolte
 *
 * @param <T> The feature class.
 * @param <K> The category class.
 */
public final class Classification<T, K> implements Serializable {

    /**
     * SUID.
     */
    private static final long serialVersionUID = -7946903194210558214L;

    /**
     * The classified featureset.
     */
    private Collection<T> featureset;

    /**
     * The category as which the featureset was classified.
     */
    private K category;

    /**
     * The probability that the featureset belongs to the given category.
     */
    private float probability;

    /**
     * Constructs a new Classification with the parameters given and a default
     * probability of 0.
     *
     * @param features The featureset.
     * @param cat The category.
     */
    public Classification(final Collection<T> features, final K cat) {
        this(features, cat, 0.0f);
    }

    /**
     * Constructs a new Classification with the parameters given.
     *
     * @param features the featureset
     * @param cat the category
     * @param prob the probability
     */
    public Classification(final Collection<T> features, final K cat,
            final float prob) {
        this.featureset = features;
        this.category = cat;
        this.probability = prob;
    }

    /**
     * Retrieves the featureset classified.
     *
     * @return the featureset
     */
    public Collection<T> getFeatureset() {
        return featureset;
    }

    /**
     * Retrieves the classification's probability.
     *
     * @return the probability
     */
    public float getProbability() {
        return this.probability;
    }

    /**
     * Retrieves the category the featureset was classified as.
     *
     * @return the category
     */
    public K getCategory() {
        return category;
    }

}

