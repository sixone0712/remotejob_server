package jp.co.canon.rss.logmanager.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jp.co.canon.rss.logmanager.config.ReqURLController;
import jp.co.canon.rss.logmanager.controller.examples.JobExamples;
import jp.co.canon.rss.logmanager.controller.examples.SiteExamples;
import jp.co.canon.rss.logmanager.controller.model.job.ReqRemoteJob;
import jp.co.canon.rss.logmanager.controller.model.job.ResRemoteJobDetail;
import jp.co.canon.rss.logmanager.controller.model.site.PlansDTO;
import jp.co.canon.rss.logmanager.dto.errorlogdownload.ReqErrorLogDownloadDTO;
import jp.co.canon.rss.logmanager.dto.errorlogdownload.ResErrorLogDownloadStatusDTO;
import jp.co.canon.rss.logmanager.dto.errorlogdownload.ResSettingListDTO;
import jp.co.canon.rss.logmanager.service.ErrorLogDownloadService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.util.List;

@Slf4j
@RestController
@RequestMapping(ReqURLController.API_DEFAULT_ERROR_LOG_URL)
public class ErrorLogDownloadController {
    private ErrorLogDownloadService errorLogDownloadService;

    public ErrorLogDownloadController(ErrorLogDownloadService errorLogDownloadService) {
        this.errorLogDownloadService = errorLogDownloadService;
    }

    // Error Log List 취득
    @GetMapping(ReqURLController.API_GET_ERROR_LOG_LIST)
    @Operation(summary = "Get detailed status information for the specified Remote Job")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "OK(Successful acquisition of Job details)",
                    content = @Content(
                            schema = @Schema(implementation = ResRemoteJobDetail.class),
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(
                                    name = "example1",
                                    value = JobExamples.RES_GET_REMOTE_JOB_DETAIL))

            ),
            @ApiResponse(responseCode = "400", description = "Bad Request"),
            @ApiResponse(responseCode = "404", description = "Not Found"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
    public ResponseEntity<?> getErrorLogList(
            HttpServletRequest request,
            @Parameter(name = "id", description = "Job ID to get detailed information", required = true, example = "10")
            @Valid @PathVariable(value = "id") @NotNull int siteId) {
        try {
            Object resErrorLogDownloadList = errorLogDownloadService.getErrorLogDownloadList(siteId);
            return ResponseEntity.status(HttpStatus.OK).body(resErrorLogDownloadList);
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Setting 정보 취득
    @GetMapping(ReqURLController.API_GET_ERROR_LOG_SETTING_LIST)
    @Operation(summary = "Get detailed status information for the specified Remote Job")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "OK(Successful acquisition of Job details)",
                    content = @Content(
                            schema = @Schema(implementation = ResRemoteJobDetail.class),
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(
                                    name = "example1",
                                    value = JobExamples.RES_GET_REMOTE_JOB_DETAIL))

            ),
            @ApiResponse(responseCode = "400", description = "Bad Request"),
            @ApiResponse(responseCode = "404", description = "Not Found"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
    public ResponseEntity<?> getSettingList(
            HttpServletRequest request,
            @Parameter(name = "id", description = "Job ID to get detailed information", required = true, example = "10")
            @Valid @PathVariable(value = "id") @NotNull int siteId) {
        try {
            List<ResSettingListDTO> resErrorLogDownloadList = errorLogDownloadService.getSettingList(siteId);
            return ResponseEntity.status(HttpStatus.OK).body(resErrorLogDownloadList);
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // 다운로드 요청
    @PostMapping(ReqURLController.API_POST_DOWNLOAD_REQ)
    @Operation(summary="Get plan list from Rapid Collector for specified site")
    @ApiResponses({
            @ApiResponse(
                    responseCode="200",
                    description="OK(Successfully retrieved the plan list for the specified Site)",
                    content = { @Content(
                            schema = @Schema(implementation = PlansDTO.class),
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(
                                    name = "example1",
                                    value = SiteExamples.GET_PLAN_LIST_RES)) }
            ),
            @ApiResponse(responseCode="400", description="Bad Request"),
            @ApiResponse(responseCode="404", description="Not Found"),
            @ApiResponse(responseCode="500", description="Internal Server Error")
    })
    public ResponseEntity<?> reqDownload(HttpServletResponse response,
                                                 @io.swagger.v3.oas.annotations.parameters.RequestBody(
                                                         description = "Remote job data to add the information",
                                                         required = true,
                                                         content = @Content(
                                                                 schema = @Schema(implementation = ReqRemoteJob.class),
                                                                 examples = @ExampleObject(value = JobExamples.REQ_ADD_EDIT_REMOTE_JOB))
                                                 )
                                                 @Parameter(name = "id", description = "Job ID to get detailed information", required = true, example = "10")
                                                 @Valid @PathVariable(value = "id") @NotNull int siteId,
                                                 @RequestBody ReqErrorLogDownloadDTO reqErrorLogDownloadDTO) {
        try {
            errorLogDownloadService.reqDownload(siteId, reqErrorLogDownloadDTO);
            return ResponseEntity.status(HttpStatus.OK).body(null);
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // 다운로드 상태 확인
    @GetMapping(ReqURLController.API_GET_DOWNLOAD_LIST)
    @Operation(summary = "Get detailed status information for the specified Remote Job")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "OK(Successful acquisition of Job details)",
                    content = @Content(
                            schema = @Schema(implementation = ResRemoteJobDetail.class),
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(
                                    name = "example1",
                                    value = JobExamples.RES_GET_REMOTE_JOB_DETAIL))

            ),
            @ApiResponse(responseCode = "400", description = "Bad Request"),
            @ApiResponse(responseCode = "404", description = "Not Found"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
    public ResponseEntity<?> getDownloadList(
            HttpServletRequest request,
            @Parameter(name = "id", description = "Job ID to get detailed information", required = true, example = "10")
            @Valid @PathVariable(value = "id") @NotNull int siteId) {
        try {
            List<ResErrorLogDownloadStatusDTO> resErrorLogDownloadStatusDTOList = errorLogDownloadService.getDownloadList(siteId);
            return ResponseEntity.status(HttpStatus.OK).body(resErrorLogDownloadStatusDTOList);
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // 다운로드 파일 요청
    @GetMapping(ReqURLController.API_GET_DOWNLOAD_FILE)
    @Operation(summary="Export Cras data")
    @ApiResponses({
            @ApiResponse(responseCode="200", description="OK(Successful export of Cras data)"),
            @ApiResponse(responseCode="400", description="Bad Request"),
            @ApiResponse(responseCode="404", description="Not Found"),
            @ApiResponse(responseCode="500", description="Internal Server Error")
    })
    public ResponseEntity<InputStreamResource> getDownloadFile(HttpServletResponse response,
                                                                         @Parameter(
                                                                                 schema = @Schema(example = "1"),
                                                                                 description = "Cras Data Site ID", required = true)
                                                                         @Valid @PathVariable(value = "id") @NotNull String rid) throws IOException {
        try {
            errorLogDownloadService.getDownloadFile(rid, response);

            return ResponseEntity.status(HttpStatus.OK).body(null);
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
