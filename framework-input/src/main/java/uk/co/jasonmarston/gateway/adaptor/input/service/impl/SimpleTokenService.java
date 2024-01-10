package uk.co.jasonmarston.gateway.adaptor.input.service.impl;

import java.util.Set;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import uk.co.jasonmarston.gateway.adaptor.input.service.JwtConverter;
import uk.co.jasonmarston.gateway.adaptor.input.service.JwtCreator;
import uk.co.jasonmarston.gateway.adaptor.input.service.TokenService;

@ApplicationScoped
public class SimpleTokenService implements TokenService {
    @Inject
    private JwtConverter jwtConverter;

    @Inject
    private JwtCreator jwtCreator;

    @ConfigProperty(
        name="gateway.type",
        defaultValue = "client"
    )
    private String gatewayType;

    @Override
    public String generateToken() {
        if(isIntegrationGateway()) {
            return jwtCreator.getToken();
        }
        return jwtConverter.getToken();
    }

    @Override
    public boolean isNotAssignedRole(final String role) {
        final Set<String> roles = jwtConverter.getRoles();
        if(roles == null) {
            return true;
        }
        for(final String group : roles) {
            if(role.equals(group)) {
                return false;
            }
        }
        return true;
    }

    private boolean isIntegrationGateway() {
        return "integration".equals(gatewayType);
    }
}
