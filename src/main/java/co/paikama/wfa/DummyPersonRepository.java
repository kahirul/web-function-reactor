package co.paikama.wfa;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

public class DummyPersonRepository implements PersonRepository {

    private final Map<Integer, Person> people = new HashMap<>();

    public DummyPersonRepository() {
        people.put(1, new Person("John Doe", 42));
        people.put(2, new Person("Jane Doe", 36));
    }

    @Override
    public Mono<Person> get(Integer id) {
        return Mono.justOrEmpty(people.get(id));
    }

    @Override
    public Flux<Person> all() {
        return Flux.fromIterable(people.values());
    }

    @Override
    public Mono<Void> save(Mono<Person> personMono) {
        return personMono.doOnNext(person -> {
            Integer id = people.size() + 1;
            people.put(id, person);
            System.out.format("Saved %s witd id %d\n", person, id);
        }).thenEmpty(Mono.empty());
    }
}
