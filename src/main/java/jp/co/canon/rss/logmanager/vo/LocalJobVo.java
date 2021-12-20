package jp.co.canon.rss.logmanager.vo;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.vladmihalcea.hibernate.type.array.IntArrayType;
import com.vladmihalcea.hibernate.type.array.StringArrayType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@TypeDef(name = "int-array", typeClass = IntArrayType.class)
@TypeDef(name = "string-array", typeClass = StringArrayType.class)
@EntityListeners(AuditingEntityListener.class)
@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class)
@Table(name = "localjob", schema = "log_manager")
@Getter
@Setter
@ToString
@Accessors(chain = true)
@NoArgsConstructor
public class LocalJobVo  {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer jobId;
    @Column(name = "site_id")
    private int siteId;
    @Column(name = "collect_status")
    private String collectStatus;
    @Column(name = "collect_error")
    private String collectError;
    @Type(type = "int-array")
    @Column(name = "file_indices", columnDefinition = "integer []")
    private int [] fileIndices;
    @Type(type = "string-array")
    @Column(name = "file_original_names", columnDefinition = "text[]")
    private String [] fileOriginalNames;
    @Column(name = "registered_date")
    private LocalDateTime registeredDate;
    @Column(name = "stop", nullable = false)
    private boolean stop;

    @Column(name = "is_error_notice", nullable = false, columnDefinition = "boolean default false")
    private Boolean isErrorNotice;

    @ManyToOne
    @JoinColumn(name = "site_vo_list_local_fk")
    private SiteVo siteVoListLocal;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "localJobVo")
    private List<SchedulerVo> schedulerVoList = new ArrayList<>();

    @OneToOne(cascade = {CascadeType.MERGE, CascadeType.PERSIST}, fetch = FetchType.EAGER, orphanRemoval = true)
    @JoinColumn(name = "error_notice_fk", unique = true)
    private MailContextVo mailContextVoErrorNotice;
}
