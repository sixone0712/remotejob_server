package jp.co.canon.rss.logmanager.system;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.stereotype.Component;

import java.io.*;
import java.net.URL;


@Slf4j
@Component
public class ClientManageService {

    public HttpResponse postMultipartToMultiFile(String requestURL, String[] path) {
        HttpResponse response = null;

        try {
            HttpClient client = HttpClientBuilder.create().build();
            HttpPost postRequest = new HttpPost(requestURL);
            MultipartEntityBuilder multiEntityBuilder = MultipartEntityBuilder.create();
            for (int idx = 0; idx < path.length; idx++) {
                File f = new File(path[idx]);
                multiEntityBuilder.addBinaryBody(
                        "files",
                        new FileInputStream(f),
                        ContentType.APPLICATION_OCTET_STREAM,
                        f.getName()
                );
            }
            HttpEntity multipart = multiEntityBuilder.build();
            postRequest.setEntity(multipart);

            response = client.execute(postRequest);
        } catch (Exception e) {
            response.setReasonPhrase(e.toString());
            log.error(e.toString());
        }

        return response;
    }

    public HttpResponse post(String requestURL, String jsonMessage) {
        HttpResponse response = null;
        try {
            HttpClient client = HttpClientBuilder.create().build();
            HttpPost postRequest = new HttpPost(requestURL);
            postRequest.setHeader("Accept", "application/json");
            postRequest.setHeader("Connection", "keep-alive");
            postRequest.setHeader("Content-Type", "application/json");
            postRequest.setEntity(new StringEntity(jsonMessage));

            response = client.execute(postRequest);

        } catch (Exception e) {
            log.error(e.toString());
            response.setReasonPhrase(e.toString());
        }

        return response;
    }

    public HttpResponse get(String requestURL) {
        HttpResponse response = null;

        try {
            HttpClient client = HttpClientBuilder.create().build();
            HttpGet getRequest = new HttpGet(requestURL);

            response = client.execute(getRequest);

        } catch (Exception e) {
            log.error(e.toString());
            response.setReasonPhrase(e.toString());
        }
        return response;
    }

    public int connectCheck(String requestURL) {
        int code = 0;
        try {
            HttpClient client = HttpClientBuilder.create().build();
            HttpGet getRequest = new HttpGet(requestURL);
            HttpResponse response = client.execute(getRequest);
            code = response.getStatusLine().getStatusCode();
        } catch (Exception e){
            log.error(e.toString());
        }
        return code;
    }

    public void download(String donwloadURL, String outputFilePath) {
        try (BufferedInputStream in = new BufferedInputStream(new URL(donwloadURL).openStream());
             FileOutputStream fileOutputStream = new FileOutputStream(outputFilePath)) {
                byte dataBuffer[] = new byte[1024];
                int bytesRead;
                while ((bytesRead = in.read(dataBuffer, 0, 1024)) != -1) {
                    fileOutputStream.write(dataBuffer, 0, bytesRead);
                }
        } catch (IOException e) {
            log.error(e.toString());
        }
    }
}
