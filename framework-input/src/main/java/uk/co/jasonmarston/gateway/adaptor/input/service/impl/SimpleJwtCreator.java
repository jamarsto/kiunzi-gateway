package uk.co.jasonmarston.gateway.adaptor.input.service.impl;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import io.smallrye.jwt.build.Jwt;
import jakarta.enterprise.context.ApplicationScoped;
import uk.co.jasonmarston.gateway.adaptor.input.service.JwtCreator;

@ApplicationScoped
public class SimpleJwtCreator implements JwtCreator {
    @ConfigProperty(
        name="gateway.jwt.issuer",
        defaultValue = "https://example.com/issuer"
    )
    private String issuer;

    @ConfigProperty(
        name="gateway.jwt.role",
        defaultValue = "user"
    )
    private String role;

    @ConfigProperty(
        name="gateway.jwt.integration.subject",
        defaultValue = "service"
    )
    private String subject;

    @Override
    public String getToken() {
        return Jwt
            .issuer(issuer)
            .subject(subject)
            .groups(role)
            .sign();
    }
}
