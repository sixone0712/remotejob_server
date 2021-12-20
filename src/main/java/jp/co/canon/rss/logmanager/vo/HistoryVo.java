package jp.co.canon.rss.logmanager.vo;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import javax.persistence.*;

@Entity
@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class)
@Table(name = "history", schema = "log_manager")
@Getter
@Setter
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
public class HistoryVo {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false)
	private Integer id;
	@Column(name = "job_id", nullable = false)
	private int jobId;
	@Column(name = "request_id")
	private String requestId;
	@Column(name = "request_id_cras")
	private String requestIdCras;

	@Column(name = "type", nullable = false)
	private String type;
	@Column(name = "manual")
	private Boolean isManual;
	@Column(name = "step", nullable = false)
	private String step;

	@Column(name = "status", nullable = false)
	private String status;

	@Column(name = "setting_time", nullable = false)
	private String settingTime;
	@Column(name = "running_time")
	private String runningTime;
	@Column(name = "ending_time")
	private String endingTime;

	@Column(name = "history_name")
	private String historyName;

	@ManyToOne
	@JoinColumn(name = "remote_job_vo_fk")
	private RemoteJobVo remoteJobVo;
}


