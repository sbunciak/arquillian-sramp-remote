package org.jboss.qa.arquillian.container.sramp;

import static org.junit.Assert.assertEquals;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.qa.arquillian.container.sramp.util.DummyClass;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.EnterpriseArchive;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.oasis_open.docs.s_ramp.ns.s_ramp_v1.BaseArtifactType;
import org.overlord.sramp.atom.err.SrampAtomException;
import org.overlord.sramp.client.SrampAtomApiClient;
import org.overlord.sramp.client.SrampClientException;

@RunWith(Arquillian.class)
public class EarDeploymentTest {

	@Deployment
	public static Archive<?> createDeployment() {
		WebArchive war = ShrinkWrap.create(WebArchive.class, "web-app.war")
				.addAsManifestResource("web.xml");

		JavaArchive jar = ShrinkWrap.create(JavaArchive.class, "java-app.jar")
				.addClass(DummyClass.class);

		EnterpriseArchive archive = ShrinkWrap
				.create(EnterpriseArchive.class, "sramp-test.ear")
				.addAsModules(war, jar)
				.addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml");
		return archive;
	}

	@ArquillianResource
	SrampAtomApiClient client;

	@ArquillianResource
	BaseArtifactType deployedArchive;

	@Test
	public void canDeployEnterpriseArchive() throws SrampClientException,
			SrampAtomException {

		long numOfarchives = client
				.buildQuery("/s-ramp/ext/JavaArchive[@uuid=?]")
				.parameter(deployedArchive.getUuid()).query().getTotalResults();

		assertEquals("Wrong number of Java Archives", 1, numOfarchives);

		long numOfResults = client
				.buildQuery("/s-ramp/ext/JavaWebApplication[@uuid=?]")
				.parameter(deployedArchive.getUuid()).query().getTotalResults();

		assertEquals("Number of web applications does not match.", 1,
				numOfResults);

	}
}
