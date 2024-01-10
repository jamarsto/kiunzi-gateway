package uk.co.jasonmarston.gateway.adaptor.output;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import io.smallrye.mutiny.Uni;
import io.vertx.core.http.HttpClientOptions;
import io.vertx.core.http.HttpMethod;
import io.vertx.mutiny.core.Vertx;
import io.vertx.mutiny.core.buffer.Buffer;
import io.vertx.mutiny.core.http.HttpClient;
import io.vertx.mutiny.core.http.HttpClientRequest;
import io.vertx.mutiny.core.http.HttpClientResponse;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import uk.co.jasonmarston.gateway.output.port.GatewayOutputPort;
import uk.co.jasonmarston.gateway.valueobject.Destination;
import uk.co.jasonmarston.gateway.valueobject.Payload;

@ApplicationScoped
public class GatewayOutputAdaptor implements GatewayOutputPort {
    private static final String BEARER_PREFIX = "Bearer ";
    private static final Response NOT_FOUND = Response
        .status(Status.NOT_FOUND)
        .build();

    private final int port;
    private final HttpClient httpClient;

    @Inject
    public GatewayOutputAdaptor(
        final Vertx vertx,
        @ConfigProperty(
            name="gateway.api.ssl.enabled",
            defaultValue = "false"
        )
        final boolean isSslEnabled,
        @ConfigProperty(
            name="gateway.api.port",
            defaultValue = "8080"
        )
        final int port
    ) {
        this.port = port;
        final HttpClientOptions options = new HttpClientOptions()
            .setSsl(isSslEnabled);
        this.httpClient = vertx.createHttpClient(options);
    }

    @Override
    public Uni<Response> post(
        final Destination destination,
        final Payload payload
    ) {
        return execute(HttpMethod.POST, destination, payload);
    }

    @Override
    public Uni<Response> get(
        final Destination destination,
        final Payload payload
    ) {
        return execute(HttpMethod.GET, destination, payload);
    }

    @Override
    public Uni<Response> put(
        final Destination destination,
        final Payload payload
    ) {
        return execute(HttpMethod.PUT, destination, payload);
    }

    @Override
    public Uni<Response> patch(
            final Destination destination,
            final Payload payload
    ) {
        return execute(HttpMethod.PATCH, destination, payload);
    }

    @Override
    public Uni<Response> delete(
        final Destination destination,
        final Payload payload
    ) {
        return execute(HttpMethod.DELETE, destination, payload);
    }

    private Uni<Response> execute(
        final HttpMethod httpMethod,
        final Destination destination,
        final Payload payload
    ) {
        return httpClient
            .request(
                httpMethod,
                port,
                destination.getHostName(),
                destination.getPath()
            )
            .onItem()
            .transform(clientRequest -> setHeaders(
                clientRequest,
                httpMethod,
                payload
            ))
            .onItem()
            .transformToUni(clientRequest -> sendRequest(
                clientRequest,
                httpMethod,
                payload
            ))
            .onItem()
            .transformToUni(this::buildResponse)
            .onFailure()
            .recoverWithItem(() -> NOT_FOUND);
    }

    private HttpClientRequest setHeaders(
        final HttpClientRequest clientRequest,
        final HttpMethod httpMethod,
        final Payload payload
    ) {
        // update this to pass along the headers from the original request except for exclusions. Exclusions default to Cookies, Set-Cookie, and Authorization (that one we replace as done at end of method)
        if(HttpMethod.POST.equals(httpMethod) ||
            HttpMethod.PUT.equals(httpMethod) ||
            HttpMethod.PATCH.equals(httpMethod)
        ) {
            clientRequest
                .putHeader(
                    HttpHeaders.CONTENT_TYPE,
                    MediaType.APPLICATION_JSON
                );
        }
        clientRequest.putHeader(
            HttpHeaders.ACCEPT,
            MediaType.APPLICATION_JSON
        );
        clientRequest
            .putHeader(
                HttpHeaders.AUTHORIZATION,
                BEARER_PREFIX + payload.getToken()
            );
        return clientRequest;
    }

    private Uni<HttpClientResponse> sendRequest(
        final HttpClientRequest clientRequest,
        final HttpMethod httpMethod,
        final Payload payload
    ) {
        if(HttpMethod.GET.equals(httpMethod) ||
            HttpMethod.DELETE.equals(httpMethod)
        ) {
            return clientRequest.send();
        }
        return clientRequest.send(payload.getBody());
    }

    private Uni<Response> buildResponse(
        final HttpClientResponse clientResponse
    ) {
        return clientResponse
            .body()
            .onItem()
            .transform(body -> buildSuccessResponse(
                clientResponse,
                body
            ))
            .onFailure()
            .recoverWithItem(t -> buildFailureResponse(
                clientResponse
            ));
    }

    private Response buildSuccessResponse(
        final HttpClientResponse clientResponse,
        final Buffer body
    ) {
        return Response
            .ok(body.toString())
            .status(clientResponse.statusCode())
            .type(clientResponse
                .getHeader(HttpHeaders.CONTENT_TYPE)
            )
            .build();
    }

    private Response buildFailureResponse(
        final HttpClientResponse clientResponse
    ) {
        return Response
            .status(clientResponse.statusCode())
            .type(clientResponse
                .getHeader(HttpHeaders.CONTENT_TYPE)
            )
            .build();
    }
}
