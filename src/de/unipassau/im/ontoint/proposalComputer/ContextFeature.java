package de.unipassau.im.ontoint.proposalComputer;

public class ContextFeature {

    private Object value;

    public static enum Feature {
        ENVIRONMENTSTRING
    }

    public ContextFeature(ContextFeature.Feature type, Object featureValue) {
        this.value = featureValue;
    }

}
