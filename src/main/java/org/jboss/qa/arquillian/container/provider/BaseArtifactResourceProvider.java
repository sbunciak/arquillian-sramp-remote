package org.jboss.qa.arquillian.container.provider;

import java.lang.annotation.Annotation;

import org.jboss.arquillian.core.api.Instance;
import org.jboss.arquillian.core.api.annotation.Inject;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.arquillian.test.spi.enricher.resource.ResourceProvider;
import org.oasis_open.docs.s_ramp.ns.s_ramp_v1.BaseArtifactType;

/**
 * Resource provider for BaseArtifactType of the deployed archive
 * 
 * @author sbunciak
 * @since 1.0.0
 */
public class BaseArtifactResourceProvider implements ResourceProvider {

	@Inject
	private Instance<BaseArtifactType> artifact;

	public boolean canProvide(Class<?> type) {
		return BaseArtifactType.class.isAssignableFrom(type);
	}

	public Object lookup(ArquillianResource resource, Annotation... qualifiers) {
		return artifact.get();
	}

}
