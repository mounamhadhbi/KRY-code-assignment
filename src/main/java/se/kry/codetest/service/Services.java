package se.kry.codetest.service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

public class Services implements Iterable<Service> {

    private List<Service> services;

    public Services(ArrayList<Service> services) {
        this.services = new ArrayList<Service>(services);
    }

    public static Services of(List<Service> services) {
        return new Services(new ArrayList<>(services));
    }

    public Services add(Service service) {
        services.add(service);
        return this;
    }

    public Stream<Service> stream() {
        return services.stream();
    }

    public List<Service> toList() {
        return services;
    }

    public Services remove(Service service) {
        this.services.remove(service);
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Services that = (Services) o;
        return services.equals(that.services);
    }

    @Override
    public int hashCode() {
        return Objects.hash(services);
    }

    @Override
    public Iterator iterator() {
        return services.iterator();
    }


}
