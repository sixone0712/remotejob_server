package jp.co.canon.rss.logmanager.service;

import jp.co.canon.rss.logmanager.config.ReqURLController;
import jp.co.canon.rss.logmanager.dto.site.*;
import jp.co.canon.rss.logmanager.exception.ConnectionFailException;
import jp.co.canon.rss.logmanager.exception.ConvertException;
import jp.co.canon.rss.logmanager.mapper.site.SiteVoSiteDtoMapper;
import jp.co.canon.rss.logmanager.repository.RemoteJobRepository;
import jp.co.canon.rss.logmanager.repository.SiteRepository;
import jp.co.canon.rss.logmanager.system.ClientManageService;
import jp.co.canon.rss.logmanager.util.CallRestAPI;
import jp.co.canon.rss.logmanager.vo.RemoteJobVo;
import jp.co.canon.rss.logmanager.vo.SiteVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.net.Socket;
import java.util.*;

@Slf4j
@Service
public class SiteService {
	private SiteRepository siteRepositoryService;
	private RemoteJobRepository remoteJobRepository;

	public SiteService(SiteRepository siteRepository, RemoteJobRepository remoteJobRepository) {
		this.siteRepositoryService = siteRepository;
		this.remoteJobRepository = remoteJobRepository;
	}

	public ResDuplicateErrDTO checkDuplicate(ReqAddSiteDTO reqSiteInfo, String flag, int siteId) {
		try {
			ResDuplicateErrDTO resDuplicateErrDTO = new ResDuplicateErrDTO();

			// Duplicate check(crasSiteName, crasAddress)
			List<ResSitesDetailDTO> resultSites = Optional
					.ofNullable(siteRepositoryService.findBy(Sort.by(Sort.Direction.ASC, "siteId")))
					.orElse(Collections.emptyList());
			if(resultSites.isEmpty())
				resDuplicateErrDTO.setErrorCode(200);

			for (ResSitesDetailDTO site : resultSites) {
				switch (flag) {
					case "new" :
						if (site.getCrasCompanyName().toLowerCase(Locale.ROOT).equals(reqSiteInfo.getCrasCompanyName().toLowerCase(Locale.ROOT))
								&& site.getCrasFabName().toLowerCase(Locale.ROOT).equals(reqSiteInfo.getCrasFabName().toLowerCase(Locale.ROOT))) {
							resDuplicateErrDTO.setErrorCode(400001);
							resDuplicateErrDTO.setErrorMsg("Duplicate Site Name '" + reqSiteInfo.getCrasCompanyName() + "_" + reqSiteInfo.getCrasFabName() + "' of Cras Server Setting");
							return resDuplicateErrDTO;
						} else if (site.getCrasAddress().equals(reqSiteInfo.getCrasAddress())) {
							resDuplicateErrDTO.setErrorCode(400002);
							resDuplicateErrDTO.setErrorMsg("Duplicate Address '" + reqSiteInfo.getCrasAddress() + "' of Cras Server Setting");
							return resDuplicateErrDTO;
						}
						else {
							resDuplicateErrDTO.setErrorCode(200);
						}
						break;
					case "edit" :
						SiteVo getSiteInfo = siteRepositoryService.findById(siteId)
								.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
						if((site.getCrasCompanyName().toLowerCase(Locale.ROOT).equals(reqSiteInfo.getCrasCompanyName().toLowerCase(Locale.ROOT))
								&& site.getCrasFabName().toLowerCase(Locale.ROOT).equals(reqSiteInfo.getCrasFabName().toLowerCase(Locale.ROOT)))
								&& ((!site.getCrasCompanyName().toLowerCase(Locale.ROOT).equals(getSiteInfo.getCrasCompanyName().toLowerCase(Locale.ROOT)))
								|| (!site.getCrasFabName().toLowerCase(Locale.ROOT).equals(getSiteInfo.getCrasFabName().toLowerCase(Locale.ROOT))))) {
							resDuplicateErrDTO.setErrorCode(400001);
							resDuplicateErrDTO.setErrorMsg("Duplicate Site Name '"+ reqSiteInfo.getCrasCompanyName() +"_"+ reqSiteInfo.getCrasFabName() + "' of Cras Server Setting");
							return resDuplicateErrDTO;
						} else if (site.getCrasAddress().equals(reqSiteInfo.getCrasAddress())
								&& !site.getCrasAddress().equals(getSiteInfo.getCrasAddress())) {
							resDuplicateErrDTO.setErrorCode(400002);
							resDuplicateErrDTO.setErrorMsg("Duplicate Address '"+ reqSiteInfo.getCrasAddress() +"' of Cras Server Setting");
							return resDuplicateErrDTO;
						}
						else {
							resDuplicateErrDTO.setErrorCode(200);
						}
						break;
				}
			}
			return resDuplicateErrDTO;
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	public void changeCrasSiteInfo(ReqAddSiteDTO reqAddSiteDTO, String path) throws ConvertException {
		CallRestAPI callCrasAPI = new CallRestAPI();

		String API_POST_USERNAME_CRAS_URL = String.format(ReqURLController.API_POST_USERNAME_INFO_CRAS,
				reqAddSiteDTO.getCrasAddress(), reqAddSiteDTO.getCrasPort());
		String API_POST_RAPID_INFO_CRAS_URL = String.format(ReqURLController.API_POST_RAPID_INFO_CRAS,
				reqAddSiteDTO.getCrasAddress(), reqAddSiteDTO.getCrasPort());

		ReqUsernameToCrasDTO reqUsernameToCrasDTO = new ReqUsernameToCrasDTO()
				.setUser(reqAddSiteDTO.getCrasCompanyName());
		ReqRapidInfoToCrasDTO reqRapidInfoToCrasDTO = new ReqRapidInfoToCrasDTO()
				.setHost(reqAddSiteDTO.getRssAddress())
				.setPort(reqAddSiteDTO.getRssPort())
				.setUser(reqAddSiteDTO.getRssUserName())
				.setPassword(reqAddSiteDTO.getRssPassword());

		callCrasAPI.postRestAPI(API_POST_USERNAME_CRAS_URL, reqUsernameToCrasDTO, Object.class, path);
		callCrasAPI.postRestAPI(API_POST_RAPID_INFO_CRAS_URL, reqRapidInfoToCrasDTO, Object.class, path);
	}

	public ResSiteIdDTO addSite(ReqAddSiteDTO reqAddSiteDTO, String path) {
		ResSiteIdDTO resSiteIdDTO = new ResSiteIdDTO()
				.setSiteId(0);
		try {
			resSiteIdDTO = new ResSiteIdDTO()
					.setSiteId(siteRepositoryService.save(SiteVoSiteDtoMapper.INSTANCE.toEntity(reqAddSiteDTO)).getSiteId());

			changeCrasSiteInfo(reqAddSiteDTO, path);

			return resSiteIdDTO;
		} catch (Exception e) {
			log.error(e.getMessage());
			if(resSiteIdDTO.getSiteId() != 0)
				siteRepositoryService.deleteById(resSiteIdDTO.getSiteId());
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	public ResSiteIdDTO editSite(int siteId, ReqAddSiteDTO reqAddSiteDTO, String path) {
		try {
			SiteVo getSiteInfo = siteRepositoryService.findById(siteId)
					.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

			ResSiteIdDTO resSiteIdDTO = new ResSiteIdDTO()
					.setSiteId(siteRepositoryService.save(SiteVoSiteDtoMapper.INSTANCE.updateFromDtoE(reqAddSiteDTO, getSiteInfo)).getSiteId());

			changeCrasSiteInfo(reqAddSiteDTO, path);
			return resSiteIdDTO;
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	public ResSiteJobStatus getSiteJobStatus(int siteId) {
		List<RemoteJobVo> remoteJobList = Optional
				.ofNullable(remoteJobRepository.findBy(Sort.by(Sort.Direction.DESC, "jobId")))
				.orElse(Collections.emptyList());

		ResSiteJobStatus resSiteJobStatus = new ResSiteJobStatus();
		for (RemoteJobVo job : remoteJobList) {
			if (job.getSiteId() == siteId) {
				resSiteJobStatus.setStatus(job.isStop() ? "stopped" : "running");
				return resSiteJobStatus;
			}
		}
		// If there is no registered job, status is set to "none".
		resSiteJobStatus.setStatus("none");
		return resSiteJobStatus;
	}

	public void deleteSite(int siteId) {
		try {
			SiteVo delSite = siteRepositoryService.findById(siteId)
					.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
			siteRepositoryService.delete(delSite);
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	public ResConnectDTO crasConnection(ReqConnectionCrasDTO reqConnectionCrasDTO) {
		try {
			ClientManageService client = new ClientManageService();
			String GET_CRAS_CONNECTION_URL = String.format(ReqURLController.API_GET_CRAS_CONNECTION,
					reqConnectionCrasDTO.getCrasAddress(),
					reqConnectionCrasDTO.getCrasPort());
			ResConnectDTO resConnectDTO = new ResConnectDTO();
			if(client.connectCheck(GET_CRAS_CONNECTION_URL)==200)
				resConnectDTO.setResult("ok");
			else
				throw new ConnectionFailException();
			return resConnectDTO;
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	public ResConnectDTO rssConnection(ReqConnectionRssDTO reqConnectionRssDTO) {
		try {
			ClientManageService client = new ClientManageService();
			String GET_RSS_CONNECTION_URL = String.format(ReqURLController.API_GET_RSS_CONNECTION,
					reqConnectionRssDTO.getCrasAddress(),
					reqConnectionRssDTO.getCrasPort(),
					reqConnectionRssDTO.getRssAddress(),
					reqConnectionRssDTO.getRssPort(),
					reqConnectionRssDTO.getRssUserName(),
					reqConnectionRssDTO.getRssPassword());
			ResConnectDTO resConnectDTO = new ResConnectDTO();
			if(client.connectCheck(GET_RSS_CONNECTION_URL)==200)
				resConnectDTO.setResult("ok");
			else
				throw new ConnectionFailException();
			return resConnectDTO;
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	public ResConnectDTO emailConnection(ReqConnectionEmailDTO reqConnectionEmailDTO) {
		try {
			Socket connectCheckResult = new Socket(reqConnectionEmailDTO.getEmailAddress(), reqConnectionEmailDTO.getEmailPort());
			connectCheckResult.isConnected();
			ResConnectDTO resConnectDTO = new ResConnectDTO().setResult("ok");
			return resConnectDTO;
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}
