package de.unipassau.im.ontoint.proposalComputer;

import java.util.Collection;
import java.util.Hashtable;
import java.util.Map;

public class DetailedClassification<T, K> {

    /**
     * The classified featureset.
     */
    private Collection<T> featureset;

    /**
     * The category as which the featureset was classified.
     */
    private Map<K, Float> categoryProbabilities;

    /**
     * The probability that the featureset belongs to the given category.
     */
    private float probability;

    /**
     * Constructs a new DetailedClassification with the parameters given.
     *
     * @param features the featureset
     * @param classifications a collection of classifications for the given
     *  featureset
     */
    public DetailedClassification(Collection<T> features, Collection<Classification<T, K>> classifications) {
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
        return this.categoryProbabilities.get(category);
    }

}

