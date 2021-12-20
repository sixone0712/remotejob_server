package jp.co.canon.rss.logmanager.mapper.job;

import jp.co.canon.rss.logmanager.dto.job.ResMailContextAddDTO;
import jp.co.canon.rss.logmanager.vo.MailContextVo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_NULL)
public interface MailContextVoResMailContextAddDTOMapper {
    MailContextVoResMailContextAddDTOMapper INSTANCE = Mappers.getMapper(MailContextVoResMailContextAddDTOMapper.class);

    @Mapping(target="preScript", expression = "java(mapScript(resMailContextAddDTO.getScript().getPrevious()))")
    @Mapping(target="nextScript", expression = "java(mapScript(resMailContextAddDTO.getScript().getNext()))")
    MailContextVo mapResMailContextAddDTOtoVo(ResMailContextAddDTO resMailContextAddDTO);

    default String mapScript(String script) { return script; }

    MailContextVo mapResMailContextAddDTOtoVoWithoutScript(ResMailContextAddDTO resMailContextAddDTO);
}