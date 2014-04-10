package org.jboss.qa.arquillian.container.sramp;

import static org.junit.Assert.assertEquals;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.qa.arquillian.container.sramp.util.DummyClass;
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
public class JavaDeploymentTest {

	@Deployment
	public static Archive<?> createDeployment() {
		JavaArchive archive = ShrinkWrap
				.create(JavaArchive.class, "sramp-test.jar")
				.addClass(DummyClass.class)
				.addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml");
		return archive;
	}

	@ArquillianResource
	SrampAtomApiClient client;

	@ArquillianResource
	BaseArtifactType deployedArchive;
	
	@Test
	public void canDeployJavaArchive() throws SrampClientException,
			SrampAtomException {

		long numOfarchives = client.buildQuery("/s-ramp/ext/JavaArchive[@uuid=?]")
				.parameter(deployedArchive.getUuid()).query()
				.getTotalResults();
		
		assertEquals("Wrong number of Java Archives", 1,
				numOfarchives);
		
	}
}
