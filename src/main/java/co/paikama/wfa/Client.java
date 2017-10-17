package co.paikama.wfa;

import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFunction;
import org.springframework.web.reactive.function.client.ExchangeFunctions;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.List;

import static co.paikama.wfa.Server.HOST;
import static co.paikama.wfa.Server.PORT;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;

public class Client {

    private final ExchangeFunction exchange = ExchangeFunctions.create(new ReactorClientHttpConnector());

    public static void main(String[] args) {
        final Client client = new Client();
        client.createPerson();
        client.printAllPeople();
    }

    private void createPerson() {
        final URI uri = URI.create(String.format("http://%s:%d/person", HOST, PORT));
        final Person jackDoe = new Person("Jack Doe", 16);

        final ClientRequest request = ClientRequest.method(POST, uri)
                .body(BodyInserters.fromObject(jackDoe)).build();

        final Mono<ClientResponse> responseMono = exchange.exchange(request);
        System.out.println(responseMono.block().statusCode());
    }

    private void printAllPeople() {
        final URI uri = URI.create(String.format("http://%s:%d/person", HOST, PORT));
        final ClientRequest request = ClientRequest.method(GET, uri).build();

        final Flux<Person> peopleFlux = exchange.exchange(request)
                .flatMapMany(clientResponse -> clientResponse.bodyToFlux(Person.class));

        final Mono<List<Person>> peopleListMono = peopleFlux.collectList();
        System.out.println(peopleListMono.block());
    }


}
