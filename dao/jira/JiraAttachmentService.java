package org.example.dao.jira;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

/**
 * A service for working with attachments in Jira.
 * Allows you to download files and upload them to tasks Jira.
 */
@Slf4j
public class JiraAttachmentService extends JiraService {

    /**
     * Adds attachments to an issue in Jira.
     *
     * @param createdTaskId ID of the created issue in Jira.
     * @param attachments   A map of attachment URLs and their names.
     */
    public void addAttachmentsToIssue(Long createdTaskId, Map<String, String> attachments) {
        //TODO to do retry
        attachments.forEach((attachmentUrl, attachmentFilename) -> {
            File downloadedFile = downloadFileFromUrl(attachmentUrl, attachmentFilename);
            if (downloadedFile == null) {
                log.warn("File {} could not be downloaded.", attachmentFilename);
                return;
            }
            try {
                httpService.uploadAttachment(createdTaskId, downloadedFile);
            } finally {
                deleteFile(downloadedFile);
            }
        });
    }

    /**
     * Downloads attachment by URL.
     *
     * @param fileUrl  URL the file.
     * @param fileName local name the file.
     * @return The downloaded file or null in case of an error.
     */
    private File downloadFileFromUrl(String fileUrl, String fileName) {
        log.debug("Start downloading attachment from: {}", fileUrl);

        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(fileUrl).openConnection();
            connection.setRequestMethod("GET");
            connection.setDoInput(true);
            connection.connect();

            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                log.error("Error downloading file: {} - {}", fileUrl, connection.getResponseMessage());
                return null;
            }

            File downloadedFile = new File(fileName);
            try (InputStream inputStream = connection.getInputStream();
                 FileOutputStream outputStream = new FileOutputStream(downloadedFile)) {

                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
            }

            log.debug("File downloaded: {} (Size: {} bytes)", downloadedFile.getName(), downloadedFile.length());
            return downloadedFile;
        } catch (IOException e) {
            log.error("Error downloading file {}: {}", fileUrl, e.getMessage());
            return null;
        }
    }

    /**
     * Deletes a temporary file.
     *
     * @param file File to delete.
     */
    private void deleteFile(File file) {
        if (file != null && file.exists()) {
            if (file.delete()) {
                log.debug("Temporary file deleted: {}", file.getAbsolutePath());
            } else {
                log.warn("Failed to delete temporary file: {}", file.getAbsolutePath());
            }
        }
    }
}
