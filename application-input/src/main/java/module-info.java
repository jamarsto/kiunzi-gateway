module application.input {
    requires transitive domain;
    requires application.output;
    requires static lombok;
    requires jakarta.cdi;
    requires transitive jakarta.ws.rs;
    requires transitive io.smallrye.mutiny;

    exports uk.co.jasonmarston.gateway.usecase;
}