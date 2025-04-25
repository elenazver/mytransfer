package org.example.dao.jira;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.example.dao.AbstractService;
import org.example.enumeration.HttpRequestAgentType;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.example.service.IssueSynchronizer.MAPPER;


/**
 * A service for interacting with the API JIRA.
 * Allows to make HTTP-requests to JIRA and process their responses.
 */
@Slf4j
public class JiraService extends AbstractService {

    public Integer checkService() {
        HttpResponse response = httpService.getRequest("https://mancalagaming.atlassian.net", HttpRequestAgentType.JIRA_AGENT);
        return response.getStatusLine().getStatusCode();
    }

    /**
     * Executes a GET-request to the API JIRA, receives a JSON-response, and deserializes it to a list of objects of the specified type.
     *
     * @param <T>      The generalized type of objects expected in the response.
     * @param endpoint URL-address of the API JIRA.
     * @param clazz    The class of objects that the JSON-response will be converted to.
     * @return A list of objects of type {@code T}, obtained from JIRA.
     * @throws RuntimeException if an error occurred during query execution or deserialization JSON.
     */
    protected <T> List<T> fetchFromJira(String endpoint, Class<T> clazz) {
        HttpResponse response = httpService.getRequest(endpoint, HttpRequestAgentType.JIRA_AGENT);
        String responseBody;

        try {
            // Reading the response content
            responseBody = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException("Error reading the response from Jira", e);
        }

        try {
            // Deserializing JSON to a list of objects of the specified type
            return MAPPER.readValue(
                    responseBody,
                    MAPPER.getTypeFactory().constructCollectionType(List.class, clazz)
            );
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Deserialization error JSON from Jira", e);
        }
    }

    protected <T> T fetchObjectFromJira(String endpoint, Class<T> clazz) {
        HttpResponse response = httpService.getRequest(endpoint, HttpRequestAgentType.JIRA_AGENT);
        String responseBody;

        try {
            responseBody = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException("Error reading the response from Jira", e);
        }

        try {
            // Deserializing JSON into an object of the specified type
            return MAPPER.readValue(responseBody, clazz);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Deserialization error JSON from Jira", e);
        }
    }


    protected <T> T getOneObject(String endpoint, Class<T> clazz) {
        HttpResponse response = httpService.getRequest(endpoint, HttpRequestAgentType.JIRA_AGENT);

        try {
            String responseBody = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
            return MAPPER.readValue(responseBody, clazz);
        } catch (IOException e) {
            throw new RuntimeException("Failed to fetch object from: " + endpoint, e);
        }
    }
}
