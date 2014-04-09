package org.jboss.qa.arquillian.container.provider;

import java.lang.annotation.Annotation;

import org.apache.log4j.Logger;
import org.jboss.arquillian.core.api.Instance;
import org.jboss.arquillian.core.api.annotation.Inject;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.arquillian.test.spi.enricher.resource.ResourceProvider;
import org.jboss.qa.arquillian.container.configuration.SrampConfiguration;

/**
 * Resource provider for S-RAMP Container Adapter Configuration.
 * 
 * @author sbunciak
 * @since 1.0.1
 */
public class SrampConfigurationResourceProvider implements ResourceProvider {

	Logger log = Logger.getLogger(SrampConfigurationResourceProvider.class);

	@Inject
	private Instance<SrampConfiguration> config;

	public boolean canProvide(Class<?> type) {
		return SrampConfiguration.class.isAssignableFrom(type);
	}

	public Object lookup(ArquillianResource resource, Annotation... qualifiers) {
		return config.get();
	}

}
