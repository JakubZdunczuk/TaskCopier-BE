package com.example.excercise.service;

import com.example.excercise.dto.JiraRestAPIProjectResponse;
import com.example.excercise.dto.JiraRestAPITaskResponse;
import com.example.excercise.entity.Task;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class JiraRestApiService {

    private static final String JIRA_REST_API_URL = "https://jakubzdunczuk.atlassian.net/rest/api/3/";
    private static final String JIRA_USER_MAIL = "jakub.zdunczuk@gmail.com";
    private static final String JIRA_TOKEN = "ATATT3xFfGF0PFTR2EPwbi7KbFBWWdWt8emRvQI42f3A2fX1GKLOGqtUF7i2OfP-AhPWIX37MC95T2YDgfgsM1aq8BvgfXuiYKCmNf6E_MbSV4CJnWYe7GB7F55j4aHiIkk0VWEuyVVhC-VExF-F9yI_k41PgQqDWX9s6Dw3iWt2jZgRMhv1MYY=F374AE82";
    public static final String PROJECT_1_NAME = "PROJ1";
    public static final String PROJECT_2_NAME = "PROJ2";
    private final RestTemplate restTemplate;
    private final JiraRestAPIMapper jiraRestAPIMapper;



    @Autowired
    public JiraRestApiService(RestTemplate restTemplate, JiraRestAPIMapper jiraRestAPIMapper) {
        this.restTemplate = restTemplate;
        this.jiraRestAPIMapper = jiraRestAPIMapper;
    }

    public List<JiraRestAPITaskResponse> getAllTasks() {

        HttpEntity<JsonNode> entity1 = prepareHttpEntityToGetTasksByProjectName(PROJECT_1_NAME);
        HttpEntity<JsonNode> entity2 = prepareHttpEntityToGetTasksByProjectName(PROJECT_2_NAME);

        ResponseEntity<JsonNode> responseEntity1 = sendSearchTasksRequestToJiraRestApi(entity1);
        ResponseEntity<JsonNode> responseEntity2 = sendSearchTasksRequestToJiraRestApi(entity2);

        List<JiraRestAPITaskResponse> jiraRestApiTaskDTOList = new ArrayList<>();
        List<JiraRestAPITaskResponse> jiraRestApiTaskDTOList1 = jiraRestAPIMapper.mapTasksFromJSON(responseEntity1);
        List<JiraRestAPITaskResponse> jiraRestApiTaskDTOList2 = jiraRestAPIMapper.mapTasksFromJSON(responseEntity2);
        jiraRestApiTaskDTOList.addAll(jiraRestApiTaskDTOList1);
        jiraRestApiTaskDTOList.addAll(jiraRestApiTaskDTOList2);

        return jiraRestApiTaskDTOList;
    }

    public ResponseEntity<JsonNode> createTask(Task task) {
        ObjectNode payload = preparePayloadToCreateTask(task);

        HttpEntity<JsonNode> entity = prepareHttpEntityWithHeadersAndAuth(payload);

        return sendCreateRequestToJiraRestApi(entity);
    }

    public List<JiraRestAPIProjectResponse> getProjects() {
        HttpEntity<JsonNode> entity = prepareHttpEntityWithHeadersAndAuth(null);

        ResponseEntity<JsonNode> responseEntity = sendGetProjectsRequestToJiraRestApi(entity);

        return jiraRestAPIMapper.mapProjectsFromResponse(responseEntity);
    }

    private ResponseEntity<JsonNode> sendCreateRequestToJiraRestApi(HttpEntity<JsonNode> entity) {
        return restTemplate.exchange(
                JIRA_REST_API_URL + "issue",
                HttpMethod.POST,
                entity,
                JsonNode.class);
    }

    private ResponseEntity<JsonNode> sendSearchTasksRequestToJiraRestApi(HttpEntity<JsonNode> entity) {
        return restTemplate.exchange(
                JIRA_REST_API_URL + "search/jql",
                HttpMethod.POST,
                entity,
                JsonNode.class);
    }

    private ResponseEntity<JsonNode> sendGetProjectsRequestToJiraRestApi(HttpEntity<JsonNode> entity) {
        return restTemplate.exchange(
                JIRA_REST_API_URL + "project/search",
                HttpMethod.GET,
                entity,
                JsonNode.class);
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

        if (!Objects.isNull(payload)) {
            return new HttpEntity<>(payload, headers);
        }
        return new HttpEntity<>(headers);
    }

    private ObjectNode preparePayloadToCreateTask(Task task) {
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
                            content2.put("text", task.getDescription());
                            content2.put("type", "text");
                        }
                    }
                    description.put("type", "doc");
                    description.put("version", 1);
                }
                ObjectNode issuetype = fields.putObject("issuetype");
                {
                    issuetype.put("id", task.getProject().getId());
                }
                ObjectNode project = fields.putObject("project");
                {
                    project.put("id", task.getProject().getId());
                }
                fields.put("summary", task.getTitle());
            }
        }
        return payload;
    }
}

