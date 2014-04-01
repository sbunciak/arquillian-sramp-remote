arquillian-sramp-remote
=======================

This project consist of S-RAMP remote container adapter implementation for Arquillian. This Arquillian extension will enable users to deploy artifacts to S-RAMP Repository before running their Arquillian tests.

how to use it
=============

- checkout this source code and run _mvn install_ (it's not available in any public maven repo, yet)
- add maven profile to your arquillian tests, and that's it!

          <profiles>
        		<profile>
        			<id>arquillian-sramp-remote</id>
        			<dependencies>
        				<dependency>
        					<groupId>org.jboss.spec</groupId>
        					<artifactId>jboss-javaee-6.0</artifactId>
        					<version>1.0.0.Final</version>
        					<type>pom</type>
        					<scope>provided</scope>
        				</dependency>
        				<dependency>
        					<groupId>org.jboss.arquillian.container</groupId>
        					<artifactId>arquillian-sramp-remote</artifactId>
        					<version>0.0.1-SNAPSHOT</version>
        					<scope>test</scope>
        				</dependency>
        			</dependencies>
        			<activation>
        				<activeByDefault>true</activeByDefault>
        			</activation>
        		</profile>
        	</profiles>

