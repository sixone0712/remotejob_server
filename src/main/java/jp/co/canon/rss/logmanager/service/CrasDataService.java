package jp.co.canon.rss.logmanager.service;

import com.google.gson.Gson;
import jp.co.canon.rss.logmanager.config.ReqURLController;
import jp.co.canon.rss.logmanager.dto.rulecrasdata.*;
import jp.co.canon.rss.logmanager.exception.StatusResourceNotFoundException;
import jp.co.canon.rss.logmanager.mapper.crasdata.*;
import jp.co.canon.rss.logmanager.repository.RemoteJobRepository;
import jp.co.canon.rss.logmanager.repository.SiteRepository;
import jp.co.canon.rss.logmanager.repository.crasdata.*;
import jp.co.canon.rss.logmanager.system.ClientManageService;
import jp.co.canon.rss.logmanager.util.ErrorMessage;
import jp.co.canon.rss.logmanager.vo.crasdata.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpResponse;
import org.apache.http.client.ResponseHandler;
import org.apache.http.impl.client.BasicResponseHandler;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class CrasDataService {
    @Value("${file.download-dir}")
    private String downloadPath;

    @Value("${cras-data.file-name}")
    private String fileName;

    private CrasDataSiteRepository crasDataSiteRepository;
    private CrasDataRepository crasDataRepository;
    private CrasItemMasterRepository crasItemMasterRepository;
    private CrasItemMasterJobRepository crasItemMasterJobRepository;
    private SiteRepository siteRepository;
    private CrasOptionRepository crasOptionRepository;
    private ClientManageService clientManageService;
    private UploadService uploadService;
    private ClientManageService client;
    private RemoteJobRepository remoteJobRepository;

    public CrasDataService(CrasDataSiteRepository crasDataSiteRepository,
                           CrasDataRepository crasDataRepository,
                           CrasItemMasterRepository crasItemMasterRepository,
                           CrasItemMasterJobRepository crasItemMasterJobRepository,
                           SiteRepository siteRepository,
                           CrasOptionRepository crasOptionRepository,
                           ClientManageService clientManageService, UploadService uploadService,
                           ClientManageService client, RemoteJobRepository remoteJobRepository) {
        this.crasDataSiteRepository = crasDataSiteRepository;
        this.crasDataRepository = crasDataRepository;
        this.crasItemMasterRepository = crasItemMasterRepository;
        this.crasItemMasterJobRepository = crasItemMasterJobRepository;
        this.siteRepository = siteRepository;
        this.crasOptionRepository = crasOptionRepository;
        this.clientManageService = clientManageService;
        this.uploadService = uploadService;
        this.client = client;
        this.remoteJobRepository = remoteJobRepository;
    }

    public List<ResCrasDataSiteDTO> getCrasDataSite() throws Exception {
        try {
            List<CrasDataSiteVo> crasDataSiteVoSite = Optional
                    .ofNullable(crasDataSiteRepository.findBy(Sort.by(Sort.Direction.DESC, "id")))
                    .orElse(Collections.emptyList());

            if (crasDataSiteVoSite == null)
                throw new ResponseStatusException(HttpStatus.NOT_FOUND);

            List<ResCrasDataSiteDTO> resCrasDataSiteDTOSite = new ArrayList<>();

            for (CrasDataSiteVo crasDataSiteVo : crasDataSiteVoSite) {
                ResCrasDataSiteDTO resCrasDataSiteDTO = new ResCrasDataSiteDTO()
                        .setId(crasDataSiteVo.getId())
                        .setSiteId(crasDataSiteVo.getCrasDataSiteVo().getSiteId())
                        .setCompanyFabName(crasDataSiteVo.getCrasDataSiteVo().getCrasCompanyName()+"-"+ crasDataSiteVo.getCrasDataSiteVo().getCrasFabName())
                        .setCreateCrasDataItemCount(crasDataRepository.countBySiteId(crasDataSiteVo.getId()))
                        .setCrasDataJudgeRulesItemCount(crasItemMasterRepository.countBySiteId(crasDataSiteVo.getId()))
                        .setDate(crasDataSiteVo.getDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

                resCrasDataSiteDTOSite.add(resCrasDataSiteDTO);
            }

            return resCrasDataSiteDTOSite;
        } catch (ResponseStatusException e) {
            log.error(e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResAddCrasDataSiteDTO addCrasDataSite(ReqAddCrasDataSiteDTO reqAddCrasDataSiteDTO) throws IOException, StatusResourceNotFoundException {
        try {
            ResAddCrasDataSiteDTO resAddCrasDataSiteDTO = new ResAddCrasDataSiteDTO();

            CrasDataSiteVo crasDataSiteVo = new CrasDataSiteVo()
                    .setDate(LocalDateTime.now())
                    .setSiteId(reqAddCrasDataSiteDTO.getSiteId())
                    .setCrasDataSiteVo(siteRepository.findById(reqAddCrasDataSiteDTO.getSiteId()).get())
                    .setLegacy(reqAddCrasDataSiteDTO.getLegacy());

            Boolean backupLegacy = false;

            if(crasDataSiteVo.getLegacy() == true) {
                try {
                    // get cras info
                    String GET_CRAS_LEGACY_DATA_URL = String.format(ReqURLController.API_GET_CRAS_LEGACY_DATA,
                            crasDataSiteVo.getCrasDataSiteVo().getCrasAddress(), crasDataSiteVo.getCrasDataSiteVo().getCrasPort());
                    clientManageService.download(GET_CRAS_LEGACY_DATA_URL, downloadPath + File.separator + fileName);
                } catch (Exception e) {
                    backupLegacy = true;
                }

                File file = new File(downloadPath + File.separator + fileName);

               resAddCrasDataSiteDTO.setId(crasDataSiteRepository.save(crasDataSiteVo).getId())
                       .setError(backupLegacy);

                resAddCrasDataSiteDTO.setError(uploadService.crasDataImport(file, resAddCrasDataSiteDTO.getId()));
            }
            else {
                resAddCrasDataSiteDTO.setId(crasDataSiteRepository.save(crasDataSiteVo).getId())
                        .setError(backupLegacy);
            }
            return resAddCrasDataSiteDTO;
        } catch (IllegalArgumentException e) {
            log.error(e.getMessage());
            throw new StatusResourceNotFoundException(e.getMessage());
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public void deleteCrasDataSite(int crasDataSiteId) {
        try {
            CrasDataSiteVo crasDataSiteVo = crasDataSiteRepository.findById(crasDataSiteId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
            List<CrasItemMasterJobVo> crasItemMasterJobVo = Optional
                    .ofNullable(crasItemMasterJobRepository.findByCrasDataSiteId(crasDataSiteId))
                    .orElse(Collections.emptyList());

            crasDataSiteRepository.delete(crasDataSiteVo);
            crasItemMasterJobRepository.deleteAll(crasItemMasterJobVo);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResAddCrasDataSiteDTO addCrasData(ReqAddCrasDataDTO reqAddCrasDataDTO, int crasDataSiteId) {
        CrasDataSiteVo crasDataSiteVo = crasDataSiteRepository.findById(crasDataSiteId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        CrasDataVo crasDataVo = CrasDataVoReqAddCrasDataDTOMapper.INSTANCE.mapReqAddCrasDataDtoToVo(reqAddCrasDataDTO, crasDataSiteId, crasDataSiteVo);
        ResAddCrasDataSiteDTO resAddCrasDataSiteDTO = new ResAddCrasDataSiteDTO()
                .setId(crasDataRepository.save(crasDataVo).getItemId());
        return resAddCrasDataSiteDTO;
    }

    public ResCrasDataItemIdDTO editCrasData(int itemId, ReqAddCrasDataDTO reqAddCrasDataDTO, int crasDataSiteId) {
        try {
            CrasDataSiteVo crasDataSiteVo = crasDataSiteRepository.findById(crasDataSiteId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

            CrasDataVo crasDataVo = crasDataRepository.findById(itemId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
            CrasDataVo reqCrasDataVo = CrasDataVoReqAddCrasDataDTOMapper.INSTANCE.mapReqEditCrasDataDtoToVo(crasDataVo, reqAddCrasDataDTO, crasDataSiteVo);

            ResCrasDataItemIdDTO resCrasDataItemIdDTO = new ResCrasDataItemIdDTO()
                    .setCrasDataItemId(crasDataRepository.save(reqCrasDataVo).getItemId());

            return resCrasDataItemIdDTO;
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public List<ResCrasDataDTO> getCrasData(int crasDataSiteId) throws Exception {
        try {
            List<CrasDataVo> crasDataVo = Optional
                    .ofNullable(crasDataRepository.findBySiteId(crasDataSiteId, Sort.by(Sort.Direction.DESC, "itemId")))
                    .orElse(Collections.emptyList());

            List<ResCrasDataDTO> resCrasDataDTOS = new ArrayList<>();
            if (crasDataVo == null)
                return resCrasDataDTOS;

            for (CrasDataVo crasDataSiteDTOSite : crasDataVo) {
                ResCrasDataDTO resCrasDataDTO = new ResCrasDataDTO()
                        .setItemId(crasDataSiteDTOSite.getItemId())
                        .setItemName(crasDataSiteDTOSite.getItemName())
                        .setEnable(crasDataSiteDTOSite.getEnable());

                resCrasDataDTOS.add(resCrasDataDTO);
            }

            return resCrasDataDTOS;
        } catch (ResponseStatusException e) {
            log.error(e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResCrasDataDetailDTO getCrasDataDetail(int itemId) throws Exception {
        try {
            CrasDataVo crasDataVo = crasDataRepository.findById(itemId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
            ResCrasDataDetailDTO resCrasDataDetailDTO = CrasDataVoResCrasDataDetailDTOMapper.INSTANCE.mapResCrasDataDetailDTO(crasDataVo);
            return resCrasDataDetailDTO;
        } catch (ResponseStatusException e) {
            log.error(e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public void deleteCrasData(int itemId) {
        try {
            CrasDataVo crasDataVo = crasDataRepository.findById(itemId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
            crasDataRepository.delete(crasDataVo);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResAddCrasJudgeRuleDTO addCrasJudgeRule(ReqAddJudgeRuleDTO reqAddJudgeRuleDTO, int crasDataSiteId) {
        CrasDataSiteVo crasDataSiteVo = crasDataSiteRepository.findById(crasDataSiteId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        CrasItemMasterVo crasItemMasterVo = CrasItemMasterVoReqAddJudgeRuleDTOMapper.INSTANCE.mapReqAddCrasJudgeRuleDtoToVo(reqAddJudgeRuleDTO, crasDataSiteId, crasDataSiteVo);
        ResAddCrasJudgeRuleDTO resAddCrasJudgeRuleDTO = new ResAddCrasJudgeRuleDTO()
                .setCrasJudgeRuleId(crasItemMasterRepository.save(crasItemMasterVo).getItemId());
        return resAddCrasJudgeRuleDTO;
    }

    public List<ResCrasDataDTO> getJudgeRules(int id, String flag) throws Exception {
        try {
            List<CrasItemMasterVo> crasItemMasterVoList = new ArrayList<>();

            if (flag.equals("all")) {
                crasItemMasterVoList = Optional
                        .ofNullable(crasItemMasterRepository.findBySiteId(
                                id, Sort.by(Sort.Direction.DESC, "itemId")))
                        .orElse(Collections.emptyList());
            }
            else if (flag.equals("true")) {
                CrasDataSiteVo crasDataSiteVo = crasDataSiteRepository.findBySiteId(id);

                crasItemMasterVoList = Optional
                        .ofNullable(crasItemMasterRepository.findBySiteId(
                                crasDataSiteVo.getId(), Sort.by(Sort.Direction.DESC, "itemId")))
                        .orElse(Collections.emptyList());
            }

            if (crasItemMasterVoList == null)
                throw new ResponseStatusException(HttpStatus.NOT_FOUND);

            List<ResCrasDataDTO> resCrasDataDTOS = new ArrayList<>();

            for (CrasItemMasterVo crasItemMasterVo : crasItemMasterVoList) {
                if(flag.equals("true") && crasItemMasterVo.getEnable()==true) {
                    ResCrasDataDTO resCrasDataDTO = new ResCrasDataDTO()
                            .setItemId(crasItemMasterVo.getItemId())
                            .setItemName(crasItemMasterVo.getItemName())
                            .setEnable(crasItemMasterVo.getEnable());

                    resCrasDataDTOS.add(resCrasDataDTO);
                }
                else if (flag.equals("all")) {
                    ResCrasDataDTO resCrasDataDTO = new ResCrasDataDTO()
                            .setItemId(crasItemMasterVo.getItemId())
                            .setItemName(crasItemMasterVo.getItemName())
                            .setEnable(crasItemMasterVo.getEnable());

                    resCrasDataDTOS.add(resCrasDataDTO);
                }
            }

            return resCrasDataDTOS;
        } catch (ResponseStatusException e) {
            log.error(e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResJudgeRuleDetailDTO getJudgeRuleDetail(int itemId) throws Exception {
        try {
            CrasItemMasterVo crasItemMasterVo = crasItemMasterRepository.findById(itemId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
            ResJudgeRuleDetailDTO resJudgeRuleDetailDTO = CrasItemMasterVoResJudgeRuleDetailDTOMapper.INSTANCE.mapResJudgeRuleDetailDTO(crasItemMasterVo);
            return resJudgeRuleDetailDTO;
        } catch (ResponseStatusException e) {
            log.error(e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResJudgeRuleItemIdDTO editJudgeRule(int itemId, ReqAddJudgeRuleDTO reqAddJudgeRuleDTO, int crasDataSiteId) {
        try {
            CrasDataSiteVo crasDataSiteVo = crasDataSiteRepository.findById(crasDataSiteId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
            CrasItemMasterVo crasItemMasterVo = crasItemMasterRepository.findById(itemId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
            Boolean enable = crasItemMasterVo.getEnable();

            CrasItemMasterVo reqCrasItemMasterVo = CrasItemMasterVoReqAddJudgeRuleDTOMapper.INSTANCE.mapReqEditJudgeRuleDtoToVo(crasItemMasterVo, reqAddJudgeRuleDTO, crasDataSiteId, crasDataSiteVo);
            if(enable != reqAddJudgeRuleDTO.getEnable()) {
                List<CrasItemMasterJobVo> crasItemMasterJobVo = Optional
                        .ofNullable(crasItemMasterJobRepository.findByCrasRuleId(itemId))
                        .orElse(Collections.emptyList());
                crasItemMasterJobRepository.deleteAll(crasItemMasterJobVo);
            }

            ResJudgeRuleItemIdDTO resJudgeRuleItemIdDTO = new ResJudgeRuleItemIdDTO()
                    .setJudgeRuleItemId(crasItemMasterRepository.save(reqCrasItemMasterVo).getItemId());

            return resJudgeRuleItemIdDTO;
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public void deleteJudge(int itemId) {
        try {
            CrasItemMasterVo crasItemMasterVo = crasItemMasterRepository.findById(itemId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
            crasItemMasterRepository.delete(crasItemMasterVo);

            List<CrasItemMasterJobVo> crasItemMasterJobVo = Optional
                    .ofNullable(crasItemMasterJobRepository.findByCrasRuleId(itemId))
                    .orElse(Collections.emptyList());
            crasItemMasterJobRepository.deleteAll(crasItemMasterJobVo);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public Map<String, List<String>> getOptionList(String cras) {
        try {
            List<CrasOptionVo> crasOptionVoList = Optional
                    .ofNullable(crasOptionRepository.findByCras(cras))
                    .orElse(Collections.emptyList());

            if (crasOptionVoList == null)
                throw new ResponseStatusException(HttpStatus.NOT_FOUND);

            Map<String, List<String>> resOptionDTO = crasOptionVoList.stream()
                    .collect(Collectors.groupingBy(a -> a.getOption(), Collectors.mapping(a -> a.getValue(), Collectors.toList())));

            return resOptionDTO;
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public JSONObject getConvertTableInfo(int crasSiteId) throws Exception {
        try {
            CrasDataSiteVo crasDataSiteVo = crasDataSiteRepository.findById(crasSiteId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

            String API_GET_CONVERT_TABLE_LIST_URL = String.format(ReqURLController.API_GET_CONVERT_TABLE_LIST,
                    crasDataSiteVo.getCrasDataSiteVo().getCrasAddress(),
                    crasDataSiteVo.getCrasDataSiteVo().getCrasPort(),
                    crasDataSiteVo.getCrasDataSiteVo().getSiteId());

            JSONObject obj = null;

            try {
                HttpResponse res = client.get(API_GET_CONVERT_TABLE_LIST_URL);

                if (res.getStatusLine().getStatusCode() == 200) {
                    JSONParser parser = new JSONParser();
                    ResponseHandler<String> handler = new BasicResponseHandler();
                    String body = handler.handleResponse(res);
                    obj = (JSONObject)parser.parse(body);
                }
            } catch (ParseException e) {
                log.error("json ParseException");
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, ErrorMessage.LEGACY_CRAS_DATA.getMsg());
            }

            return obj;
        } catch (ResponseStatusException e) {
            log.error(e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public List<String> getTableList(int crasSiteId) throws Exception {
        try {
            JSONObject resConvertTableJSONObject = getConvertTableInfo(crasSiteId);
            Iterator keySet = resConvertTableJSONObject.keySet().iterator();

            List<String> tableList = new ArrayList<>();

            while(keySet.hasNext())
                tableList.add(keySet.next().toString());

            return tableList;
        } catch (ResponseStatusException e) {
            log.error(e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public Object getTableColumnList(int crasSiteId, String tableName) throws Exception {
        try {
            JSONObject resConvertTableJSONObject = getConvertTableInfo(crasSiteId);
            Object resultColumn = resConvertTableJSONObject.get(tableName);

            return resultColumn;
        } catch (ResponseStatusException e) {
            log.error(e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public void testQuery(ReqTestQueryDTO reqTestQueryDTO) throws Exception {
        CrasDataSiteVo crasDataSiteVo = crasDataSiteRepository.findById(reqTestQueryDTO.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        String API_POST_TEST_QUERY_URL = String.format(ReqURLController.API_POST_TEST_QUERY_TO_CRAS,
                crasDataSiteVo.getCrasDataSiteVo().getCrasAddress(),
                crasDataSiteVo.getCrasDataSiteVo().getCrasPort());

        ReqTestQueryCrasDTO reqTestQueryCrasDTO = ReqTestQueryDTOReqTestQueryCrasDTOMapper.INSTANCE.mapReqTestQueryCrasDTO(reqTestQueryDTO);

        Gson gson = new Gson();
        String msg = gson.toJson(reqTestQueryCrasDTO);

        client.post(API_POST_TEST_QUERY_URL, msg);
    }
}
