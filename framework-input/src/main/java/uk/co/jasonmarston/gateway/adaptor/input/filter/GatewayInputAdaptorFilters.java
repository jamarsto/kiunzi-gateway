package uk.co.jasonmarston.gateway.adaptor.input.filter;

import io.smallrye.mutiny.Uni;
import jakarta.annotation.Priority;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.resteasy.reactive.server.ServerRequestFilter;
import org.jboss.resteasy.reactive.server.ServerResponseFilter;
import uk.co.jasonmarston.gateway.adaptor.input.service.AuthService;

import java.util.Set;

@ApplicationScoped
@Slf4j
public class GatewayInputAdaptorFilters {
    private static final Uni<Response> UNAUTHORIZED = Uni
        .createFrom()
        .item(Response
            .status(Response.Status.UNAUTHORIZED)
            .build()
        );

    @ConfigProperty(
        name = "gateway.filter.remove-request-header",
        defaultValue = "Cookie,Set-Cookie"
    )
    private Set<String> removeRequestHeaders;

    @ConfigProperty(
        name = "gateway.filter.remove-response-header",
        defaultValue = "Connection,Server,Transfer-Encoding," +
            "X-Powered-By,X-Application-Context,breadcrumbId"
    )
    private Set<String> removeResponseHeaders;

    @Inject
    private AuthService authService;

    @ServerRequestFilter(preMatching = true)
    @Priority(Priorities.AUTHORIZATION)
    public Uni<Response> authRequest(
        final ContainerRequestContext requestContext
    ) {
        log.warn("Entered my filter");
        if(authService.unAuthorized()) {
            log.warn("Unauthorized");
            return UNAUTHORIZED;
        }
        log.warn("Authorised");
        return null;
    }

    @ServerRequestFilter(preMatching = true)
    @Priority(Priorities.HEADER_DECORATOR)
    public Uni<Response> removeHeadersRequest(
        final ContainerRequestContext requestContext
    ) {
        log.warn("Request Headers to Remove {}", removeRequestHeaders);
        log.warn("Size = {}", removeRequestHeaders.size());
        for(final String headerName : removeRequestHeaders) {
            requestContext.getHeaders().remove(headerName);
        }
        return null;
    }

    @ServerResponseFilter
    @Priority(Priorities.HEADER_DECORATOR)
    public Uni<Void> removeHeadersResponse(
        final ContainerResponseContext responseContext
    ) {
        log.warn("Response Headers to Remove {}", removeResponseHeaders);
        log.warn("Size = {}", removeResponseHeaders.size());
        for(final String headerName : removeResponseHeaders) {
            responseContext.getHeaders().remove(headerName);
        }
        return Uni.createFrom().nullItem();
    }
}
