package uk.co.jasonmarston.gateway.adaptor.input.service.impl;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.jwt.JsonWebToken;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import uk.co.jasonmarston.gateway.adaptor.input.service.AuthService;
import uk.co.jasonmarston.gateway.adaptor.input.service.TokenService;

@ApplicationScoped
public class SimpleAuthService implements AuthService {
	@Inject
	private JsonWebToken jsonWebToken;
	@Inject
	private TokenService tokenService;

	@ConfigProperty(
		name="gateway.type",
		defaultValue = "client"
	)
	private String gatewayType;
	
	@ConfigProperty(
		name="gateway.jwt.role",
		defaultValue = "user"
	)
	private String role;
	

	@Override
	public boolean unAuthorized() {
		if(isIntegrationGateway()) {
			if(jsonWebToken.getClaimNames() == null) {
				return false;
			}
			return true;
		}
		if(jsonWebToken == null) {
			return true;
		}
		return tokenService.isNotAssignedRole(role);
	}

	private boolean isIntegrationGateway() {
		if("integration".equals(gatewayType)) {
			return true;
		}
		return false;
	}
}
