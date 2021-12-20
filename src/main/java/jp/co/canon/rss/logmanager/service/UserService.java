package jp.co.canon.rss.logmanager.service;

import jp.co.canon.rss.logmanager.dto.auth.ReqSingUpDTO;
import jp.co.canon.rss.logmanager.dto.user.ReqChangePasswordDTO;
import jp.co.canon.rss.logmanager.dto.user.ResUserDTO;
import jp.co.canon.rss.logmanager.jwt.JwtTokenProvider;
import jp.co.canon.rss.logmanager.mapper.UserVoReqSignUpDTOMapper;
import jp.co.canon.rss.logmanager.mapper.UserVoResUserDtoMapper;
import jp.co.canon.rss.logmanager.repository.BlockedTokenRepository;
import jp.co.canon.rss.logmanager.repository.UserRepository;
import jp.co.canon.rss.logmanager.util.ErrorMessage;
import jp.co.canon.rss.logmanager.util.UserRole;
import jp.co.canon.rss.logmanager.vo.UserVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service()
public class UserService {

	UserRepository userRepository;
	BlockedTokenRepository blockedTokenRepository;
	JwtTokenProvider jwtTokenProvider;

	public UserService(UserRepository userRepository, BlockedTokenRepository blockedTokenRepository, JwtTokenProvider jwtTokenProvider) {
		this.userRepository = userRepository;
		this.blockedTokenRepository = blockedTokenRepository;
		this.jwtTokenProvider = jwtTokenProvider;
	}

	public List<ResUserDTO> getUsers() throws Exception {
		try {
			List<UserVo> users = this.userRepository.findAll(Sort.by(Sort.Direction.DESC, "id"));
			List<ResUserDTO> usersDto = new ArrayList<ResUserDTO>();
			for (UserVo user : users) {
				if (!user.getUsername().equals("Administrator")) {
					log.info(user.toString());
					log.info(UserVoResUserDtoMapper.INSTANCE.ToResUserDTO(user).toString());
					usersDto.add(UserVoResUserDtoMapper.INSTANCE.ToResUserDTO(user));
				}
			}
			return usersDto;
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	public void signUp(ReqSingUpDTO singUpInput) throws Exception {

		// validate name value
		if (!isExistName(singUpInput.getUsername())) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ErrorMessage.DUPLICATE_USERNAME.getMsg());
		}

		// validate role value
		if (!isValidRole(singUpInput.getRoles())) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ErrorMessage.INVALID_ROLES.getMsg());
		}

		try {
			UserVo user = UserVoReqSignUpDTOMapper.INSTANCE.toEntity(singUpInput);
			userRepository.save(user);
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	public void deleteUser(Integer id) throws Exception {

		try {
			// validate request user
			Optional<UserVo> optionalUser = userRepository.findById(id);
			if (!optionalUser.isPresent()) {
				throw new ResponseStatusException(HttpStatus.NOT_FOUND, ErrorMessage.INVALID_USER.getMsg());
			}
			userRepository.deleteById(id);
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

	public void changePassword(Integer id, ReqChangePasswordDTO password) throws Exception {

		if (ObjectUtils.isEmpty(password.getCurrentPassword()) || ObjectUtils.isEmpty(password.getNewPassword())) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ErrorMessage.INVALID_PASSWORD.getMsg());
		}

		try {
			// validate request user
			Optional<UserVo> optionalUser = userRepository.findById(id);
			if (!optionalUser.isPresent()) {
				throw new ResponseStatusException(HttpStatus.NOT_FOUND, ErrorMessage.INVALID_USER.getMsg());
			}
			// validate old password
			UserVo user = optionalUser.get();
			if (!user.getPassword().equals(password.getCurrentPassword())) {
				throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ErrorMessage.INVALID_CURRENT_PASSWORD.getMsg());
			}
			user.setPassword(password.getNewPassword());
		} catch (ResponseStatusException e) {
			log.error(e.getMessage());
			throw e;
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	public void changeRoles(Integer id, List<String> roles) throws Exception {

		// validate role value
		if (!isValidRole(roles)) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ErrorMessage.INVALID_ROLES.getMsg());
		}

		try {
			// validate request user
			Optional<UserVo> optionalUser = userRepository.findById(id);
			if (!optionalUser.isPresent()) {
				throw new ResponseStatusException(HttpStatus.NOT_FOUND, ErrorMessage.INVALID_USER.getMsg());
			}
			UserVo user = optionalUser.get();
			user.setRoles(roles);
			userRepository.save(user);
		} catch (ResponseStatusException e) {
			log.error(e.getMessage());
			throw e;
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	public Boolean isExistName(String name) {

		try {
			UserVo user = this.userRepository.findByUsername(name);
			if (user != null) {
				return false;
			}
			return true;
		} catch (Exception e) {
			log.error(e.getMessage());
			return false;
		}
	}

	public Boolean isValidRole(List<String> roles) {

		if (roles.size() != 0) {
			for (String role : roles) {
				if (!Arrays.stream(UserRole.values()).anyMatch(v -> v.getRole().equals(role))) {
					return false;
				}
			}
		}
		return true;
	}
}
