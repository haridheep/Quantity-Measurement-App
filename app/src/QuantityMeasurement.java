@Override
public boolean equals(Object obj) {
    if (this == obj) return true;
    if (!(obj instanceof QuantityLength)) return false;

    QuantityLength other = (QuantityLength) obj;
    return Double.compare(this.toBaseUnit(), other.toBaseUnit()) == 0;
}

@Override
public int hashCode() {
    return Double.hashCode(this.toBaseUnit());
}