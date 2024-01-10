package uk.co.jasonmarston.gateway.adaptor.input;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import io.quarkus.oidc.client.filter.OidcClientFilter;
import io.smallrye.mutiny.Uni;
import io.vertx.core.http.HttpServerRequest;
import jakarta.annotation.security.PermitAll;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import uk.co.jasonmarston.gateway.adaptor.input.service.AuthService;
import uk.co.jasonmarston.gateway.adaptor.input.service.TokenService;
import uk.co.jasonmarston.gateway.usecase.GatewayUseCase;
import uk.co.jasonmarston.gateway.valueobject.Destination;
import uk.co.jasonmarston.gateway.valueobject.Payload;

@ApplicationScoped
@Path("/")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@OidcClientFilter
@PermitAll
public class GatewayInputAdaptor {
    private static final Uni<Response> UNAUTHORIZED = Uni
        .createFrom()
        .item(Response
            .status(Status.UNAUTHORIZED)
            .build()
        );

    @Inject
    private AuthService authService;

    @Inject
    private TokenService tokenService;

    @Inject
    private GatewayUseCase gatewayUseCase;

    @ConfigProperty(
        name="gateway.api.root",
        defaultValue = "client-api"
    )
    private String apiRoot;

    @POST
    @Path("{path:.*}")
    public Uni<Response> post(
        final String body,
        @PathParam("path") final String path,
        @Context final HttpServerRequest request
    ) {
        if(authService.unAuthorized()) {
            return UNAUTHORIZED;
        }
        final Destination destination = DestinationBuilder
            .buildDestination(
                apiRoot,
                path,
                request.query()
            );
        final Payload payload = Payload
            .builder()
            .body(body)
            .token(tokenService.generateToken())
            .build();
        return gatewayUseCase
            .post(destination, payload);
    }

    @GET
    @Path("{path:.*}")
    public Uni<Response> get(
        @PathParam("path") final String path,
        @Context final HttpServerRequest request
    ) {
        if(authService.unAuthorized()) {
            return UNAUTHORIZED;
        }
        final Destination destination = DestinationBuilder
            .buildDestination(
                apiRoot,
                path,
                request.query()
            );
        final Payload payload = Payload
            .builder()
            .token(tokenService.generateToken())
            .build();
        return gatewayUseCase
            .get(destination, payload);
    }

    @PUT
    @Path("{path:.*}")
    public Uni<Response> put(
        final String body,
        @PathParam("path") final String path,
        @Context final HttpServerRequest request
    ) {
        if(authService.unAuthorized()) {
            return UNAUTHORIZED;
        }
        final Destination destination = DestinationBuilder
            .buildDestination(
                apiRoot,
                path,
                request.query()
            );
        final Payload payload = Payload
            .builder()
            .body(body)
            .token(tokenService.generateToken())
            .build();
        return gatewayUseCase
            .put(destination, payload);
    }

    @DELETE
    @Path("{path:.*}")
    public Uni<Response> delete(
        @PathParam("path") final String path,
        @Context final HttpServerRequest request
    ) {
        if(authService.unAuthorized()) {
            return UNAUTHORIZED;
        }
        final Destination destination = DestinationBuilder
            .buildDestination(
                apiRoot,
                path,
                request.query()
            );
        final Payload payload = Payload
            .builder()
            .token(tokenService.generateToken())
            .build();
        return gatewayUseCase
            .delete(destination, payload);
    }
}
