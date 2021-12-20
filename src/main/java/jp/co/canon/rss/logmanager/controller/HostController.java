package jp.co.canon.rss.logmanager.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jp.co.canon.rss.logmanager.config.ReqURLController;
import jp.co.canon.rss.logmanager.controller.examples.SiteExamples;
import jp.co.canon.rss.logmanager.dto.host.ResHostInfoDTO;
import jp.co.canon.rss.logmanager.service.HostService;
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
@RequestMapping(ReqURLController.API_DEFAULT_HOST_URL)
public class HostController {
    private HostService hostService;

    public HostController(HostService hostService) {
        this.hostService = hostService;
    }

    // setting db 정보 취득
    @GetMapping(ReqURLController.API_GET_SETTING_DB_INFO)
    @Operation(summary="Get the 'Settings DB' information stored in the 'application.yml'")
    @ApiResponses({
            @ApiResponse(
                    responseCode="200",
                    description="OK(Successful acquisition of Settings DB information in 'application.yml')",
                    content = @Content(
                            schema = @Schema(implementation = ResHostInfoDTO.class),
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(
                                    name = "example1",
                                    value = SiteExamples.GET_APPLICATION_YML_RES))
            ),
            @ApiResponse(responseCode="400", description="Bad Request"),
            @ApiResponse(responseCode="404", description="Not Found"),
            @ApiResponse(responseCode="500", description="Internal Server Error")
    })
    public ResponseEntity<?> getApplicationYml(HttpServletRequest request) {
        try {
            ResHostInfoDTO resHostInfoDTO = hostService.getHostInfo();
            return ResponseEntity.status(HttpStatus.OK).body(resHostInfoDTO);
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
