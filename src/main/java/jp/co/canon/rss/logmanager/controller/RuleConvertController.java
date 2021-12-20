package jp.co.canon.rss.logmanager.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jp.co.canon.rss.logmanager.config.ReqURLController;
import jp.co.canon.rss.logmanager.controller.examples.SiteExamples;
import jp.co.canon.rss.logmanager.controller.model.site.ResPostReturnSiteId;
import jp.co.canon.rss.logmanager.dto.site.ReqAddSiteDTO;
import jp.co.canon.rss.logmanager.dto.site.ResSitesNamesDTO;
import jp.co.canon.rss.logmanager.exception.ConvertException;
import jp.co.canon.rss.logmanager.exception.dto.ConvertPreviewExceptionDTO;
import jp.co.canon.rss.logmanager.service.ConvertRulesService;
import jp.co.canon.rss.logmanager.util.CallRestAPI;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.net.URLEncoder;

@Slf4j
@RestController
@RequestMapping(ReqURLController.API_DEFAULT_RULE_CONVERT_URL)
public class RuleConvertController {
    @Value("${convert-data.cras-server-address}")
    private String crasServer;

    private static String convertFileName;
    @Value("${convert-data.file-name}")
    public void setConvertFileName(String fileName) {
        convertFileName = fileName;
    }

    private ConvertRulesService convertRulesService;
    public RuleConvertController(ConvertRulesService convertRulesService) {
        this.convertRulesService = convertRulesService;
    }

    // get log list
    @GetMapping(ReqURLController.API_GET_LOG_LIST)
    @Operation(summary="Get all Cras data site list")
    @ApiResponses({
            @ApiResponse(
                    responseCode="200",
                    description="OK(Successful acquisition of all Cras data site list)",
                    content = { @Content(
                            schema = @Schema(implementation = ResSitesNamesDTO.class),
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(
                                    name = "example1",
                                    value = SiteExamples.GET_CRAS_DATA_SITE_RES)) }
            ),
            @ApiResponse(responseCode="400", description="Bad Request"),
            @ApiResponse(responseCode="404", description="Not Found"),
            @ApiResponse(responseCode="500", description="Internal Server Error")
    })
    public ResponseEntity<?> getLogList(HttpServletRequest request) {
        try {
            Object resConvertRuleDTOList = convertRulesService.getLogList(ReqURLController.API_GET_LOG_LIST);
            return ResponseEntity.status(HttpStatus.OK).body(resConvertRuleDTOList);
        } catch (ConvertException e) {
            log.error(e.getMessage());
            ConvertPreviewExceptionDTO convertPreviewExceptionDTO = new ConvertPreviewExceptionDTO()
                    .setTimestamp(e.getTimestamp())
                    .setStatus(e.getStatus())
                    .setCras_error(e.getCrasError())
                    .setMessage(e.getMessage())
                    .setPath(e.getPath());
            return ResponseEntity
                    .status(500)
                    .body(convertPreviewExceptionDTO);
        }
    }

    // get specific log
    @GetMapping(ReqURLController.API_GET_SPECIFIC_LOG_LIST)
    @Operation(summary="Get all Cras data site list")
    @ApiResponses({
            @ApiResponse(
                    responseCode="200",
                    description="OK(Successful acquisition of all Cras data site list)",
                    content = { @Content(
                            schema = @Schema(implementation = ResSitesNamesDTO.class),
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(
                                    name = "example1",
                                    value = SiteExamples.GET_CRAS_DATA_SITE_RES)) }
            ),
            @ApiResponse(responseCode="400", description="Bad Request"),
            @ApiResponse(responseCode="404", description="Not Found"),
            @ApiResponse(responseCode="500", description="Internal Server Error")
    })
    public ResponseEntity<?> getSpecificLog(HttpServletRequest request,
                                            @Parameter(name = "logId", description = "Cras Data Site ID", required = true, example = "1")
                                                @Valid @PathVariable(value = "logId") @NotNull int logId) {
        try {
            Object resConvertRuleDTOList = convertRulesService.getSpecificLog(logId, ReqURLController.API_GET_SPECIFIC_LOG_LIST);
            return ResponseEntity.status(HttpStatus.OK).body(resConvertRuleDTOList);
        } catch (ConvertException e) {
            log.error(e.getMessage());
            ConvertPreviewExceptionDTO convertPreviewExceptionDTO = new ConvertPreviewExceptionDTO()
                    .setTimestamp(e.getTimestamp())
                    .setStatus(e.getStatus())
                    .setCras_error(e.getCrasError())
                    .setMessage(e.getMessage())
                    .setPath(e.getPath());
            return ResponseEntity
                    .status(500)
                    .body(convertPreviewExceptionDTO);
        }
    }

    // add log
    @PostMapping(ReqURLController.API_POST_ADD_LOG)
    @Operation(summary="Add new Cras data site")
    @ApiResponses({
            @ApiResponse(
                    responseCode="200",
                    description="OK(Successful addition of new Cras data site)",
                    content = { @Content(
                            schema = @Schema(implementation = ResPostReturnSiteId.class),
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(
                                    name = "example1",
                                    value = SiteExamples.POST_ADD_CRAS_DATA_SITE_RES)) }
            ),
            @ApiResponse(responseCode="400", description="Bad Request"),
            @ApiResponse(responseCode="404", description="Not Found"),
            @ApiResponse(responseCode="500", description="Internal Server Error")
    })
    public ResponseEntity<?> addLog(HttpServletRequest request,
                                             @io.swagger.v3.oas.annotations.parameters.RequestBody(
                                                     description = "Information for Cras data site registration",
                                                     required = true,
                                                     content = @Content(
                                                             schema = @Schema(implementation = ReqAddSiteDTO.class),
                                                             examples = @ExampleObject(value = SiteExamples.POST_ADD_CRAS_DATA_SITE_REQ)))
                                             @RequestBody Object reqAddLog) {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(convertRulesService.addLog(reqAddLog, ReqURLController.API_POST_ADD_LOG));
        } catch (ConvertException e) {
            log.error(e.getMessage());
            ConvertPreviewExceptionDTO convertPreviewExceptionDTO = new ConvertPreviewExceptionDTO()
                    .setTimestamp(e.getTimestamp())
                    .setStatus(e.getStatus())
                    .setCras_error(e.getCrasError())
                    .setMessage(e.getMessage())
                    .setPath(e.getPath());
            return ResponseEntity
                    .status(500)
                    .body(convertPreviewExceptionDTO);
        }
    }

    // edit log
    @PatchMapping(ReqURLController.API_PATCH_EDIT_LOG)
    @Operation(summary="Add new Cras data site")
    @ApiResponses({
            @ApiResponse(
                    responseCode="200",
                    description="OK(Successful addition of new Cras data site)",
                    content = { @Content(
                            schema = @Schema(implementation = ResPostReturnSiteId.class),
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(
                                    name = "example1",
                                    value = SiteExamples.POST_ADD_CRAS_DATA_SITE_RES)) }
            ),
            @ApiResponse(responseCode="400", description="Bad Request"),
            @ApiResponse(responseCode="404", description="Not Found"),
            @ApiResponse(responseCode="500", description="Internal Server Error")
    })
    public ResponseEntity<?> editLog(HttpServletRequest request,
                                     @io.swagger.v3.oas.annotations.parameters.RequestBody(
                                             description = "Information for Cras data site registration",
                                             required = true,
                                             content = @Content(
                                                     schema = @Schema(implementation = ReqAddSiteDTO.class),
                                                     examples = @ExampleObject(value = SiteExamples.POST_ADD_CRAS_DATA_SITE_REQ)))
                                     @RequestBody Object reqEditLog,
                                             @Parameter(name = "logId", description = "Cras Data Site ID", required = true, example = "1")
                                             @Valid @PathVariable(value = "logId") @NotNull int logId) {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(convertRulesService.editLog(reqEditLog, logId, ReqURLController.API_PATCH_EDIT_LOG));
        } catch (ConvertException e) {
            log.error(e.getMessage());
            ConvertPreviewExceptionDTO convertPreviewExceptionDTO = new ConvertPreviewExceptionDTO()
                    .setTimestamp(e.getTimestamp())
                    .setStatus(e.getStatus())
                    .setCras_error(e.getCrasError())
                    .setMessage(e.getMessage())
                    .setPath(e.getPath());
            return ResponseEntity
                    .status(500)
                    .body(convertPreviewExceptionDTO);
        }
    }

    // delete log
    @DeleteMapping(ReqURLController.API_DELETE_LOG)
    @Operation(summary = "Delete Cras data site (Delete related Cras data and Cras data judge rules)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK(Successful Cras data site deletion)"),
            @ApiResponse(responseCode = "400", description = "Bad Request"),
            @ApiResponse(responseCode = "404", description = "Not Found"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
    public ResponseEntity<?> deleteLog(
            HttpServletRequest request,
            @Parameter(name = "logId", description = "Cras Data Site ID", required = true, example = "1")
            @Valid @PathVariable(value = "logId") @NotNull int logId) {
        try {
            convertRulesService.deleteLog(logId, ReqURLController.API_DELETE_LOG);
            return ResponseEntity.status(HttpStatus.OK).body(null);
        } catch (ConvertException e) {
            log.error(e.getMessage());
            ConvertPreviewExceptionDTO convertPreviewExceptionDTO = new ConvertPreviewExceptionDTO()
                    .setTimestamp(e.getTimestamp())
                    .setStatus(e.getStatus())
                    .setCras_error(e.getCrasError())
                    .setMessage(e.getMessage())
                    .setPath(e.getPath());
            return ResponseEntity
                    .status(500)
                    .body(convertPreviewExceptionDTO);
        }
    }

    // get rule list
    @GetMapping(ReqURLController.API_GET_RULE_LIST)
    @Operation(summary="Get all Cras data site list")
    @ApiResponses({
            @ApiResponse(
                    responseCode="200",
                    description="OK(Successful acquisition of all Cras data site list)",
                    content = { @Content(
                            schema = @Schema(implementation = ResSitesNamesDTO.class),
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(
                                    name = "example1",
                                    value = SiteExamples.GET_CRAS_DATA_SITE_RES)) }
            ),
            @ApiResponse(responseCode="400", description="Bad Request"),
            @ApiResponse(responseCode="404", description="Not Found"),
            @ApiResponse(responseCode="500", description="Internal Server Error")
    })
    public ResponseEntity<?> getRuleList(HttpServletRequest request,
                                         @Parameter(name = "logId", description = "Cras Data Site ID", required = true, example = "1")
                                         @Valid @PathVariable(value = "logId") @NotNull int logId) {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(convertRulesService.getRuleList(logId, ReqURLController.API_GET_RULE_LIST));
        } catch (ConvertException e) {
            log.error(e.getMessage());
            ConvertPreviewExceptionDTO convertPreviewExceptionDTO = new ConvertPreviewExceptionDTO()
                    .setTimestamp(e.getTimestamp())
                    .setStatus(e.getStatus())
                    .setCras_error(e.getCrasError())
                    .setMessage(e.getMessage())
                    .setPath(e.getPath());
            return ResponseEntity
                    .status(500)
                    .body(convertPreviewExceptionDTO);
        }
    }

    // get rule option
    @GetMapping(ReqURLController.API_GET_RULE_OPTION)
    @Operation(summary="Get all Cras data site list")
    @ApiResponses({
            @ApiResponse(
                    responseCode="200",
                    description="OK(Successful acquisition of all Cras data site list)",
                    content = { @Content(
                            schema = @Schema(implementation = ResSitesNamesDTO.class),
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(
                                    name = "example1",
                                    value = SiteExamples.GET_CRAS_DATA_SITE_RES)) }
            ),
            @ApiResponse(responseCode="400", description="Bad Request"),
            @ApiResponse(responseCode="404", description="Not Found"),
            @ApiResponse(responseCode="500", description="Internal Server Error")
    })
    public ResponseEntity<?> getRuleOption(HttpServletRequest request) {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(convertRulesService.getRuleOption(ReqURLController.API_GET_RULE_OPTION));
        } catch (ConvertException e) {
            log.error(e.getMessage());
            ConvertPreviewExceptionDTO convertPreviewExceptionDTO = new ConvertPreviewExceptionDTO()
                    .setTimestamp(e.getTimestamp())
                    .setStatus(e.getStatus())
                    .setCras_error(e.getCrasError())
                    .setMessage(e.getMessage())
                    .setPath(e.getPath());
            return ResponseEntity
                    .status(500)
                    .body(convertPreviewExceptionDTO);
        }
    }

    // get specific rule
    @GetMapping(ReqURLController.API_GET_SPECIFIC_RULE)
    @Operation(summary="Get all Cras data site list")
    @ApiResponses({
            @ApiResponse(
                    responseCode="200",
                    description="OK(Successful acquisition of all Cras data site list)",
                    content = { @Content(
                            schema = @Schema(implementation = ResSitesNamesDTO.class),
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(
                                    name = "example1",
                                    value = SiteExamples.GET_CRAS_DATA_SITE_RES)) }
            ),
            @ApiResponse(responseCode="400", description="Bad Request"),
            @ApiResponse(responseCode="404", description="Not Found"),
            @ApiResponse(responseCode="500", description="Internal Server Error")
    })
    public ResponseEntity<?> getSpecificRule(HttpServletRequest request,
                                             @Parameter(name = "logId", description = "Cras Data Site ID", required = true, example = "1")
                                             @Valid @PathVariable(value = "logId") @NotNull int logId,
                                             @Parameter(name = "ruleId", description = "Cras Data Site ID", required = true, example = "1")
                                             @Valid @PathVariable(value = "ruleId") @NotNull int ruleId) {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(convertRulesService.getSpecificRule(logId, ruleId, ReqURLController.API_GET_SPECIFIC_RULE));
        } catch (ConvertException e) {
            log.error(e.getMessage());
            ConvertPreviewExceptionDTO convertPreviewExceptionDTO = new ConvertPreviewExceptionDTO()
                    .setTimestamp(e.getTimestamp())
                    .setStatus(e.getStatus())
                    .setCras_error(e.getCrasError())
                    .setMessage(e.getMessage())
                    .setPath(e.getPath());
            return ResponseEntity
                    .status(500)
                    .body(convertPreviewExceptionDTO);
        }
    }

    // sample preview csv
    @PostMapping(value = ReqURLController.API_POST_SAMPLE_PREVIEW_CSV, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary="Add new Cras data site")
    @ApiResponses({
            @ApiResponse(
                    responseCode="200",
                    description="OK(Successful addition of new Cras data site)",
                    content = { @Content(
                            schema = @Schema(implementation = ResPostReturnSiteId.class),
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(
                                    name = "example1",
                                    value = SiteExamples.POST_ADD_CRAS_DATA_SITE_RES)) }
            ),
            @ApiResponse(responseCode="400", description="Bad Request"),
            @ApiResponse(responseCode="404", description="Not Found"),
            @ApiResponse(responseCode="500", description="Internal Server Error")
    })
    public ResponseEntity<?> samplePreviewCsv(HttpServletRequest request,
                                              @Parameter(
                                                      name = "files",
                                                      description = "Excel file containing the Cras data",
                                                      required = true,
                                                      content = @Content(
                                                              mediaType = MediaType.MULTIPART_FORM_DATA_VALUE,
                                                              schema = @Schema(implementation = MultipartFile.class))
                                              )
                                              @RequestParam("files") MultipartFile files) throws Exception {
        try {
            ResponseEntity<?> responseEntity = convertRulesService.samplePreviewCsv(
                    files,
                    ReqURLController.API_POST_SAMPLE_PREVIEW_CSV);
            return ResponseEntity.status(responseEntity.getStatusCode()).body(responseEntity.getBody());
        } catch (ConvertException e) {
            log.error(e.getMessage());
            ConvertPreviewExceptionDTO convertPreviewExceptionDTO = new ConvertPreviewExceptionDTO()
                    .setTimestamp(e.getTimestamp())
                    .setStatus(e.getStatus())
                    .setCras_error(e.getCrasError())
                    .setMessage(e.getMessage())
                    .setPath(e.getPath());
            return ResponseEntity
                    .status(500)
                    .body(convertPreviewExceptionDTO);
        }
    }

    // sample preview regex
    @PostMapping(value = ReqURLController.API_POST_SAMPLE_PREVIEW_REGEX, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary="Add new Cras data site")
    @ApiResponses({
            @ApiResponse(
                    responseCode="200",
                    description="OK(Successful addition of new Cras data site)",
                    content = { @Content(
                            schema = @Schema(implementation = ResPostReturnSiteId.class),
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(
                                    name = "example1",
                                    value = SiteExamples.POST_ADD_CRAS_DATA_SITE_RES)) }
            ),
            @ApiResponse(responseCode="400", description="Bad Request"),
            @ApiResponse(responseCode="404", description="Not Found"),
            @ApiResponse(responseCode="500", description="Internal Server Error")
    })
    public ResponseEntity<?> samplePreviewRegex(HttpServletRequest request,
                                              @Parameter(
                                                      name = "file",
                                                      description = "Excel file containing the Cras data",
                                                      required = true,
                                                      content = @Content(
                                                              mediaType = MediaType.MULTIPART_FORM_DATA_VALUE,
                                                              schema = @Schema(implementation = MultipartFile.class))
                                              )
                                              @RequestParam("files") MultipartFile files) {
        try {
            ResponseEntity<?> responseEntity = convertRulesService.samplePreviewRegex(files,
                    ReqURLController.API_POST_SAMPLE_PREVIEW_REGEX);
            return ResponseEntity.status(responseEntity.getStatusCode()).body(responseEntity.getBody());
        } catch (ConvertException e) {
            log.error(e.getMessage());
            ConvertPreviewExceptionDTO convertPreviewExceptionDTO = new ConvertPreviewExceptionDTO()
                    .setTimestamp(e.getTimestamp())
                    .setStatus(e.getStatus())
                    .setCras_error(e.getCrasError())
                    .setMessage(e.getMessage())
                    .setPath(e.getPath());
            return ResponseEntity
                    .status(500)
                    .body(convertPreviewExceptionDTO);
        }
    }

    // convert preview csv
    @PostMapping(ReqURLController.API_POST_CONVERT_PREVIEW_CSV)
    @Operation(summary="Add new Cras data site")
    @ApiResponses({
            @ApiResponse(
                    responseCode="200",
                    description="OK(Successful addition of new Cras data site)",
                    content = { @Content(
                            schema = @Schema(implementation = ResPostReturnSiteId.class),
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(
                                    name = "example1",
                                    value = SiteExamples.POST_ADD_CRAS_DATA_SITE_RES)) }
            ),
            @ApiResponse(responseCode="400", description="Bad Request"),
            @ApiResponse(responseCode="404", description="Not Found"),
            @ApiResponse(responseCode="500", description="Internal Server Error")
    })
    public ResponseEntity<?> getConvertPreviewCras(HttpServletRequest request,
                                             @io.swagger.v3.oas.annotations.parameters.RequestBody(
                                                     description = "Information for Cras data site registration",
                                                     required = true,
                                                     content = @Content(
                                                             schema = @Schema(implementation = ReqAddSiteDTO.class),
                                                             examples = @ExampleObject(value = SiteExamples.POST_ADD_CRAS_DATA_SITE_REQ)))
                                             @RequestBody Object reqGetConvertPreview) {
        try {
            ResponseEntity<?> responseEntity = convertRulesService.getConvertPreviewCras(reqGetConvertPreview,
                    ReqURLController.API_POST_CONVERT_PREVIEW_CSV);
            return ResponseEntity.status(responseEntity.getStatusCode()).body(responseEntity.getBody());
        } catch (ConvertException e) {
            log.error(e.getMessage());
            ConvertPreviewExceptionDTO convertPreviewExceptionDTO = new ConvertPreviewExceptionDTO()
                    .setTimestamp(e.getTimestamp())
                    .setStatus(e.getStatus())
                    .setCras_error(e.getCrasError())
                    .setMessage(e.getMessage())
                    .setPath(e.getPath());
            return ResponseEntity
                    .status(500)
                    .body(convertPreviewExceptionDTO);
        }
    }

    // convert preview regex
    @PostMapping(ReqURLController.API_POST_CONVERT_PREVIEW_REGEX)
    @Operation(summary="Add new Cras data site")
    @ApiResponses({
            @ApiResponse(
                    responseCode="200",
                    description="OK(Successful addition of new Cras data site)",
                    content = { @Content(
                            schema = @Schema(implementation = ResPostReturnSiteId.class),
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(
                                    name = "example1",
                                    value = SiteExamples.POST_ADD_CRAS_DATA_SITE_RES)) }
            ),
            @ApiResponse(responseCode="400", description="Bad Request"),
            @ApiResponse(responseCode="404", description="Not Found"),
            @ApiResponse(responseCode="500", description="Internal Server Error")
    })
    public ResponseEntity<?> getConvertPreviewRegex(HttpServletRequest request,
                                                   @io.swagger.v3.oas.annotations.parameters.RequestBody(
                                                           description = "Information for Cras data site registration",
                                                           required = true,
                                                           content = @Content(
                                                                   schema = @Schema(implementation = ReqAddSiteDTO.class),
                                                                   examples = @ExampleObject(value = SiteExamples.POST_ADD_CRAS_DATA_SITE_REQ)))
                                                   @RequestBody Object reqGetConvertPreview) {
        try {
            ResponseEntity<?> responseEntity = convertRulesService.getConvertPreviewRegex(reqGetConvertPreview,
                    ReqURLController.API_POST_CONVERT_PREVIEW_REGEX);
            return ResponseEntity.status(responseEntity.getStatusCode()).body(responseEntity.getBody());
        } catch (ConvertException e) {
            log.error(e.getMessage());
            ConvertPreviewExceptionDTO convertPreviewExceptionDTO = new ConvertPreviewExceptionDTO()
                    .setTimestamp(e.getTimestamp())
                    .setStatus(e.getStatus())
                    .setCras_error(e.getCrasError())
                    .setMessage(e.getMessage())
                    .setPath(e.getPath());
            return ResponseEntity
                    .status(500)
                    .body(convertPreviewExceptionDTO);
        }
    }

    // filter preview
    @PostMapping(ReqURLController.API_POST_FILTER_PREVIEW)
    @Operation(summary="Add new Cras data site")
    @ApiResponses({
            @ApiResponse(
                    responseCode="200",
                    description="OK(Successful addition of new Cras data site)",
                    content = { @Content(
                            schema = @Schema(implementation = ResPostReturnSiteId.class),
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(
                                    name = "example1",
                                    value = SiteExamples.POST_ADD_CRAS_DATA_SITE_RES)) }
            ),
            @ApiResponse(responseCode="400", description="Bad Request"),
            @ApiResponse(responseCode="404", description="Not Found"),
            @ApiResponse(responseCode="500", description="Internal Server Error")
    })
    public ResponseEntity<?> getFilterPreview(HttpServletRequest request,
                                               @io.swagger.v3.oas.annotations.parameters.RequestBody(
                                                       description = "Information for Cras data site registration",
                                                       required = true,
                                                       content = @Content(
                                                               schema = @Schema(implementation = ReqAddSiteDTO.class),
                                                               examples = @ExampleObject(value = SiteExamples.POST_ADD_CRAS_DATA_SITE_REQ)))
                                               @RequestBody Object reqGetFilterPreview) {
        try {
            ResponseEntity<?> responseEntity = convertRulesService.getFilterPreview(reqGetFilterPreview,
                    ReqURLController.API_POST_FILTER_PREVIEW);
            return ResponseEntity.status(responseEntity.getStatusCode()).body(responseEntity.getBody());
        } catch (ConvertException e) {
            log.error(e.getMessage());
            ConvertPreviewExceptionDTO convertPreviewExceptionDTO = new ConvertPreviewExceptionDTO()
                    .setTimestamp(e.getTimestamp())
                    .setStatus(e.getStatus())
                    .setCras_error(e.getCrasError())
                    .setMessage(e.getMessage())
                    .setPath(e.getPath());
            return ResponseEntity
                    .status(500)
                    .body(convertPreviewExceptionDTO);
        }
    }

    // add covert rule
    @PostMapping(ReqURLController.API_POST_ADD_CONVERT_RULE)
    @Operation(summary="Add new Cras data site")
    @ApiResponses({
            @ApiResponse(
                    responseCode="200",
                    description="OK(Successful addition of new Cras data site)",
                    content = { @Content(
                            schema = @Schema(implementation = ResPostReturnSiteId.class),
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(
                                    name = "example1",
                                    value = SiteExamples.POST_ADD_CRAS_DATA_SITE_RES)) }
            ),
            @ApiResponse(responseCode="400", description="Bad Request"),
            @ApiResponse(responseCode="404", description="Not Found"),
            @ApiResponse(responseCode="500", description="Internal Server Error")
    })
    public ResponseEntity<?> addConvertRule(HttpServletRequest request,
                                              @io.swagger.v3.oas.annotations.parameters.RequestBody(
                                                      description = "Information for Cras data site registration",
                                                      required = true,
                                                      content = @Content(
                                                              schema = @Schema(implementation = ReqAddSiteDTO.class),
                                                              examples = @ExampleObject(value = SiteExamples.POST_ADD_CRAS_DATA_SITE_REQ)))
                                              @RequestBody Object reqAddConvertRule,
                                              @Parameter(name = "logId", description = "Cras Data Site ID", required = true, example = "1")
                                              @Valid @PathVariable(value = "logId") @NotNull int logId) {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(convertRulesService.addConvertRule(reqAddConvertRule, logId, ReqURLController.API_POST_ADD_CONVERT_RULE));
        } catch (ConvertException e) {
            log.error(e.getMessage());
            ConvertPreviewExceptionDTO convertPreviewExceptionDTO = new ConvertPreviewExceptionDTO()
                    .setTimestamp(e.getTimestamp())
                    .setStatus(e.getStatus())
                    .setCras_error(e.getCrasError())
                    .setMessage(e.getMessage())
                    .setPath(e.getPath());
            return ResponseEntity
                    .status(500)
                    .body(convertPreviewExceptionDTO);
        }
    }

    // edit convert rule
    @PatchMapping(ReqURLController.API_PUT_EDIT_CONVERT_RULE)
    @Operation(summary="Add new Cras data site")
    @ApiResponses({
            @ApiResponse(
                    responseCode="200",
                    description="OK(Successful addition of new Cras data site)",
                    content = { @Content(
                            schema = @Schema(implementation = ResPostReturnSiteId.class),
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(
                                    name = "example1",
                                    value = SiteExamples.POST_ADD_CRAS_DATA_SITE_RES)) }
            ),
            @ApiResponse(responseCode="400", description="Bad Request"),
            @ApiResponse(responseCode="404", description="Not Found"),
            @ApiResponse(responseCode="500", description="Internal Server Error")
    })
    public ResponseEntity<?> editConvertRule(HttpServletRequest request,
                                            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                                                    description = "Information for Cras data site registration",
                                                    required = true,
                                                    content = @Content(
                                                            schema = @Schema(implementation = ReqAddSiteDTO.class),
                                                            examples = @ExampleObject(value = SiteExamples.POST_ADD_CRAS_DATA_SITE_REQ)))
                                            @RequestBody Object reqEditConvertRule,
                                            @Parameter(name = "logId", description = "Cras Data Site ID", required = true, example = "1")
                                            @Valid @PathVariable(value = "logId") @NotNull int logId,
                                            @Parameter(name = "ruleId", description = "Cras Data Site ID", required = true, example = "1")
                                            @Valid @PathVariable(value = "ruleId") @NotNull int ruleId) {
        try {
            ResponseEntity<?> responseEntity = convertRulesService.editConvertRule(reqEditConvertRule, logId, ruleId, ReqURLController.API_PUT_EDIT_CONVERT_RULE);
            return ResponseEntity.status(responseEntity.getStatusCode()).body(responseEntity.getBody());
        } catch (ConvertException e) {
            log.error(e.getMessage());
            ConvertPreviewExceptionDTO convertPreviewExceptionDTO = new ConvertPreviewExceptionDTO()
                    .setTimestamp(e.getTimestamp())
                    .setStatus(e.getStatus())
                    .setCras_error(e.getCrasError())
                    .setMessage(e.getMessage())
                    .setPath(e.getPath());
            return ResponseEntity
                    .status(500)
                    .body(convertPreviewExceptionDTO);
        }
    }

    // convert rules import
    @PostMapping(value = ReqURLController.API_POST_CONVERTRULESFILE, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary="Import Convert rules")
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
    public ResponseEntity<?> convertRulesImport(
            HttpServletRequest request,
            @Parameter(
                    name = "file",
                    description = "Excel file containing the Convert rules",
                    required = true,
                    content = @Content(
                            mediaType = MediaType.MULTIPART_FORM_DATA_VALUE,
                            schema = @Schema(implementation = MultipartFile.class))
            )
            @RequestParam("file") MultipartFile file) {
        try {
            CallRestAPI callRestAPI = new CallRestAPI();
            callRestAPI.postConvertPreviewMultipartFileRestAPI(crasServer+ReqURLController.API_POST_CONVERTRULESFILE_CRAS,
                    file, Object.class, ReqURLController.API_POST_CONVERTRULESFILE);

            return ResponseEntity.status(HttpStatus.OK).body(null);
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // convert rules export
    @GetMapping(ReqURLController.API_GET_CONVERTRULESFILE)
    @Operation(summary="Export Convert Rules")
    @ApiResponses({
            @ApiResponse(responseCode="200", description="OK(Successful export of Cras data)"),
            @ApiResponse(responseCode="400", description="Bad Request"),
            @ApiResponse(responseCode="404", description="Not Found"),
            @ApiResponse(responseCode="500", description="Internal Server Error")
    })
    public ResponseEntity<InputStreamResource> convertRulesExport(HttpServletResponse response) {
        try {
            CallRestAPI callRestAPI = new CallRestAPI();
            ResponseEntity<?> res = callRestAPI.getDownloadFileRestAPI(crasServer+ReqURLController.API_GET_CONVERTRULESFILE_CRAS);

            response.setContentType("application/msexcel");
            response.setHeader("Content-Disposition",String.format("attachment; filename=\"%s\"",
                    URLEncoder.encode(convertFileName,"UTF-8")));
            ServletOutputStream sos = response.getOutputStream();
            IOUtils.write((byte[]) res.getBody(), sos);
            return ResponseEntity.status(HttpStatus.OK).body(null);
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
