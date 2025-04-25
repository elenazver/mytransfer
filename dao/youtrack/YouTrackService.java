package org.example.dao.youtrack;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.example.dao.AbstractService;
import org.example.enumeration.HttpRequestAgentType;

import java.io.IOException;

public class YouTrackService extends AbstractService {

    public Integer checkService() {
        try (CloseableHttpResponse response = httpService.getRequest(
                "https://mancalagaming.youtrack.cloud/",
                HttpRequestAgentType.YOUR_TRACK_AGENT)) {
            return response.getStatusLine().getStatusCode();
        } catch (IOException e) {
            throw new RuntimeException("Error when making a request to YouTrack", e);
        }
    }
}
