package uk.co.jasonmarston.gateway.usecase;

import io.smallrye.mutiny.Uni;
import jakarta.ws.rs.core.Response;
import uk.co.jasonmarston.gateway.valueobject.Destination;
import uk.co.jasonmarston.gateway.valueobject.Payload;

public interface GatewayUseCase {
    Uni<Response> post(
        final Destination destination,
        final Payload payload
    );
    Uni<Response> get(
        final Destination destination,
        final Payload payload
    );
    Uni<Response> put(
        final Destination destination,
        final Payload payload
    );
    Uni<Response> patch(
        final Destination destination,
        final Payload payload
    );
    Uni<Response> delete(
        final Destination destination,
        final Payload payload
    );
}
