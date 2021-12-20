package jp.co.canon.rss.logmanager.vo.crasdata;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.*;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class)
@Table(name = "cras_abnormal_data", schema = "cras")
@Getter
@Setter
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class CrasAbnormalData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;
    @Column(name = "user_name", nullable = false)
    private String userName;
    @Column(name = "fab_name", nullable = false)
    private String fabName;
    @Column(name = "date", nullable = false)
    private LocalDateTime date;
    @Column(name = "toolid", nullable = false)
    private String toolid;
    @Column(name = "cras_id", nullable = false)
    private int crasId;
    @Column(name = "value", nullable = false)
    private BigDecimal value;
}
