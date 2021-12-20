package jp.co.canon.rss.logmanager.mapper;

import jp.co.canon.rss.logmanager.dto.user.ResUserDTO;
import jp.co.canon.rss.logmanager.vo.UserVo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(
	componentModel = "spring",
	unmappedTargetPolicy = ReportingPolicy.IGNORE,
	nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_NULL,
	imports = java.time.format.DateTimeFormatter.class
)
public interface UserVoResUserDtoMapper {
	UserVoResUserDtoMapper INSTANCE = Mappers.getMapper(UserVoResUserDtoMapper.class);

	UserVo ToUserVo(ResUserDTO dto);

	@Mapping(target = "updateAt", expression = "java(vo.getUpdateAt().format(DateTimeFormatter.ofPattern(\"yyyy-MM-dd HH:mm:ss\")))")
	@Mapping(target = "accessAt", expression = "java(vo.getAccessAt() != null ? vo.getAccessAt().format(DateTimeFormatter.ofPattern(\"yyyy-MM-dd HH:mm:ss\")) : \"\")")
	ResUserDTO ToResUserDTO(UserVo vo);
}

