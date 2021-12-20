package jp.co.canon.rss.logmanager.service;

import jp.co.canon.rss.logmanager.config.ReqURLController;
import jp.co.canon.rss.logmanager.dto.analysis.ResEquipmentDTO;
import jp.co.canon.rss.logmanager.dto.analysis.ResEquipmentsListDTO;
import jp.co.canon.rss.logmanager.dto.analysis.ResLogData;
import jp.co.canon.rss.logmanager.dto.analysis.ResLogTimeDTO;
import jp.co.canon.rss.logmanager.dto.job.ResLogDataTimeDTO;
import jp.co.canon.rss.logmanager.dto.site.ResSitesDetailDTO;
import jp.co.canon.rss.logmanager.exception.ConvertException;
import jp.co.canon.rss.logmanager.repository.SiteRepository;
import jp.co.canon.rss.logmanager.util.CallRestAPI;
import jp.co.canon.rss.logmanager.vo.SiteVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

@Slf4j
@Service()
public class AnalysisToolService {
    SiteRepository siteRepository;

    @Value("${convert-data.cras-server-address}")
    private String crasServer;

    public AnalysisToolService(SiteRepository siteRepository) {
        this.siteRepository = siteRepository;
    }

    public ResEquipmentsListDTO getAllMpaList() throws Exception {
        try {
            List<SiteVo> getAllSite = Optional.ofNullable(siteRepository.findAll())
                    .orElse(Collections.emptyList());
            if(getAllSite.isEmpty())
                throw new ResponseStatusException(HttpStatus.NOT_FOUND);

            Map<String, Map<String, List<String>>> companies = new HashMap<>();

            for (SiteVo site : getAllSite) {
                String GET_ALL_MPA_LIST_URL = String.format(ReqURLController.API_GET_ALL_MPA_LIST,
                        site.getCrasAddress(),
                        site.getCrasPort(),
                        site.getCrasFabName());

                CallRestAPI callRestAPI = new CallRestAPI();
                ResponseEntity<?> response = callRestAPI.getRestAPIforAnalysis(GET_ALL_MPA_LIST_URL, ResEquipmentDTO[].class);
                if(response!=null) {
                    List<ResEquipmentDTO> equipments = Arrays.asList((ResEquipmentDTO[]) response.getBody());

                    for (ResEquipmentDTO equipment : equipments) {
                        if (!companies.containsKey(site.getCrasCompanyName())) {
                            List<String> equipmentNames = new ArrayList<String>();
                            equipmentNames.add(equipment.getEquipment_name());

                            Map<String, List<String>> fabs = new HashMap<>();
                            fabs.put(site.getCrasFabName(), equipmentNames);

                            companies.put(site.getCrasCompanyName(), fabs);
                        } else {
                            if (!companies.get(site.getCrasCompanyName()).containsKey(equipment.getFab_name())) {
                                List<String> equipmentNames = new ArrayList<String>();
                                equipmentNames.add(equipment.getEquipment_name());
                                companies.get(site.getCrasCompanyName()).put(site.getCrasFabName(), equipmentNames);
                            } else {
                                companies.get(site.getCrasCompanyName()).get(site.getCrasFabName()).add(equipment.getEquipment_name());
                            }
                        }
                    }
                }
            }

            ResEquipmentsListDTO resEquipmentsListDTO = new ResEquipmentsListDTO();
            return resEquipmentsListDTO.setEquipments(companies);
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResLogTimeDTO getLogTime(String logName, String equipment) throws Exception {
        try {
            String[] findSite = equipment.split("_");
            ResSitesDetailDTO sitesDetail = siteRepository.findByCrasCompanyNameIgnoreCaseAndCrasFabNameIgnoreCase(findSite[0], findSite[1])
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

            String GET_LOG_DATA_TIME_URL = String.format(ReqURLController.API_GET_LOG_DATA_TIME,
                    sitesDetail.getCrasAddress(),
                    sitesDetail.getCrasPort(),
                    logName,
                    equipment);

            CallRestAPI callRestAPI = new CallRestAPI();
            ResponseEntity<?> response = callRestAPI.getRestAPI(GET_LOG_DATA_TIME_URL, ResLogDataTimeDTO.class);
            ResLogDataTimeDTO logDataTime = (ResLogDataTimeDTO) response.getBody();

            ResLogTimeDTO resLogTimeDTO = new ResLogTimeDTO()
                    .setCount(logDataTime.getCount());

            if(logDataTime.getEnd()!=null) {
                String [] endDate = logDataTime.getEnd().split(" ");
                resLogTimeDTO.setEnd(endDate[0]);
            }
            else {
                resLogTimeDTO.setEnd(null);
            }

            if(logDataTime.getStart()!=null) {
                String [] endDate = logDataTime.getStart().split(" ");
                resLogTimeDTO.setStart(endDate[0]);
            }
            else {
                resLogTimeDTO.setStart(null);
            }

            return resLogTimeDTO;
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResLogData getLogData(String equipment, String logName, String start, String end) throws Exception {
        String[] findSite = equipment.split("_");
        ResSitesDetailDTO sitesDetail = siteRepository.findByCrasCompanyNameIgnoreCaseAndCrasFabNameIgnoreCase(findSite[0], findSite[1])
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        String GET_LOG_DATA_URL = String.format(ReqURLController.API_GET_LOG_DATA,
                sitesDetail.getCrasAddress(),
                sitesDetail.getCrasPort(),
                logName,
                start,
                end,
                equipment);

        CallRestAPI callRestAPI = new CallRestAPI();
        ResponseEntity<?> response = callRestAPI.getRestAPI(GET_LOG_DATA_URL, ResLogData.class);
        ResLogData logData = (ResLogData) response.getBody();
        if(logData.getData() == null)
            logData.setData("");

        return logData;
    }

    public ResponseEntity<?> getLogList(String path) throws ConvertException {
        try {
            CallRestAPI callRestAPI = new CallRestAPI();
            ResponseEntity<?> response = callRestAPI.getConvertRestAPI(crasServer+ReqURLController.API_GET_LOG_LIST_CRAS,
                    Object.class, path);
            return response;
        } catch (ConvertException e) {
            log.error(e.getMessage());
            throw e;
        }
    }
}
