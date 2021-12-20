package jp.co.canon.rss.logmanager.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jp.co.canon.rss.logmanager.config.ReqURLController;
import jp.co.canon.rss.logmanager.controller.examples.SiteExamples;
import jp.co.canon.rss.logmanager.controller.model.site.ResGetReturnVersion;
import jp.co.canon.rss.logmanager.dto.version.ResVersionDTO;
import jp.co.canon.rss.logmanager.service.VersionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@RestController
@RequestMapping(ReqURLController.API_DEFAULT_VERSION_URL)
public class VersionController {
    VersionService versionService;
    public VersionController(VersionService versionService) {
        this.versionService = versionService;
    }

    // 서버 구동 확인
    @GetMapping(ReqURLController.API_GET_SERVER_VERSION)
    @Operation(summary="get server version")
    @ApiResponses({
            @ApiResponse(
                    responseCode="200",
                    description="OK",
                    content = @Content(
                            schema = @Schema(implementation = ResGetReturnVersion.class),
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(
                                    name = "example1",
                                    value = SiteExamples.GET_LOGMONITOR_SERVER_STATUS_RES))
            ),
            @ApiResponse(responseCode="400", description="Bad Request"),
            @ApiResponse(responseCode="404", description="Not Found"),
            @ApiResponse(responseCode="500", description="Internal Server Error")
    })
    public ResponseEntity<?> getVersion(HttpServletRequest request) {
        try {
            ResVersionDTO buildLogList = versionService.checkVersionTXT();
            return ResponseEntity.status(HttpStatus.OK).body(buildLogList);
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
