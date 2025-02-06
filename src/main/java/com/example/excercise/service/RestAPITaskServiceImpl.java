package com.example.excercise.service;

import com.example.excercise.entity.Task;
import com.example.excercise.repo.TaskRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class RestAPITaskServiceImpl implements RestAPITaskService {

    private static final String JIRA_REST_API_URL = "https://jakubzdunczuk.atlassian.net/rest/api/3/";
    private static final String JIRA_USER_MAIL = "jakub.zdunczuk@gmail.com";
    private static final String JIRA_TOKEN = "ATATT3xFfGF0PFTR2EPwbi7KbFBWWdWt8emRvQI42f3A2fX1GKLOGqtUF7i2OfP-AhPWIX37MC95T2YDgfgsM1aq8BvgfXuiYKCmNf6E_MbSV4CJnWYe7GB7F55j4aHiIkk0VWEuyVVhC-VExF-F9yI_k41PgQqDWX9s6Dw3iWt2jZgRMhv1MYY=F374AE82";
    private final RestTemplate restTemplate;
    private final TaskRepository taskRepository;


    @Autowired
    public RestAPITaskServiceImpl(RestTemplate restTemplate, TaskRepository taskRepository) {
        this.restTemplate = restTemplate;
        this.taskRepository = taskRepository;
    }

    @Override
    public List<Task> getTasks() {

        HttpEntity<JsonNode> entity1 = prepareHttpEntityToGetTasksByProjectName("PROJ1");
        HttpEntity<JsonNode> entity2 = prepareHttpEntityToGetTasksByProjectName("PROJ2");

        ResponseEntity<JsonNode> responseEntity1 = sendSearchRequestToJiraRestApi(entity1);
        ResponseEntity<JsonNode> responseEntity2 = sendSearchRequestToJiraRestApi(entity2);

        List<Task> taskList = new ArrayList<>();
        List<Task> taskList1 = getTasksFromRestApi(responseEntity1);
        List<Task> taskList2 = getTasksFromRestApi(responseEntity2);
        taskList.addAll(taskList1);
        taskList.addAll(taskList2);

        return taskList;
    }

    @Override
    public String copyTask(Task task) {
        ObjectNode payload = getJsonNodeToCopyTask(task);

        HttpEntity<JsonNode> entity = prepareHttpEntityWithHeadersAndAuth(payload);

        ResponseEntity<JsonNode> responseEntity = sendSaveRequestToJiraRestApi(entity);

        if (responseEntity.getStatusCode().equals(HttpStatus.CREATED)) {
            taskRepository.save(new Task(Objects.requireNonNull(responseEntity.getBody()).get("id").asLong(), task.getTitle(), task.getDescription(), task.getProjectId()));
        }
        System.out.println(responseEntity.getBody().toString());
        return responseEntity.getBody().toString();
    }

    private ResponseEntity<JsonNode> sendSaveRequestToJiraRestApi(HttpEntity<JsonNode> entity) {
        return restTemplate.exchange(
                JIRA_REST_API_URL + "issue",
                HttpMethod.POST,
                entity,
                JsonNode.class);
    }

    private ResponseEntity<JsonNode> sendSearchRequestToJiraRestApi(HttpEntity<JsonNode> entity1) {
        return restTemplate.exchange(
                JIRA_REST_API_URL + "search/jql",
                HttpMethod.POST,
                entity1,
                JsonNode.class);
    }

    private List<Task> getTasksFromRestApi(ResponseEntity<JsonNode> responseEntity) {
        List<Task> taskList = new ArrayList<>();
        Objects.requireNonNull(responseEntity.getBody()).get("issues").forEach(issue ->
            taskList.add(new Task(
                    issue.get("id").asLong(),
                    issue.get("fields").get("summary").asText(),
                    issue.get("fields").get("description").isNull() ? "" : issue.get("fields").get("description").get("content").get(0).get("content").get(0).get("text").asText(),
                    issue.get("fields").get("project").get("id").asLong()))
        );
        return taskList;
    }

    private HttpEntity<JsonNode> prepareHttpEntityToGetTasksByProjectName(String projectName) {
        JsonNodeFactory jnf = JsonNodeFactory.instance;
        ObjectNode payload = jnf.objectNode();
        {
            ArrayNode fields = payload.putArray("fields");
            fields.add("issuetype");
            fields.add("summary");
            fields.add("description");
            fields.add("project");
            payload.put("fieldsByKeys", true);
            payload.put("jql", "project = " + projectName + " ORDER BY created DESC");
            payload.put("maxResults", 100);
        }
        return prepareHttpEntityWithHeadersAndAuth(payload);
    }



    private HttpEntity<JsonNode> prepareHttpEntityWithHeadersAndAuth(ObjectNode payload) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Accept", "application/json");
        headers.add("Content-Type", "application/json");
        headers.setBasicAuth(JIRA_USER_MAIL, JIRA_TOKEN);
        return new HttpEntity<>(payload, headers);
    }

    private ObjectNode getJsonNodeToCopyTask(Task task) {
        JsonNodeFactory jnf = JsonNodeFactory.instance;
        ObjectNode payload = jnf.objectNode();
        {
            ObjectNode fields = payload.putObject("fields");
            {
                ObjectNode description = fields.putObject("description");
                {
                    ArrayNode content = description.putArray("content");
                    ObjectNode content0 = content.addObject();
                    {
                        content0.put("type", "paragraph");
                        ArrayNode content1 = content0.putArray("content");
                        ObjectNode content2 = content1.addObject();
                        {
                            content2.put("text", Objects.nonNull(task.getDescription()) ? task.getDescription() : "");
                            content2.put("type", "text");
                        }
                    }
                    description.put("type", "doc");
                    description.put("version", 1);
                }
                ObjectNode issuetype = fields.putObject("issuetype");
                {
                    issuetype.put("id", task.getProjectId().equals(10000L) ? "10006" : "10001");
                }
                ObjectNode project = fields.putObject("project");
                {
                    project.put("id", task.getProjectId().equals(10000L) ? "10001" : "10000");
                }
                fields.put("summary", task.getTitle());
            }
        }
        return payload;
    }
}

