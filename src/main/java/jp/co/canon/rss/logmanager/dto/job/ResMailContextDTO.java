package jp.co.canon.rss.logmanager.dto.job;

import jp.co.canon.rss.logmanager.dto.address.AddressBookDTO;
import jp.co.canon.rss.logmanager.dto.rulecrasdata.ResCrasDataDTO;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.util.List;

@Getter
@Setter
@ToString
@Accessors(chain = true)
public class ResMailContextDTO {
    private String [] customEmails;
    private List<AddressBookDTO> emailBook;
    private List<AddressBookDTO> groupBook;
    private List<ResCrasDataDTO> selectJudgeRules;

    private String subject;
    private String body;
    private int before;

    private String mode;
    private String [] time;
    private String cycle;
    private int period;
    private ResMailContextScriptDTO script;
}
