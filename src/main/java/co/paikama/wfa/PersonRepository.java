package co.paikama.wfa;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface PersonRepository {

    Mono<Person> get(Integer id);

    Flux<Person> all();

    Mono<Void> save(Mono<Person> person);
}
