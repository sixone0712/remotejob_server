package jp.co.canon.rss.logmanager.service;

import jp.co.canon.rss.logmanager.dto.address.AddressBookDTO;
import jp.co.canon.rss.logmanager.repository.AddressBookRepository;
import jp.co.canon.rss.logmanager.repository.GroupBookRepository;
import jp.co.canon.rss.logmanager.repository.JobAddressBookRepository;
import jp.co.canon.rss.logmanager.repository.JobGroupBookRepository;
import jp.co.canon.rss.logmanager.service.exception.ServiceException;
import jp.co.canon.rss.logmanager.util.ErrorMessage;
import jp.co.canon.rss.logmanager.vo.address.AddressBookEntity;
import jp.co.canon.rss.logmanager.vo.address.GroupBookEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
public class AddressBookService {

	final private AddressBookRepository addressRepository;
	final private GroupBookRepository groupRepository;
	final private JobAddressBookRepository jobAddressBookRepository;
	final private JobGroupBookRepository jobGroupBookRepository;

	public AddressBookService(AddressBookRepository addressRepository,
							  GroupBookRepository groupRepository,
							  JobAddressBookRepository jobAddressBookRepository,
							  JobGroupBookRepository jobGroupBookRepository) {
		this.addressRepository = addressRepository;
		this.groupRepository = groupRepository;
		this.jobAddressBookRepository = jobAddressBookRepository;
		this.jobGroupBookRepository = jobGroupBookRepository;
	}

	/**
	 * Get the specified address information by id.
	 *
	 * @param id
	 * @return null when an address doesn't exist.
	 */
	public AddressBookEntity getAddress(Long id) {
		Optional<AddressBookEntity> entity = addressRepository.findById(id);
		if (entity.isPresent()) {
			return entity.get();
		} else {
			return null;
		}
	}

	/**
	 * @param pageable
	 * @return
	 */
	public List<AddressBookEntity> getAddressList(Pageable pageable) {
		return addressRepository.findAll(pageable).getContent();
	}

	/**
	 * Get all address.
	 *
	 * @return
	 */
	public List<AddressBookEntity> getAddressList() {
		return addressRepository.findAll(Sort.by(Sort.Direction.ASC, "name"));
	}

	public List<AddressBookDTO> getAddressListDto() {
		return addressRepository.getAddressListDto(Sort.by(Sort.Direction.ASC, "name"));
	}

	/**
	 * Add an address into address book.
	 *
	 * @param name
	 * @param address
	 * @param addGroupIds
	 * @throws ResponseStatusException
	 */
	public void addAddress(String name, String address, List<Long> addGroupIds) {
		try {
			if (existAddress(address)) {
				throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ErrorMessage.DUPLICATE_EMAIL.getMsg());
			}

			AddressBookEntity addrEntity = addressRepository.save(new AddressBookEntity(name, address));

			if (addGroupIds != null) {
				List<GroupBookEntity> newGroupList = new ArrayList<>();

				for (Long groupId : addGroupIds) {
					String groupName = groupRepository.getGroupName(groupId);
					if (ObjectUtils.isEmpty(groupName)) {
						throw new ResponseStatusException(HttpStatus.NOT_FOUND, ErrorMessage.INVALID_GROUP.getMsg());
					}
					GroupBookEntity newGroup = new GroupBookEntity(groupName, addrEntity);
					newGroupList.add(newGroup);
				}

				if (newGroupList.size() > 0) {
					groupRepository.saveAll(newGroupList);
				}
			}
		} catch (ResponseStatusException e) {
			log.error(e.getMessage());
			throw e;
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * Modify an address information with the specified address id.
	 *
	 * @param id
	 * @param name
	 * @param address
	 * @param editGroupIds
	 * @throws ResponseStatusException
	 */
	@Transactional
	public void modifyAddress(Long id, String name, String address, List<Long> editGroupIds) {
		try {
			AddressBookEntity addrEntity = getAddress(id);

			if (ObjectUtils.isEmpty(addrEntity)) {
				throw new ResponseStatusException(HttpStatus.NOT_FOUND, ErrorMessage.INVALID_EMAIL.getMsg());
			}

			if (existAddress(address) && !addrEntity.getEmail().equals(address)) {
				throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ErrorMessage.DUPLICATE_EMAIL.getMsg());
			}

			// save modified address
			AddressBookEntity addedAddress = addressRepository.save(addrEntity.setName(name).setEmail(address));

			if (editGroupIds != null) {
				for (Long groupId : editGroupIds) {
					String groupName = groupRepository.getGroupName(groupId);
					if (ObjectUtils.isEmpty(groupName)) {
						throw new ResponseStatusException(HttpStatus.NOT_FOUND, ErrorMessage.INVALID_GROUP.getMsg());
					}
				}

				List<Long> registeredGids = groupRepository.getGroupIdByAddressId(id);
				List<Long> deleteGids = registeredGids.stream().filter(t -> !editGroupIds.contains(t)).collect(Collectors.toList());
				List<Long> addGids = editGroupIds.stream().filter(t -> !registeredGids.contains(t)).collect(Collectors.toList());

				// delete group member
				if (!ObjectUtils.isEmpty(deleteGids)) {
					groupRepository.deleteAllByGid(deleteGids);
				}

				// add group member
				List<GroupBookEntity> addGroupList = new ArrayList<>();
				for (Long addGid : addGids) {
					String findName = groupRepository.getGroupName(addGid);
					addGroupList.add(new GroupBookEntity(findName, addedAddress));
				}
				if (!ObjectUtils.isEmpty(addGroupList)) {
					groupRepository.saveAll(addGroupList);
				}
			}
		} catch (ResponseStatusException e) {
			log.error(e.getMessage());
			throw e;
		} catch (Exception e) {
			log.info(e.getMessage());
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * Delete an address in address book.(cascade)
	 *
	 * @param id
	 * @throws ResponseStatusException
	 */
	public void deleteAddress(long id) {
		try {
			addressRepository.deleteById(id);
			// TODO
			// 더 나은 방법을 찾자!!!!
			// delete mail_context_address table
			jobAddressBookRepository.deleteById(id);
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new ResponseStatusException(HttpStatus.NOT_FOUND);
		}
	}

	public void deleteAddressList(List<Long> ids) {
		try {
			List<AddressBookEntity> deleteList = addressRepository.findByIdIn(ids);
			addressRepository.deleteAll(deleteList);
			// TODO
			// 더 나은 방법을 찾자!!!!
			// delete mail_context_address table
			jobAddressBookRepository.deleteAllByGroupIds(ids);
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new ResponseStatusException(HttpStatus.NOT_FOUND);
		}
	}

	/**
	 * Delete the specified addresses in address book.(cascade)
	 *
	 * @param idList
	 */
	public void deleteAddress(List<Long> idList) {
		try {
			List<AddressBookEntity> list = addressRepository.findByIdIn(idList);
			addressRepository.deleteAll(list);
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new ResponseStatusException(HttpStatus.NOT_FOUND);
		}
	}

	public List<AddressBookDTO> getGroupNameList() {
		return groupRepository.getGroupNameListDto(Sort.by(Sort.Direction.ASC, "name"));
	}

	public List<AddressBookDTO> getGroupListByAddressId(Long addressId) {
		List<String> groupNames = groupRepository.getGroupNameByAddressId(addressId);
		return groupRepository.getPrimaryGroupInGroupName(groupNames, Sort.by(Sort.Direction.ASC, "name"));
	}

	/**
	 * Get address list of the specified group id
	 *
	 * @param id
	 * @return
	 */
	public List<AddressBookEntity> getGroupMembers(Long id) {
		try {
			String groupName = groupRepository.getGroupName(id);
			if (ObjectUtils.isEmpty(groupName)) {
				throw new ResponseStatusException(HttpStatus.NOT_FOUND);
			}

			List<Long> memberIds = groupRepository.getAddressIdList(groupName);
			if (memberIds.size() > 0) {
				List<AddressBookEntity> members = addressRepository.findByIdIn(memberIds, Sort.by(Sort.Direction.ASC, "name"));
				if (members.size() != memberIds.size()) {
					throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
						"cascade operation failed in AddressBookService#getGroupMembers");
				}
				return members;
			}
			return new ArrayList<AddressBookEntity>();
		} catch (ResponseStatusException e) {
			log.error(e.getMessage());
			throw e;
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	public List<AddressBookDTO> getGroupMembersDto(Long id) {
		try {
			String groupName = groupRepository.getGroupName(id);
			if (ObjectUtils.isEmpty(groupName)) {
				throw new ResponseStatusException(HttpStatus.NOT_FOUND);
			}

			List<Long> memberIds = groupRepository.getAddressIdList(groupName);
			if (memberIds.size() > 0) {
				List<AddressBookDTO> members = addressRepository.findInIdsDto(memberIds, Sort.by(Sort.Direction.ASC, "name"));
				if (members.size() != memberIds.size()) {
					throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
						"cascade operation failed in AddressBookService#getGroupMembers");
				}
				return members;
			}
			return new ArrayList<AddressBookDTO>();
		} catch (ResponseStatusException e) {
			log.error(e.getMessage());
			throw e;
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * @param group     Group name to add.
	 * @param addresses List to be included addresses from the address book.
	 * @return Return `group-name` that caller requested on success.
	 * @throws ServiceException
	 */
	public void addGroup(String group, List<Long> addresses) {
		try {
			if (ObjectUtils.isEmpty(group) || existGroup(group)) {
				throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ErrorMessage.DUPLICATE_GROUP.getMsg());
			}

			List<GroupBookEntity> newGroupList = new ArrayList<>();
			// add group primary key
			newGroupList.add(new GroupBookEntity(group, null));

			if (addresses != null) {
				List<AddressBookEntity> addressList = addressRepository.findByIdIn(addresses);
				if (addresses.size() != addressList.size()) {
					throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ErrorMessage.INVALID_EMAIL.getMsg());
				}

				for (AddressBookEntity addr : addressList) {
					newGroupList.add(new GroupBookEntity(group, addr));
				}
			}
			groupRepository.saveAll(newGroupList);
		} catch (ResponseStatusException e) {
			log.error(e.getMessage());
			throw e;
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	public void deleteGroup(Long id) {
		try {
			String groupName = groupRepository.getGroupName(id);
			if (ObjectUtils.isEmpty(groupName)) {
				throw new ResponseStatusException(HttpStatus.NOT_FOUND);
			}

			// delete group members
			List<Long> members = groupRepository.getAddressIdList(groupName);
			deleteGroupMembers(groupName, members);

			// delete primary key with cascade
			groupRepository.deleteById(id);

			// TODO
			// 더 나은 방법을 찾자!!!!
			// delete mail_context_group table
			jobGroupBookRepository.deleteAllByGroupId(id);

		} catch (ResponseStatusException e) {
			log.error(e.getMessage());
			throw e;
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	public void deleteGroupMembers(String group, List<Long> members) {
		for (Long memberId : members) {
			groupRepository.deleteGroupMember(group, memberId);
		}
	}

	public void deleteGroupMembers(Long groupId, List<Long> members) {
		String groupName = groupRepository.getGroupName(groupId);
		for (Long memberId : members) {
			groupRepository.deleteGroupMember(groupName, memberId);
		}
	}

	/**
	 * Test if the specified group name exists.
	 *
	 * @param group
	 * @return
	 * @throws ServiceException
	 */
	boolean existGroup(String group) throws ServiceException {
		return groupRepository.countAllByName(group) > 0;
	}

	/**
	 * Test if the specified address exists in the address book.
	 *
	 * @param address
	 * @return
	 */
	boolean existAddress(String address) {
		AddressBookEntity entity = addressRepository.findFirstByEmail(address);
		return entity != null;
	}

	/**
	 * Test if the specified name exists in the address book.
	 *
	 * @param name
	 * @return
	 */
	boolean existAddressName(String name) {
		AddressBookEntity entity = addressRepository.findFirstByName(name);
		return entity != null;
	}


	public void modifyGroup(Long id, String name, List<Long> address) {
		try {
			String groupName = groupRepository.getGroupName(id);
			if (ObjectUtils.isEmpty(groupName)) {
				throw new ResponseStatusException(HttpStatus.NOT_FOUND, ErrorMessage.INVALID_GROUP.getMsg());
			}

			if (existGroup(name) && !groupName.equals(name)) {
				throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ErrorMessage.DUPLICATE_GROUP.getMsg());
			}

			List<GroupBookEntity> editGroupList = null;

			if (address != null) {
				List<Long> memberIds = groupRepository.getAddressIdList(groupName);
				List<Long> deleteIds = memberIds.stream().filter(t -> !address.contains(t)).collect(Collectors.toList());
				List<Long> addIds = address.stream().filter(t -> !memberIds.contains(t)).collect(Collectors.toList());

				deleteGroupMembers(groupName, deleteIds);

				List<GroupBookEntity> groupList = groupRepository.findByName(groupName);
				List<AddressBookEntity> addEmailList = addressRepository.findByIdIn(addIds);

				for (AddressBookEntity email : addEmailList) {
					GroupBookEntity newMember = new GroupBookEntity(groupName, email);
					groupList.add(newMember);
				}
				editGroupList = groupList;
			} else {
				List<GroupBookEntity> groupList = groupRepository.findByName(groupName);
				editGroupList = groupList;
			}

			if (!groupName.equals(name)) {
				for (GroupBookEntity item : editGroupList) {
					item.setName(name);
				}
			}

			groupRepository.saveAll(editGroupList);
		} catch (ResponseStatusException e) {
			log.error(e.getMessage());
			throw e;
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * Get all group name and address name in both address book and group book.
	 *
	 * @return
	 */
	public List<AddressBookDTO> getGroupAndAddressList() {
		try {
			List<AddressBookDTO> list = new ArrayList<>();
			List<AddressBookDTO> group = groupRepository.getGroupNameListDto(Sort.by(Sort.Direction.ASC, "name"));
			List<AddressBookDTO> address = addressRepository.getAddressListDto(Sort.by(Sort.Direction.ASC, "name"));
			list.addAll(group);
			list.addAll(address);
			return list;
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	public List<AddressBookDTO> search(String keyword, Boolean withGroup) {
		try {
			List<AddressBookDTO> searchedList = new ArrayList<>();
			List<AddressBookDTO> group = null;
			List<AddressBookDTO> address = null;
			String lowerKeyword = keyword.toLowerCase();
			String groupKeyword = keyword.toLowerCase();
			boolean isAllGroup = false;
			if(lowerKeyword.startsWith("@")) {
				groupKeyword = lowerKeyword.replace("@", "");
				if(groupKeyword.equals("")) {
					isAllGroup = true;
				}
			}

			if (withGroup) {
				if(isAllGroup) {
					group = groupRepository.getAllGroup(Sort.by(Sort.Direction.ASC, "name"));
				} else {
					group = groupRepository.searchByNameDto(groupKeyword, Sort.by(Sort.Direction.ASC, "name"));
				}
			}
			address = addressRepository.searchByNameOrEmailDto(lowerKeyword, Sort.by(Sort.Direction.ASC, "name"));

			if (group != null && group.size() > 0) {
				searchedList.addAll(group);
			}

			if (address != null && address.size() > 0) {
				searchedList.addAll(address);
			}

			return searchedList;
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}
