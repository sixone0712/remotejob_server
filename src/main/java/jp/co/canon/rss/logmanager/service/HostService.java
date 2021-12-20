package jp.co.canon.rss.logmanager.service;

import jp.co.canon.rss.logmanager.dto.host.ReqSettingsDBInfo;
import jp.co.canon.rss.logmanager.dto.host.ResHostInfoDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

@Slf4j
@Service()
public class HostService {
    private ReqSettingsDBInfo applicationYml;

    public HostService(ReqSettingsDBInfo applicationYml) {
        this.applicationYml = applicationYml;
    }

    public ResHostInfoDTO getHostInfo() {
        /*String hostname = getSettingServerIP("hostname -I");
            String [] ipAddress = hostname.split(" ");
            applicationYml.setAddress(ipAddress[0]);*/

        ResHostInfoDTO resHostInfoDTO = new ResHostInfoDTO()
                .setAddress(applicationYml.getAddress())
                .setPort(Integer.parseInt(applicationYml.getPort()))
                .setUser(applicationYml.getUser())
                .setPassword(applicationYml.getPassword());

        return resHostInfoDTO;
    }

    public static String getSettingServerIP(String command){
        String result = "";
        Runtime rt = Runtime.getRuntime();
        Process p = null;
        StringBuffer sb = new StringBuffer();
        try{
            p=rt.exec(command);
            BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String cl = null;
            while((cl=in.readLine())!=null){
                sb.append(cl);
            }
            result = sb.toString();
            in.close();
        }catch(IOException e){
            e.printStackTrace();
            return "";
        }
        return result;
    }
}
