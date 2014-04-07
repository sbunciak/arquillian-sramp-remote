package org.jboss.qa.arquillian.container;

import org.jboss.arquillian.container.spi.client.container.DeployableContainer;
import org.jboss.arquillian.core.spi.LoadableExtension;
import org.jboss.arquillian.test.spi.enricher.resource.ResourceProvider;
import org.jboss.qa.arquillian.container.provider.BaseArtifactResourceProvider;
import org.jboss.qa.arquillian.container.provider.SrampConfigurationResourceProvider;

/**
 * 
 * @author sbunciak
 * 
 */
public class SrampExtension implements LoadableExtension {

	public void register(ExtensionBuilder builder) {
		builder.service(DeployableContainer.class, SrampContainer.class)
			   .service(ResourceProvider.class, SrampConfigurationResourceProvider.class)
			   .service(ResourceProvider.class, BaseArtifactResourceProvider.class);
	}

}
