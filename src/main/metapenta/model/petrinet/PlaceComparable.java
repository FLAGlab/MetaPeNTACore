package metapenta.model.petrinet;

public final class PlaceComparable<O> extends Place<O> implements Comparable<PlaceComparable<O>> {
    private int priority;

    public PlaceComparable(Place<O> place, int priority) {
        super(place);

        this.priority = priority;
    }

    @Override
    public int compareTo(PlaceComparable placeComparable) {
        return this.priority - placeComparable.priority;
    }
}
