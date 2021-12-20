package jp.co.canon.rss.logmanager.mapper.job;

import jp.co.canon.rss.logmanager.dto.job.ResMailContextDTO;
import jp.co.canon.rss.logmanager.mapper.GenericMapper;
import jp.co.canon.rss.logmanager.vo.MailContextVo;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE, nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_NULL)
public interface MailContextVoResMailContextDTOMapper extends GenericMapper<ResMailContextDTO, MailContextVo> {
    MailContextVoResMailContextDTOMapper INSTANCE = Mappers.getMapper(MailContextVoResMailContextDTOMapper.class);
}