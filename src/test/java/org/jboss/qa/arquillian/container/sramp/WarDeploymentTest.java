package org.jboss.qa.arquillian.container.sramp;

import static org.junit.Assert.assertEquals;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.qa.arquillian.container.sramp.util.DummyClass;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.oasis_open.docs.s_ramp.ns.s_ramp_v1.BaseArtifactType;
import org.overlord.sramp.atom.err.SrampAtomException;
import org.overlord.sramp.client.SrampAtomApiClient;
import org.overlord.sramp.client.SrampClientException;

@RunWith(Arquillian.class)
public class WarDeploymentTest {

	@Deployment
	public static Archive<?> createDeployment() {
		WebArchive archive = ShrinkWrap
				.create(WebArchive.class, "sramp-test.war")
				.addClass(DummyClass.class).setWebXML("web.xml")
				.addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml");
		return archive;
	}

	@ArquillianResource
	SrampAtomApiClient client;

	@ArquillianResource
	BaseArtifactType deployedArchive;

	@Test
	public void canDeployWebArchive() throws SrampClientException,
			SrampAtomException {

		long numOfResults = client
				.buildQuery("/s-ramp/ext/JavaWebApplication[@uuid=?]")
				.parameter(deployedArchive.getUuid()).query().getTotalResults();

		assertEquals("Number of web applications does not match.", 1,
				numOfResults);

	}

}
