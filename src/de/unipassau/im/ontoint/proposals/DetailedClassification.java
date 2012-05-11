package de.unipassau.im.ontoint.proposals;

import java.util.Collection;
import java.util.Hashtable;
import java.util.Map;

/**
 * Instances of this class wrap a collection of classifications in order to
 * give easy access to the mapping of categories and their probabilities.
 *
 * @author Philipp Nolte
 *
 * @param <T> the feature class
 * @param <K> the category class
 */
public final class DetailedClassification<T, K> {

    /**
     * The classified featureset.
     */
    private Collection<T> featureset;

    /**
     * The category as which the featureset was classified.
     */
    private Map<K, Float> categoryProbabilities;

    /**
     * Constructs a new DetailedClassification with the parameters given.
     *
     * @param features the featureset
     * @param classifications a collection of classifications for the given
     *  featureset
     */
    public DetailedClassification(final Collection<T> features,
            final Collection<Classification<T, K>> classifications) {
        this.featureset = features;
        this.categoryProbabilities = new Hashtable<K, Float>();
        for (Classification<T, K> c : classifications)
            this.categoryProbabilities.put(c.getCategory(), c.getProbability());
    }

    /**
     * Retrieves the featureset classified.
     *
     * @return The featureset.
     */
    public Collection<T> getFeatureset() {
        return this.featureset;
    }

    /**
     * Retrieves the classification's probability.
     * @return
     */
    public float getProbabilityFor(K category) {
        Float toReturn = this.categoryProbabilities.get(category);
        if (toReturn == null)
            return 0.0f;
        return toReturn.floatValue();
    }

}

