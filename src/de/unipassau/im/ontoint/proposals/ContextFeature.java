package de.unipassau.im.ontoint.proposals;

public class ContextFeature {

    private ContextFeature.Feature featureType;

    private Object value;

    public static enum Feature {
        ENVIRONMENTSTRING
    }

    public ContextFeature(ContextFeature.Feature type, Object featureValue) {
        this.featureType = type;
        this.value = featureValue;
    }

    public int hashCode() {
        return this.value.hashCode() * this.featureType.hashCode();
    }

    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final ContextFeature other = (ContextFeature) obj;
        if (featureType != other.featureType)
            return false;
        if (value == null) {
            if (other.value != null)
                return false;
        } else if (!value.equals(other.value))
            return false;
        return true;
    }
    
    public String toString() {
        return this.featureType.toString() + ":" + this.value.toString();
    }

}
