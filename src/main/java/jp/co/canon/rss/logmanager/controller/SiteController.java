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
import jp.co.canon.rss.logmanager.controller.model.site.*;
import jp.co.canon.rss.logmanager.dto.site.*;
import jp.co.canon.rss.logmanager.repository.SiteRepository;
import jp.co.canon.rss.logmanager.service.SiteService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping(ReqURLController.API_DEFAULT_SITE_URL)
public class SiteController {
    @Autowired
    private SiteRepository siteRepository;

    private SiteService siteService;
    public SiteController(SiteService siteService) {
        this.siteService = siteService;
    }

    // 전체 site 리스트 취득
    @GetMapping(ReqURLController.API_GET_ALL_SITE_LIST)
    @Operation(summary="Get detailed information for the all Sites")
    @ApiResponses({
        @ApiResponse(
            responseCode="200",
            description="OK(Successful acquisition of all Sites information)",
            content = { @Content(
                schema = @Schema(implementation = ResSitesDetailDTO.class),
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                examples = @ExampleObject(
                    name = "example1",
                    value = SiteExamples.GET_SITE_LIST_RES)) }
        ),
        @ApiResponse(responseCode="400", description="Bad Request"),
        @ApiResponse(responseCode="404", description="Not Found"),
        @ApiResponse(responseCode="500", description="Internal Server Error")
    })
    public ResponseEntity<?> getAllSites(HttpServletRequest request) {
        try {
            List<ResSitesDetailDTO> resultSites = Optional
                .ofNullable(siteRepository.findBy(Sort.by(Sort.Direction.DESC, "siteId")))
                .orElse(Collections.emptyList());
            if(resultSites==null)
                throw new ResponseStatusException(HttpStatus.NOT_FOUND);
            return ResponseEntity.status(HttpStatus.OK).body(resultSites);
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // 지정 site 리스트 취득
    @GetMapping(ReqURLController.API_GET_SITE_DETAIL)
    @Operation(summary="Get detailed information for the specified Site")
    @ApiResponses({
        @ApiResponse(
            responseCode="200",
            description="OK(Successful acquisition of specified Site information)",
            content = { @Content(
                schema = @Schema(implementation = ResSitesDetailDTO.class),
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                examples = @ExampleObject(
                    name = "example1",
                    value = SiteExamples.GET_SITE_INFO_RES)) }
        ),
        @ApiResponse(responseCode="400", description="Bad Request"),
        @ApiResponse(responseCode="404", description="Not Found"),
        @ApiResponse(responseCode="500", description="Internal Server Error")
    })
    public ResponseEntity<?> getSitesDetail(HttpServletRequest request,
                                            @Parameter(
                                                schema = @Schema(example = "1"),
                                                description = "Site ID of the Site to check detailed information", required = true)
                                            @Valid @PathVariable(value = "id") @NotNull int siteId) {
        try {
            ResSitesDetailDTO resultSitesDetail = siteRepository.findBySiteId(siteId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
            return ResponseEntity.status(HttpStatus.OK).body(resultSitesDetail);
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // site 추가
    @PostMapping(ReqURLController.API_POST_ADD_NEW_SITE)
    @Operation(summary="Add new site")
    @ApiResponses({
        @ApiResponse(
            responseCode="200",
            description="OK(Successful addition of new Site)",
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
    public ResponseEntity<?> addSites(HttpServletRequest request,
                                      @io.swagger.v3.oas.annotations.parameters.RequestBody(
                                          description = "Site ID of the site to check detailed information",
                                          required = true,
                                          content = @Content(
                                              schema = @Schema(implementation = ReqAddSiteDTO.class),
                                              examples = @ExampleObject(value = SiteExamples.POST_ADD_NEW_SITE_REQ))
                                      )
                                      @RequestBody ReqAddSiteDTO reqAddSiteDTO) {
        try {
            ResDuplicateErrDTO responseEntity = siteService.checkDuplicate(reqAddSiteDTO, "new", 0);

            if(responseEntity.getErrorCode() == 200) {
                ResSiteIdDTO resSiteIdDTO = siteService.addSite(reqAddSiteDTO, ReqURLController.API_POST_ADD_NEW_SITE);
                return ResponseEntity.status(HttpStatus.OK).body(resSiteIdDTO);
            }
            else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseEntity);
            }
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // site 수정
    @PutMapping(ReqURLController.API_PUT_SITE_INFO)
    @Operation(summary="Modify existing Site details")
    @ApiResponses({
        @ApiResponse(
            responseCode="200",
            description="OK(Success modify the details of an existing Site)",
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
    public ResponseEntity<?> updateSites(HttpServletRequest request,
                                         @Parameter(
                                             schema = @Schema(example = "1"),
                                             description = "Site ID of the site to check detailed information", required = true)
                                         @Valid @PathVariable(value = "id") @NotNull int siteId,
                                         @io.swagger.v3.oas.annotations.parameters.RequestBody(
                                             description = "Detailed information on newly added Site",
                                             required = true,
                                             content = @Content(
                                                 schema = @Schema(implementation = ResSitesDetailDTO.class),
                                                 examples = @ExampleObject(value = SiteExamples.POST_ADD_NEW_SITE_REQ))
                                         )
                                         @RequestBody ReqAddSiteDTO reqAddSiteDTO) {
        try {
            ResDuplicateErrDTO responseEntity = siteService.checkDuplicate(reqAddSiteDTO, "edit", siteId);

            if(responseEntity.getErrorCode() == 200) {
                ResSiteIdDTO resSiteIdDTO = siteService.editSite(siteId, reqAddSiteDTO, ReqURLController.API_PUT_SITE_INFO);
                return ResponseEntity.status(HttpStatus.OK).body(resSiteIdDTO);
            }
            else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseEntity);
            }
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // site 잡이 실행되고 있는 지 확인
    @GetMapping(ReqURLController.API_GET_JOB_STATUS)
    @Operation(summary="Get job status for a site")
    @ApiResponses({
        @ApiResponse(
            responseCode="200",
            description="OK",
            content = { @Content(
                schema = @Schema(implementation = ResGetReturnJobStatus.class),
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                examples = @ExampleObject(
                    name = "example1",
                    value = SiteExamples.GET_SITE_STATUS_RES)) }
        ),
        @ApiResponse(responseCode="400", description="Bad Request"),
        @ApiResponse(responseCode="404", description="Not Found"),
        @ApiResponse(responseCode="500", description="Internal Server Error")
    })
    public ResponseEntity<?> getSiteJobStatus(HttpServletRequest request,
                                              @Parameter(
                                                  schema = @Schema(example = "1"),
                                                  description = "Site ID of the Site", required = true)
                                              @Valid @PathVariable(value = "siteId") @NotNull int siteId) {
        try {
            ResSiteJobStatus resSiteJobStatus = siteService.getSiteJobStatus(siteId);
            return ResponseEntity.status(HttpStatus.OK).body(resSiteJobStatus);
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // site 삭제
    @DeleteMapping(ReqURLController.API_DEL_SITE)
    @Operation(summary="Delete existing Site")
    @ApiResponses({
        @ApiResponse(
            responseCode="200",
            description="OK(Successful deletion of existing Site(Related Jobs are also deleted at the same time)",
            content = @Content(
                schema = @Schema(implementation = ResDeleteReturn.class),
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                examples = @ExampleObject(
                    name = "example1",
                    value = SiteExamples.DEL_SITE_RES))
        ),
        @ApiResponse(responseCode="400", description="Bad Request"),
        @ApiResponse(responseCode="404", description="Not Found"),
        @ApiResponse(responseCode="500", description="Internal Server Error")
    })
    public ResponseEntity<?> deletesSite(HttpServletRequest request,
                                         @Parameter(
                                             schema = @Schema(example = "1"),
                                             description = "Site ID of the deleted Site", required = true)
                                         @Valid @PathVariable(value = "id") @NotNull int siteId) {
        try {
            siteService.deleteSite(siteId);
            return ResponseEntity.status(HttpStatus.OK).body(null);
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // cras 서버 test conenction
    @PostMapping(ReqURLController.API_POST_CRAS_CONNECTION)
    @Operation(summary="Test connectivity to the CRAS server")
    @ApiResponses({
        @ApiResponse(
            responseCode="200",
            description="OK(Successful CRAS Server Test connection)",
            content = @Content(
                schema = @Schema(implementation = ResGetReturnServerStatus.class),
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                examples = @ExampleObject(
                    name = "example1",
                    value = SiteExamples.GET_SERVER_STATUS_RES))
        ),
        @ApiResponse(responseCode="400", description="Bad Request"),
        @ApiResponse(responseCode="404", description="Not Found"),
        @ApiResponse(responseCode="500", description="Internal Server Error")
    })
    public ResponseEntity<?> crasConnection(HttpServletRequest request,
                                            @Parameter(
                                                schema = @Schema(implementation = ReqConnectionCrasDTO.class),
                                                description = "Cras Server information", required = true)
                                            @RequestBody ReqConnectionCrasDTO reqConnectionCrasDTO) {
        try {
            ResConnectDTO resConnectDTO = siteService.crasConnection(reqConnectionCrasDTO);
            return ResponseEntity.status(HttpStatus.OK).body(resConnectDTO);
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // rss 서버 test conenction
    @PostMapping(ReqURLController.API_POST_RSS_CONNECTION)
    @Operation(summary="Test connectivity to the RSS server")
    @ApiResponses({
        @ApiResponse(
            responseCode="200",
            description="OK(Successful RSS Server Test connection)",
            content = @Content(
                schema = @Schema(implementation = ResGetReturnServerStatus.class),
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                examples = @ExampleObject(
                    name = "example1",
                    value = SiteExamples.GET_SERVER_STATUS_RES))
        ),
        @ApiResponse(responseCode="400", description="Bad Request"),
        @ApiResponse(responseCode="404", description="Not Found"),
        @ApiResponse(responseCode="500", description="Internal Server Error")
    })
    public ResponseEntity<?> rssConnection(HttpServletRequest request,
                                           @Parameter(
                                               schema = @Schema(implementation = ReqConnectionRssDTO.class),
                                               description = "Rapid Collector Server information", required = true)
                                           @RequestBody ReqConnectionRssDTO reqConnectionRssDTO) {
        try {
            ResConnectDTO resConnectDTO = siteService.rssConnection(reqConnectionRssDTO);
            return ResponseEntity.status(HttpStatus.OK).body(resConnectDTO);
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Email 서버 test conenction
    @PostMapping(ReqURLController.API_POST_EMAIL_CONNECTION)
    @Operation(summary="Test connectivity to the E-mail server")
    @ApiResponses({
        @ApiResponse(
            responseCode="200",
            description="OK(Successful Email server test connection)",
            content = @Content(
                schema = @Schema(implementation = ResGetReturnServerStatus.class),
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                examples = @ExampleObject(
                    name = "example1",
                    value = SiteExamples.GET_SERVER_STATUS_RES))
        ),
        @ApiResponse(responseCode="400", description="Bad Request"),
        @ApiResponse(responseCode="404", description="Not Found"),
        @ApiResponse(responseCode="500", description="Internal Server Error")
    })
    public ResponseEntity<?> emailConnection(HttpServletRequest request,
                                             @Parameter(
                                                 description = "Email Server information",
                                                 required = true,
                                                 content = @Content(
                                                     schema = @Schema(implementation = ReqConnectionEmailDTO.class),
                                                     mediaType = MediaType.APPLICATION_JSON_VALUE)
                                             )
                                             @RequestBody ReqConnectionEmailDTO reqConnectionEmailDTO) {
        try {
            ResConnectDTO resConnectDTO = siteService.emailConnection(reqConnectionEmailDTO);
            return ResponseEntity.status(HttpStatus.OK).body(resConnectDTO);
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
