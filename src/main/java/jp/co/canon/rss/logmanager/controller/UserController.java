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
import jp.co.canon.rss.logmanager.dto.auth.ReqSingUpDTO;
import jp.co.canon.rss.logmanager.dto.user.ReqChangePasswordDTO;
import jp.co.canon.rss.logmanager.dto.user.ReqChangeRoleDTO;
import jp.co.canon.rss.logmanager.dto.user.ResUserDTO;
import jp.co.canon.rss.logmanager.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

@Slf4j
@RestController
@RequestMapping(ReqURLController.API_DEFAULT_USER_URL)
public class UserController {

	private UserService userService;

	public UserController(UserService userService) {
		this.userService = userService;
	}

	@GetMapping(ReqURLController.API_GET_USERS)
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
	public ResponseEntity<?> getUsers(
		HttpServletRequest request, HttpServletResponse response) {

		try {
			List<ResUserDTO> users = userService.getUsers();
			return ResponseEntity.status(HttpStatus.OK).body(users);
		} catch (ResponseStatusException e) {
			throw e;
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PostMapping(ReqURLController.API_POST_SIGNUP)
	public ResponseEntity<?> signUp(
		HttpServletRequest request,
		HttpServletResponse response,
		@Valid @RequestBody ReqSingUpDTO singUpInput) {

		try {
			this.userService.signUp(singUpInput);
			return ResponseEntity.status(HttpStatus.OK).body(null);
		} catch (ResponseStatusException e) {
			throw e;
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@DeleteMapping(ReqURLController.API_DELETE_USER)
	public ResponseEntity<?> deleteUser(
		HttpServletRequest request,
		HttpServletResponse response,
		@Valid @PathVariable(value = "id") @NotNull Integer inputId) {

		try {
			this.userService.deleteUser(inputId);
			return ResponseEntity.status(HttpStatus.OK).body(null);
		} catch (ResponseStatusException e) {
			throw e;
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
		}
	}

	@PutMapping(ReqURLController.API_PUT_CHANGE_ROLES)
	public ResponseEntity<?> changeRoles(
		HttpServletRequest request,
		HttpServletResponse response,
		@Valid @PathVariable(value = "id") @NotNull Integer inputId,
		@Valid @RequestBody ReqChangeRoleDTO roleInput) {

		try {
			List<String> role = roleInput.getRoles();
			this.userService.changeRoles(inputId, role);
			return ResponseEntity.status(HttpStatus.OK).body(null);
		} catch (ResponseStatusException e) {
			throw e;
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
		}
	}

	@PutMapping(ReqURLController.API_PUT_CHANGE_PASSWORD)
	public ResponseEntity<?> changePassword(
		HttpServletRequest request,
		HttpServletResponse response,
		@Valid @PathVariable(value = "id") @NotNull Integer inputId,
		@Valid @RequestBody ReqChangePasswordDTO passwordInput) {

		try {
			this.userService.changePassword(inputId, passwordInput);
			return ResponseEntity.status(HttpStatus.OK).body(null);
		} catch (ResponseStatusException e) {
			throw e;
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
		}
	}

}
