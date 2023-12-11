package uk.co.jasonmarston.gateway.adaptor.input.service;

import java.util.Set;

public interface JwtConverter {
	String getToken();
	Set<String> getRoles();
}