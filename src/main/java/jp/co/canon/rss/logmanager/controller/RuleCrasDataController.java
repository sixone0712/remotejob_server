package jp.co.canon.rss.logmanager.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jp.co.canon.rss.logmanager.config.ReqURLController;
import jp.co.canon.rss.logmanager.controller.examples.JobExamples;
import jp.co.canon.rss.logmanager.controller.examples.SiteExamples;
import jp.co.canon.rss.logmanager.controller.model.job.ReqRemoteJob;
import jp.co.canon.rss.logmanager.controller.model.site.ResPostReturnSiteId;
import jp.co.canon.rss.logmanager.dto.rulecrasdata.*;
import jp.co.canon.rss.logmanager.dto.site.ReqAddSiteDTO;
import jp.co.canon.rss.logmanager.dto.site.ResSitesNamesDTO;
import jp.co.canon.rss.logmanager.service.CrasDataService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping(ReqURLController.API_DEFAULT_RULE_CRAS_URL)
public class RuleCrasDataController {
    private CrasDataService crasDataService;
    public RuleCrasDataController(CrasDataService crasDataService) {
        this.crasDataService = crasDataService;
    }

    // 전체 리스트 취득
    @GetMapping(ReqURLController.API_GET_CRAS_DATA_SITE)
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
    public ResponseEntity<?> getCrasDataSite(HttpServletRequest request) {
        try {
            List<ResCrasDataSiteDTO> resCrasDataSiteDTOSite = crasDataService.getCrasDataSite();
            return ResponseEntity.status(HttpStatus.OK).body(resCrasDataSiteDTOSite);
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Cras Data Site 추가
    @PostMapping(ReqURLController.API_POST_CRAS_DATA_SITE)
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
    public ResponseEntity<?> addCrasDataSite(HttpServletRequest request,
                                             @io.swagger.v3.oas.annotations.parameters.RequestBody(
                                              description = "Information for Cras data site registration",
                                              required = true,
                                              content = @Content(
                                                      schema = @Schema(implementation = ReqAddSiteDTO.class),
                                                      examples = @ExampleObject(value = SiteExamples.POST_ADD_CRAS_DATA_SITE_REQ)))
                                                 @RequestBody ReqAddCrasDataSiteDTO reqAddCrasDataSiteDTO) {
        try {
            ResAddCrasDataSiteDTO resAddCrasDataSiteDTO = crasDataService.addCrasDataSite(reqAddCrasDataSiteDTO);
            return ResponseEntity.status(HttpStatus.OK).body(resAddCrasDataSiteDTO);
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Cras Data List 삭제
    @DeleteMapping(ReqURLController.API_DELETE_CRAS_DATA_SITE)
    @Operation(summary = "Delete Cras data site (Delete related Cras data and Cras data judge rules)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK(Successful Cras data site deletion)"),
            @ApiResponse(responseCode = "400", description = "Bad Request"),
            @ApiResponse(responseCode = "404", description = "Not Found"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
    public ResponseEntity<?> deleteCrasDataSite(
            HttpServletRequest request,
            @Parameter(name = "id", description = "Cras Data Site ID", required = true, example = "1")
            @Valid @PathVariable(value = "id") @NotNull int crasDataSiteId) {
        try {
            crasDataService.deleteCrasDataSite(crasDataSiteId);
            return ResponseEntity.status(HttpStatus.OK).body(null);
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Cras Data 추가
    @PostMapping(ReqURLController.API_POST_CRAS_DATA)
    @Operation(summary="Add new Cras data")
    @ApiResponses({
            @ApiResponse(
                    responseCode="200",
                    description="OK(Successful addition of new Cras data)",
                    content = { @Content(
                            schema = @Schema(implementation = ResPostReturnSiteId.class),
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(
                                    name = "example1",
                                    value = SiteExamples.POST_ADD_NEW_CRAS_DATA_RES)) }
            ),
            @ApiResponse(responseCode="400", description="Bad Request"),
            @ApiResponse(responseCode="404", description="Not Found"),
            @ApiResponse(responseCode="500", description="Internal Server Error")
    })
    public ResponseEntity<?> addCrasData(HttpServletRequest request,
                                         @Parameter(name = "id", description = "Cras Data Site ID to add", required = true, example = "1")
                                         @Valid @PathVariable(value = "id") @NotNull int crasDataSiteId,
                                         @io.swagger.v3.oas.annotations.parameters.RequestBody(
                                                 description = "Information for add new Cras data",
                                                 required = true,
                                                 content = @Content(
                                                         schema = @Schema(implementation = ReqAddSiteDTO.class),
                                                         examples = @ExampleObject(value = SiteExamples.POST_ADD_NEW_CRAS_DATA_REQ))
                                         )
                                         @RequestBody ReqAddCrasDataDTO reqAddCrasDataDTO) {
        try {
            ResAddCrasDataSiteDTO resAddCrasDataSiteDTO = crasDataService.addCrasData(reqAddCrasDataDTO, crasDataSiteId);
            return ResponseEntity.status(HttpStatus.OK).body(resAddCrasDataSiteDTO);
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Create Data List 취득
    @GetMapping(ReqURLController.API_GET_CRAS_DATA)
    @Operation(summary="Get all Site name and Fab name lists")
    @ApiResponses({
            @ApiResponse(
                    responseCode="200",
                    description="OK(Successful acquisition of 'Site ID(int), Site name(String)')",
                    content = { @Content(
                            schema = @Schema(implementation = ResSitesNamesDTO.class),
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(
                                    name = "example1",
                                    value = SiteExamples.GET_SITE_NAME_RES)) }
            ),
            @ApiResponse(responseCode="400", description="Bad Request"),
            @ApiResponse(responseCode="404", description="Not Found"),
            @ApiResponse(responseCode="500", description="Internal Server Error")
    })
    public ResponseEntity<?> getCrasData(HttpServletRequest request,
                                         @Parameter(name = "id", description = "Site ID to add", required = true, example = "1")
                                         @Valid @PathVariable(value = "id") @NotNull int crasDataSiteId) {
        try {
            List<ResCrasDataDTO> resCrasDataDTO = crasDataService.getCrasData(crasDataSiteId);
            return ResponseEntity.status(HttpStatus.OK).body(resCrasDataDTO);
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Create Data Detail 취득
    @GetMapping(ReqURLController.API_GET_CRAS_DATA_DETAIL)
    @Operation(summary="Get all Site name and Fab name lists")
    @ApiResponses({
            @ApiResponse(
                    responseCode="200",
                    description="OK(Successful acquisition of 'Site ID(int), Site name(String)')",
                    content = { @Content(
                            schema = @Schema(implementation = ResSitesNamesDTO.class),
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(
                                    name = "example1",
                                    value = SiteExamples.GET_SITE_NAME_RES)) }
            ),
            @ApiResponse(responseCode="400", description="Bad Request"),
            @ApiResponse(responseCode="404", description="Not Found"),
            @ApiResponse(responseCode="500", description="Internal Server Error")
    })
    public ResponseEntity<?> getCrasDataDetail(HttpServletRequest request,
                                         @Parameter(name = "id", description = "Site ID to add", required = true, example = "1")
                                         @Valid @PathVariable(value = "id") @NotNull int crasDataSiteId,
                                         @Parameter(name = "itemId", description = "item Id to add", required = true, example = "1")
                                             @Valid @PathVariable(value = "itemId") @NotNull int itemId) {
        try {
            ResCrasDataDetailDTO resCrasDataDetailDTO = crasDataService.getCrasDataDetail(itemId);
            return ResponseEntity.status(HttpStatus.OK).body(resCrasDataDetailDTO);
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Create Data 수정
    @PutMapping(ReqURLController.API_PUT_CRAS_DATA)
    @Operation(summary = "Modify the information of the already registered Remote Job")
    @Parameters({
            @Parameter(name = "id", description = "Job ID to modify the information", required = true)
    })
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK(Successful Job information modify)"),
            @ApiResponse(responseCode = "400", description = "Bad Request"),
            @ApiResponse(responseCode = "404", description = "Not Found"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
    public ResponseEntity<?> updateCrasData(
            HttpServletRequest request,
            @Parameter(name = "id", description = "Job ID to modify the information", required = true, example = "3")
            @Valid @PathVariable(value = "id") @NotNull int crasDataSiteId,
            @Parameter(name = "itemId", description = "Job ID to modify the information", required = true, example = "3")
            @Valid @PathVariable(value = "itemId") @NotNull int itemId,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Remote job modify to add the information",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = ReqRemoteJob.class),
                            examples = @ExampleObject(value = JobExamples.REQ_ADD_EDIT_REMOTE_JOB))
            )
            @Parameter(
                    name = "jsonObj",
                    description = "Remote job data to modify the information",
                    required = true,
                    schema = @Schema(implementation = ReqRemoteJob.class),
                    example = JobExamples.REQ_ADD_EDIT_REMOTE_JOB
            )
            @RequestBody ReqAddCrasDataDTO reqAddCrasDataDTO) {
        try {
            ResCrasDataItemIdDTO resCrasDataItemIdDTO = crasDataService.editCrasData(itemId, reqAddCrasDataDTO, crasDataSiteId);
            return ResponseEntity.status(HttpStatus.OK).body(resCrasDataItemIdDTO);
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Cras Data 삭제
    @DeleteMapping(ReqURLController.API_DELETE_CRAS_DATA)
    @Operation(summary = "Delete specified Remote Job (Delete related Notification information and MailContext information at the same time)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK(Successful Job deletion)"),
            @ApiResponse(responseCode = "400", description = "Bad Request"),
            @ApiResponse(responseCode = "404", description = "Not Found"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
    public ResponseEntity<?> deleteCrasData(
            HttpServletRequest request,
            @Parameter(name = "id", description = "Job ID to delete", required = true, example = "1")
            @Valid @PathVariable(value = "id") @NotNull int crasDataSiteId,
            @Parameter(name = "itemId", description = "Job ID to delete", required = true, example = "1")
            @Valid @PathVariable(value = "itemId") @NotNull int itemId){
        try {
            crasDataService.deleteCrasData(itemId);
            return ResponseEntity.status(HttpStatus.OK).body(null);
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Judge Rules 추가
    @PostMapping(ReqURLController.API_POST_JUDGE_RULE)
    @Operation(summary="Add new cras data site")
    @ApiResponses({
            @ApiResponse(
                    responseCode="200",
                    description="OK(Successful addition of new Cras data site)",
                    content = { @Content(
                            schema = @Schema(implementation = ResPostReturnSiteId.class),
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(
                                    name = "example1",
                                    value = SiteExamples.POST_ADD_NEW_SITE_RES)) }
            ),
            @ApiResponse(responseCode="400", description="Bad Request"),
            @ApiResponse(responseCode="404", description="Not Found"),
            @ApiResponse(responseCode="500", description="Internal Server Error")
    })
    public ResponseEntity<?> addJudgeRules(HttpServletRequest request,
                                           @Parameter(name = "id", description = "Site ID to add", required = true, example = "1")
                                           @Valid @PathVariable(value = "id") @NotNull int crasDataSiteId,
                                             @io.swagger.v3.oas.annotations.parameters.RequestBody(
                                                     description = "Site ID of the site to check detailed information",
                                                     required = true,
                                                     content = @Content(
                                                             schema = @Schema(implementation = ReqAddSiteDTO.class),
                                                             examples = @ExampleObject(value = SiteExamples.POST_ADD_NEW_SITE_REQ))
                                             )
                                             @RequestBody ReqAddJudgeRuleDTO reqAddJudgeRuleDTO) {
        try {
            ResAddCrasJudgeRuleDTO resAddCrasJudgeRuleDTO = crasDataService.addCrasJudgeRule(reqAddJudgeRuleDTO, crasDataSiteId);
            return ResponseEntity.status(HttpStatus.OK).body(resAddCrasJudgeRuleDTO);
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Judge Rules List 취득
    @GetMapping(ReqURLController.API_GET_JUDGE_RULES)
    @Operation(summary="Get all Site name and Fab name lists")
    @ApiResponses({
            @ApiResponse(
                    responseCode="200",
                    description="OK(Successful acquisition of 'Site ID(int), Site name(String)')",
                    content = { @Content(
                            schema = @Schema(implementation = ResSitesNamesDTO.class),
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(
                                    name = "example1",
                                    value = SiteExamples.GET_SITE_NAME_RES)) }
            ),
            @ApiResponse(responseCode="400", description="Bad Request"),
            @ApiResponse(responseCode="404", description="Not Found"),
            @ApiResponse(responseCode="500", description="Internal Server Error")
    })
    public ResponseEntity<?> getJudgeRules(HttpServletRequest request,
                                         @Parameter(name = "id", description = "Site ID to add", required = true, example = "1")
                                         @Valid @PathVariable(value = "id") @NotNull int crasDataSiteId) {
        try {
            List<ResCrasDataDTO> resCrasDataDTO = crasDataService.getJudgeRules(crasDataSiteId, "all");
            return ResponseEntity.status(HttpStatus.OK).body(resCrasDataDTO);
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Judge Rules List 취득(true or false)
    @GetMapping(ReqURLController.API_GET_JUDGE_RULES_ENABLE)
    @Operation(summary="Get all Site name and Fab name lists")
    @ApiResponses({
            @ApiResponse(
                    responseCode="200",
                    description="OK(Successful acquisition of 'Site ID(int), Site name(String)')",
                    content = { @Content(
                            schema = @Schema(implementation = ResSitesNamesDTO.class),
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(
                                    name = "example1",
                                    value = SiteExamples.GET_SITE_NAME_RES)) }
            ),
            @ApiResponse(responseCode="400", description="Bad Request"),
            @ApiResponse(responseCode="404", description="Not Found"),
            @ApiResponse(responseCode="500", description="Internal Server Error")
    })
    public ResponseEntity<?> getJudgeRulesEnable(HttpServletRequest request,
                                                 @Parameter(name = "id", description = "Site ID to add", required = true, example = "1")
                                                 @Valid @PathVariable(value = "id") @NotNull int siteId) {
        try {
            List<ResCrasDataDTO> resCrasDataDTO = crasDataService.getJudgeRules(siteId, "true");
            return ResponseEntity.status(HttpStatus.OK).body(resCrasDataDTO);
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Judge Rules Detail 취득
    @GetMapping(ReqURLController.API_GET_JUDGE_RULE_DETAIL)
    @Operation(summary="Get all Site name and Fab name lists")
    @ApiResponses({
            @ApiResponse(
                    responseCode="200",
                    description="OK(Successful acquisition of 'Site ID(int), Site name(String)')",
                    content = { @Content(
                            schema = @Schema(implementation = ResSitesNamesDTO.class),
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(
                                    name = "example1",
                                    value = SiteExamples.GET_SITE_NAME_RES)) }
            ),
            @ApiResponse(responseCode="400", description="Bad Request"),
            @ApiResponse(responseCode="404", description="Not Found"),
            @ApiResponse(responseCode="500", description="Internal Server Error")
    })
    public ResponseEntity<?> getJudgeRuleDetail(HttpServletRequest request,
                                               @Parameter(name = "id", description = "Site ID to add", required = true, example = "1")
                                               @Valid @PathVariable(value = "id") @NotNull int crasDataSiteId,
                                               @Parameter(name = "itemId", description = "item Id to add", required = true, example = "1")
                                               @Valid @PathVariable(value = "itemId") @NotNull int itemId) {
        try {
            ResJudgeRuleDetailDTO resJudgeRuleDetailDTO = crasDataService.getJudgeRuleDetail(itemId);
            return ResponseEntity.status(HttpStatus.OK).body(resJudgeRuleDetailDTO);
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Judge Rule 수정
    @PutMapping(ReqURLController.API_PUT_JUDGE_RULE_DETAIL)
    @Operation(summary = "Modify the information of the already registered Remote Job")
    @Parameters({
            @Parameter(name = "id", description = "Job ID to modify the information", required = true)
    })
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK(Successful Job information modify)"),
            @ApiResponse(responseCode = "400", description = "Bad Request"),
            @ApiResponse(responseCode = "404", description = "Not Found"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
    public ResponseEntity<?> updateJudgeRule(
            HttpServletRequest request,
            @Parameter(name = "id", description = "Job ID to modify the information", required = true, example = "3")
            @Valid @PathVariable(value = "id") @NotNull int crasDataSiteId,
            @Parameter(name = "itemId", description = "Job ID to modify the information", required = true, example = "3")
            @Valid @PathVariable(value = "itemId") @NotNull int itemId,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Remote job modify to add the information",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = ReqRemoteJob.class),
                            examples = @ExampleObject(value = JobExamples.REQ_ADD_EDIT_REMOTE_JOB))
            )
            @Parameter(
                    name = "jsonObj",
                    description = "Remote job data to modify the information",
                    required = true,
                    schema = @Schema(implementation = ReqRemoteJob.class),
                    example = JobExamples.REQ_ADD_EDIT_REMOTE_JOB
            )
            @RequestBody ReqAddJudgeRuleDTO reqAddJudgeRuleDTO) {
        try {
            ResJudgeRuleItemIdDTO resJudgeRuleItemIdDTO = crasDataService.editJudgeRule(itemId, reqAddJudgeRuleDTO, crasDataSiteId);
            return ResponseEntity.status(HttpStatus.OK).body(resJudgeRuleItemIdDTO);
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Judge Rule 삭제
    @DeleteMapping(ReqURLController.API_DELETE_JUDGE_RULE_DETAIL)
    @Operation(summary = "Delete specified Remote Job (Delete related Notification information and MailContext information at the same time)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK(Successful Job deletion)"),
            @ApiResponse(responseCode = "400", description = "Bad Request"),
            @ApiResponse(responseCode = "404", description = "Not Found"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
    public ResponseEntity<?> deleteJudgeRule(
            HttpServletRequest request,
            @Parameter(name = "id", description = "Job ID to delete", required = true, example = "1")
            @Valid @PathVariable(value = "id") @NotNull int crasDataSiteId,
            @Parameter(name = "itemId", description = "Job ID to delete", required = true, example = "1")
            @Valid @PathVariable(value = "itemId") @NotNull int itemId){
        try {
            crasDataService.deleteJudge(itemId);
            return ResponseEntity.status(HttpStatus.OK).body(null);
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Option List 취득
    @GetMapping(ReqURLController.API_GET_OPTION_LIST_DETAIL)
    @Operation(summary="Get all Site name and Fab name lists")
    @ApiResponses({
            @ApiResponse(
                    responseCode="200",
                    description="OK(Successful acquisition of 'Site ID(int), Site name(String)')",
                    content = { @Content(
                            schema = @Schema(implementation = ResSitesNamesDTO.class),
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(
                                    name = "example1",
                                    value = SiteExamples.GET_SITE_NAME_RES)) }
            ),
            @ApiResponse(responseCode="400", description="Bad Request"),
            @ApiResponse(responseCode="404", description="Not Found"),
            @ApiResponse(responseCode="500", description="Internal Server Error")
    })
    public ResponseEntity<?> getOptionList(HttpServletRequest request,
                                                @Parameter(
                                                        name = "cras",
                                                        description = "Flags representing createlist, judgelist",
                                                        required = true,
                                                        examples = {
                                                                @ExampleObject(value="createlist", name="createlist"),
                                                                @ExampleObject(value="judgelist", name="judgelist")
                                                        }
                                                )
                                                @Valid @PathVariable(value = "cras") @NotNull String cras) {
        try {
            Map<String, List<String>> resOptionDTO = crasDataService.getOptionList(cras);
            return ResponseEntity.status(HttpStatus.OK).body(resOptionDTO);
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Table List 취득
    @GetMapping(ReqURLController.API_GET_TABLE_LIST_DETAIL)
    @Operation(summary="Get all Site name and Fab name lists")
    @ApiResponses({
            @ApiResponse(
                    responseCode="200",
                    description="OK(Successful acquisition of 'Site ID(int), Site name(String)')",
                    content = { @Content(
                            schema = @Schema(implementation = ResSitesNamesDTO.class),
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(
                                    name = "example1",
                                    value = SiteExamples.GET_SITE_NAME_RES)) }
            ),
            @ApiResponse(responseCode="400", description="Bad Request"),
            @ApiResponse(responseCode="404", description="Not Found"),
            @ApiResponse(responseCode="500", description="Internal Server Error")
    })
    public ResponseEntity<?> getTableList(HttpServletRequest request,
                                          @Parameter(name = "id", description = "Site ID to add", required = true, example = "1")
                                          @Valid @PathVariable(value = "id") @NotNull int crasSiteId) {
        try {
            List<String> tableList = crasDataService.getTableList(crasSiteId);
            return ResponseEntity.status(HttpStatus.OK).body(tableList);
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Table Column List 취득
    @GetMapping(ReqURLController.API_GET_COLUMN_LIST_DETAIL)
    @Operation(summary="Get all Site name and Fab name lists")
    @ApiResponses({
            @ApiResponse(
                    responseCode="200",
                    description="OK(Successful acquisition of 'Site ID(int), Site name(String)')",
                    content = { @Content(
                            schema = @Schema(implementation = ResSitesNamesDTO.class),
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(
                                    name = "example1",
                                    value = SiteExamples.GET_SITE_NAME_RES)) }
            ),
            @ApiResponse(responseCode="400", description="Bad Request"),
            @ApiResponse(responseCode="404", description="Not Found"),
            @ApiResponse(responseCode="500", description="Internal Server Error")
    })
    public ResponseEntity<?> getTableColumnList(HttpServletRequest request,
                                          @Parameter(name = "id", description = "Site ID to add", required = true, example = "1")
                                          @Valid @PathVariable(value = "id") @NotNull int crasSiteId,
                                          @Parameter(name = "tableName", description = "Site ID to add", required = true, example = "1")
                                          @Valid @PathVariable(value = "tableName") @NotNull String tableName) {
        try {
            Object tableColumnList = crasDataService.getTableColumnList(crasSiteId, tableName);
            return ResponseEntity.status(HttpStatus.OK).body(tableColumnList);
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Test Query
    @PostMapping(ReqURLController.API_POST_TEST_QUERY)
    @Operation(summary="Get all Site name and Fab name lists")
    @ApiResponses({
            @ApiResponse(
                    responseCode="200",
                    description="OK(Successful acquisition of 'Site ID(int), Site name(String)')",
                    content = { @Content(
                            schema = @Schema(implementation = ResSitesNamesDTO.class),
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(
                                    name = "example1",
                                    value = SiteExamples.GET_SITE_NAME_RES)) }
            ),
            @ApiResponse(responseCode="400", description="Bad Request"),
            @ApiResponse(responseCode="404", description="Not Found"),
            @ApiResponse(responseCode="500", description="Internal Server Error")
    })
    public ResponseEntity<?> getTestQuery(HttpServletRequest request,
                                          @io.swagger.v3.oas.annotations.parameters.RequestBody(
                                                  description = "Site ID of the site to check detailed information",
                                                  required = true,
                                                  content = @Content(
                                                          schema = @Schema(implementation = ReqAddSiteDTO.class),
                                                          examples = @ExampleObject(value = SiteExamples.POST_ADD_NEW_SITE_REQ))
                                          )
                                          @RequestBody ReqTestQueryDTO reqTestQueryDTO) {
        try {
            crasDataService.testQuery(reqTestQueryDTO);
            return ResponseEntity.status(HttpStatus.OK).body(null);
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
