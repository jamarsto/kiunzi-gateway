module application.output {
	requires transitive  domain;
	requires static lombok;
	requires transitive jakarta.ws.rs;
	requires transitive io.smallrye.mutiny;

	exports uk.co.jasonmarston.gateway.output.port;
}