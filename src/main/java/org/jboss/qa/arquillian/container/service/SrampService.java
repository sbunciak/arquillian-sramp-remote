package org.jboss.qa.arquillian.container.service;

import java.io.InputStream;

import org.oasis_open.docs.s_ramp.ns.s_ramp_v1.BaseArtifactType;
import org.overlord.sramp.atom.err.SrampAtomException;
import org.overlord.sramp.client.SrampAtomApiClient;
import org.overlord.sramp.client.SrampClientException;

public interface SrampService {

	/**
	 * 
	 * @return
	 */
	public SrampAtomApiClient getClient();

	/**
	 * Deploys artifact to S-RAMP repository
	 * 
	 * @param archiveId
	 *            will be set in s-ramp as custom property 'arquillian-id' to
	 *            identify deployed archive
	 * @param artifactTypeArg
	 *            is used to recognize appropriate artifact type
	 * @param InputStream
	 *            content
	 * @return 
	 */
	public BaseArtifactType deployArchive(String archiveName, String artifactTypeArg,
			InputStream content);

	/**
	 * 
	 * @param artifactId
	 * @throws SrampClientException
	 * @throws SrampAtomException
	 */
	public void undeployArchives() throws SrampClientException,
			SrampAtomException;

}