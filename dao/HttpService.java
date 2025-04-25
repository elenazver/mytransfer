package org.example.dao;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.example.enumeration.HttpRequestAgentType;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import static org.example.constants.ProjectConstants.HEADER_ACCEPT;
import static org.example.constants.ProjectConstants.HEADER_AUTHORIZATION;
import static org.example.constants.ProjectConstants.HEADER_CONTENT_TYPE;
import static org.example.constants.ProjectConstants.JIRA_API_KEY;
import static org.example.constants.ProjectConstants.JIRA_URL;
import static org.example.constants.ProjectConstants.JIRA_USERNAME;
import static org.example.constants.ProjectConstants.YOUTRACK_API_KEY;


/**
 * A service for making HTTP-requests to JIRA and YouTrack.
 * Supports GET and POST methods with authentication.
 */
@Slf4j
public class HttpService {
    private final CloseableHttpClient client;

    public HttpService() {
        this.client = HttpClients.createDefault();
    }

    private String getAuthorizationHeader(HttpRequestAgentType agentType) {
        return switch (agentType) {
            case JIRA_AGENT -> {
                String auth = JIRA_USERNAME + ":" + JIRA_API_KEY;
                yield "Basic " + Base64.getEncoder().encodeToString(auth.getBytes(StandardCharsets.ISO_8859_1));
            }
            case YOUR_TRACK_AGENT -> "Bearer " + YOUTRACK_API_KEY;
        };
    }

    private void setCommonHeaders(HttpRequestBase request, HttpRequestAgentType agentType) {
        request.setHeader(HEADER_AUTHORIZATION, getAuthorizationHeader(agentType));
        request.setHeader(HEADER_ACCEPT, "application/json");
    }

    /**
     * Executes a GET-request to API JIRA or YouTrack.
     *
     * @param url       URL-address of the endpoint API.
     * @param agentType The type of agent that determines which service the request is being sent to ({@link HttpRequestAgentType}).
     * @return {@link CloseableHttpResponse}, containing the response API.
     */
    public CloseableHttpResponse getRequest(String url, HttpRequestAgentType agentType) {
        HttpGet get = new HttpGet(url);
        setCommonHeaders(get, agentType);

        try {
            CloseableHttpResponse response = client.execute(get);
            validateResponse(response, url, "GET");
            return response;
        } catch (IOException e) {
            log.error("Error when making a GET-request to {}: {}", url, e.getMessage());
            throw new RuntimeException("Error when executing the GET-request", e);
        }
    }

    /**
     * Executes a POST-request to API JIRA.
     *
     * @param url         URL-address endpoint API.
     * @param jsonPayload JSON-object containing the data to be sent.
     * @return {@link JSONObject}, containing the response body API.
     */
    public JSONObject postRequest(String url, JSONObject jsonPayload) {
        if (jsonPayload == null || jsonPayload.isEmpty()) {
            log.warn("The request body is empty or incorrect. The request is not being sent.");
            return null;
        }
        HttpPost post = new HttpPost(url);
        setCommonHeaders(post, HttpRequestAgentType.JIRA_AGENT);
        post.setHeader(HEADER_CONTENT_TYPE, "application/json; charset=UTF-8");
        post.setEntity(new StringEntity(jsonPayload.toString(), ContentType.APPLICATION_JSON));

        try (CloseableHttpResponse response = client.execute(post)) {
            String responseBody = getResponseBody(response);
            log.info("Jira API Response: {}", responseBody); // Logging the response before parsing
            validateResponse(response, url, "POST");
            return (responseBody != null && !responseBody.isEmpty()) ? new JSONObject(responseBody) : null;
        } catch (IOException e) {
            log.error("Error when executing a POST-request: {}", e.getMessage());
            throw new RuntimeException("Error in the POST-request", e);
        }
    }

    /**
     * Uploading attachments to JIRA
     *
     * @param issueKey The issue key is in JIRA
     * @param file     the uploaded File
     */
    public void uploadAttachment(Long issueKey, File file) {
        String url = JIRA_URL + "/issue/" + issueKey + "/attachments";
        HttpPost post = new HttpPost(url);
        setCommonHeaders(post, HttpRequestAgentType.JIRA_AGENT);
        post.setHeader("X-Atlassian-Token", "no-check");

        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        builder.addPart("file", new FileBody(file, ContentType.DEFAULT_BINARY));
        post.setEntity(builder.build());

        try (CloseableHttpResponse response = client.execute(post)) {
            validateResponse(response, url, "UPLOAD");
        } catch (IOException e) {
            log.error("Error when uploading a file to JIRA: {}", e.getMessage());
            throw new RuntimeException("Attachment upload error", e);
        }
    }

    /**
     * Executes a DELETE-request to the API JIRA or YouTrack.
     *
     * @param url       URL-address endpoint API.
     * @param agentType Agent type.
     */
    public void delete(String url, HttpRequestAgentType agentType) {
        HttpDelete delete = new HttpDelete(url);
        setCommonHeaders(delete, agentType);

        try (CloseableHttpResponse response = client.execute(delete)) {
            validateResponse(response, url, "DELETE");
        } catch (IOException e) {
            log.error("Error when executing a DELETE-request to {}: {}", url, e.getMessage());
            throw new RuntimeException("Error when executing a DELETE-request", e);
        }
    }

    private void validateResponse(CloseableHttpResponse response, String url, String method) {
        int statusCode = response.getStatusLine().getStatusCode();
        if (statusCode != HttpStatus.SC_OK &&
                statusCode != HttpStatus.SC_CREATED &&
                statusCode != HttpStatus.SC_NO_CONTENT) {
            String responseBody = getResponseBody(response);
            log.error("Error {}-request. Code: {}, URL: {}, Body: {}", method, statusCode, url, responseBody);
            throw new RuntimeException("Error " + method + "-request. Code: " + statusCode);
        }
    }

    private String getResponseBody(CloseableHttpResponse response) {
        try {
            return (response.getEntity() != null)
                    ? EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8)
                    : null;
        } catch (IOException e) {
            log.error("Error reading the response body: {}", e.getMessage());
            return null;
        }
    }
}
