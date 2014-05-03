package org.jboss.qa.arquillian.container;

import org.jboss.arquillian.container.spi.client.container.DeployableContainer;
import org.jboss.arquillian.core.spi.LoadableExtension;
import org.jboss.arquillian.test.spi.enricher.resource.ResourceProvider;
import org.jboss.qa.arquillian.container.provider.BaseArtifactResourceProvider;
import org.jboss.qa.arquillian.container.provider.SrampClientResourceProvider;
import org.jboss.qa.arquillian.container.provider.SrampConfigurationResourceProvider;
import org.jboss.qa.arquillian.container.provider.TaskApiClientResourceProvider;

/**
 * Plug-able extension for Arquillian to register S-RAMP Adapter Container  
 * 
 * @author sbunciak
 * @since 1.0.0
 */
public class SrampExtension implements LoadableExtension {

	public void register(ExtensionBuilder builder) {
		builder.service(DeployableContainer.class, SrampContainer.class)
			   .service(ResourceProvider.class, SrampConfigurationResourceProvider.class)
			   .service(ResourceProvider.class, SrampClientResourceProvider.class)
			   .service(ResourceProvider.class, BaseArtifactResourceProvider.class)
			   .service(ResourceProvider.class, TaskApiClientResourceProvider.class);
	}

}
