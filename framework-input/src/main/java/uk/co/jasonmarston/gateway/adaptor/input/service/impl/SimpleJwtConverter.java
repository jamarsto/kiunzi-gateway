package uk.co.jasonmarston.gateway.adaptor.input.service.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.jwt.JsonWebToken;

import io.smallrye.jwt.build.Jwt;
import io.smallrye.jwt.build.JwtClaimsBuilder;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.json.JsonObject;
import jakarta.json.JsonValue;
import uk.co.jasonmarston.gateway.adaptor.input.service.JwtConverter;

@ApplicationScoped
public class SimpleJwtConverter implements JwtConverter {
    private static final String SEPARATOR = "/";
    private static final String PREFERRED_USERNAME = "preferred_username";
    private static final String UNIQUE_NAME = "unique_name";
    private static final String UPN = "upn";
    private static final String EMAIL = "email";

    private final List<String> defaultRoleClaimsPath;

    @Inject
    private JsonWebToken jsonWebToken;

    @ConfigProperty(
        name="gateway.jwt.issuer",
        defaultValue = "https://example.com/issuer"
    )
    private String issuer;

    @ConfigProperty(name = "quarkus.oidc.roles.role-claim-path")
    private List<String> roleClaimPaths;

    @Inject
    public SimpleJwtConverter(
        @ConfigProperty(
            name = "quarkus.oidc.client-id",
            defaultValue=""
        )
        final String clientId
    ) {
        this.defaultRoleClaimsPath = new ArrayList<> (
            List
                .of(
                    "realm_access/roles",
                    "resource_access/" + clientId + "/roles"
                )
        );
    }

    @Override
    public String getToken() {
        final JwtClaimsBuilder builder = Jwt.issuer(issuer);

        final String subject = jsonWebToken.getSubject();
        if(null != subject) {
            builder.subject(subject);
        }

        final String preferredUserName = getPreferredUsername();
        if(null != preferredUserName) {
            builder.preferredUserName(preferredUserName);
        }

        final String email = jsonWebToken.getClaim(EMAIL);
        if(null != email) {
            builder.claim(EMAIL, email);
        }

        builder.groups(getRoles());

        return builder.sign();
    }

    @Override
    public Set<String> getRoles() {
        final Set<String> roles = new HashSet<>();
        for(String claimPath : getRoleClaimPaths()) {
            final JsonValue jsonValue = getJsonValue(claimPath);

            if(jsonValue == null) {
                continue;
            }

            if(jsonValue instanceof JsonObject) {
                roles.add(StringUtility
                    .unquoteString(jsonValue.toString())
                );
                continue;
            }

            roles
                .addAll(jsonValue
                    .asJsonArray()
                    .stream()
                    .map(role -> StringUtility
                        .unquoteString(role.toString())
                    )
                    .collect(Collectors.toSet())
                );
        }
        return roles;
    }

    private List<String> getRoleClaimPaths() {
        if(roleClaimPaths == null || roleClaimPaths.isEmpty()) {
            return defaultRoleClaimsPath;
        }

        return roleClaimPaths;
    }

    private JsonValue getJsonValue(
        final String claimPath
    ) {
        final String[] claimPathSegments = claimPath
            .split(SEPARATOR);

        JsonValue jsonValue = jsonWebToken
            .getClaim(claimPathSegments[0]);

        for(int i = 1; i < claimPathSegments.length; i++) {
            if(jsonValue == null) {
                break;
            }

            final String claimPathSegment = claimPathSegments[i];

            jsonValue = jsonValue
                .asJsonObject()
                .get(claimPathSegment);
        }

        return jsonValue;
    }

    private String getPreferredUsername() {
        final String preferredUsername = jsonWebToken
            .getClaim(PREFERRED_USERNAME);
        if(preferredUsername != null) {
            return preferredUsername;
        }

        final String uniqueName = jsonWebToken
            .getClaim(UNIQUE_NAME);
        if(uniqueName != null) {
            return uniqueName;
        }

        return jsonWebToken.getClaim(UPN);
    }
}
