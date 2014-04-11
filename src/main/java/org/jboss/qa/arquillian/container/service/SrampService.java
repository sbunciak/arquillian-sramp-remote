package org.jboss.qa.arquillian.container.service;

import java.io.InputStream;

import org.oasis_open.docs.s_ramp.ns.s_ramp_v1.BaseArtifactType;
import org.overlord.sramp.atom.err.SrampAtomException;
import org.overlord.sramp.client.SrampAtomApiClient;
import org.overlord.sramp.client.SrampClientException;

/**
 * Interface for S-RAMP Container interaction
 * 
 * @author sbunciak
 * @since 1.0.0
 */
public interface SrampService {

	/**
	 * 
	 * @return SrampAtomApiClient
	 */
	public SrampAtomApiClient getClient();

	/**
	 * Deploys artifact to S-RAMP repository
	 * 
	 * @param archiveId
	 *            will be set in s-ramp as custom property 'arquillian-id' to
	 *            identify deployed archive
	 * @param archiveName
	 *            name of the archive to deploy
	 * @param artifactTypeArg
	 *            is used to recognize appropriate artifact type
	 * @param InputStream
	 *            content
	 * @return BaseArtifactType instance as a result of deploy containing
	 *         information about deployed archive
	 */
	public BaseArtifactType deployArchive(String archiveId, String archiveName,
			String artifactTypeArg, InputStream content);

	/**
	 * Undeploys archive from S-RAMp repository
	 * 
	 * @param archiveId
	 *            of archive to undeploy
	 * @throws SrampClientException
	 * @throws SrampAtomException
	 */
	public void undeployArchives(String archiveId) throws SrampClientException,
			SrampAtomException;

}