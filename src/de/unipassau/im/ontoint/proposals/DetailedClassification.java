package de.unipassau.im.ontoint.proposals;

import java.util.Collection;
import java.util.Hashtable;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedSet;

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
     * FOR EVALUATION PURPOSE ONLY! Maps the positions in the sorted set to the
     * category.
     */
    private Map<K, Integer> categoryPositions;

    /**
     * Constructs a new DetailedClassification with the parameters given.
     *
     * @param features the featureset
     * @param classifications a collection of classifications for the given
     *  featureset
     */
    public DetailedClassification(final Collection<T> features,
            final SortedSet<Classification<T, K>> classifications) {
        this.featureset = features;
        this.categoryProbabilities = new Hashtable<K, Float>();
        this.categoryPositions = new Hashtable<K, Integer>();

        int position = classifications.size();
        for (Classification<T, K> c : classifications) {
            this.categoryProbabilities.put(c.getCategory(), c.getProbability());
            this.categoryPositions.put(c.getCategory(), new Integer(position));
            position--;
        }
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
     *
     * @param category the category
     * @return the probability
     */
    public float getProbabilityFor(final K category) {
        Float toReturn = this.categoryProbabilities.get(category);
        if (toReturn == null)
            return 0.0f;
        return toReturn.floatValue();
    }

    /**
     * FOR EVALUATION PURPOSES ONLY. Returns the categories's position in the
     * sorted classification set.
     *
     * @param category the category
     * @return the position
     */
    public int getPositionFor(final K category) {
        Integer toReturn = this.categoryPositions.get(category);
        if (toReturn == null)
            return this.categoryPositions.size();
        return toReturn.intValue();
    }

    /**
     * FOR EVALUATION PURPOSES ONLY. Returns the category at the given position.
     *
     * @param position the position
     * @return the category
     */
    public K getCategoryAtPosition(final int position) {
        for (Entry<K, Integer> e : this.categoryPositions.entrySet()) {
            if (e.getValue().intValue() == position)
                return e.getKey();
        }
        return null;
    }

}

