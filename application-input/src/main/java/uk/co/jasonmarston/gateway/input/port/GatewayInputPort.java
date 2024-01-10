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
    public Uni<Response> post(
        final Destination destination,
        final Payload payload
    ) {
        return gatewayOutputPort.post(destination, payload);
    }

    @Override
    public Uni<Response> get(
        final Destination destination,
        final Payload payload
    ) {
        return gatewayOutputPort.get(destination, payload);
    }

    @Override
    public Uni<Response> put(
        final Destination destination,
        final Payload payload
    ) {
        return gatewayOutputPort.put(destination, payload);
    }

    @Override
    public Uni<Response> delete(
        final Destination destination,
        final Payload payload
    ) {
        return gatewayOutputPort.delete(destination, payload);
    }
}
