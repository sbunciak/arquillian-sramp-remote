package org.jboss.arquillian.container.sramp;

import static org.junit.Assert.assertNotNull;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.overlord.sramp.client.SrampAtomApiClient;

@RunWith(Arquillian.class)
public class DeploymentTest {

	@Deployment
	public static Archive<?> createDeployment() {
		JavaArchive archive = ShrinkWrap
				.create(JavaArchive.class, "sramp-test.jar")
				.addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml")
				.addAsManifestResource("switchyard.xml");
		return archive;
	}
	
	@ArquillianResource
	SrampAtomApiClient client;
	

	@Test
	public void deploymentTest() {
		assertNotNull(client);
	}

}
