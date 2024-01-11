package uk.co.jasonmarston.gateway.input.port;

import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;
import uk.co.jasonmarston.gateway.output.port.GatewayOutputPort;
import uk.co.jasonmarston.gateway.usecase.GatewayUseCase;
import uk.co.jasonmarston.gateway.valueobject.Destination;
import uk.co.jasonmarston.gateway.valueobject.Payload;

@ApplicationScoped
public class GatewayInputPort implements GatewayUseCase {
    @Inject
    private GatewayOutputPort gatewayOutputPort;

    @Override
    public Uni<Response> forward(
        final String httpMethod,
        final Destination destination,
            final Payload payload
    ) {
        return gatewayOutputPort.forward(httpMethod, destination, payload);
    }
}
