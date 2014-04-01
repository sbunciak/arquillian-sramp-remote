package org.jboss.arquillian.container;

import java.io.InputStream;

import org.jboss.arquillian.container.configuration.SrampConfiguration;
import org.jboss.arquillian.container.service.SrampService;
import org.jboss.arquillian.container.service.SrampServiceImpl;
import org.jboss.arquillian.container.spi.client.container.DeployableContainer;
import org.jboss.arquillian.container.spi.client.container.DeploymentException;
import org.jboss.arquillian.container.spi.client.container.LifecycleException;
import org.jboss.arquillian.container.spi.client.protocol.ProtocolDescription;
import org.jboss.arquillian.container.spi.client.protocol.metadata.ProtocolMetaData;
import org.jboss.resteasy.logging.Logger;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.exporter.ZipExporter;
import org.jboss.shrinkwrap.api.spec.EnterpriseArchive;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.descriptor.api.Descriptor;

/**
 * Implementation of Arquillian container to support S-RAMP as a remote
 * deployment target.
 * 
 * @author sbunciak
 * 
 */
public class SrampContainer implements DeployableContainer<SrampConfiguration> {

	private Logger log = Logger.getLogger(SrampContainer.class);

	private SrampConfiguration config = null;

	private SrampService srampService = null;

	public Class<SrampConfiguration> getConfigurationClass() {
		return SrampConfiguration.class;
	}

	public void setup(SrampConfiguration configuration) {
		this.config = configuration;
	}

	public void start() throws LifecycleException {
		// In the case of remote container adapters, this is ideally the place
		// to verify if the container is running, along with any other necessary
		// validations.
		try {
			srampService = new SrampServiceImpl(config);
		} catch (Exception e) {
			log.error("Exception occured connecting to S-RAMP: ", e);

			throw new LifecycleException(
					"Exception occured connecting to S-RAMP: ", e);
		}

		if (srampService != null) {
			log.info("Connection to S-RAMP established.");
		}
	}

	public void stop() throws LifecycleException {
		// Any cleanup operations can be performed in this method.
	}

	public ProtocolDescription getDefaultProtocol() {
		return new ProtocolDescription("Local");
	}

	public ProtocolMetaData deploy(Archive<?> archive)
			throws DeploymentException {

		String applicationType = "";

		// Deploying war
		if (archive instanceof WebArchive) {
			applicationType = "JavaWebApplication";
		}

		// Deploying jar
		if (archive instanceof JavaArchive) {
			// decide whether it contains switchyard.xml
			if (archive.contains("META-INF/switchyard.xml")) {
				applicationType = "SwitchYardApplication";
			} else {
				applicationType = "JavaArchive";
			}
		}

		// Deploying ear
		if (archive instanceof EnterpriseArchive) {
			applicationType = "JavaEnterpriseApplication";
		}

		log.debug("Deploying " + applicationType + ": " + archive.getName());

		// export SkrinkWrap archive as InputStream
		InputStream in = archive.as(ZipExporter.class).exportAsInputStream();

		// deploy archive to S-RAMP
		srampService.deployArchive(archive.getName(), applicationType, in);

		log.info("Deployed " + applicationType + ": " + archive.getName());

		// Return empty meta-data of no use
		return new ProtocolMetaData();
	}

	public void undeploy(Archive<?> archive) throws DeploymentException {
		try {
			log.info("Undeploying " + archive.getName());
			srampService.undeployArchives();
		} catch (Exception e) {
			log.error("Error occured during undeployment: ", e);
			throw new DeploymentException("Error occured during undeployment",
					e);
		}
	}

	public void deploy(Descriptor descriptor) throws DeploymentException {
		throw new UnsupportedOperationException(
				"Descriptor-based deployment not implemented.");
	}

	public void undeploy(Descriptor descriptor) throws DeploymentException {
		throw new UnsupportedOperationException(
				"Descriptor-based deployment not implemented.");
	}

}
