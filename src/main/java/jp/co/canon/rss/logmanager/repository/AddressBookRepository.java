package jp.co.canon.rss.logmanager.repository;

import jp.co.canon.rss.logmanager.dto.address.AddressBookDTO;
import jp.co.canon.rss.logmanager.vo.address.AddressBookEntity;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AddressBookRepository extends JpaRepository<AddressBookEntity, Long> {
	@Query("select new jp.co.canon.rss.logmanager.dto.address.AddressBookDTO(a.id, a.name, a.email, false) from AddressBookEntity a")
	List<AddressBookDTO> getAddressListDto(Sort sort);

	@Query("select new jp.co.canon.rss.logmanager.dto.address.AddressBookDTO(a.id, a.name, a.email,false) from AddressBookEntity a where a.id in (:address_ids)")
	List<AddressBookDTO> findInIdsDto(@Param("address_ids") List<Long> addressIds, Sort sort);

	List<AddressBookEntity> findByIdIn(List<Long> idList);

	List<AddressBookEntity> findByIdIn(List<Long> idList, Sort sort);

	AddressBookEntity findFirstByName(String name);

	AddressBookEntity findFirstByEmail(String address);

	@Query("select new jp.co.canon.rss.logmanager.dto.address.AddressBookDTO(a.id, a.name, a.email, false ) " +
		"from AddressBookEntity a " +
		"where lower(a.name) like %:keyword% or lower(a.email) like %:keyword%")
	List<AddressBookDTO> searchByNameOrEmailDto(@Param("keyword") String keyword, Sort sort);
}
