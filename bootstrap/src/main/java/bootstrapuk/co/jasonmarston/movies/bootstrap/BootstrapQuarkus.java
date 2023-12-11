package bootstrapuk.co.jasonmarston.movies.bootstrap;

import io.quarkus.runtime.Quarkus;
import io.quarkus.runtime.annotations.QuarkusMain;

public class BootstrapQuarkus {
	@QuarkusMain
	public class App {
		public static void main(final String[] args) {
			Quarkus.run(args);
		}
	}
}
