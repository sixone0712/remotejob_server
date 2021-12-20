package jp.co.canon.rss.logmanager.mapper.site;

import jp.co.canon.rss.logmanager.dto.site.ResSitesDetailDTO;
import jp.co.canon.rss.logmanager.mapper.GenericMapper;
import jp.co.canon.rss.logmanager.vo.SiteVo;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_NULL)
public interface SiteVoResSitesDetailDTOMapper extends GenericMapper<ResSitesDetailDTO, SiteVo> {
    SiteVoResSitesDetailDTOMapper INSTANCE = Mappers.getMapper(SiteVoResSitesDetailDTOMapper.class);
}
