package jp.co.canon.rss.logmanager.repository;

import jp.co.canon.rss.logmanager.dto.address.AddressBookDTO;
import jp.co.canon.rss.logmanager.vo.address.GroupBookEntity;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface GroupBookRepository extends CrudRepository<GroupBookEntity, Long> {
	@Query("select g.gid from GroupBookEntity g where g.name = :group_name and g.address is null")
	Long getGroupPrimaryKey(@Param("group_name") String group);

	@Query("select g from GroupBookEntity g where g.gid in (:group_ids)")
	List<GroupBookEntity> findInGroupIds(@Param("group_ids") List<Long> groupIds, Sort sort);

	@Query("select new jp.co.canon.rss.logmanager.dto.address.AddressBookDTO(g.gid, g.name, '', true) from GroupBookEntity g where g.gid in (:group_ids)")
	List<AddressBookDTO> findInGroupIdsDto(@Param("group_ids") List<Long> groupIds, Sort sort);

	@Query("select g.name from GroupBookEntity g where g.gid = :group_id")
	String getGroupName(@Param("group_id") Long id);

	@Query("select g.gid from GroupBookEntity g where g.address.id = :address_id")
	List<Long> getGroupIdByAddressId(@Param("address_id") Long id);

	@Query("select new jp.co.canon.rss.logmanager.dto.address.AddressBookDTO(g.gid, g.name, '', true) " +
		"from GroupBookEntity g where g.address is null")
	List<AddressBookDTO> getGroupNameListDto(Sort sort);

	@Query("select g.name from GroupBookEntity g where g.address.id = :address_id")
	List<String> getGroupNameByAddressId(@Param("address_id") Long addressId);

	@Query("select new jp.co.canon.rss.logmanager.dto.address.AddressBookDTO(g.gid, g.name, '', true) from GroupBookEntity g where g.name in (:group_names) and g.address is null")
	List<AddressBookDTO> getPrimaryGroupInGroupName(@Param("group_names") List<String> groupNames, Sort sort);

	@Query("select g.address.id from GroupBookEntity g where g.name = :group_name and g.address is not null")
	List<Long> getAddressIdList(@Param("group_name") String group);

	Long countAllByName(String name);

	List<GroupBookEntity> findByName(String group);

	// not cascade
	@Query("delete from GroupBookEntity g where g.gid = :gid")
	void deleteByGid(Long gid);

	// not cascade
	@Transactional
	@Modifying
	@Query("delete from GroupBookEntity g where g.gid in (:group_ids)")
	void deleteAllByGid(@Param("group_ids") List<Long> gids);

	// not cascade
	@Query("delete from GroupBookEntity g where g.name = :group")
	void deleteByName(String group);

	@Transactional
	@Modifying
	@Query(value = "delete from log_manager.group_book where name = :group and id = :member", nativeQuery = true)
	void deleteGroupMember(String group, Long member);

	@Query("select new jp.co.canon.rss.logmanager.dto.address.AddressBookDTO(g.gid, g.name, '', true) " +
		"from GroupBookEntity g where g.address is null " +
		"and lower(g.name) like %:keyword%")
	List<AddressBookDTO> searchByNameDto(@Param("keyword") String keyword, Sort sort);

	@Query("select new jp.co.canon.rss.logmanager.dto.address.AddressBookDTO(g.gid, g.name, '', true) " +
		"from GroupBookEntity g where g.address is null")
	List<AddressBookDTO> getAllGroup(Sort sort);
}
