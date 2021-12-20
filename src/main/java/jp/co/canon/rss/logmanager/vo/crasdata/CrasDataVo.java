package jp.co.canon.rss.logmanager.vo.crasdata;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.*;
import lombok.experimental.Accessors;

import javax.persistence.*;

@Entity
@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class)
@Table(name = "cras_data", schema = "cras")
@Getter
@Setter
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class CrasDataVo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer itemId;
    @Column(name = "site_id", nullable = false)
    private int siteId;
    @Column(name = "user_name", nullable = false)
    private String userName;
    @Column(name = "fab_name", nullable = false)
    private String fabName;
    @Column(name = "item_name", nullable = false)
    private String itemName;
    @Column(name = "target_table", nullable = false)
    private String targetTable;
    @Column(name = "target_col1", nullable = false)
    private String targetCol1;
    @Column(name = "target_col2")
    private String targetCol2;
    @Column(name = "operations", nullable = false)
    private String operations;
    @Column(name = "calc_period_unit", nullable = false)
    private String calPeriodUnit;
    @Column(name = "coef", nullable = false)
    private Double coef;
    @Column(name = "group_col")
    private String groupCol;
    @Column(name = "where_str")
    private String manualWhere;
    @Column(name = "col_type", nullable = false)
    private String calResultType;
    @Column(name = "comments")
    private String comments;
    @Column(name = "enable", nullable = false)
    private Boolean enable;

    @ManyToOne
    @JoinColumn(name = "cras_data_site_id_fk")
    private CrasDataSiteVo crasDataSiteVo;
}
