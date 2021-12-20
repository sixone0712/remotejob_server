package jp.co.canon.rss.logmanager.vo;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.vladmihalcea.hibernate.type.array.IntArrayType;
import com.vladmihalcea.hibernate.type.array.StringArrayType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@TypeDef(name = "int-array", typeClass = IntArrayType.class)
@TypeDef(name = "string-array", typeClass = StringArrayType.class)
@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class)
@Table(name = "remotejob", schema = "log_manager")
@Getter
@Setter
@Accessors(chain = true)
@NoArgsConstructor
public class RemoteJobVo {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false)
	private Integer jobId;
	@Column(name = "job_name", nullable = false)
	private String jobName;
	@Column(name = "site_id", nullable = false)
	private int siteId;

	@Column(name = "collect_status", nullable = false)
	private String collectStatus;
	@Column(name = "convert_status", nullable = false)
	private String convertStatus;
	@Column(name = "error_summary_status", nullable = false)
	private String errorSummaryStatus;
	@Column(name = "cras_status", nullable = false)
	private String crasDataStatus;
	@Column(name = "version_check_status", nullable = false)
	private String mpaVersionStatus;
	@Column(name = "db_purge_status", nullable = false)
	private String dbPurgeStatus;

	@Column(name = "collect_error", columnDefinition = "TEXT")
	private String collectError;
	@Column(name = "convert_error", columnDefinition = "TEXT")
	private String convertError;
	@Column(name = "error_summary_error", columnDefinition = "TEXT")
	private String errorSummaryError;
	@Column(name = "cras_error", columnDefinition = "TEXT")
	private String crasDataError;
	@Column(name = "version_check_error", columnDefinition = "TEXT")
	private String mpaVersionError;
	@Column(name = "db_purge_error", columnDefinition = "TEXT")
	private String dbPurgeError;

	@Column(name = "is_convert", nullable = false, columnDefinition = "boolean default false")
	private Boolean isConvert;
	@Column(name = "is_error_summary", nullable = false, columnDefinition = "boolean default false")
	private Boolean isErrorSummary;
	@Column(name = "is_cras_data", nullable = false, columnDefinition = "boolean default false")
	private Boolean isCrasData;
	@Column(name = "is_mpa_version", nullable = false, columnDefinition = "boolean default false")
	private Boolean isMpaVersion;
	@Column(name = "is_db_purge", nullable = false, columnDefinition = "boolean default false")
	private Boolean isDbPurge;
	@Column(name = "is_error_notice", nullable = false, columnDefinition = "boolean default false")
	private Boolean isErrorNotice;

	@Column(name = "created", nullable = false)
	private LocalDateTime created;
	@Column(name = "last_action")
	private LocalDateTime lastAction;
	@Column(name = "owner")
	private int owner;

	@Type(type = "int-array")
	@Column(name = "plan_id", columnDefinition = "integer []")
	private int[] planIds;
	@Column(name = "stop", nullable = false)
	private boolean stop;

	@ManyToOne
	@JoinColumn(name = "site_vo_list_fk")
	private SiteVo siteVoList;

	@OneToOne(cascade = {CascadeType.MERGE, CascadeType.PERSIST}, fetch = FetchType.EAGER, orphanRemoval = true)
	@JoinColumn(name = "collect_fk", unique = true)
	private MailContextVo mailContextVoCollect;

	@OneToOne(cascade = {CascadeType.MERGE, CascadeType.PERSIST}, fetch = FetchType.EAGER, orphanRemoval = true)
	@JoinColumn(name = "convert_fk", unique = true)
	private MailContextVo mailContextVoConvert;

	@OneToOne(cascade = {CascadeType.MERGE, CascadeType.PERSIST}, fetch = FetchType.EAGER, orphanRemoval = true)
	@JoinColumn(name = "error_summary_fk", unique = true)
	private MailContextVo mailContextVoErrorSummary;

	@OneToOne(cascade = {CascadeType.MERGE, CascadeType.PERSIST}, fetch = FetchType.EAGER, orphanRemoval = true)
	@JoinColumn(name = "cras_data_fk", unique = true)
	private MailContextVo mailContextVoCrasData;

	@OneToOne(cascade = {CascadeType.MERGE, CascadeType.PERSIST}, fetch = FetchType.EAGER, orphanRemoval = true)
	@JoinColumn(name = "version_fk", unique = true)
	private MailContextVo mailContextVoVersion;

	@OneToOne(cascade = {CascadeType.MERGE, CascadeType.PERSIST}, fetch = FetchType.EAGER, orphanRemoval = true)
	@JoinColumn(name = "db_purge_fk", unique = true)
	private MailContextVo mailContextVoDBPurge;

	@OneToOne(cascade = {CascadeType.MERGE, CascadeType.PERSIST}, fetch = FetchType.EAGER, orphanRemoval = true)
	@JoinColumn(name = "error_notice_fk", unique = true)
	private MailContextVo mailContextVoErrorNotice;

	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "remoteJobVo")
	private List<SchedulerVo> schedulerVoList = new ArrayList<>();

	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "remoteJobVo")
	private List<HistoryVo> historyVoList = new ArrayList<>();
}
