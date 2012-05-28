package de.unipassau.im.ontoint.proposals;

/**
 * A feature can have a special weight for the feature weighted average
 * calculation.
 */
public interface IWeightedFeature {

    /**
     * Retrieves this feature's weight.
     */
    float getWeight();

}
