package uk.co.jasonmarston.gateway.adaptor.input.service;

public interface TokenService {
	String generateToken();
	boolean isNotAssignedRole(final String role);
}