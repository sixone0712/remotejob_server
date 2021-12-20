package jp.co.canon.rss.logmanager.util;

import jp.co.canon.rss.logmanager.config.ReqURLController;
import jp.co.canon.rss.logmanager.dto.address.AddressBookDTO;
import jp.co.canon.rss.logmanager.repository.AddressBookRepository;
import jp.co.canon.rss.logmanager.repository.GroupBookRepository;
import jp.co.canon.rss.logmanager.repository.JobAddressBookRepository;
import jp.co.canon.rss.logmanager.repository.JobGroupBookRepository;
import jp.co.canon.rss.logmanager.service.AddressBookService;
import jp.co.canon.rss.logmanager.vo.LocalJobVo;
import jp.co.canon.rss.logmanager.vo.RemoteJobVo;
import jp.co.canon.rss.logmanager.vo.address.JobAddressBookEntity;
import jp.co.canon.rss.logmanager.vo.address.JobGroupBookEntity;

import java.util.ArrayList;
import java.util.List;

public class GetRecipients {
    AddressBookRepository addressRepository;
    GroupBookRepository groupRepository;
    JobAddressBookRepository jobAddressBookRepository;
    JobGroupBookRepository jobGroupBookRepository;

    public String [] getRecipients(RemoteJobVo resultRemoteJobDetail, String flag) {
        AddressBookService addressBookService = new AddressBookService(
                addressRepository, groupRepository, jobAddressBookRepository, jobGroupBookRepository);
        
        List<String> recipients = new ArrayList<>();
        List<JobAddressBookEntity> jobAddressBookEntityList = new ArrayList<>();
        List<JobGroupBookEntity> jobGroupBookEntityList = new ArrayList<>();

        switch (flag) {
            case ReqURLController.JOB_STEP_ERROR :
                if(resultRemoteJobDetail.getMailContextVoErrorSummary().getCustomEmails().length!=0) {
                    for(String customEmail : resultRemoteJobDetail.getMailContextVoErrorSummary().getCustomEmails())
                        recipients.add(customEmail);
                }
                if(resultRemoteJobDetail.getMailContextVoErrorSummary().getAddress().size()!=0) {
                    jobAddressBookEntityList =
                            new ArrayList<>(resultRemoteJobDetail.getMailContextVoErrorSummary().getAddress());
                }
                if(resultRemoteJobDetail.getMailContextVoErrorSummary().getGroup().size()!=0) {
                    jobGroupBookEntityList =
                            new ArrayList<>(resultRemoteJobDetail.getMailContextVoErrorSummary().getGroup());
                }
                break;
            case ReqURLController.JOB_STEP_CRAS :
                if(resultRemoteJobDetail.getMailContextVoCrasData().getCustomEmails().length!=0) {
                    for (String customEmail : resultRemoteJobDetail.getMailContextVoCrasData().getCustomEmails())
                        recipients.add(customEmail);
                }
                if(resultRemoteJobDetail.getMailContextVoCrasData().getAddress().size()!=0) {
                    jobAddressBookEntityList =
                            new ArrayList<>(resultRemoteJobDetail.getMailContextVoCrasData().getAddress());
                }
                if(resultRemoteJobDetail.getMailContextVoCrasData().getGroup().size()!=0) {
                    jobGroupBookEntityList =
                            new ArrayList<>(resultRemoteJobDetail.getMailContextVoCrasData().getGroup());
                }
                break;
            case ReqURLController.JOB_STEP_VERSION :
                if(resultRemoteJobDetail.getMailContextVoVersion().getCustomEmails().length!=0) {
                    for(String customEmail : resultRemoteJobDetail.getMailContextVoVersion().getCustomEmails())
                        recipients.add(customEmail);
                }
                if(resultRemoteJobDetail.getMailContextVoVersion().getAddress().size()!=0) {
                    jobAddressBookEntityList =
                            new ArrayList<>(resultRemoteJobDetail.getMailContextVoVersion().getAddress());
                }
                if(resultRemoteJobDetail.getMailContextVoVersion().getGroup().size()!=0) {
                    jobGroupBookEntityList =
                            new ArrayList<>(resultRemoteJobDetail.getMailContextVoVersion().getGroup());
                }
                break;
            case ReqURLController.JOB_STEP_ERR_NOTICE :
                if(resultRemoteJobDetail.getMailContextVoErrorNotice().getCustomEmails().length!=0) {
                    for(String customEmail : resultRemoteJobDetail.getMailContextVoErrorNotice().getCustomEmails())
                        recipients.add(customEmail);
                }
                if(resultRemoteJobDetail.getMailContextVoErrorNotice().getAddress().size()!=0) {
                    jobAddressBookEntityList =
                            new ArrayList<>(resultRemoteJobDetail.getMailContextVoErrorNotice().getAddress());
                }
                if(resultRemoteJobDetail.getMailContextVoErrorNotice().getGroup().size()!=0) {
                    jobGroupBookEntityList =
                            new ArrayList<>(resultRemoteJobDetail.getMailContextVoErrorNotice().getGroup());
                }
                break;
        }

        for(JobAddressBookEntity jobAddressBookEntity : jobAddressBookEntityList)
            recipients.add(jobAddressBookEntity.getAddress().getEmail());

        for(JobGroupBookEntity jobGroupBookEntity : jobGroupBookEntityList) {
            List<AddressBookDTO> addressBookDTOList = addressBookService.getGroupMembersDto(jobGroupBookEntity.getGroup().getGid());
            for(AddressBookDTO addressBookDTO : addressBookDTOList)
                recipients.add(addressBookDTO.getEmail());
        }

        return recipients.toArray(new String[recipients.size()]);
    }

    public String [] getRecipientsLocalJob(LocalJobVo resultLocalJobDetail, String flag) {
        AddressBookService addressBookService = new AddressBookService(
                addressRepository, groupRepository, jobAddressBookRepository, jobGroupBookRepository);

        List<String> recipients = new ArrayList<>();
        List<JobAddressBookEntity> jobAddressBookEntityList = new ArrayList<>();
        List<JobGroupBookEntity> jobGroupBookEntityList = new ArrayList<>();

        if(resultLocalJobDetail.getMailContextVoErrorNotice().getCustomEmails().length!=0) {
            for(String customEmail : resultLocalJobDetail.getMailContextVoErrorNotice().getCustomEmails())
                recipients.add(customEmail);
        }
        if(resultLocalJobDetail.getMailContextVoErrorNotice().getAddress().size()!=0) {
            jobAddressBookEntityList =
                    new ArrayList<>(resultLocalJobDetail.getMailContextVoErrorNotice().getAddress());
        }
        if(resultLocalJobDetail.getMailContextVoErrorNotice().getGroup().size()!=0) {
            jobGroupBookEntityList =
                    new ArrayList<>(resultLocalJobDetail.getMailContextVoErrorNotice().getGroup());
        }

        for(JobAddressBookEntity jobAddressBookEntity : jobAddressBookEntityList)
            recipients.add(jobAddressBookEntity.getAddress().getEmail());

        for(JobGroupBookEntity jobGroupBookEntity : jobGroupBookEntityList) {
            List<AddressBookDTO> addressBookDTOList = addressBookService.getGroupMembersDto(jobGroupBookEntity.getGroup().getGid());
            for(AddressBookDTO addressBookDTO : addressBookDTOList)
                recipients.add(addressBookDTO.getEmail());
        }

        return recipients.toArray(new String[recipients.size()]);
    }
}
