package jp.co.canon.rss.logmanager.mapper.job;

import jp.co.canon.rss.logmanager.config.ReqURLController;
import jp.co.canon.rss.logmanager.dto.address.AddressBookDTO;
import jp.co.canon.rss.logmanager.dto.job.ResMailContextDTO;
import jp.co.canon.rss.logmanager.dto.job.ResMailContextScriptDTO;
import jp.co.canon.rss.logmanager.dto.job.ResRemoteJobDetailAddDTO;
import jp.co.canon.rss.logmanager.dto.job.ResRemoteJobDetailDTO;
import jp.co.canon.rss.logmanager.dto.rulecrasdata.ResCrasDataDTO;
import jp.co.canon.rss.logmanager.util.AscendingObj;
import jp.co.canon.rss.logmanager.vo.MailContextVo;
import jp.co.canon.rss.logmanager.vo.RemoteJobVo;
import jp.co.canon.rss.logmanager.vo.SiteVo;
import jp.co.canon.rss.logmanager.vo.UserVo;
import jp.co.canon.rss.logmanager.vo.address.JobAddressBookEntity;
import jp.co.canon.rss.logmanager.vo.address.JobGroupBookEntity;
import jp.co.canon.rss.logmanager.vo.crasdata.CrasItemMasterJobVo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;
import java.util.*;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface RemoteJobVoResRemoteJobDtoMapper {
    RemoteJobVoResRemoteJobDtoMapper INSTANCE = Mappers.getMapper(RemoteJobVoResRemoteJobDtoMapper.class);

    @Mapping(target="siteName", expression = "java(mapSiteName(remoteJobVo))")
    @Mapping(target="collect", expression = "java(mapCollect(remoteJobVo))")
    @Mapping(target="convert", expression = "java(mapConvert(remoteJobVo))")
    @Mapping(target="errorSummary", expression = "java(mapErrorSummary(remoteJobVo))")
    @Mapping(target="crasData", expression = "java(mapCrasData(remoteJobVo))")
    @Mapping(target="mpaVersion", expression = "java(mapMpaVersion(remoteJobVo))")
    @Mapping(target="dbPurge", expression = "java(mapDBPurge(remoteJobVo))")
    @Mapping(target="errorNotice", expression = "java(mapErrorNotice(remoteJobVo))")
    ResRemoteJobDetailDTO mapRemoteJobVoToDto(RemoteJobVo remoteJobVo);

    default String mapSiteName(RemoteJobVo remoteJobVo) {
        return remoteJobVo.getSiteVoList().getCrasCompanyName()+"-"+remoteJobVo.getSiteVoList().getCrasFabName();
    }
    default ResMailContextDTO mapCollect(RemoteJobVo remoteJobVo) {
        if(remoteJobVo.getMailContextVoCollect()!=null) {
            ResMailContextDTO resMailContextDTO =
                    MailContextVoResMailContextDTOMapper.INSTANCE.toDto(remoteJobVo.getMailContextVoCollect());
            resMailContextDTO.setScript(setScript(remoteJobVo.getMailContextVoCollect().getPreScript(),
                    remoteJobVo.getMailContextVoCollect().getNextScript()));
            return resMailContextDTO;
        }
        return null;
    }
    default ResMailContextDTO mapConvert(RemoteJobVo remoteJobVo) {
        if(remoteJobVo.getMailContextVoConvert()!=null) {
            ResMailContextDTO resMailContextDTO =
                    MailContextVoResMailContextDTOMapper.INSTANCE.toDto(remoteJobVo.getMailContextVoConvert());
            resMailContextDTO.setScript(setScript(remoteJobVo.getMailContextVoConvert().getPreScript(),
                    remoteJobVo.getMailContextVoConvert().getNextScript()));
            return resMailContextDTO;
        }
        return null;
    }
    default ResMailContextDTO mapErrorSummary(RemoteJobVo remoteJobVo) {
        if(remoteJobVo.getMailContextVoErrorSummary() !=null) {
            ResMailContextDTO resMailContextDTO =
                    MailContextVoResMailContextDTOMapper.INSTANCE.toDto(remoteJobVo.getMailContextVoErrorSummary());
            resMailContextDTO.setEmailBook(setAddress(remoteJobVo.getMailContextVoErrorSummary()));
            resMailContextDTO.setGroupBook(setGroup(remoteJobVo.getMailContextVoErrorSummary()));
            resMailContextDTO.setScript(setScript(remoteJobVo.getMailContextVoErrorSummary().getPreScript(),
                    remoteJobVo.getMailContextVoErrorSummary().getNextScript()));
            return resMailContextDTO;
        }
        return null;
    }
    default ResMailContextDTO mapCrasData(RemoteJobVo remoteJobVo) {
        if(remoteJobVo.getMailContextVoCrasData()!=null) {
            ResMailContextDTO resMailContextDTO =
                    MailContextVoResMailContextDTOMapper.INSTANCE.toDto(remoteJobVo.getMailContextVoCrasData());
            resMailContextDTO.setEmailBook(setAddress(remoteJobVo.getMailContextVoCrasData()));
            resMailContextDTO.setGroupBook(setGroup(remoteJobVo.getMailContextVoCrasData()));
            resMailContextDTO.setSelectJudgeRules(setSelectJudgeRules(remoteJobVo.getMailContextVoCrasData()));
            resMailContextDTO.setScript(setScript(remoteJobVo.getMailContextVoCrasData().getPreScript(),
                    remoteJobVo.getMailContextVoCrasData().getNextScript()));
            return resMailContextDTO;
        }
        return null;
    }
    default ResMailContextDTO mapMpaVersion(RemoteJobVo remoteJobVo) {
        if(remoteJobVo.getMailContextVoVersion()!=null) {
            ResMailContextDTO resMailContextDTO =
                    MailContextVoResMailContextDTOMapper.INSTANCE.toDto(remoteJobVo.getMailContextVoVersion());
            resMailContextDTO.setEmailBook(setAddress(remoteJobVo.getMailContextVoVersion()));
            resMailContextDTO.setGroupBook(setGroup(remoteJobVo.getMailContextVoVersion()));
            resMailContextDTO.setScript(setScript(remoteJobVo.getMailContextVoVersion().getPreScript(),
                    remoteJobVo.getMailContextVoVersion().getNextScript()));
            return resMailContextDTO;
        }
        return null;
    }
    default ResMailContextDTO mapDBPurge(RemoteJobVo remoteJobVo) {
        if(remoteJobVo.getMailContextVoDBPurge()!=null) {
            ResMailContextDTO resMailContextDTO =
                    MailContextVoResMailContextDTOMapper.INSTANCE.toDto(remoteJobVo.getMailContextVoDBPurge());
            resMailContextDTO.setScript(setScript(remoteJobVo.getMailContextVoDBPurge().getPreScript(),
                    remoteJobVo.getMailContextVoDBPurge().getNextScript()));
            return resMailContextDTO;
        }
        return null;
    }
    default ResMailContextDTO mapErrorNotice(RemoteJobVo remoteJobVo) {
        if(remoteJobVo.getMailContextVoErrorNotice()!=null) {
            ResMailContextDTO resMailContextDTO =
                    MailContextVoResMailContextDTOMapper.INSTANCE.toDto(remoteJobVo.getMailContextVoErrorNotice());
            resMailContextDTO.setEmailBook(setAddress(remoteJobVo.getMailContextVoErrorNotice()));
            resMailContextDTO.setGroupBook(setGroup(remoteJobVo.getMailContextVoErrorNotice()));
            return resMailContextDTO;
        }
        return null;
    }

    default List<AddressBookDTO> setAddress(MailContextVo mailContextVo) {
        Iterator<JobAddressBookEntity> jobAddressBookEntityIterator = mailContextVo.getAddress().iterator();
        List<AddressBookDTO> address = new ArrayList<>();

        while (jobAddressBookEntityIterator.hasNext()) {
            JobAddressBookEntity jobAddressBookEntity = jobAddressBookEntityIterator.next();
            AddressBookDTO addressBookDTO = new AddressBookDTO(
                    jobAddressBookEntity.getAddress().getId(),
                    jobAddressBookEntity.getAddress().getName(),
                    jobAddressBookEntity.getAddress().getEmail(),
                    false
            );
            address.add(addressBookDTO);
        }
        Collections.sort(address, new AscendingObj());
        return address;
    }

    default List<AddressBookDTO> setGroup(MailContextVo mailContextVo) {
        Iterator<JobGroupBookEntity> jobGroupBookEntityIterator = mailContextVo.getGroup().iterator();
        List<AddressBookDTO> group = new ArrayList<>();

        while (jobGroupBookEntityIterator.hasNext()) {
            JobGroupBookEntity jobGroupBookEntity = jobGroupBookEntityIterator.next();
            AddressBookDTO addressBookDTO = new AddressBookDTO(
                    jobGroupBookEntity.getGroup().getGid(),
                    jobGroupBookEntity.getGroup().getName(),
                    "",
                    true
            );
            group.add(addressBookDTO);
        }
        Collections.sort(group, new AscendingObj());
        return group;
    }
    default List<ResCrasDataDTO> setSelectJudgeRules(MailContextVo mailContextVo) {
        Iterator<CrasItemMasterJobVo> crasItemMasterJobVoIterator = mailContextVo.getCrasItem().iterator();
        List<ResCrasDataDTO> crasItem = new ArrayList<>();

        while (crasItemMasterJobVoIterator.hasNext()) {
            CrasItemMasterJobVo crasItemMasterJobVo = crasItemMasterJobVoIterator.next();
            ResCrasDataDTO resCrasDataDTO = new ResCrasDataDTO()
                    .setItemId(crasItemMasterJobVo.getCrasRuleId())
                    .setItemName(crasItemMasterJobVo.getCrasItemMasterVo().getItemName())
                    .setEnable(crasItemMasterJobVo.getCrasItemMasterVo().getEnable());
            crasItem.add(resCrasDataDTO);
        }
        Collections.sort(crasItem, new Comparator<ResCrasDataDTO>() {
           @Override
           public  int compare(ResCrasDataDTO o1, ResCrasDataDTO o2) {
               return o1.getItemId() - o2.getItemId();
           }
        });
        return crasItem;
    }
    default ResMailContextScriptDTO setScript(String preScript, String nextScript) {
        ResMailContextScriptDTO resMailContextScriptDTO = new ResMailContextScriptDTO()
                .setPrevious(preScript)
                .setNext(nextScript);
        return resMailContextScriptDTO;
    }

    default RemoteJobVo mapResRemoteJobDtoToVo(ResRemoteJobDetailAddDTO resRemoteJobDetailDTO) {
        SiteVo siteVo = new SiteVo()
                .setSiteId(resRemoteJobDetailDTO.getSiteId());

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserVo user = (UserVo) authentication.getPrincipal();

        RemoteJobVo remoteJobVo = new RemoteJobVo()
                .setJobName(resRemoteJobDetailDTO.getJobName())
                .setSiteId(resRemoteJobDetailDTO.getSiteId())
                .setCollectStatus(ReqURLController.JOB_STATUS_NOTBUILD)
                .setConvertStatus(ReqURLController.JOB_STATUS_NOTBUILD)
                .setErrorSummaryStatus(ReqURLController.JOB_STATUS_NOTBUILD)
                .setCrasDataStatus(ReqURLController.JOB_STATUS_NOTBUILD)
                .setMpaVersionStatus(ReqURLController.JOB_STATUS_NOTBUILD)
                .setDbPurgeStatus(ReqURLController.JOB_STATUS_NOTBUILD)
                .setIsConvert(resRemoteJobDetailDTO.getIsConvert())
                .setIsErrorSummary(resRemoteJobDetailDTO.getIsErrorSummary())
                .setIsCrasData(resRemoteJobDetailDTO.getIsCrasData())
                .setIsMpaVersion(resRemoteJobDetailDTO.getIsMpaVersion())
                .setIsDbPurge(resRemoteJobDetailDTO.getIsDbPurge())
                .setIsErrorNotice(resRemoteJobDetailDTO.getIsErrorNotice())
                .setCreated(LocalDateTime.now())
                .setLastAction(LocalDateTime.now())
                .setOwner(user.getId())
                .setPlanIds(resRemoteJobDetailDTO.getPlanIds())
                .setStop(true)
                .setSiteVoList(siteVo)
                .setMailContextVoCollect(MailContextVoResMailContextAddDTOMapper.INSTANCE.mapResMailContextAddDTOtoVo(resRemoteJobDetailDTO.getCollect()))
                .setMailContextVoConvert(resRemoteJobDetailDTO.getConvert()==null ? null : MailContextVoResMailContextAddDTOMapper.INSTANCE.mapResMailContextAddDTOtoVo(resRemoteJobDetailDTO.getConvert()))
                .setMailContextVoErrorSummary(resRemoteJobDetailDTO.getErrorSummary()==null ? null : MailContextVoResMailContextAddDTOMapper.INSTANCE.mapResMailContextAddDTOtoVo(resRemoteJobDetailDTO.getErrorSummary()))
                .setMailContextVoCrasData(resRemoteJobDetailDTO.getCrasData()==null ? null : MailContextVoResMailContextAddDTOMapper.INSTANCE.mapResMailContextAddDTOtoVo(resRemoteJobDetailDTO.getCrasData()))
                .setMailContextVoVersion(resRemoteJobDetailDTO.getMpaVersion()==null ? null : MailContextVoResMailContextAddDTOMapper.INSTANCE.mapResMailContextAddDTOtoVo(resRemoteJobDetailDTO.getMpaVersion()))
                .setMailContextVoDBPurge(resRemoteJobDetailDTO.getDbPurge()==null ? null : MailContextVoResMailContextAddDTOMapper.INSTANCE.mapResMailContextAddDTOtoVo(resRemoteJobDetailDTO.getDbPurge()))
                .setMailContextVoErrorNotice(resRemoteJobDetailDTO.getErrorNotice()==null ? null : MailContextVoResMailContextAddDTOMapper.INSTANCE.mapResMailContextAddDTOtoVoWithoutScript(resRemoteJobDetailDTO.getErrorNotice()));

        return remoteJobVo;
    }

    default RemoteJobVo mapResRemoteJobEditDtoToVo(RemoteJobVo remoteJobVo, ResRemoteJobDetailAddDTO resRemoteJobDetailDTO) {
        remoteJobVo
                .setJobName(resRemoteJobDetailDTO.getJobName())
                .setIsConvert(resRemoteJobDetailDTO.getIsConvert())
                .setIsErrorSummary(resRemoteJobDetailDTO.getIsErrorSummary())
                .setIsCrasData(resRemoteJobDetailDTO.getIsCrasData())
                .setIsMpaVersion(resRemoteJobDetailDTO.getIsMpaVersion())
                .setIsDbPurge(resRemoteJobDetailDTO.getIsDbPurge())
                .setIsErrorNotice(resRemoteJobDetailDTO.getIsErrorNotice())
                .setLastAction(LocalDateTime.now())
                .setMailContextVoCollect(MailContextVoResMailContextAddDTOMapper.INSTANCE.mapResMailContextAddDTOtoVo(resRemoteJobDetailDTO.getCollect()))
                .setMailContextVoConvert(resRemoteJobDetailDTO.getConvert()==null ? null : MailContextVoResMailContextAddDTOMapper.INSTANCE.mapResMailContextAddDTOtoVo(resRemoteJobDetailDTO.getConvert()))
                .setMailContextVoErrorSummary(resRemoteJobDetailDTO.getErrorSummary()==null ? null : MailContextVoResMailContextAddDTOMapper.INSTANCE.mapResMailContextAddDTOtoVo(resRemoteJobDetailDTO.getErrorSummary()))
                .setMailContextVoCrasData(resRemoteJobDetailDTO.getCrasData()==null ? null : MailContextVoResMailContextAddDTOMapper.INSTANCE.mapResMailContextAddDTOtoVo(resRemoteJobDetailDTO.getCrasData()))
                .setMailContextVoVersion(resRemoteJobDetailDTO.getMpaVersion()==null ? null : MailContextVoResMailContextAddDTOMapper.INSTANCE.mapResMailContextAddDTOtoVo(resRemoteJobDetailDTO.getMpaVersion()))
                .setMailContextVoDBPurge(resRemoteJobDetailDTO.getDbPurge()==null ? null : MailContextVoResMailContextAddDTOMapper.INSTANCE.mapResMailContextAddDTOtoVo(resRemoteJobDetailDTO.getDbPurge()))
                .setMailContextVoErrorNotice(resRemoteJobDetailDTO.getErrorNotice()==null ? null : MailContextVoResMailContextAddDTOMapper.INSTANCE.mapResMailContextAddDTOtoVoWithoutScript(resRemoteJobDetailDTO.getErrorNotice()));

        return remoteJobVo;
    }
}
