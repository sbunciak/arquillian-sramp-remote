package org.jboss.arquillian.container;

import org.apache.log4j.Logger;
import org.jboss.arquillian.container.configuration.SrampConfiguration;
import org.jboss.arquillian.container.configuration.SrampConfigurationResourceProvider;
import org.jboss.arquillian.container.spi.event.SetupContainer;
import org.jboss.arquillian.core.api.Injector;
import org.jboss.arquillian.core.api.Instance;
import org.jboss.arquillian.core.api.InstanceProducer;
import org.jboss.arquillian.core.api.annotation.ApplicationScoped;
import org.jboss.arquillian.core.api.annotation.Inject;
import org.jboss.arquillian.core.api.annotation.Observes;

public class SrampObserver {

	Logger log = Logger.getLogger(SrampConfigurationResourceProvider.class);

	@Inject
	@ApplicationScoped
	private InstanceProducer<SrampConfiguration> config;

	@Inject
	private Instance<Injector> injector;

	public void getConfiguration(@Observes SetupContainer setup) {
		try {
			SrampConfiguration c = (SrampConfiguration) setup.getContainer()
					.createDeployableConfiguration();

			config.set(injector.get().inject(c));
		} catch (Exception e) {
			log.error(
					"Error occured creating S-RAMP Configuration injection point:", e);
		}

	}

}
