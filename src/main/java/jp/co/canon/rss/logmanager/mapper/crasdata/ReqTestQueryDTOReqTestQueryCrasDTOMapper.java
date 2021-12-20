package jp.co.canon.rss.logmanager.mapper.crasdata;

import jp.co.canon.rss.logmanager.dto.rulecrasdata.ReqTestQueryCrasDTO;
import jp.co.canon.rss.logmanager.dto.rulecrasdata.ReqTestQueryDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ReqTestQueryDTOReqTestQueryCrasDTOMapper {
    ReqTestQueryDTOReqTestQueryCrasDTOMapper INSTANCE = Mappers.getMapper(ReqTestQueryDTOReqTestQueryCrasDTOMapper.class);

    @Mapping(target="table", expression = "java(mapTable(reqTestQueryDTO.getTargetTable()))")
    @Mapping(target="columns", expression = "java(mapColumns(reqTestQueryDTO.getTargetCol()))")
    @Mapping(target="where", expression = "java(mapWhere(reqTestQueryDTO.getManualWhere()))")
    ReqTestQueryCrasDTO mapReqTestQueryCrasDTO(ReqTestQueryDTO reqTestQueryDTO);

    default String mapTable(String table) { return table; }
    default String [] mapColumns(String [] columns) { return columns; }
    default String mapWhere(String where) { return where; }
}
