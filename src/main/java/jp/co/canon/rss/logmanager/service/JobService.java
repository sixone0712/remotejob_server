package jp.co.canon.rss.logmanager.service;

import jp.co.canon.rss.logmanager.config.ReqURLController;
import jp.co.canon.rss.logmanager.dto.job.*;
import jp.co.canon.rss.logmanager.dto.site.ResPlanDTO;
import jp.co.canon.rss.logmanager.dto.site.ResRCPlanDTO;
import jp.co.canon.rss.logmanager.exception.ConvertException;
import jp.co.canon.rss.logmanager.manager.JobManager;
import jp.co.canon.rss.logmanager.manager.NotiManager;
import jp.co.canon.rss.logmanager.mapper.job.HistoryVoResTimeLineDtoMapper;
import jp.co.canon.rss.logmanager.mapper.job.LocalJobVoResLocalJobDtoMapper;
import jp.co.canon.rss.logmanager.mapper.job.RemoteJobVoResRemoteJobDtoMapper;
import jp.co.canon.rss.logmanager.repository.*;
import jp.co.canon.rss.logmanager.repository.crasdata.CrasItemMasterJobRepository;
import jp.co.canon.rss.logmanager.repository.crasdata.CrasItemMasterRepository;
import jp.co.canon.rss.logmanager.scheduler.JobScheduler;
import jp.co.canon.rss.logmanager.scheduler.NotiScheduler;
import jp.co.canon.rss.logmanager.system.ClientManageService;
import jp.co.canon.rss.logmanager.util.CallRestAPI;
import jp.co.canon.rss.logmanager.vo.*;
import jp.co.canon.rss.logmanager.vo.address.JobAddressBookEntity;
import jp.co.canon.rss.logmanager.vo.address.JobGroupBookEntity;
import jp.co.canon.rss.logmanager.vo.crasdata.CrasItemMasterJobVo;
import jp.co.canon.rss.logmanager.vo.crasdata.CrasItemMasterVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Slf4j
@Service()
public class JobService {
	private final static String PLAN_TYPE_FTP = "ftp";
	private final static String PLAN_TYPE_VFTP_COMPAT = "vftp_compat";
	private final static String PLAN_TYPE_VFTP_SSS = "vftp_sss";

	RemoteJobRepository remoteJobRepository;
	LocalJobRepository localJobRepository;
	LocalJobFileIdVoRepository localJobFileIdVoRepository;
	JobAddressBookRepository jobAddressBookRepository;
	JobGroupBookRepository jobGroupBookRepository;
	AddressBookRepository addressBookRepository;
	GroupBookRepository groupBookRepository;
	MailContextRepository mailContextRepository;
	CrasItemMasterRepository crasItemMasterRepository;
	CrasItemMasterJobRepository crasItemMasterJobRepository;
	JobManager job;
	NotiManager noti;
	ClientManageService clientManageService;
	UploadService uploadService;
	SiteRepository siteRepositoryService;
	JobScheduler js;
	NotiScheduler ns;
	SchedulerRepository schedulerRepository;
	HistoryRepository historyRepository;

	public JobService(RemoteJobRepository remoteJobRepository,
					  LocalJobRepository localJobRepository, LocalJobFileIdVoRepository localJobFileIdVoRepository,
					  JobAddressBookRepository jobAddressBookRepository, JobGroupBookRepository jobGroupBookRepository,
					  AddressBookRepository addressBookRepository, GroupBookRepository groupBookRepository, MailContextRepository mailContextRepository,
					  JobManager job, NotiManager noti, UploadService uploadService, ClientManageService clientManageService,
					  CrasItemMasterRepository crasItemMasterRepository,CrasItemMasterJobRepository crasItemMasterJobRepository,
					  SiteRepository siteRepositoryService, JobScheduler js, NotiScheduler ns, SchedulerRepository schedulerRepository,
					  HistoryRepository historyRepository) {
		this.remoteJobRepository = remoteJobRepository;
		this.localJobRepository = localJobRepository;
		this.localJobFileIdVoRepository = localJobFileIdVoRepository;
		this.jobAddressBookRepository = jobAddressBookRepository;
		this.jobGroupBookRepository = jobGroupBookRepository;
		this.addressBookRepository = addressBookRepository;
		this.groupBookRepository = groupBookRepository;
		this.mailContextRepository = mailContextRepository;
		this.crasItemMasterRepository = crasItemMasterRepository;
		this.crasItemMasterJobRepository = crasItemMasterJobRepository;
		this.job = job;
		this.noti = noti;
		this.uploadService = uploadService;
		this.clientManageService = clientManageService;
		this.siteRepositoryService = siteRepositoryService;
		this.js = js;
		this.ns = ns;
		this.schedulerRepository = schedulerRepository;
		this.historyRepository = historyRepository;
	}

	public ResRemoteJobDetailDTO getRemoteJobDetail(int remoteJobId) throws Exception {
		try {
			RemoteJobVo remoteJobVo = remoteJobRepository.findByJobId(remoteJobId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
			ResRemoteJobDetailDTO resRemoteJobDetailDTO = RemoteJobVoResRemoteJobDtoMapper.INSTANCE.mapRemoteJobVoToDto(remoteJobVo);

			String crasServer = String.format(ReqURLController.API_DEFAULT_CRAS_SERVER_JOB,
					remoteJobVo.getSiteVoList().getCrasAddress(), remoteJobVo.getSiteVoList().getCrasPort());

			if(resRemoteJobDetailDTO.getCollect()!=null) {
				if(resRemoteJobDetailDTO.getCollect().getScript()!=null) {
					resRemoteJobDetailDTO.getCollect().getScript().setPrevious(getScript(remoteJobId, ReqURLController.JOB_STEP_COLLECT, ReqURLController.JOB_SCRIPT_PRE, crasServer));
					resRemoteJobDetailDTO.getCollect().getScript().setNext(getScript(remoteJobId, ReqURLController.JOB_STEP_COLLECT, ReqURLController.JOB_SCRIPT_POST, crasServer));
				}
			}

			if(resRemoteJobDetailDTO.getConvert()!=null) {
				if(resRemoteJobDetailDTO.getConvert().getScript()!=null) {
					resRemoteJobDetailDTO.getConvert().getScript().setPrevious(getScript(remoteJobId, ReqURLController.JOB_STEP_CONVERT, ReqURLController.JOB_SCRIPT_PRE, crasServer));
					resRemoteJobDetailDTO.getConvert().getScript().setNext(getScript(remoteJobId, ReqURLController.JOB_STEP_CONVERT, ReqURLController.JOB_SCRIPT_POST, crasServer));
				}
			}

			if(resRemoteJobDetailDTO.getErrorSummary()!=null) {
				if(resRemoteJobDetailDTO.getErrorSummary().getScript()!=null) {
					resRemoteJobDetailDTO.getErrorSummary().getScript().setPrevious(getScript(remoteJobId, ReqURLController.JOB_STEP_SUMMARY_CRAS, ReqURLController.JOB_SCRIPT_PRE, crasServer));
					resRemoteJobDetailDTO.getErrorSummary().getScript().setNext(getScript(remoteJobId, ReqURLController.JOB_STEP_SUMMARY_CRAS, ReqURLController.JOB_SCRIPT_POST, crasServer));
				}
			}

			if(resRemoteJobDetailDTO.getCrasData()!=null) {
				if(resRemoteJobDetailDTO.getCrasData().getScript()!=null) {
					resRemoteJobDetailDTO.getCrasData().getScript().setPrevious(getScript(remoteJobId, ReqURLController.JOB_STEP_CRAS, ReqURLController.JOB_SCRIPT_PRE, crasServer));
					resRemoteJobDetailDTO.getCrasData().getScript().setNext(getScript(remoteJobId, ReqURLController.JOB_STEP_CRAS, ReqURLController.JOB_SCRIPT_POST, crasServer));
				}
			}

			if(resRemoteJobDetailDTO.getMpaVersion()!=null) {
				if(resRemoteJobDetailDTO.getMpaVersion().getScript()!=null) {
					resRemoteJobDetailDTO.getMpaVersion().getScript().setPrevious(getScript(remoteJobId, ReqURLController.JOB_STEP_VERSION, ReqURLController.JOB_SCRIPT_PRE, crasServer));
					resRemoteJobDetailDTO.getMpaVersion().getScript().setNext(getScript(remoteJobId, ReqURLController.JOB_STEP_VERSION, ReqURLController.JOB_SCRIPT_POST, crasServer));
				}
			}

			if(resRemoteJobDetailDTO.getDbPurge()!=null) {
				if(resRemoteJobDetailDTO.getDbPurge().getScript()!=null) {
					resRemoteJobDetailDTO.getDbPurge().getScript().setPrevious(getScript(remoteJobId, ReqURLController.JOB_STEP_DBPURGE, ReqURLController.JOB_SCRIPT_PRE, crasServer));
					resRemoteJobDetailDTO.getDbPurge().getScript().setNext(getScript(remoteJobId, ReqURLController.JOB_STEP_DBPURGE, ReqURLController.JOB_SCRIPT_POST, crasServer));
				}
			}

			return resRemoteJobDetailDTO;
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	public String getScript(int jobId, String step, String position, String cras) throws ConvertException {
		CallRestAPI callRestAPI = new CallRestAPI();
		HttpHeaders headers = new HttpHeaders();
		headers.set(ReqURLController.JOB_CONTENT_TYPE, ReqURLController.JOB_APPLICATION_JSON);
		headers.set(ReqURLController.JOB_CLIENT_ID, ReqURLController.JOB_TYPE_REMOTEJOB + "_" + String.format("%06d", jobId));
		HttpEntity reqHeaders = new HttpEntity<>(headers);

		String apiURL = String.format(ReqURLController.API_GET_GET_SCRIPT, step, position);

		ResponseEntity<?> response = callRestAPI.getWithCustomHeaderRestAPI(cras+apiURL, reqHeaders, String.class);
		String script = (String) response.getBody();

		if(script.equals("204\n") || script.equals("204"))
			script = "";

		return script;
	}

	public ResJobIdDTO addRemoteJob(ResRemoteJobDetailAddDTO resRemoteJobDetailDTO) {
		RemoteJobVo setRemoteJobVo = null;
		try {
			RemoteJobVo remoteJobVo = RemoteJobVoResRemoteJobDtoMapper.INSTANCE.mapResRemoteJobDtoToVo(resRemoteJobDetailDTO);
			setRemoteJobVo = remoteJobRepository.save(remoteJobVo);

			ResJobIdDTO resJobIdDTO = new ResJobIdDTO()
					.setJobId(setRemoteJobVo.getJobId());

			if(!ObjectUtils.isEmpty(setRemoteJobVo) && setRemoteJobVo.getJobId() > 0) {
				callAddJobAddressGroupBook(resRemoteJobDetailDTO, setRemoteJobVo);

				if(resRemoteJobDetailDTO.getIsCrasData())
					callAddCrasDataJudgeRules(resRemoteJobDetailDTO, setRemoteJobVo.getMailContextVoCrasData().getId());

				Optional<SiteVo> siteVo = siteRepositoryService.findById(setRemoteJobVo.getSiteId());

				checkScript(resRemoteJobDetailDTO, setRemoteJobVo, siteVo);
			}
			return resJobIdDTO;
		} catch (Exception e) {
			log.error(e.getMessage());
			if(!ObjectUtils.isEmpty(setRemoteJobVo) && setRemoteJobVo.getJobId() > 0) {
				remoteJobRepository.deleteById(setRemoteJobVo.getJobId());
				crasItemMasterJobRepository.deleteById(setRemoteJobVo.getJobId());
			}
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	public void checkScript(ResRemoteJobDetailAddDTO resRemoteJobDetailDTO, RemoteJobVo setRemoteJobVo, Optional<SiteVo> siteVo) throws ConvertException {
		String crasServer = String.format(ReqURLController.API_DEFAULT_CRAS_SERVER_JOB,
				siteVo.get().getCrasAddress(), siteVo.get().getCrasPort());

		setScript(setRemoteJobVo.getJobId(), resRemoteJobDetailDTO.getCollect().getScript().getPrevious(),
				ReqURLController.JOB_SCRIPT_PRE, ReqURLController.JOB_STEP_COLLECT, crasServer);
		setScript(setRemoteJobVo.getJobId(), resRemoteJobDetailDTO.getCollect().getScript().getNext(),
				ReqURLController.JOB_SCRIPT_POST, ReqURLController.JOB_STEP_COLLECT, crasServer);

		if(resRemoteJobDetailDTO.getConvert()!=null) {
			setScript(setRemoteJobVo.getJobId(), resRemoteJobDetailDTO.getConvert().getScript().getPrevious(),
					ReqURLController.JOB_SCRIPT_PRE, ReqURLController.JOB_STEP_CONVERT, crasServer);
			setScript(setRemoteJobVo.getJobId(), resRemoteJobDetailDTO.getConvert().getScript().getNext(),
					ReqURLController.JOB_SCRIPT_POST, ReqURLController.JOB_STEP_CONVERT, crasServer);
		}

		if(resRemoteJobDetailDTO.getErrorSummary()!=null) {
			setScript(setRemoteJobVo.getJobId(), resRemoteJobDetailDTO.getErrorSummary().getScript().getPrevious(),
					ReqURLController.JOB_SCRIPT_PRE, ReqURLController.JOB_STEP_SUMMARY_CRAS, crasServer);
			setScript(setRemoteJobVo.getJobId(), resRemoteJobDetailDTO.getErrorSummary().getScript().getNext(),
					ReqURLController.JOB_SCRIPT_POST, ReqURLController.JOB_STEP_SUMMARY_CRAS, crasServer);
		}

		if(resRemoteJobDetailDTO.getCrasData()!=null) {
			setScript(setRemoteJobVo.getJobId(), resRemoteJobDetailDTO.getCrasData().getScript().getPrevious(),
					ReqURLController.JOB_SCRIPT_PRE, ReqURLController.JOB_STEP_CRAS, crasServer);
			setScript(setRemoteJobVo.getJobId(), resRemoteJobDetailDTO.getCrasData().getScript().getNext(),
					ReqURLController.JOB_SCRIPT_POST, ReqURLController.JOB_STEP_CRAS, crasServer);
		}

		if(resRemoteJobDetailDTO.getMpaVersion()!=null) {
			setScript(setRemoteJobVo.getJobId(), resRemoteJobDetailDTO.getMpaVersion().getScript().getPrevious(),
					ReqURLController.JOB_SCRIPT_PRE, ReqURLController.JOB_STEP_VERSION, crasServer);
			setScript(setRemoteJobVo.getJobId(), resRemoteJobDetailDTO.getMpaVersion().getScript().getNext(),
					ReqURLController.JOB_SCRIPT_POST, ReqURLController.JOB_STEP_VERSION, crasServer);
		}

		if(resRemoteJobDetailDTO.getDbPurge()!=null) {
			setScript(setRemoteJobVo.getJobId(), resRemoteJobDetailDTO.getDbPurge().getScript().getPrevious(),
					ReqURLController.JOB_SCRIPT_PRE, ReqURLController.JOB_STEP_DBPURGE, crasServer);
			setScript(setRemoteJobVo.getJobId(), resRemoteJobDetailDTO.getDbPurge().getScript().getNext(),
					ReqURLController.JOB_SCRIPT_POST, ReqURLController.JOB_STEP_DBPURGE, crasServer);
		}
	}

	public void setScript(int jobId, String script, String position, String step, String crasServer) throws ConvertException {
		String apiURL = String.format(ReqURLController.API_POST_SET_SCRIPT, step);

		HttpHeaders headers = new HttpHeaders();
		headers.set(ReqURLController.JOB_CONTENT_TYPE, ReqURLController.JOB_APPLICATION_JSON);
		headers.set(ReqURLController.JOB_CLIENT_ID,
				ReqURLController.JOB_TYPE_REMOTEJOB + "_" + String.format("%06d", jobId));
		log.info("client id for set script : " + ReqURLController.JOB_TYPE_REMOTEJOB + "_" + String.format("%06d", jobId));

		ReqSetScriptDTO reqSetScriptDTO = new ReqSetScriptDTO()
				.setPosition(position)
				.setScript(script=="" ? null : script);

		CallRestAPI callRestAPI = new CallRestAPI();
		HttpEntity<Object> requestCollect = new HttpEntity<>(reqSetScriptDTO, headers);

		callRestAPI.postRestAPI(crasServer+apiURL,
				requestCollect,
				Object.class,
				apiURL);
		log.info(step + " step " + position + "script upload complete to Cras Server.");
	}

	public void callAddJobAddressGroupBook(ResRemoteJobDetailAddDTO resRemoteJobDetailDTO, RemoteJobVo setRemoteJobVo) {
		if(resRemoteJobDetailDTO.getErrorSummary()!=null) {
			addJobAddressGroupBook(resRemoteJobDetailDTO.getErrorSummary().getEmailBookIds(),
					setRemoteJobVo.getMailContextVoErrorSummary().getId(), 1);
			addJobAddressGroupBook(resRemoteJobDetailDTO.getErrorSummary().getGroupBookIds(),
					setRemoteJobVo.getMailContextVoErrorSummary().getId(), 2);
		}
		if(resRemoteJobDetailDTO.getCrasData()!=null) {
			addJobAddressGroupBook(resRemoteJobDetailDTO.getCrasData().getEmailBookIds(),
					setRemoteJobVo.getMailContextVoCrasData().getId(), 1);
			addJobAddressGroupBook(resRemoteJobDetailDTO.getCrasData().getGroupBookIds(),
					setRemoteJobVo.getMailContextVoCrasData().getId(), 2);
		}
		if(resRemoteJobDetailDTO.getMpaVersion()!=null) {
			addJobAddressGroupBook(resRemoteJobDetailDTO.getMpaVersion().getEmailBookIds(),
					setRemoteJobVo.getMailContextVoVersion().getId(), 1);
			addJobAddressGroupBook(resRemoteJobDetailDTO.getMpaVersion().getGroupBookIds(),
					setRemoteJobVo.getMailContextVoVersion().getId(), 2);
		}
		if(resRemoteJobDetailDTO.getErrorNotice()!=null) {
			addJobAddressGroupBook(resRemoteJobDetailDTO.getErrorNotice().getEmailBookIds(),
					setRemoteJobVo.getMailContextVoErrorNotice().getId(), 1);
			addJobAddressGroupBook(resRemoteJobDetailDTO.getErrorNotice().getGroupBookIds(),
					setRemoteJobVo.getMailContextVoErrorNotice().getId(), 2);
		}
	}

	public void callAddJobAddressGroupBookLocal(ReqLocalJobAddDTO reqLocalJobAddDTO, LocalJobVo localJobResult) {
		if(reqLocalJobAddDTO.getErrorNotice()!=null) {
			addJobAddressGroupBook(reqLocalJobAddDTO.getErrorNotice().getEmailBookIds(),
					localJobResult.getMailContextVoErrorNotice().getId(), 1);
			addJobAddressGroupBook(reqLocalJobAddDTO.getErrorNotice().getGroupBookIds(),
					localJobResult.getMailContextVoErrorNotice().getId(), 2);
		}
	}

	public void addJobAddressGroupBook(long [] ids, int mail_context_id, int flag) {	// flag 1 : address / 2 : group
		if(ids.length != 0) {
			for (long id : ids) {
				switch (flag) {
					case 1:
						JobAddressBookEntity jobAddressBookEntity = new JobAddressBookEntity()
								.setAddress(addressBookRepository.findById(id).get())
								.setMailContext(mailContextRepository.findById(mail_context_id).get());
						jobAddressBookRepository.save(jobAddressBookEntity);
						break;
					case 2:
						JobGroupBookEntity jobGroupBookEntity = new JobGroupBookEntity()
								.setGroup(groupBookRepository.findById(id).get())
								.setMailContext(mailContextRepository.findById(mail_context_id).get());
						jobGroupBookRepository.save(jobGroupBookEntity);
						break;
				}
			}
		}
	}

	public void callAddCrasDataJudgeRules(ResRemoteJobDetailAddDTO resRemoteJobDetailDTO, int mail_context_id) {
		for(int selectJudgeRule : resRemoteJobDetailDTO.getCrasData().getSelectJudgeRules()) {
			CrasItemMasterVo crasItemMasterVo = crasItemMasterRepository.findById(selectJudgeRule)
					.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

			CrasItemMasterJobVo crasItemMasterJobVo = new CrasItemMasterJobVo()
					.setCrasDataSiteId(crasItemMasterVo.getSiteId())
					.setCrasRuleId(selectJudgeRule)
					.setCrasItemMasterVo(crasItemMasterVo)
					.setMailContext(mailContextRepository.findById(mail_context_id).get());
			crasItemMasterJobRepository.save(crasItemMasterJobVo);
		}
	}

	public void deleteRemoteJob(int remoteJobId) {
		try {
			RemoteJobVo delJob = remoteJobRepository.findById(remoteJobId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
			remoteJobRepository.delete(delJob);
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	public ResJobIdDTO editRemoteJob(int remoteJobId, ResRemoteJobDetailAddDTO resRemoteJobDetailDTO) {
		try {
			RemoteJobVo remoteJobVo = remoteJobRepository.findById(remoteJobId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

			RemoteJobVo reqRemoteJobVo = RemoteJobVoResRemoteJobDtoMapper.INSTANCE.mapResRemoteJobEditDtoToVo(remoteJobVo, resRemoteJobDetailDTO);
			RemoteJobVo setRemoteJobVo = remoteJobRepository.save(reqRemoteJobVo);
			ResJobIdDTO resJobIdDTO = new ResJobIdDTO()
					.setJobId(setRemoteJobVo.getJobId());

			if(!ObjectUtils.isEmpty(setRemoteJobVo) && setRemoteJobVo.getJobId() > 0) {
				callAddJobAddressGroupBook(resRemoteJobDetailDTO, setRemoteJobVo);

				if(resRemoteJobDetailDTO.getIsCrasData())
					callAddCrasDataJudgeRules(resRemoteJobDetailDTO, setRemoteJobVo.getMailContextVoCrasData().getId());
			}

			Optional<SiteVo> siteVo = siteRepositoryService.findById(setRemoteJobVo.getSiteId());
			checkScript(resRemoteJobDetailDTO, setRemoteJobVo, siteVo);

			return resJobIdDTO;
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	public ResRemoteJobStatusDTO getStatusRemoteJob(int remoteJobId) {
		try {
			RemoteJobVo getJobInfo = remoteJobRepository.findById(remoteJobId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
			ResRemoteJobStatusDTO resRemoteJobStatusDTO = new ResRemoteJobStatusDTO()
				.setStop(getJobInfo.isStop());

			return resRemoteJobStatusDTO;
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	public ResJobIdDTO runStopRemoteJob(int remoteJobId, String flag) {
		try {
			RemoteJobVo getJobInfo = remoteJobRepository.findById(remoteJobId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
			ResJobIdDTO resJobIdDTO = new ResJobIdDTO();

			if (flag.equals("run")) {
				getJobInfo.setStop(Boolean.FALSE);

				List<SchedulerVo> jobTimes = new ArrayList<>();
				jobTimes.addAll(setSchedule(getJobInfo, ReqURLController.JOB_STEP_COLLECT, getJobInfo.getMailContextVoCollect()));
				if(getJobInfo.getMailContextVoConvert()!=null)
					jobTimes.addAll(setSchedule(getJobInfo, ReqURLController.JOB_STEP_CONVERT, getJobInfo.getMailContextVoConvert()));
				if(getJobInfo.getMailContextVoDBPurge()!=null)
					jobTimes.addAll(setSchedule(getJobInfo, ReqURLController.JOB_STEP_DBPURGE, getJobInfo.getMailContextVoDBPurge()));
				js.startScheduler(jobTimes);

				List<SchedulerVo> notiTimes = new ArrayList<>();
				if(getJobInfo.getMailContextVoErrorSummary()!=null)
					notiTimes.addAll(setSchedule(getJobInfo, ReqURLController.JOB_STEP_ERROR, getJobInfo.getMailContextVoErrorSummary()));
				if(getJobInfo.getMailContextVoCrasData()!=null)
					notiTimes.addAll(setSchedule(getJobInfo, ReqURLController.JOB_STEP_CRAS, getJobInfo.getMailContextVoCrasData()));
				if(getJobInfo.getMailContextVoVersion()!=null)
					notiTimes.addAll(setSchedule(getJobInfo, ReqURLController.JOB_STEP_VERSION, getJobInfo.getMailContextVoVersion()));
				ns.startScheduler(notiTimes);
			} else if (flag.equals("stop")) {
				getJobInfo.setStop(Boolean.TRUE);
				js.stopScheduler(getJobInfo.getJobId(), ReqURLController.JOB_THREAD_JOB);
				ns.stopScheduler(getJobInfo.getJobId(), ReqURLController.JOB_THREAD_NOTI);

				List<SchedulerVo> schedulerVo = schedulerRepository.findByJobId(remoteJobId);
				schedulerRepository.deleteAll(schedulerVo);
			}
			resJobIdDTO.setJobId(remoteJobRepository.save(getJobInfo).getJobId());
			return resJobIdDTO;
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	public List<SchedulerVo> setSchedule(RemoteJobVo getJobInfo, String step, MailContextVo mailContextVo) {
		List<SchedulerVo> settingTimes = new ArrayList<>();

		switch (mailContextVo.getMode()) {
			case (ReqURLController.JOB_RUN_CYCLE) :
				String cycleTime = null;
				String currentTime = LocalDateTime.now().plusSeconds(ReqURLController.JOB_PLUS_SECOND).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
				String currentTimeDay = currentTime.split(" ")[0];
				String currentTimeTime = currentTime.split(" ")[1];
				switch (mailContextVo.getCycle()) {
					case (ReqURLController.JOB_CYCLE_DAY):
						cycleTime = String.format("%s %s %s %s/%s * *",
								currentTimeTime.split(":")[2], currentTimeTime.split(":")[1], currentTimeTime.split(":")[0],
								currentTimeDay.split("-")[2], mailContextVo.getPeriod());
						break;
					case (ReqURLController.JOB_CYCLE_HOUR):
						cycleTime = String.format("%s %s %s/%s * * *",
								currentTimeTime.split(":")[2], currentTimeTime.split(":")[1], currentTimeTime.split(":")[0],
								mailContextVo.getPeriod());
						break;
					case (ReqURLController.JOB_CYCLE_MINUTE):
						cycleTime = String.format("%s %s/%s * * * *",
								currentTimeTime.split(":")[2], currentTimeTime.split(":")[1],
								mailContextVo.getPeriod());
						break;
				}
				SchedulerVo settingCycle = new SchedulerVo()
						.setJobId(getJobInfo.getJobId())
						.setType(ReqURLController.JOB_TYPE_REMOTEJOB)
						.setStep(step)
						.setCron(cycleTime)
						.setSettingTime(currentTime)
						.setCycle(mailContextVo.getCycle())
						.setPeriod(mailContextVo.getPeriod())
						.setRemoteJobVo(getJobInfo);
				settingTimes.add(settingCycle);
				break;
			case (ReqURLController.JOB_RUN_TIME) :
				for(String time : mailContextVo.getTime()) {
					String jobTime = String.format("00 %s %s * * *", time.split(":")[1], time.split(":")[0]);
					SchedulerVo settingTime = new SchedulerVo()
							.setJobId(getJobInfo.getJobId())
							.setType(ReqURLController.JOB_TYPE_REMOTEJOB)
							.setStep(step)
							.setCron(jobTime)
							.setCycle("none")
							.setSettingTime(time.split(":")[0] + ":" + time.split(":")[1] + ":00")
							.setRemoteJobVo(getJobInfo);
					settingTimes.add(settingTime);
				}
				break;
		}
		schedulerRepository.saveAll(settingTimes);
		return settingTimes;
	}

	public ResJobIdDTO addLocalJob(ReqLocalJobAddDTO reqLocalJobAddDTO) {
		try {
			int[] fileIndex = reqLocalJobAddDTO.getFileIndices();
			List<String> fileOriginalName = new ArrayList<>();
			for (int fileIdx : fileIndex) {
				LocalJobFileIdVo localJobFileIdVo = localJobFileIdVoRepository.findById(fileIdx)
					.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
				fileOriginalName.add(localJobFileIdVo.getFileOriginalName());
			}

			LocalJobVo localJobVo = LocalJobVoResLocalJobDtoMapper.INSTANCE.mapResLocalJobDtoToVo(reqLocalJobAddDTO, fileOriginalName);
			LocalJobVo localJobResult = localJobRepository.save(localJobVo);

			if(!ObjectUtils.isEmpty(localJobResult) && localJobResult.getJobId() > 0)
				callAddJobAddressGroupBookLocal(reqLocalJobAddDTO, localJobResult);

			js.localJobExcute(localJobResult, ReqURLController.JOB_STEP_CONVERT_LOCAL);

			ResJobIdDTO resJobIdDTO = new ResJobIdDTO()
				.setJobId(localJobResult.getJobId());
			return resJobIdDTO;
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	public void deleteLocalJob(int localJobId) {
		try {
			LocalJobVo delJob = localJobRepository.findById(localJobId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
			localJobRepository.delete(delJob);
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	public List<ResPlanDTO> getPlanList(Integer siteId) throws Exception {
		try {
			SiteVo getSite = siteRepositoryService.findById(siteId)
					.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

			String GET_PLAN_LIST_URL = String.format(ReqURLController.API_GET_PLAN_LIST_FROM_CRAS,
					getSite.getCrasAddress(),
					getSite.getCrasPort(),
					getSite.getRssAddress(),
					getSite.getRssPort(),
					getSite.getRssUserName(),
					getSite.getRssPassword());

			CallRestAPI callRestAPI = new CallRestAPI();
			ResponseEntity<?> response = callRestAPI.getRestAPI(GET_PLAN_LIST_URL, ResRCPlanDTO[].class);
			List<ResRCPlanDTO> resRCPlanDTOList = Arrays.asList((ResRCPlanDTO[]) response.getBody());
			List<ResPlanDTO> newPlanList = new ArrayList<>();

			for (ResRCPlanDTO plan : resRCPlanDTOList) {
				ResPlanDTO data = new ResPlanDTO()
						.setPlanId(plan.getPlanId())
						.setPlanName(plan.getPlanName())
						.setPlanType(plan.getPlanType())
						.setMachineNames(plan.getMachineNames())
						.setStatus(plan.getStatus())
						.setDescription(plan.getDescription())
						.setMeasure(plan.getMeasure())
						.setError(plan.getError())
						.setDetail(plan.getDetailedStatus());

				if (plan.getPlanType().equals(PLAN_TYPE_FTP)) {
					data.setTargetNames(plan.getCategoryNames());
				} else if (plan.getPlanType().equals(PLAN_TYPE_VFTP_COMPAT)) {
					List<String> newTarget = new ArrayList<>();
					for (String command : plan.getCommands()) {
						if (command.equals("none")) {
							newTarget.add(String.format("get %s_%s.log", plan.getFrom(), plan.getTo()));
						} else {
							newTarget.add(String.format("get " + command + ".log", plan.getFrom(), plan.getTo()));
						}
					}
				} else if (plan.getPlanType().equals(PLAN_TYPE_VFTP_SSS)) {
					List<String> newTarget = new ArrayList<>();
					for (String command : plan.getCommands()) {
						newTarget.add(String.format("cd " + command, plan.getFrom(), plan.getTo()));
					}
				}
				newPlanList.add(data);
			}
			return newPlanList;
		} catch (ResponseStatusException e) {
			log.error(e.getMessage());
			throw e;
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	public void runManualExcute(int remoteJobId, String step) {
		try {
			RemoteJobVo remoteJobVo = remoteJobRepository.findByJobId(remoteJobId)
					.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

			if(remoteJobVo.isStop() == true)
				throw new ResponseStatusException(HttpStatus.NOT_FOUND);

			ResRemoteJobStatusDTO resRemoteJobStatusDTO = getStatusRemoteJob(remoteJobId);
			String errorMsgNoSetting = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
					+ " : " + ReqURLController.JOB_NOT_SETTING;
			if(resRemoteJobStatusDTO.getStop() == true)
				throw new ResponseStatusException(HttpStatus.NOT_FOUND);
			else {
				switch (step) {
					case ReqURLController.JOB_STEP_COLLECT:
						js.manualExcute(remoteJobVo, step);
						break;
					case ReqURLController.JOB_STEP_CONVERT:
						if(remoteJobVo.getMailContextVoConvert()!=null)
							js.manualExcute(remoteJobVo, step);
						else {
							remoteJobRepository.updateConvertStatus(remoteJobVo.getJobId(), ReqURLController.JOB_STATUS_FAILURE);
							remoteJobRepository.updateConvertError(remoteJobVo.getJobId(), errorMsgNoSetting);
						}
						break;
					case ReqURLController.JOB_STEP_ERROR:
						if(remoteJobVo.getMailContextVoErrorSummary()!=null)
							ns.manualExcute(remoteJobVo, step);
						else {
							remoteJobRepository.updateErrorSummaryStatus(remoteJobVo.getJobId(), ReqURLController.JOB_STATUS_FAILURE);
							remoteJobRepository.updateErrorSummaryError(remoteJobVo.getJobId(), errorMsgNoSetting);
						}
						break;
					case ReqURLController.JOB_STEP_CRAS:
						if(remoteJobVo.getMailContextVoCrasData()!=null)
							ns.manualExcute(remoteJobVo, step);
						else {
							remoteJobRepository.updateCrasStatus(remoteJobVo.getJobId(), ReqURLController.JOB_STATUS_FAILURE);
							remoteJobRepository.updateCrasError(remoteJobVo.getJobId(), errorMsgNoSetting);
						}
						break;
					case ReqURLController.JOB_STEP_VERSION:
						if(remoteJobVo.getMailContextVoVersion()!=null)
							ns.manualExcute(remoteJobVo, step);
						else {
							remoteJobRepository.updateVersionCheckStatus(remoteJobVo.getJobId(), ReqURLController.JOB_STATUS_FAILURE);
							remoteJobRepository.updateVersionCheckError(remoteJobVo.getJobId(), errorMsgNoSetting);
						}
						break;
					case ReqURLController.JOB_STEP_DBPURGE:
						if(remoteJobVo.getMailContextVoDBPurge()!=null)
							js.manualExcute(remoteJobVo, step);
						else {
							remoteJobRepository.updateDbPurgeStatus(remoteJobVo.getJobId(), ReqURLController.JOB_STATUS_FAILURE);
							remoteJobRepository.updateDbPurgeError(remoteJobVo.getJobId(), errorMsgNoSetting);
						}
						break;
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

	public List<ResTimeLineDTO> getTimeLine(int remoteJobId) {
		try {
			ResRemoteJobStatusDTO resRemoteJobStatusDTO = getStatusRemoteJob(remoteJobId);
			if(resRemoteJobStatusDTO.getStop() == true)
				throw new ResponseStatusException(HttpStatus.NOT_FOUND);

			List<HistoryVo> historyVoList = historyRepository.findByJobIdAndType(remoteJobId,
					ReqURLController.JOB_TYPE_REMOTEJOB, Sort.by(Sort.Direction.ASC, "runningTime"));
			List<ResTimeLineDTO> resTimeLineDTOList = new ArrayList<>();
			int newJobNumber = 0;

			if(historyVoList.size()>=ReqURLController.JOB_TIMELINE) {
				for(int i=historyVoList.size()-ReqURLController.JOB_TIMELINE; i<historyVoList.size(); i++) {
					ResTimeLineDTO resTimeLineDTO = HistoryVoResTimeLineDtoMapper.INSTANCE.mapHistoryVotoDto(historyVoList.get(i));
					resTimeLineDTOList.add(resTimeLineDTO);
				}
				newJobNumber = ReqURLController.JOB_TIMELINE;
			}
			else {
				for(HistoryVo historyVo : historyVoList) {
					ResTimeLineDTO resTimeLineDTO = HistoryVoResTimeLineDtoMapper.INSTANCE.mapHistoryVotoDto(historyVo);
					resTimeLineDTOList.add(resTimeLineDTO);
				}
				newJobNumber = (ReqURLController.JOB_TIMELINE*2) - historyVoList.size();
			}

			List<SchedulerVo> schedulerVoList = schedulerRepository.findByJobId(remoteJobId);
			List<ResTimeLineDTO> makeTimeLine = new ArrayList<>();

			if(resTimeLineDTOList.size()>0)
				makeTimeLine = makeTimeLine(schedulerVoList, newJobNumber, resTimeLineDTOList);
			else
				makeTimeLine = makeTimeLine(schedulerVoList, newJobNumber, resTimeLineDTOList);

			Collections.sort(makeTimeLine, new Comparator<ResTimeLineDTO>() {
				@Override
				public int compare(ResTimeLineDTO o1, ResTimeLineDTO o2) {
					return o1.getStart().compareTo(o2.getStart());
				}
			});

			for (int i = 0; i < newJobNumber; i++)
				resTimeLineDTOList.add(makeTimeLine.get(i));

			return resTimeLineDTOList;
		} catch (ResponseStatusException e) {
			log.error(e.getMessage());
			throw e;
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	public List<ResTimeLineDTO> makeTimeLine(List<SchedulerVo> schedulerVoList, int newJobNumber, List<ResTimeLineDTO> timeLineDTOList) {
		List<ResTimeLineDTO> resTimeLineDTOList = new ArrayList<>();

		LocalDateTime currentDay = LocalDateTime.now();

		for(SchedulerVo schedulerVo : schedulerVoList) {
			String lastStartTime = "empty";

			for (int num = timeLineDTOList.size()-1; num >= 0; num--) {
				if (timeLineDTOList.get(num).getName().equals(schedulerVo.getStep())) {
					lastStartTime = timeLineDTOList.get(num).getStart();
					break;
				}
			}

			if (!schedulerVo.getCycle().equals("none")) {
				LocalDateTime settingTime = LocalDateTime.parse(schedulerVo.getSettingTime(),
						DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

				if(lastStartTime.equals("empty"))
					lastStartTime = settingTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

				for (int i = 0; i < newJobNumber*2; i++) {
					String setStartCycle = null;

					if (i == 0 && settingTime.isAfter(LocalDateTime.parse(
							String.format("%s", lastStartTime), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
							&& settingTime.equals(lastStartTime)) {
						ResTimeLineDTO resTimeLineDTONone = new ResTimeLineDTO()
								.setName(schedulerVo.getStep())
								.setStatus(ReqURLController.JOB_STATUS_NOTBUILD)
								.setIsManual(false)
								.setStart(settingTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
						resTimeLineDTOList.add(resTimeLineDTONone);
					}

					switch (schedulerVo.getCycle()) {
						case ReqURLController.JOB_CYCLE_DAY:
							setStartCycle = settingTime.plusDays(
									schedulerVo.getPeriod()*i).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
							break;
						case ReqURLController.JOB_CYCLE_HOUR:
							setStartCycle = settingTime.plusHours(
									schedulerVo.getPeriod()*i).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
							break;
						case ReqURLController.JOB_CYCLE_MINUTE:
							setStartCycle = settingTime.plusMinutes(
									schedulerVo.getPeriod()*i).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
							break;
					}

					if(!setStartCycle.equals(settingTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))) {
						ResTimeLineDTO resTimeLineDTONone = new ResTimeLineDTO()
								.setName(schedulerVo.getStep())
								.setStatus(ReqURLController.JOB_STATUS_NOTBUILD)
								.setIsManual(false)
								.setStart(setStartCycle);
						resTimeLineDTOList.add(resTimeLineDTONone);
					}
				}
			}
			else {
				int plusDay = 0;
				Boolean currentIsAfter = false;

				LocalDateTime startDay;

				if(!lastStartTime.equals("empty"))
					startDay = LocalDateTime.parse(lastStartTime, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
				else
					startDay = LocalDateTime.parse(String.format("%s %s", currentDay.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
							schedulerVo.getSettingTime()),
							DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

				if (currentDay.isAfter(LocalDateTime.parse(String.format("%s %s", currentDay.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
						schedulerVo.getSettingTime()),
						DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))))
					currentIsAfter = true;

				for (int i = 0; i < newJobNumber; i++) {
					String setStartNone;

					if (currentIsAfter)
						setStartNone = String.format("%s %s",
								startDay.plusDays(plusDay + 1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd")), schedulerVo.getSettingTime());
					else
						setStartNone = String.format("%s %s",
								startDay.plusDays(plusDay).format(DateTimeFormatter.ofPattern("yyyy-MM-dd")), schedulerVo.getSettingTime());

					ResTimeLineDTO resTimeLineDTONone = new ResTimeLineDTO()
							.setName(schedulerVo.getStep())
							.setStatus(ReqURLController.JOB_STATUS_NOTBUILD)
							.setIsManual(false)
							.setStart(setStartNone);
					resTimeLineDTOList.add(resTimeLineDTONone);
					plusDay++;
				}
			}
		}

		return resTimeLineDTOList;
	}
}
