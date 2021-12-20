package jp.co.canon.rss.logmanager.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jp.co.canon.rss.logmanager.config.ReqURLController;
import jp.co.canon.rss.logmanager.dto.upload.ResLocalJobFileIdx;
import jp.co.canon.rss.logmanager.exception.StatusResourceNotFoundException;
import jp.co.canon.rss.logmanager.service.UploadService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

@Slf4j
@RestController
@RequestMapping(ReqURLController.API_DEFAULT_UPLOAD_URL)
public class UploadController {
    private static String uploadPath;
    @Value("${file.upload-dir}")
    public void setUploadPath(String path) {
        uploadPath = path;
    }

    private static String crasFileName;
    @Value("${cras-data.file-name}")
    public void setCrasFileName(String fileName) {
        crasFileName = fileName;
    }

    private static String errorLogDownloadFileName;
    @Value("${error-log-download.file-name}")
    public void setErrorLogDownloadFileName(String fileName) {
        errorLogDownloadFileName = fileName;
    }

    private UploadService uploadService;
    public UploadController(UploadService uploadService) {
        this.uploadService = uploadService;
    }

    // local file 업로드
    @PostMapping(value = ReqURLController.API_POST_LOCALFILE, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary="Log file registration for Local Job registration")
    @ApiResponses({
            @ApiResponse(
                    responseCode="200",
                    description="OK(Log file upload success and automatically generated file ID return)",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(value = "{\n  \"fileIndex\": 1\n}"))
            ),
            @ApiResponse(responseCode="400", description="Bad Request"),
            @ApiResponse(responseCode="404", description="Not Found"),
            @ApiResponse(responseCode="500", description="Internal Server Error")
    })
    public ResponseEntity<?> uploadFile(
            HttpServletRequest request,
            @Parameter(
                    name = "file",
                    description = "Local Log file to be analyzed(MultipartFile)",
                    required = true,
                    content = @Content(
                            mediaType = MediaType.MULTIPART_FORM_DATA_VALUE,
                            schema = @Schema(implementation = MultipartFile.class))
            )
            @RequestParam("file") MultipartFile file) {
        try {
            ResLocalJobFileIdx buildLogList = uploadService.uploadLocalJobFile(file);
            return ResponseEntity.status(HttpStatus.OK).body(buildLogList);
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // cras data import
    @PostMapping(value = ReqURLController.API_POST_CRASDATAFILE, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary="Import Cras data")
    @ApiResponses({
            @ApiResponse(
                    responseCode="200",
                    description="OK(Successful import of Cras data)",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
            ),
            @ApiResponse(responseCode="400", description="Bad Request"),
            @ApiResponse(responseCode="404", description="Not Found"),
            @ApiResponse(responseCode="500", description="Internal Server Error")
    })
    public ResponseEntity<?> crasFileImport(
            HttpServletRequest request,
            @Parameter(
                    name = "file",
                    description = "Excel file containing the Cras data",
                    required = true,
                    content = @Content(
                            mediaType = MediaType.MULTIPART_FORM_DATA_VALUE,
                            schema = @Schema(implementation = MultipartFile.class))
            )
            @RequestParam("file") MultipartFile file,
            @Parameter(
                    schema = @Schema(example = "1"),
                    description = "Cras Data Site ID", required = true)
            @Valid @PathVariable(value = "id") @NotNull int crasDataSiteId) {
        try {
            File convFile = new File(uploadPath+File.separator+file.getOriginalFilename());
            convFile.createNewFile();
            FileOutputStream fos = new FileOutputStream(convFile);
            fos.write(file.getBytes());
            fos.close();

            uploadService.crasDataImport(convFile, crasDataSiteId);
            return ResponseEntity.status(HttpStatus.OK).body(null);
        } catch (ResponseStatusException e) {
            throw e;
        } catch (StatusResourceNotFoundException e) {
            log.error(e.getMessage());
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // cras data export
    @GetMapping(ReqURLController.API_GET_CRASDATAFILE)
    @Operation(summary="Export Cras data")
    @ApiResponses({
            @ApiResponse(responseCode="200", description="OK(Successful export of Cras data)"),
            @ApiResponse(responseCode="400", description="Bad Request"),
            @ApiResponse(responseCode="404", description="Not Found"),
            @ApiResponse(responseCode="500", description="Internal Server Error")
    })
    public ResponseEntity<InputStreamResource> crasFileExport(HttpServletResponse response,
                                                              @Parameter(
                                                                      schema = @Schema(example = "1"),
                                                                      description = "Cras Data Site ID", required = true)
                                                              @Valid @PathVariable(value = "id") @NotNull int crasDataSiteId) throws IOException {
        try {
            uploadService.crasDataExport(crasDataSiteId, response, crasFileName);
            return ResponseEntity.status(HttpStatus.OK).body(null);
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // errorlog setting file import
    @PostMapping(value = ReqURLController.API_POST_ERRORLOG_DOWNLOAD, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary="Import Cras data")
    @ApiResponses({
            @ApiResponse(
                    responseCode="200",
                    description="OK(Successful import of Cras data)",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
            ),
            @ApiResponse(responseCode="400", description="Bad Request"),
            @ApiResponse(responseCode="404", description="Not Found"),
            @ApiResponse(responseCode="500", description="Internal Server Error")
    })
    public ResponseEntity<?> errorLogSettingFileImport(
            HttpServletRequest request,
            @Parameter(
                    name = "file",
                    description = "Excel file containing the Cras data",
                    required = true,
                    content = @Content(
                            mediaType = MediaType.MULTIPART_FORM_DATA_VALUE,
                            schema = @Schema(implementation = MultipartFile.class))
            )
            @RequestParam("file") MultipartFile file,
            @Parameter(
                    schema = @Schema(example = "1"),
                    description = "Cras Data Site ID", required = true)
            @Valid @PathVariable(value = "id") @NotNull int siteId) {
        try {
            File convFile = new File(uploadPath+File.separator+file.getOriginalFilename());
            convFile.createNewFile();
            FileOutputStream fos = new FileOutputStream(convFile);
            fos.write(file.getBytes());
            fos.close();

            uploadService.errorLogSettingDataImport(convFile, siteId);
            return ResponseEntity.status(HttpStatus.OK).body(null);
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // errorlog setting file export
    @GetMapping(ReqURLController.API_GET_ERRORLOG_DOWNLOAD)
    @Operation(summary="Export Cras data")
    @ApiResponses({
            @ApiResponse(responseCode="200", description="OK(Successful export of Cras data)"),
            @ApiResponse(responseCode="400", description="Bad Request"),
            @ApiResponse(responseCode="404", description="Not Found"),
            @ApiResponse(responseCode="500", description="Internal Server Error")
    })
    public ResponseEntity<InputStreamResource> errorLogSettingFileExport(HttpServletResponse response,
                                                              @Parameter(
                                                                      schema = @Schema(example = "1"),
                                                                      description = "Cras Data Site ID", required = true)
                                                              @Valid @PathVariable(value = "id") @NotNull int siteId) throws IOException {
        try {
            uploadService.errorLogSettingDataExport(siteId, response, errorLogDownloadFileName);
            return ResponseEntity.status(HttpStatus.OK).body(null);
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
