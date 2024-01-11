package uk.co.jasonmarston.gateway.usecase;

import io.smallrye.mutiny.Uni;
import jakarta.ws.rs.core.Response;
import uk.co.jasonmarston.gateway.valueobject.Destination;
import uk.co.jasonmarston.gateway.valueobject.Payload;

public interface GatewayUseCase {
    Uni<Response> forward(
        final String httpMethod,
        final Destination destination,
        final Payload payload
    );
}
