package org.jboss.qa.arquillian.container.provider;

import java.lang.annotation.Annotation;

import org.jboss.arquillian.core.api.Instance;
import org.jboss.arquillian.core.api.annotation.Inject;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.arquillian.test.spi.enricher.resource.ResourceProvider;
import org.jboss.resteasy.logging.Logger;
import org.overlord.dtgov.taskclient.TaskApiClient;

/**
 * Resource provider for DTGov Task Api Client
 * 
 * @author sbunciak
 * @since 1.1.1
 */
public class TaskApiClientResourceProvider implements ResourceProvider {

	Logger log = Logger.getLogger(TaskApiClientResourceProvider.class);

	@Inject
	private Instance<TaskApiClient> taskApiClient;
	
	public boolean canProvide(Class<?> type) {
		return TaskApiClient.class.isAssignableFrom(type);
	}

	public Object lookup(ArquillianResource resource, Annotation... qualifiers) {
		return taskApiClient.get();
	}
}
