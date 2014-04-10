package org.jboss.qa.arquillian.container;

import java.io.InputStream;

import org.jboss.arquillian.container.spi.client.container.DeployableContainer;
import org.jboss.arquillian.container.spi.client.container.DeploymentException;
import org.jboss.arquillian.container.spi.client.container.LifecycleException;
import org.jboss.arquillian.container.spi.client.protocol.ProtocolDescription;
import org.jboss.arquillian.container.spi.client.protocol.metadata.ProtocolMetaData;
import org.jboss.arquillian.container.spi.context.annotation.ContainerScoped;
import org.jboss.arquillian.container.spi.context.annotation.DeploymentScoped;
import org.jboss.arquillian.core.api.InstanceProducer;
import org.jboss.arquillian.core.api.annotation.Inject;
import org.jboss.qa.arquillian.container.configuration.SrampConfiguration;
import org.jboss.qa.arquillian.container.service.SrampService;
import org.jboss.qa.arquillian.container.service.SrampServiceImpl;
import org.jboss.resteasy.logging.Logger;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.exporter.ZipExporter;
import org.jboss.shrinkwrap.api.spec.EnterpriseArchive;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.descriptor.api.Descriptor;
import org.oasis_open.docs.s_ramp.ns.s_ramp_v1.BaseArtifactType;

/**
 * Implementation of Arquillian container to support S-RAMP as a remote
 * deployment target.
 * 
 * @author sbunciak
 * @since 1.0.0
 */
public class SrampContainer implements DeployableContainer<SrampConfiguration> {

	@Inject
	@DeploymentScoped
	private InstanceProducer<BaseArtifactType> artifactProd;

	@Inject
	@ContainerScoped
	private InstanceProducer<SrampConfiguration> confProd;

	private Logger log = Logger.getLogger(SrampContainer.class);

	private SrampConfiguration config = null;

	private SrampService srampService = null;

	public Class<SrampConfiguration> getConfigurationClass() {
		return SrampConfiguration.class;
	}

	public void setup(SrampConfiguration configuration) {
		this.config = configuration;
		confProd.set(configuration);
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

			// default archive type
			applicationType = "JavaArchive";

			// SwitchYardApplication if it contains switchyard.xml
			if (archive.contains("META-INF/switchyard.xml")) {
				applicationType = "SwitchYardApplication";
			}

			// TeiidVdb if it contains vdb.xml
			if (archive.contains("META-INF/vdb.xml")) {
				applicationType = "TeiidVdb";
			}
			
			// KieJarArchive if it contains kmodule.xml
			if (archive.contains("META-INF/kmodule.xml")) {
				applicationType = "KieJarArchive";
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
		BaseArtifactType artifact = srampService.deployArchive(
				archive.getName(), applicationType, in);

		// save the deployed archive to injection point
		artifactProd.set(artifact);

		log.info("Deployed " + applicationType + ": " + archive.getName());

		// Return empty meta-data of no use
		return new ProtocolMetaData();
	}

	public void undeploy(Archive<?> archive) throws DeploymentException {
		try {
			srampService.undeployArchives();
			log.info("Undeployed " + archive.getName());
		} catch (Exception e) {
			log.error(
					"Error occured during undeployment of " + archive.getName(),
					e);
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
