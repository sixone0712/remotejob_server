package jp.co.canon.rss.logmanager.vo;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.vladmihalcea.hibernate.type.array.IntArrayType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class)
@Table(name = "scheduler", schema = "log_manager")
@Getter
@Setter
@Accessors(chain = true)
@NoArgsConstructor
public class SchedulerVo {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false)
	private Integer id;
	@Column(name = "job_id", nullable = false)
	private int jobId;

	@Column(name = "type", nullable = false)
	private String type;
	@Column(name = "step", nullable = false)
	private String step;

	@Column(name = "cron", nullable = false)
	private String cron;
	@Column(name = "setting_time", nullable = false)
	private String settingTime;
	@Column(name = "cycle")
	private String cycle;
	@Column(name = "period")
	private int period;

	@ManyToOne
	@JoinColumn(name = "remote_job_vo_fk")
	private RemoteJobVo remoteJobVo;

	@ManyToOne
	@JoinColumn(name = "local_job_vo_fk")
	private LocalJobVo localJobVo;
}
