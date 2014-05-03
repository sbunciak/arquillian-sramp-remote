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
public class TeiidDeploymentTest {

	@Deployment
	public static Archive<?> createDeployment() {
		JavaArchive archive = ShrinkWrap
				.create(JavaArchive.class, "sramp-test.vdb")
				.addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml")
				.addAsManifestResource("vdb.xml");
		return archive;
	}

	@ArquillianResource
	SrampAtomApiClient client;

	@ArquillianResource
	BaseArtifactType deployedArchive;

	@Test
	public void canDeployTeiidArchive() throws SrampClientException,
			SrampAtomException {

		long numOfResults = client
				.buildQuery("/s-ramp/ext/TeiidVdb[@uuid=?]")
				.parameter(deployedArchive.getUuid()).query().getTotalResults();

		assertEquals("Number of Tied Vdbs does not match.", 1,
				numOfResults);

		// check also expanded TeiidVdbManifest
		numOfResults = client
				.buildQuery("/s-ramp/ext/TeiidVdbManifest[expandedFromDocument[@uuid=?]]")
				.parameter(deployedArchive.getUuid()).query().getTotalResults();

		assertEquals("Number of expanded Manifests does not match.", 1,
				numOfResults);
	}

}
