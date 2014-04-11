package org.jboss.qa.arquillian.container.service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.jboss.qa.arquillian.container.configuration.SrampConfiguration;
import org.jboss.resteasy.logging.Logger;
import org.oasis_open.docs.s_ramp.ns.s_ramp_v1.BaseArtifactType;
import org.overlord.sramp.atom.archive.SrampArchive;
import org.overlord.sramp.atom.archive.expand.DefaultMetaDataFactory;
import org.overlord.sramp.atom.archive.expand.ZipToSrampArchive;
import org.overlord.sramp.atom.archive.expand.registry.ZipToSrampArchiveRegistry;
import org.overlord.sramp.atom.err.SrampAtomException;
import org.overlord.sramp.client.SrampAtomApiClient;
import org.overlord.sramp.client.SrampClientException;
import org.overlord.sramp.client.query.ArtifactSummary;
import org.overlord.sramp.client.query.QueryResultSet;
import org.overlord.sramp.common.ArtifactType;
import org.overlord.sramp.common.SrampModelUtils;

public class SrampServiceImpl implements SrampService {

	private Logger log = Logger.getLogger(SrampServiceImpl.class);

	private SrampAtomApiClient client = null;

	private long artifactCounter = 0;

	public SrampServiceImpl(SrampConfiguration config)
			throws SrampClientException, SrampAtomException {

		// create connection to S-RAMP
		this.client = new SrampAtomApiClient(config.getSrampServerURL(),
				config.getSrampUsername(), config.getSrampPassword(), true);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jboss.arquillian.container.sramp.SrampService#getClient()
	 */
	public SrampAtomApiClient getClient() {
		return client;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.jboss.arquillian.container.sramp.SrampService#deployArchive(java.
	 * lang.String, java.lang.String, java.io.InputStream)
	 */
	public BaseArtifactType deployArchive(String archiveId, String archiveName,
			String artifactTypeArg, InputStream content) {

		assert content != null;

		ZipToSrampArchive expander = null;
		SrampArchive archive = null;
		BaseArtifactType artifact = null;
		File tempResourceFile = null;
		try {
			// internal integrity check
			artifactCounter = client.query("/s-ramp").getTotalResults();

			// First, stash the content in a temp file - we may need it multiple
			// times.
			tempResourceFile = stashResourceContent(content);
			content = FileUtils.openInputStream(tempResourceFile);

			ArtifactType artifactType = ArtifactType.valueOf(artifactTypeArg);
			if (artifactType.isExtendedType()) {
				artifactType = ArtifactType.ExtendedDocument(artifactType
						.getExtendedType());
			}

			artifact = client
					.uploadArtifact(artifactType, content, archiveName);
			IOUtils.closeQuietly(content);

			// for all uploaded files add custom property
			SrampModelUtils.setCustomProperty(artifact,
					"arquillian-archive-id", archiveId);
			client.updateArtifactMetaData(artifact);

			content = FileUtils.openInputStream(tempResourceFile);

			// Now also add "expanded" content to the s-ramp repository
			expander = ZipToSrampArchiveRegistry.createExpander(artifactType,
					content);

			if (expander != null) {
				expander.setContextParam(DefaultMetaDataFactory.PARENT_UUID,
						artifact.getUuid());
				archive = expander.createSrampArchive();
				client.uploadBatch(archive);
			}
		} catch (Exception e) {
			log.error("Upload failure:", e);
			IOUtils.closeQuietly(content);
		} finally {
			SrampArchive.closeQuietly(archive);
			ZipToSrampArchive.closeQuietly(expander);
			FileUtils.deleteQuietly(tempResourceFile);
		}

		return artifact;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jboss.arquillian.container.sramp.SrampService#undeployArchives()
	 */
	public void undeployArchives(String archiveId) throws SrampClientException,
			SrampAtomException {

		log.debug("Deleting expanded artifacts");

		// Delete expanded artifacts
		QueryResultSet rset = client
				.buildQuery(
						"/s-ramp[expandedFromDocument[@arquillian-archive-id = ?]]")
				.parameter(archiveId).query();

		for (ArtifactSummary artifactSummary : rset) {
			log.debug("Deleting: " + artifactSummary.getName());
			client.deleteArtifact(artifactSummary.getUuid(),
					artifactSummary.getType());
		}

		// Delete main archive
		// Related are deleted along with the primary
		rset = client.buildQuery("/s-ramp[@arquillian-archive-id = ?]")
				.parameter(archiveId).query();

		ArtifactSummary archiveArtifact = rset.get(0);

		log.debug("Deleting: " + archiveArtifact.getName());

		client.deleteArtifact(archiveArtifact.getUuid(),
				archiveArtifact.getType());

		// Internal consistency check whether the number of artifacts before
		// deploy and after deploy match
		long artifactCounterTemp = client.query("/s-ramp").getTotalResults();

		if (artifactCounter != artifactCounterTemp) {
			log.warn("Artifact counts does not match!");
			log.warn("Artifacts before deploy: " + artifactCounter
					+ ". Artifacts after undeploy: " + artifactCounterTemp);
			artifactCounter = artifactCounterTemp;
		}

	}

	/**
	 * Make a temporary copy of the resource by saving the content to a temp
	 * file.
	 * 
	 * @param resourceInputStream
	 * @throws IOException
	 */
	private File stashResourceContent(InputStream resourceInputStream)
			throws IOException {
		File resourceTempFile = null;
		OutputStream oStream = null;
		try {
			resourceTempFile = File.createTempFile("s-ramp-resource", ".tmp");
			oStream = FileUtils.openOutputStream(resourceTempFile);
		} finally {
			IOUtils.copy(resourceInputStream, oStream);
			IOUtils.closeQuietly(resourceInputStream);
			IOUtils.closeQuietly(oStream);
		}
		return resourceTempFile;
	}
}
