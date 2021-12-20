package jp.co.canon.rss.logmanager.mapper.job;

import jp.co.canon.rss.logmanager.config.ReqURLController;
import jp.co.canon.rss.logmanager.dto.job.ReqLocalJobAddDTO;
import jp.co.canon.rss.logmanager.vo.LocalJobVo;
import jp.co.canon.rss.logmanager.vo.SiteVo;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface LocalJobVoResLocalJobDtoMapper {
    LocalJobVoResLocalJobDtoMapper INSTANCE = Mappers.getMapper(LocalJobVoResLocalJobDtoMapper.class);

    default LocalJobVo mapResLocalJobDtoToVo(ReqLocalJobAddDTO reqLocalJobAddDTO, List<String> fileOriginalName) {
        SiteVo siteVo = new SiteVo()
                .setSiteId(reqLocalJobAddDTO.getSiteId());
        LocalJobVo localJobVo = new LocalJobVo()
                .setSiteId(reqLocalJobAddDTO.getSiteId())
                .setCollectStatus(ReqURLController.JOB_STATUS_NOTBUILD)
                .setFileIndices(reqLocalJobAddDTO.getFileIndices())
                .setFileOriginalNames(fileOriginalName.toArray(new String[fileOriginalName.size()]))
                .setRegisteredDate(LocalDateTime.now())
                .setStop(false)
                .setCollectError("")
                .setIsErrorNotice(reqLocalJobAddDTO.getIsErrorNotice())
                .setMailContextVoErrorNotice(
                        reqLocalJobAddDTO.getErrorNotice()==null ? null
                                : MailContextVoResMailContextAddDTOMapper.INSTANCE.mapResMailContextAddDTOtoVoWithoutScript(reqLocalJobAddDTO.getErrorNotice())
                )
                .setSiteVoListLocal(siteVo);
        return localJobVo;


    }
}
