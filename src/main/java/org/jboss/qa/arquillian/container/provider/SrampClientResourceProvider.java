package org.jboss.qa.arquillian.container.provider;

import java.lang.annotation.Annotation;

import org.apache.log4j.Logger;
import org.jboss.arquillian.core.api.Instance;
import org.jboss.arquillian.core.api.annotation.Inject;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.arquillian.test.spi.enricher.resource.ResourceProvider;
import org.jboss.qa.arquillian.container.configuration.SrampConfiguration;
import org.overlord.sramp.atom.err.SrampAtomException;
import org.overlord.sramp.client.SrampAtomApiClient;
import org.overlord.sramp.client.SrampClientException;

/**
 * Resource provider for S-RAMP Atom API Client implementation
 * 
 * @author sbunciak
 * @since 1.0.0
 */
public class SrampClientResourceProvider implements ResourceProvider {

	Logger log = Logger.getLogger(SrampClientResourceProvider.class);
	
	@Inject
	private Instance<SrampConfiguration> config;
	
	@Override
	public boolean canProvide(Class<?> type) {
		return SrampAtomApiClient.class.isAssignableFrom(type);
	}

	@Override
	public Object lookup(ArquillianResource resource, Annotation... qualifiers) {
		SrampConfiguration c = config.get();

		try {
			return new SrampAtomApiClient(c.getSrampServerURL(),
					c.getSrampUsername(), c.getSrampPassword(), true);
		} catch (SrampClientException e) {
			log.error("Error occured connecting to S-RAMP.", e);
		} catch (SrampAtomException e) {
			log.error("Error occured connecting to S-RAMP.", e);
		}
		return null;
	}

}
