package org.jboss.qa.arquillian.container.sramp;

import static org.junit.Assert.assertEquals;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.oasis_open.docs.s_ramp.ns.s_ramp_v1.BaseArtifactType;
import org.overlord.sramp.atom.err.SrampAtomException;
import org.overlord.sramp.client.SrampAtomApiClient;
import org.overlord.sramp.client.SrampClientException;

@RunWith(Arquillian.class)
public class SwitchYardDeploymentTest {

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

	@ArquillianResource
	BaseArtifactType deployedArchive;

	@Test
	public void deploymentTest() throws SrampClientException,
			SrampAtomException {
		
		long numOfResults = client
				.buildQuery("/s-ramp/ext/SwitchYardApplication[@uuid=?]")
				.parameter(deployedArchive.getUuid()).query().getTotalResults();

		assertEquals("Number of SY applications does not match.", 1,
				numOfResults);
	}

}
