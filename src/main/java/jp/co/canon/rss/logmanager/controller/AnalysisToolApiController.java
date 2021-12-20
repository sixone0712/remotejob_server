package jp.co.canon.rss.logmanager.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jp.co.canon.rss.logmanager.config.ReqURLController;
import jp.co.canon.rss.logmanager.dto.analysis.ResEquipmentsListDTO;
import jp.co.canon.rss.logmanager.dto.analysis.ResLogData;
import jp.co.canon.rss.logmanager.dto.analysis.ResLogTimeDTO;
import jp.co.canon.rss.logmanager.dto.version.ResVersionDTO;
import jp.co.canon.rss.logmanager.exception.StatusResourceNotFoundException;
import jp.co.canon.rss.logmanager.service.AnalysisToolService;
import jp.co.canon.rss.logmanager.service.ConvertRulesService;
import jp.co.canon.rss.logmanager.service.VersionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@Slf4j
@RestController
@RequestMapping(ReqURLController.API_DEFAULT_ANALYSIS_URL)
public class AnalysisToolApiController {
	private AnalysisToolService analysisToolService;
	private VersionService versionService;
	private ConvertRulesService convertRulesService;

	public AnalysisToolApiController(AnalysisToolService analysisToolService, VersionService versionService) {
		this.analysisToolService = analysisToolService;
		this.versionService = versionService;
	}

	// 모든 거점의 mpa 리스트 취득
	@GetMapping(ReqURLController.API_GET_EQUIPMENTS)
	@Operation(summary = "Get MPA list for all Sites from Rapid Collector")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "OK(Successful acquisition of MPA list for all sites from Rapid Collector)"),
		@ApiResponse(responseCode = "400", description = "Bad Request"),
		@ApiResponse(responseCode = "404", description = "Not Found"),
		@ApiResponse(responseCode = "5004", description = "Internal Server Error")
	})
	public ResponseEntity<?> getAllMpaList(HttpServletRequest request) {
		try {
			ResEquipmentsListDTO resEquipmentsListDTO = analysisToolService.getAllMpaList();
			return ResponseEntity.status(HttpStatus.OK).body(resEquipmentsListDTO);
		} catch (ResponseStatusException e) {
			throw e;
		} catch (Exception e) {
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	// 선택한 장치의 로그 종류 취득
	@GetMapping(ReqURLController.API_GET_LOGLIST)
	@Operation(summary = "Get MPA list for all Sites from Rapid Collector")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "OK(Successful acquisition of MPA list for all sites from Rapid Collector)"),
			@ApiResponse(responseCode = "400", description = "Bad Request"),
			@ApiResponse(responseCode = "404", description = "Not Found"),
			@ApiResponse(responseCode = "5004", description = "Internal Server Error")
	})
	public ResponseEntity<?> getLogList(HttpServletRequest request) {
		try {
			ResponseEntity<?> resConvertRuleDTOList = analysisToolService.getLogList(ReqURLController.API_GET_LOG_LIST);
			return ResponseEntity.status(HttpStatus.OK).body(resConvertRuleDTOList.getBody());
		} catch (ResponseStatusException e) {
			throw e;
		} catch (Exception e) {
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	// 장치명에 해당하는 로그 데이터의 log_time의 최초시간, 최종시간을 획득
	@GetMapping(ReqURLController.API_GET_LOGDATA)
	@Operation(summary = "Get the first time and last time of 'log_time' of the log data corresponding to the device name")
	@Parameters({
		@Parameter(name = "log_name", description = "Log data name to be acquired", required = true)
	})
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "OK(Successful acquisition of specified log data information)"),
		@ApiResponse(responseCode = "400", description = "Bad Request"),
		@ApiResponse(responseCode = "404", description = "Not Found"),
		@ApiResponse(responseCode = "5004", description = "Internal Server Error")
	})
	public ResponseEntity<?> getLogTime(HttpServletRequest request,
										@Valid @PathVariable(value = "log_name") @NotNull String logName,
										@Valid @PathVariable(value = "equipment") @NotNull String equipment)
		throws StatusResourceNotFoundException {
			try {
				ResLogTimeDTO resEquipmentsListDTO = analysisToolService.getLogTime(logName, equipment);
				return ResponseEntity.status(HttpStatus.OK).body(resEquipmentsListDTO);
			} catch (ResponseStatusException e) {
				throw e;
			} catch (Exception e) {
				throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
			}
	}

	// 서버 구동 확인
	@GetMapping(ReqURLController.API_GET_LOGMANAGER_CONNECTION)
	@Operation(summary = "Check if Monitoring Server is running")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "OK(Monitoring Server is running)"),
		@ApiResponse(responseCode = "400", description = "Bad Request"),
		@ApiResponse(responseCode = "404", description = "Not Found"),
		@ApiResponse(responseCode = "5004", description = "Internal Server Error")
	})
	public ResponseEntity<?> getVersionInfo(HttpServletRequest request) {
		try {
			ResVersionDTO buildLogList = versionService.checkVersionTXT();
			ResVersionDTO resVersionDTO = new ResVersionDTO()
					.setVersion("Log Monitor Version " + buildLogList.getVersion());
			return ResponseEntity.status(HttpStatus.OK).body(resVersionDTO);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
		}
	}

	// 로그데이터
	@GetMapping(ReqURLController.API_GET_LOGTIME)
	@Operation(summary = "Get the first time and last time of 'log_time' of the log data corresponding to the device name")
	@Parameters({
		@Parameter(name = "log_name", description = "Log data name to be acquired", required = true)
	})
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "OK(Successful acquisition of specified log data information)"),
		@ApiResponse(responseCode = "400", description = "Bad Request"),
		@ApiResponse(responseCode = "404", description = "Not Found"),
		@ApiResponse(responseCode = "5004", description = "Internal Server Error")
	})
	public ResponseEntity<?> getLogData(HttpServletRequest request,
										@Valid @PathVariable(value = "equipment") @NotNull String equipment,
										@Valid @PathVariable(value = "log_name") @NotNull String logName,
										@Valid @RequestParam("start") @NotNull String start,
										@Valid @RequestParam("end") @NotNull String end) {
		try {
			ResLogData resEquipmentsListDTO = analysisToolService.getLogData(equipment, logName, start, end);
			return ResponseEntity.status(HttpStatus.OK).body(resEquipmentsListDTO);
		} catch (ResponseStatusException e) {
			throw e;
		} catch (Exception e) {
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}
