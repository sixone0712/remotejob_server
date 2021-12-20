package jp.co.canon.rss.logmanager.mapper.site;

import jp.co.canon.rss.logmanager.dto.site.ReqAddSiteDTO;
import jp.co.canon.rss.logmanager.mapper.GenericMapper;
import jp.co.canon.rss.logmanager.vo.SiteVo;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_NULL,
        imports = java.time.format.DateTimeFormatter.class)
public interface SiteVoSiteDtoMapper extends GenericMapper<ReqAddSiteDTO, SiteVo> {
    SiteVoSiteDtoMapper INSTANCE = Mappers.getMapper(SiteVoSiteDtoMapper.class);
}
