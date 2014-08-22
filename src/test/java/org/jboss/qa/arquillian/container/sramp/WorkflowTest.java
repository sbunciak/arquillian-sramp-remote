package org.jboss.qa.arquillian.container.sramp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.oasis_open.docs.s_ramp.ns.s_ramp_v1.BaseArtifactType;
import org.overlord.dtgov.taskapi.types.FindTasksRequest;
import org.overlord.dtgov.taskapi.types.StatusType;
import org.overlord.dtgov.taskapi.types.TaskDataType;
import org.overlord.dtgov.taskapi.types.TaskDataType.Entry;
import org.overlord.dtgov.taskapi.types.TaskSummaryType;
import org.overlord.dtgov.taskapi.types.TaskType;
import org.overlord.dtgov.taskclient.TaskApiClient;
import org.overlord.dtgov.taskclient.TaskApiClientException;
import org.overlord.sramp.client.SrampAtomApiClient;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
 * This test should be used with SimpleReleaseProcess workflow for DTGov.
 */
@RunWith(Arquillian.class)
public class WorkflowTest {

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

	@ArquillianResource
	TaskApiClient taskClient;

	@Before
	public void setUp() {
		assertNotNull(taskClient);
	}

	@Test
	public void canCompleteDeploymentWorkflow() {
		try {
			// Obtain new Task instance
			TaskType devTask = getCurrentTasks().get(0);
			// check name of the task
			assertEquals("Test deployment '" + deployedArchive.getName() + "' in the DEV environment",
					devTask.getName());
			// check successful deployment
			// *** If you modify governance.targets you need to also modify this location ***
			assertTrue(new File("/tmp/dev/jbossas7/standalone/deployments/" + deployedArchive.getName()).exists());
			// claim the task
			assertNotNull(taskClient.claimTask(devTask.getId()));
			// start the task
			assertNotNull(taskClient.startTask(devTask.getId()));
			// successfully complete the task
			Map<String, String> params = taskDataToMap(devTask.getTaskData());
			params.put("Status", "pass");
			assertNotNull(taskClient.completeTask(devTask.getId(), params));

			// complete Test QA
			TaskType qaTask = getCurrentTasks().get(0);
			assertEquals("Test deployment '" + deployedArchive.getName() + "' in the QA environment", qaTask.getName());
			assertTrue(new File("/tmp/qa/jbossas7/standalone/deployments/" + deployedArchive.getName()).exists());
			assertNotNull(taskClient.claimTask(qaTask.getId()));
			assertNotNull(taskClient.startTask(qaTask.getId()));
			// successfully complete the task
			params = taskDataToMap(qaTask.getTaskData());
			params.put("Status", "pass");
			assertNotNull(taskClient.completeTask(qaTask.getId(), params));

			// complete Test Stage
			TaskType stageTask = getCurrentTasks().get(0);
			assertEquals("Test deployment '" + deployedArchive.getName() + "' in the STAGE environment",
					stageTask.getName());
			assertTrue(new File("/tmp/stage/jbossas7/standalone/deployments/" + deployedArchive.getName()).exists());
			assertNotNull(taskClient.claimTask(stageTask.getId()));
			assertNotNull(taskClient.startTask(stageTask.getId()));
			// successfully complete the task
			params = taskDataToMap(stageTask.getTaskData());
			params.put("Status", "pass");
			assertNotNull(taskClient.completeTask(stageTask.getId(), params));

			// complete Test Prod
			TaskType prodTask = getCurrentTasks().get(0);
			assertEquals("Test deployment '" + deployedArchive.getName() + "' in the PROD environment",
					prodTask.getName());
			assertTrue(new File("/tmp/prod/jbossas7/standalone/deployments/" + deployedArchive.getName()).exists());
			assertNotNull(taskClient.claimTask(prodTask.getId()));
			assertNotNull(taskClient.startTask(prodTask.getId()));
			// successfully complete the task
			params = taskDataToMap(prodTask.getTaskData());
			params.put("Status", "pass");
			assertNotNull(taskClient.completeTask(prodTask.getId(), params));
		} catch (TaskApiClientException e) {
			e.printStackTrace();
			fail();
		}
	}

	/*
	 * Retrieve DTGov Tasks with timeout
	 */
	private List<TaskType> getCurrentTasks() {

		int numOfWaits = 5;
		List<TaskType> tasks = new ArrayList<TaskType>();

		for (int i = 0; i < numOfWaits; i++) {

			tasks = getCurrentTasksForDeployment(deployedArchive.getUuid());
			if (tasks.isEmpty()) {
				try {
					Thread.sleep(5000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			} else {
				return tasks;
			}
		}

		return tasks;
	}

	/*
	 * Retrieve all new DTGov Tasks for single deployment
	 */
	private List<TaskType> getCurrentTasksForDeployment(String deploymentUuid) {
		// construct task request
		List<TaskType> result = new ArrayList<TaskType>();
		FindTasksRequest taskRequest = new FindTasksRequest();
		taskRequest.setEndIndex(Integer.MAX_VALUE);
		taskRequest.getStatus().add(StatusType.READY);

		try {
			// iterate over request result
			for (TaskSummaryType taskSummary : taskClient.findTasks(taskRequest).getTaskSummary()) {
				//
				TaskType task = taskClient.getTask(taskSummary.getId());

				for (Entry e : task.getTaskData().getEntry()) {
					if (e.getKey().equalsIgnoreCase("DeploymentUuid") && e.getValue().equalsIgnoreCase(deploymentUuid)) {
						result.add(task);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return result;
	}

	/*
	 * Auxiliary
	 */
	private Map<String, String> taskDataToMap(TaskDataType taskData) {

		Map<String, String> map = new HashMap<String, String>();

		for (Entry e : taskData.getEntry()) {
			map.put(e.getKey(), e.getValue());
			System.out.println(e.getKey() + ": " + e.getValue());
		}

		return map;
	}
}
