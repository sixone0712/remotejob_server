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
import jp.co.canon.rss.logmanager.controller.model.site.PlansDTO;
import jp.co.canon.rss.logmanager.dto.devlog.ReqLogMonitorDevLogDTO;
import jp.co.canon.rss.logmanager.service.DevLogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@Slf4j
@RestController
@RequestMapping(ReqURLController.API_DEFAULT_DEVLOG_URL)
public class DevLogController {
    DevLogService devLogService;
    public DevLogController(DevLogService devLogService) {
        this.devLogService = devLogService;
    }

    // get LOG MONITOR dev log
    @GetMapping(ReqURLController.API_GET_LOGMONITOR)
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
    public ResponseEntity<?> getLogMonitorDevLog(HttpServletResponse response,
                                                 @io.swagger.v3.oas.annotations.parameters.RequestBody(
                                                         description = "Remote job data to add the information",
                                                         required = true,
                                                         content = @Content(
                                                                 schema = @Schema(implementation = ReqRemoteJob.class),
                                                                 examples = @ExampleObject(value = JobExamples.REQ_ADD_EDIT_REMOTE_JOB))
                                                 )
                                                 @Valid @RequestParam(name = "path", required = true, defaultValue = "") String path) {
        try {
            ReqLogMonitorDevLogDTO reqLogMonitorDevLogDTO = new ReqLogMonitorDevLogDTO();
            reqLogMonitorDevLogDTO.setPath(path);
            devLogService.getLogMonitorDevLog(response, reqLogMonitorDevLogDTO);
            return ResponseEntity.status(HttpStatus.OK).body(null);
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // get CRAS Server dev log
    @GetMapping(ReqURLController.API_GET_CRASSERVER)
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
    public ResponseEntity<?> getCrasServerDevLog(HttpServletResponse response,
                                                 @Parameter(
                                                         schema = @Schema(example = "1"),
                                                         description = "The Site ID of the Site for which you want to get the plan", required = true)
                                                 @Valid @PathVariable(value = "siteId") @NotNull int siteId) {
        try {
            devLogService.getCrasServerDevLog(response, siteId);
            return ResponseEntity.status(HttpStatus.OK).body(null);
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
