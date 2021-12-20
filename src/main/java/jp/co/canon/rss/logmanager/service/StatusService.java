package jp.co.canon.rss.logmanager.service;

import jp.co.canon.rss.logmanager.dto.job.ResLocalJobListDTO;
import jp.co.canon.rss.logmanager.dto.job.ResRemoteJobListDTO;
import jp.co.canon.rss.logmanager.dto.rulecrasdata.ResCrasDataSiteInfoDTO;
import jp.co.canon.rss.logmanager.dto.site.ResSitesNamesDTO;
import jp.co.canon.rss.logmanager.mapper.job.LocalJobVoResLocalJobListDtoMapper;
import jp.co.canon.rss.logmanager.mapper.status.RemoteJobVoResRemoteJobListDtoMapper;
import jp.co.canon.rss.logmanager.repository.LocalJobRepository;
import jp.co.canon.rss.logmanager.repository.RemoteJobRepository;
import jp.co.canon.rss.logmanager.repository.SiteRepository;
import jp.co.canon.rss.logmanager.repository.crasdata.CrasDataSiteRepository;
import jp.co.canon.rss.logmanager.vo.LocalJobVo;
import jp.co.canon.rss.logmanager.vo.RemoteJobVo;
import jp.co.canon.rss.logmanager.vo.SiteVo;
import jp.co.canon.rss.logmanager.vo.crasdata.CrasDataSiteVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service()
public class StatusService {
    RemoteJobRepository remoteJobRepository;
    LocalJobRepository localJobRepository;
    SiteRepository siteRepositoryService;
    CrasDataSiteRepository crasDataSiteRepository;
    SiteService siteService;

    public StatusService(RemoteJobRepository remoteJobRepository,
                      LocalJobRepository localJobRepository,
                         SiteRepository siteRepositoryService,
                         CrasDataSiteRepository crasDataSiteRepository,
                         SiteService siteService) {
        this.remoteJobRepository = remoteJobRepository;
        this.localJobRepository = localJobRepository;
        this.siteRepositoryService = siteRepositoryService;
        this.crasDataSiteRepository = crasDataSiteRepository;
        this.siteService = siteService;
    }

    public List<ResRemoteJobListDTO> getRemoteJobs() throws Exception {
        try {
            List<RemoteJobVo> remoteJobVoList = Optional
                    .ofNullable(remoteJobRepository.findBy(Sort.by(Sort.Direction.DESC, "jobId")))
                    .orElse(Collections.emptyList());

            List<ResRemoteJobListDTO> resultRemoteList = new ArrayList<>();
            for(RemoteJobVo remoteJobVo : remoteJobVoList) {
                resultRemoteList.add(RemoteJobVoResRemoteJobListDtoMapper.INSTANCE.mapRemoteJobVoToDto(remoteJobVo));
            }

            if(resultRemoteList.size() == 0)
                return resultRemoteList;

            return resultRemoteList;
        } catch (ResponseStatusException e) {
            log.error(e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public List<ResLocalJobListDTO> localJobListDTOS() {
        try {
            List<LocalJobVo> localJobVoList = Optional
                    .ofNullable(localJobRepository.findBy(Sort.by(Sort.Direction.DESC, "jobId")))
                    .orElse(Collections.emptyList());

            List<ResLocalJobListDTO> resultLocalList =
                    LocalJobVoResLocalJobListDtoMapper.INSTANCE.mapResLocalJobListVoToDto(localJobVoList);
            if (resultLocalList == null)
                throw new ResponseStatusException(HttpStatus.NOT_FOUND);

            return resultLocalList;
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public List<ResSitesNamesDTO> getSitesNamesList(Boolean notAdded) {
        List<ResSitesNamesDTO> resultSitesNamesList = Optional
                .ofNullable(siteRepositoryService.findBy())
                .orElse(Collections.emptyList());
        if(resultSitesNamesList==null)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        Collections.sort(resultSitesNamesList);
        if(notAdded) {
            List<RemoteJobVo> remoteJobList = Optional
                    .ofNullable(remoteJobRepository.findBy(Sort.by(Sort.Direction.DESC, "jobId")))
                    .orElse(Collections.emptyList());

            List<ResSitesNamesDTO> jobSiteNamesList = new ArrayList<>();

            for (RemoteJobVo job : remoteJobList) {
                ResSitesNamesDTO sitesNames = new ResSitesNamesDTO(job.getSiteId(),
                        job.getSiteVoList().getCrasCompanyName(), job.getSiteVoList().getCrasFabName());
                jobSiteNamesList.add(sitesNames);
            }
            resultSitesNamesList.removeAll(jobSiteNamesList);
        }
        return resultSitesNamesList;
    }

    public List<ResCrasDataSiteInfoDTO> getCrasDataSiteInfo() throws Exception {
        try {
            List<CrasDataSiteVo> crasDataSiteVoList = Optional
                    .ofNullable(crasDataSiteRepository.findAll())
                    .orElse(Collections.emptyList());
            List<SiteVo> siteVoList = Optional
                    .ofNullable(siteRepositoryService.findAll())
                    .orElse(Collections.emptyList());

            if (crasDataSiteVoList == null || siteVoList == null)
                throw new ResponseStatusException(HttpStatus.NOT_FOUND);

            List<ResCrasDataSiteInfoDTO> resCrasDataSiteInfoDTOList = new ArrayList<>();

            for (SiteVo siteVo : siteVoList) {
                int flag = 0;
                for (CrasDataSiteVo crasDataSiteVo : crasDataSiteVoList) {
                    if (siteVo.getSiteId() == crasDataSiteVo.getCrasDataSiteVo().getSiteId()) {
                        flag = 1;
                        break;
                    }
                }

                if (flag == 0) {
                    ResCrasDataSiteInfoDTO resCrasDataSiteInfoDTO = new ResCrasDataSiteInfoDTO()
                            .setSiteId(siteVo.getSiteId())
                            .setName(siteVo.getCrasCompanyName()+"-"+siteVo.getCrasFabName());
                    resCrasDataSiteInfoDTOList.add(resCrasDataSiteInfoDTO);
                }
            }

            return resCrasDataSiteInfoDTOList;
        } catch (ResponseStatusException e) {
            log.error(e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
