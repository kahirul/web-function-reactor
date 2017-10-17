package co.paikama.wfa;

import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.web.reactive.function.BodyInserters.fromObject;

public class PersonHandler {

    private final PersonRepository repository;

    public PersonHandler(PersonRepository repository) {
        this.repository = repository;
    }

    public Mono<ServerResponse> get(ServerRequest request) {
        final Integer id = Integer.valueOf(request.pathVariable("id"));
        final Mono<ServerResponse> notFound = ServerResponse.notFound().build();

        final Mono<Person> personMono = repository.get(id);
        return personMono.flatMap(person ->
                ServerResponse.ok().contentType(APPLICATION_JSON).body(fromObject(person))
        ).switchIfEmpty(notFound);
    }

    public Mono<ServerResponse> all(ServerRequest request) {
        final Flux<Person> personFlux = repository.all();
        return ServerResponse.ok().contentType(APPLICATION_JSON).body(personFlux, Person.class);
    }


    public Mono<ServerResponse> save(ServerRequest request) {
        final Mono<Person> personMono = request.bodyToMono(Person.class);
        return ServerResponse.ok().build(repository.save(personMono));
    }
}
