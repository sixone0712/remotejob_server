package jp.co.canon.rss.logmanager.mapper;

import org.mapstruct.BeanMapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

public interface GenericMapper<D, E> {

	D toDto(E e);

	E toEntity(D d);

	List<D> toDto(List<E> e);

	List<E> toEntity(List<D> d);

	@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
	void updateFromDto(D dto, @MappingTarget E entity);

	@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
	E updateFromDtoE(D dto, @MappingTarget E entity);
}