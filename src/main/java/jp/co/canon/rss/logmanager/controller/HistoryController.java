package jp.co.canon.rss.logmanager.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jp.co.canon.rss.logmanager.config.ReqURLController;
import jp.co.canon.rss.logmanager.controller.examples.HistoryExamples;
import jp.co.canon.rss.logmanager.dto.history.ResBuildLog;
import jp.co.canon.rss.logmanager.dto.history.ResHistoryDTO;
import jp.co.canon.rss.logmanager.service.HistroyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

@Slf4j
@RestController
@RequestMapping(ReqURLController.API_DEFAULT_HISTORY_URL)
public class HistoryController {
    private HistroyService histroyService;

    public HistoryController(HistroyService histroyService) {
        this.histroyService = histroyService;
    }

    // 빌드로그 리스트 취득
    @GetMapping(ReqURLController.API_GET_BUILD_LOG_LIST)
    @Operation(summary="Acquisition of Build History List")
    @ApiResponses({
            @ApiResponse(
                    responseCode="200",
                    description="OK(Build History List acquisition success)",
                    content = @Content(
                            schema = @Schema(implementation = ResBuildLog.class),
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(
                                    name = "example1",
                                    value = HistoryExamples.RES_GET_BUILD_LOG_LIST))
            ),
            @ApiResponse(responseCode="400", description="Bad Request"),
            @ApiResponse(responseCode="404", description="Not Found"),
            @ApiResponse(responseCode="500", description="Internal Server Error")
    })
    public ResponseEntity<?> getBuildLogList(
            HttpServletRequest request,
            @Parameter(
                    name = "jobType",
                    description = "JobType to display Build History List(Remote or Local)",
                    required = true,
                    examples = {
                            @ExampleObject(value="remote", name="remote"),
                            @ExampleObject(value="local", name="local")
                    }
            )
            @Valid @PathVariable(value = "jobType") @NotNull String jobTypeFlag,
            @Parameter(name = "jobId", description = "JobId to display Build History List", required = true, example = "1")
            @Valid @PathVariable(value = "jobId") @NotNull int jobId,
            @Parameter(
                    name = "flag",
                    description = "Flags representing CONVERT, ERROR, CRAS, VERSION",
                    required = true,
                    examples = {
                            @ExampleObject(value="collect", name="collect"),
                            @ExampleObject(value="convert", name="convert"),
                            @ExampleObject(value="error", name="error"),
                            @ExampleObject(value="cras", name="cras"),
                            @ExampleObject(value="version", name="version"),
                            @ExampleObject(value="purge", name="purge")
                    }
            )
            @Valid @PathVariable(value = "flag") @NotNull String flag) {
        try {
            List<ResHistoryDTO> buildLogList = histroyService.getBuildLogList(jobTypeFlag, jobId, flag);
            return ResponseEntity.status(HttpStatus.OK).body(buildLogList);
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // 빌드로그 텍스트 취득(Cras Server)
    @GetMapping(ReqURLController.API_GET_BUILD_LOG_DETAIL_CRAS)
    @Operation(summary="Acquisition of Build History List")
    @ApiResponses({
            @ApiResponse(
                    responseCode="200",
                    description="OK(Build History List acquisition success)",
                    content = @Content(
                            mediaType = MediaType.TEXT_PLAIN_VALUE,
                            examples = @ExampleObject(
                                    name = "example1",
                                    value = HistoryExamples.RES_GET_BUILD_LOG_TEXT))

            ),
            @ApiResponse(responseCode="400", description="Bad Request"),
            @ApiResponse(responseCode="404", description="Not Found"),
            @ApiResponse(responseCode="500", description="Internal Server Error")
    })
    public ResponseEntity<?> getBuildLogTextListCras(
            HttpServletRequest request,
            @Parameter(
                    name = "jobType",
                    description = "JobType to display Build History List(Remote or Local)",
                    required = true,
                    examples = {
                            @ExampleObject(value="remote", name="remote"),
                            @ExampleObject(value="local", name="local")
                    }
            )
            @Valid @PathVariable(value = "jobType") @NotNull String jobTypeFlag,
            @Parameter(name = "jobId", description = "JobId to display Build History List", required = true, example = "1")
            @Valid @PathVariable(value = "jobId") int jobId,
            @Parameter(
                    name = "flag",
                    description = "Flags representing CONVERT, ERROR, CRAS, VERSION",
                    required = true,
                    examples = {
                            @ExampleObject(value="collect", name="collect"),
                            @ExampleObject(value="convert", name="convert"),
                            @ExampleObject(value="error", name="error"),
                            @ExampleObject(value="cras", name="cras"),
                            @ExampleObject(value="version", name="version"),
                            @ExampleObject(value="purge", name="purge")
                    }
            )
            @Valid @PathVariable(value = "flag") @NotNull String flag,
            @Parameter(name = "id", description = "Build Log ID", required = true, example = "cras-qmsd8g")
            @Valid @PathVariable(value = "id") @NotNull String buildLogId) {
        try {
            String buildLogText = histroyService.getBuildLogTextCras(jobTypeFlag, jobId, flag, buildLogId);
            return ResponseEntity.status(HttpStatus.OK).body(buildLogText);
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // 빌드로그 텍스트 취득(Log Monitor Server)
    @GetMapping(ReqURLController.API_GET_BUILD_LOG_DETAIL_LOGMONITOR)
    @Operation(summary="Acquisition of Build History List")
    @ApiResponses({
            @ApiResponse(
                    responseCode="200",
                    description="OK(Build History List acquisition success)",
                    content = @Content(
                            mediaType = MediaType.TEXT_PLAIN_VALUE,
                            examples = @ExampleObject(
                                    name = "example1",
                                    value = HistoryExamples.RES_GET_BUILD_LOG_TEXT))

            ),
            @ApiResponse(responseCode="400", description="Bad Request"),
            @ApiResponse(responseCode="404", description="Not Found"),
            @ApiResponse(responseCode="500", description="Internal Server Error")
    })
    public ResponseEntity<?> getBuildLogTextListLogMonitor(
            HttpServletRequest request,
            @Parameter(
                    name = "jobType",
                    description = "JobType to display Build History List(Remote or Local)",
                    required = true,
                    examples = {
                            @ExampleObject(value="remote", name="remote"),
                            @ExampleObject(value="local", name="local")
                    }
            )
            @Valid @PathVariable(value = "jobType") @NotNull String jobTypeFlag,
            @Parameter(name = "jobId", description = "JobId to display Build History List", required = true, example = "1")
            @Valid @PathVariable(value = "jobId") int jobId,
            @Parameter(
                    name = "flag",
                    description = "Flags representing CONVERT, ERROR, CRAS, VERSION",
                    required = true,
                    examples = {
                            @ExampleObject(value="collect", name="collect"),
                            @ExampleObject(value="convert", name="convert"),
                            @ExampleObject(value="error", name="error"),
                            @ExampleObject(value="cras", name="cras"),
                            @ExampleObject(value="version", name="version"),
                            @ExampleObject(value="purge", name="purge")
                    }
            )
            @Valid @PathVariable(value = "flag") @NotNull String flag,
            @Parameter(name = "id", description = "Build Log ID", required = true, example = "cras-qmsd8g")
            @Valid @PathVariable(value = "id") @NotNull String buildLogId) {
        try {
            String buildLogText = histroyService.getBuildLogTextLogMonitor(jobTypeFlag, jobId, flag, buildLogId);
            return ResponseEntity.status(HttpStatus.OK).body(buildLogText);
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
