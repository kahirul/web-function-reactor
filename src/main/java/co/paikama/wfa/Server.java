package co.paikama.wfa;

import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.startup.Tomcat;
import org.springframework.http.server.reactive.HttpHandler;
import org.springframework.http.server.reactive.ReactorHttpHandlerAdapter;
import org.springframework.http.server.reactive.ServletHttpHandlerAdapter;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.ipc.netty.http.server.HttpServer;

import java.io.IOException;

import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RequestPredicates.accept;
import static org.springframework.web.reactive.function.server.RequestPredicates.contentType;
import static org.springframework.web.reactive.function.server.RequestPredicates.method;
import static org.springframework.web.reactive.function.server.RequestPredicates.path;
import static org.springframework.web.reactive.function.server.RouterFunctions.*;

public class Server {

    static final String HOST = "localhost";
    static final Integer PORT = 8080;

    public static void main(String[] args) throws IOException, LifecycleException {

        final Server server = new Server();
        server.startReactorServer();
        // server.startTomcatServer();

        System.out.println("Press ENTER to exit.");
        System.in.read();
    }


    private void startReactorServer() {
        final RouterFunction<ServerResponse> route = routingFunction();
        final HttpHandler httpHandler = toHttpHandler(route);

        final ReactorHttpHandlerAdapter adapter = new ReactorHttpHandlerAdapter(httpHandler);
        final HttpServer server = HttpServer.create(HOST, PORT);
        server.newHandler(adapter).block();
    }

    private void startTomcatServer() throws LifecycleException {
        final RouterFunction<ServerResponse> route = routingFunction();
        final HttpHandler httpHandler = toHttpHandler(route);

        final Tomcat tomcatServer = new Tomcat();
        tomcatServer.setHostname(HOST);
        tomcatServer.setPort(PORT);

        final Context rootContext = tomcatServer.addContext("", System.getProperty("java.io.tmpdir"));
        final ServletHttpHandlerAdapter servlet = new ServletHttpHandlerAdapter(httpHandler);

        Tomcat.addServlet(rootContext, "httpHandlerServlet", servlet);
        rootContext.addServletMappingDecoded("/", "httpHandlerServlet");
        tomcatServer.start();
    }

    private RouterFunction<ServerResponse> routingFunction() {
        final PersonRepository repository = new DummyPersonRepository();
        final PersonHandler handler = new PersonHandler(repository);

        return nest(path("/person"),
                nest(accept(APPLICATION_JSON),
                        route(GET("/{id}"), handler::get)
                                .andRoute(method(GET), handler::all)
                ).andRoute(POST("/").and(contentType(APPLICATION_JSON)), handler::save));
    }
}
