package de.unipassau.im.ontoint.proposalComputer;

import java.util.Collection;
import java.util.Comparator;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * A concrete implementation of the abstract Classifier class.  The Bayes
 * classifier implements a naive Bayes approach to classifying a given set of
 * features: classify(feat1,...,featN) = argmax(P(cat)*PROD(P(featI|cat)
 *
 * This implementation also includes basic caching of category probability
 * values. The cache lifetime expires as soon as the classifier learns new
 * classifications.
 *
 * @author Philipp Nolte
 *
 * @param <T> The feature class.
 * @param <K> The category class.
 */
public final class BayesClassifier<T, K> extends Classifier<T, K> {

    /**
     * <code>true</code> if the cache values are up-to-date.
     */
    private boolean cacheValid = false;

    /**
     * Cached classification values.
     */
    private SortedSet<Classification<T, K>> cache;

    /**
     * Calculates the product of all feature probabilities: PROD(P(featI|cat).
     *
     * @param features The set of features to use.
     * @param category The category to test for.
     * @return The product of all feature probabilities.
     */
    private float featuresProbabilityProduct(final Collection<T> features,
            final K category) {
        float product = 1.0f;
        for (T feature : features)
            product *= this.featureWeighedAverage(feature, category);
        return product;
    }

    /**
     * Calculates the probability that the features can be classified as the
     * category given.
     *
     * @param features The set of features to use.
     * @param category The category to test for.
     * @return The probability that the features can be classified as the
     *    category.
     */
    private float categoryProbability(final Collection<T> features,
            final K category) {
        return ((float) this.categoryCount(category)
                    / (float) this.getCategoriesTotal())
                * this.featuresProbabilityProduct(features, category);
    }

    /**
     * Retrieves a sorted <code>Set</code> of probabilities that the given set
     * of features is classified as the available categories.
     *
     * @param features The set of features to use.
     * @return A sorted <code>Set</code> of category-probability-entries.
     */
    private SortedSet<Classification<T, K>> categoryProbabilities(
            final Collection<T> features) {

        /*
         * Sort the set according to the possibilities. Because we have to sort
         * by the mapped value and not by the mapped key, we can not use a
         * sorted tree (TreeMap) and we have to use a set-entry approach to
         * achieve the desired functionality. A custom comparator is therefore
         * needed.
         */
        SortedSet<Classification<T, K>> probabilities =
                new TreeSet<Classification<T, K>>(
                        new Comparator<Classification<T, K>>() {

                    /**
                     * {@inheritDoc}
                     */
                    public int compare(final Classification<T, K> o1,
                            final Classification<T, K> o2) {
                        int toReturn = Float.compare(
                                o1.getProbability(), o2.getProbability());
                        if ((toReturn == 0)
                                && !o1.getCategory().equals(o2.getCategory()))
                            toReturn = -1;
                        return toReturn;
                    }
                });

        for (K category : this.getCategories())
            probabilities.add(new Classification<T, K>(
                    features, category,
                    this.categoryProbability(features, category)));
        return probabilities;
    }

    /**
     * Retrieves a valid set of category probabilities.
     *
     * @param features the featureset
     * @return the values (may be cached)
     */
    private SortedSet<Classification<T, K>> cachedCategoryProbabilities(
            final Collection<T> features) {
        if (!cacheValid) {
            this.cache = this.categoryProbabilities(features);
            this.cacheValid = true;
        }
        return this.cache;
    }

    /**
     * {@inheritDoc}
     */
    public Classification<T, K> classify(final Collection<T> features) {
        SortedSet<Classification<T, K>> probabilites =
                this.cachedCategoryProbabilities(features);

        if (probabilites.size() > 0) {
            return probabilites.last();
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public DetailedClassification<T, K> classifyDetailed(
            final Collection<T> features) {
        return new DetailedClassification<T, K>(features,
                this.cachedCategoryProbabilities(features));
    }

    /**
     * {@inheritDoc}
     */
    public void setMemoryCapacity(final int memoryCapacity) {
        this.cacheValid = false;
        super.setMemoryCapacity(memoryCapacity);
    }

    /**
     * {@inheritDoc}
     */
    public void learn(final K category, final Collection<T> features) {
        this.cacheValid = false;
        super.learn(category, features);
    }

    /**
     * {@inheritDoc}
     */
    public void learn(final Classification<T, K> classification) {
        this.cacheValid = false;
        super.learn(classification);
    }

}
