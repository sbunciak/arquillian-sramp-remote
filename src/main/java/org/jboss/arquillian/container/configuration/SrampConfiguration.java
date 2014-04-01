package org.jboss.arquillian.container.configuration;

import org.jboss.arquillian.container.spi.ConfigurationException;
import org.jboss.arquillian.container.spi.client.container.ContainerConfiguration;

/**
 * Configuration for S-RAMP Container.
 * 
 * @author sbunciak
 * 
 */
public class SrampConfiguration implements ContainerConfiguration {

	public SrampConfiguration() {
		srampHost = "localhost";
		srampPort = 8080;
		srampUsername = "admin";
		srampPassword = "overlord";
	}

	private String srampUsername;
	private String srampPassword;
	private String srampHost;
	private int srampPort;

	public String getSrampServerURL() {
		return "http://" + getSrampHost() + ":" + getSrampPort()
				+ "/s-ramp-server";
	}

	public String getSrampUsername() {
		return srampUsername;
	}

	public void setSrampPassword(String password) {
		this.srampPassword = password;
	}

	public String getSrampPassword() {
		return srampPassword;
	}

	public void setSrampUsername(String username) {
		this.srampUsername = username;
	}

	public String getSrampHost() {
		return srampHost;
	}

	public void setSrampHost(String srampHost) {
		this.srampHost = srampHost;
	}

	public int getSrampPort() {
		return srampPort;
	}

	public void setSrampPort(int srampPort) {
		this.srampPort = srampPort;
	}

	public void validate() throws ConfigurationException {
		if (srampHost.isEmpty() || srampPassword.isEmpty()
				|| srampUsername.isEmpty() || srampPort <= 0) {
			throw new ConfigurationException("Empty configuration field found!");
		}
	}
	
	@Override
	public String toString() {
		return "srampUsername=[" + srampUsername + "], srampServerUrl=[" + getSrampServerURL() + "]";
	}
}
