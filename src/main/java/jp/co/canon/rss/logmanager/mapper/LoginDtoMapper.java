package jp.co.canon.rss.logmanager.mapper;

import jp.co.canon.rss.logmanager.dto.auth.ResLoginDTO;
import jp.co.canon.rss.logmanager.dto.auth.ResTokenServiceDTO;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface LoginDtoMapper {
	LoginDtoMapper INSTANCE = Mappers.getMapper(LoginDtoMapper.class);

	ResTokenServiceDTO ToLoginServiceDto(ResLoginDTO e);

	ResLoginDTO ToLoginResDto(ResTokenServiceDTO d);
}
