package jp.co.canon.rss.logmanager.util;

import com.google.gson.Gson;
import jp.co.canon.rss.logmanager.config.ReqURLController;
import jp.co.canon.rss.logmanager.exception.ConvertException;
import jp.co.canon.rss.logmanager.exception.dto.CrasErrorContentsDTO;
import jp.co.canon.rss.logmanager.exception.dto.CrasErrorDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.*;
import java.nio.file.Paths;
import java.time.LocalDateTime;

@Slf4j
public class CallRestAPI {
    public HttpComponentsClientHttpRequestFactory settingFactory() {
        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();

        factory.setReadTimeout(300000); // 읽기시간초과, ms
        factory.setConnectTimeout(3000); // 연결시간초과, ms
        HttpClient httpClient = HttpClientBuilder.create()
                .setMaxConnTotal(100) // connection pool 적용
                .setMaxConnPerRoute(5) // connection pool 적용
                .build();
        factory.setHttpClient(httpClient); // 동기실행에 사용될 HttpClient 세팅

        return factory;
    }

    public ResponseEntity<?> getRestAPI(String url, Class<?> responseType) throws ConvertException {
        ResponseEntity<?> response = null;
        try {
            log.info(url);
            RestTemplate restTemplate = new RestTemplate(settingFactory());
            response = restTemplate.getForEntity(url, responseType);
        } catch (HttpServerErrorException e) {
            log.error(e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, serverErrorExceptionMessage(e));
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        return response;
    }

    public ResponseEntity<?> getWithCustomHeaderRestAPI(String url, HttpEntity reqHeaders, Class<?> responseType) throws ConvertException {
        ResponseEntity<?> response = null;
        try {
            log.info(url);
            RestTemplate restTemplate = new RestTemplate(settingFactory());
            response = restTemplate.exchange(url, HttpMethod.GET, reqHeaders, responseType);
        } catch (HttpServerErrorException e) {
            log.error(e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, serverErrorExceptionMessage(e));
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        return response;
    }

    public ResponseEntity<?> getRestAPIforAnalysis(String url, Class<?> responseType) throws ConvertException {
        ResponseEntity<?> response = null;
        try {
            log.info(url);
            RestTemplate restTemplate = new RestTemplate(settingFactory());
            response = restTemplate.getForEntity(url, responseType);
        } catch (HttpServerErrorException e) {
            log.error(e.getMessage());
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return response;
    }

    public ResponseEntity<?> getConvertRestAPI(String url, Class<?> responseType, String path) throws ConvertException {
        ResponseEntity<?> response = null;
        try {
            log.info(url);
            RestTemplate restTemplate = new RestTemplate(settingFactory());
            response = restTemplate.getForEntity(url, responseType);
        } catch (HttpServerErrorException e) {
            log.error(e.getMessage());
            throw new ConvertException(LocalDateTime.now(), 500, serverPreviewErrorExceptionMessage(e), ErrorMessage.CONVERTER_ERROR.getMsg(), path);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new ConvertException(LocalDateTime.now(), 500, serverPreviewErrorExceptionMessage(e.getMessage(), path), ErrorMessage.CONVERTER_ERROR.getMsg(), path);
        }
        return response;
    }

    public Object postRestAPI(String url, Object requestType, Class<?> responseType, String path) throws ConvertException {
        Object response = null;
        try {
            log.info(url);
            RestTemplate restTemplate = new RestTemplate(settingFactory());
            response = restTemplate.postForObject(url, requestType, responseType);
        } catch (HttpServerErrorException e) {
            log.error(e.getMessage());
            throw new ConvertException(LocalDateTime.now(), 500, serverPreviewErrorExceptionMessage(e), ErrorMessage.CONVERTER_ERROR.getMsg(), path);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new ConvertException(LocalDateTime.now(), 500, serverPreviewErrorExceptionMessage(e.getMessage(), path), ErrorMessage.CONVERTER_ERROR.getMsg(), path);
        }
        return response;
    }

    public ResponseEntity<?> postConvertPreviewRestAPI(String url, Object requestType, Class<?> responseType, String path) throws ConvertException {
        ResponseEntity<?> response = null;
        try {
            log.info(url);
            RestTemplate restTemplate = new RestTemplate(settingFactory());
            response = restTemplate.postForEntity(url, requestType, responseType);
        } catch (HttpServerErrorException e) {
            log.error(e.getMessage());
            throw new ConvertException(LocalDateTime.now(), 500, serverPreviewErrorExceptionMessage(e), ErrorMessage.CONVERTER_ERROR.getMsg(), path);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new ConvertException(LocalDateTime.now(), 500, serverPreviewErrorExceptionMessage(e.getMessage(), path), ErrorMessage.CONVERTER_ERROR.getMsg(), path);
        }
        return response;
    }

    public ResponseEntity<?> postConvertPreviewMultipartFileRestAPI(String url, MultipartFile requestType, Class<?> responseType, String path) throws ConvertException {
        ResponseEntity<?> response = null;
        try {
            log.info(url);
            response = postMultipartFile(url, requestType, responseType);
        } catch (HttpServerErrorException e) {
            log.error(e.getMessage());
            throw new ConvertException(LocalDateTime.now(), 500, serverPreviewErrorExceptionMessage(e), ErrorMessage.CONVERTER_ERROR.getMsg(), path);
        } catch (IOException e) {
            throw new ConvertException(LocalDateTime.now(), 500, serverPreviewErrorExceptionMessage(e.getMessage(), path), ErrorMessage.CONVERTER_ERROR.getMsg(), path);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new ConvertException(LocalDateTime.now(), 500, serverPreviewErrorExceptionMessage(e.getMessage(), path), ErrorMessage.CONVERTER_ERROR.getMsg(), path);
        }
        return response;
    }

    public ResponseEntity<?> postMultipartFile(String url, MultipartFile requestType, Class<?> responseType) throws IOException {
        ByteArrayResource resource = new ByteArrayResource(requestType.getBytes()){
            @Override
            public String getFilename() throws IllegalStateException {
                return requestType.getOriginalFilename();
            }
        };
        MultiValueMap<String, Object> multiValueMap = new LinkedMultiValueMap<>();
        multiValueMap.add("files", resource);

        RestTemplate restTemplate = new RestTemplate(settingFactory());
        log.info(url);
        return restTemplate.postForEntity(url, multiValueMap, responseType);
    }

    public ResponseEntity<?> postWithCustomHeaderMultipartFile(String url, HttpEntity<MultiValueMap<String, Object>> requestType, Class<?> responseType) throws IOException {
        ResponseEntity<?> response = null;
        try {
            log.info(url);
            RestTemplate restTemplate = new RestTemplate(settingFactory());
            response = restTemplate.postForEntity(url, requestType, responseType);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        return response;
    }

    public ResponseEntity<?> getDownloadFileRestAPI(String url) {
        ResponseEntity<?> response = null;
        try {
            log.info(url);
            RestTemplate restTemplate = new RestTemplate(settingFactory());
            response = restTemplate.getForEntity(url, byte[].class);
        } catch (HttpServerErrorException e) {
            log.error(e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, serverErrorExceptionMessage(e));
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        return response;
    }

    public DownloadStreamInfo getWithCustomHeaderDownloadFileRestAPI(String url, HttpEntity reqHeaders, String downloadPath, String fileName) throws ResponseStatusException {
        ResponseEntity<Resource> response = null;
        try {
            log.info(url);
            DownloadStreamInfo streamInfo = new DownloadStreamInfo();
            RestTemplate restTemplate = new RestTemplate(settingFactory());

            restTemplate.getInterceptors().add((request, body, execution)->{
                request.getHeaders().set(ReqURLController.JOB_CLIENT_ID, reqHeaders.getHeaders().getFirst(ReqURLController.JOB_CLIENT_ID));
                return execution.execute(request, body);
            });

            File destFile = Paths.get(downloadPath, fileName).toFile();
            String dest = destFile.toString();

            String downloadFile = restTemplate.execute(url, HttpMethod.GET, null,
                resp -> {
                    byte[] buffer = new byte[1024];
                    int read;

                    try (OutputStream outputStream = new FileOutputStream(dest)) {
                        InputStream inputStream = resp.getBody();
                        while ((read = inputStream.read(buffer)) > 0) {
                            outputStream.write(buffer, 0, read);
                            outputStream.flush();
                        }
                        inputStream.close();
                    }
                    return dest;
                });
            streamInfo.setErrorCode(500);
            return streamInfo;
        } catch (HttpServerErrorException e) {
            log.error(e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, serverErrorExceptionMessage(e));
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
    }

    public void deleteRestAPI(String url, String path) throws ConvertException {
        try {
            log.info(url);
            RestTemplate restTemplate = new RestTemplate(settingFactory());
            restTemplate.delete(url);
        } catch (HttpServerErrorException e) {
            log.error(e.getMessage());
            throw new ConvertException(LocalDateTime.now(), 500, serverPreviewErrorExceptionMessage(e), ErrorMessage.CONVERTER_ERROR.getMsg(), path);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new ConvertException(LocalDateTime.now(), 500, serverPreviewErrorExceptionMessage(e.getMessage(), path), ErrorMessage.CONVERTER_ERROR.getMsg(), path);
        }
    }

    public ResponseEntity<?> patchRestAPI(String url, Object requestType, Class<?> responseType, String path) throws ConvertException {
        Object response = null;
        try {
            log.info(url);
            RestTemplate restTemplate = new RestTemplate(settingFactory());
            response = restTemplate.patchForObject(url, requestType, responseType);
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(response);
        } catch (HttpServerErrorException e) {
            log.error(e.getMessage());
            throw new ConvertException(LocalDateTime.now(), 500, serverPreviewErrorExceptionMessage(e), ErrorMessage.CONVERTER_ERROR.getMsg(), path);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new ConvertException(LocalDateTime.now(), 500, serverPreviewErrorExceptionMessage(e.getMessage(), path), ErrorMessage.CONVERTER_ERROR.getMsg(), path);
        }
    }

    public String serverErrorExceptionMessage (HttpServerErrorException e) {
        Gson gson = new Gson();
        CrasErrorDTO crasErrorDTO = gson.fromJson(e.getResponseBodyAsString(), CrasErrorDTO.class);
        return (String) crasErrorDTO.getError();
    }

    public CrasErrorDTO serverPreviewErrorExceptionMessage (HttpServerErrorException e) {
        Gson gson = new Gson();
        String temp = e.getResponseBodyAsString();
        CrasErrorContentsDTO crasErrorContentsDTO = gson.fromJson(temp, CrasErrorContentsDTO.class);
        CrasErrorDTO crasErrorDTO = new CrasErrorDTO()
                .setError(crasErrorContentsDTO.getCras_error().get("error"))
                .setPath(crasErrorContentsDTO.getCras_error().get("path"))
                .setError_list(crasErrorContentsDTO.getCras_error().get("error_list"));
        return crasErrorDTO;
    }

    public CrasErrorDTO serverPreviewErrorExceptionMessage (String msg, String path) {
        CrasErrorDTO crasErrorDTO = new CrasErrorDTO()
                .setError(msg)
                .setPath(path)
                .setError_list(null);
        return crasErrorDTO;
    }
}
