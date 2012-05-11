package de.unipassau.im.ontoint.proposals;

/**
 * Simple interface defining the method to calculate the feature probability.
 *
 * @author Philipp Nolte
 *
 * @param <T> The feature class.
 * @param <K> The category class.
 */
public interface IFeatureProbability<T, K> {

    /**
     * Calculates the probability for the given feature to appear in the given
     * category:
     *   number of occurrences of feature in category
     * / number of total occurences of category
     *
     * @param feature
     * @param category
     * @return
     */
    public float featureProbability(T feature, K category);

}
