package jp.co.canon.rss.logmanager.vo.address;

import org.hibernate.HibernateException;
import org.hibernate.MappingException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.Configurable;
import org.hibernate.id.IdentifierGenerator;
import org.hibernate.internal.util.config.ConfigurationHelper;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.type.Type;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

public class AddressIdGenerator implements IdentifierGenerator, Configurable {

    private String target;
    private final long offset = 100000000;
    private static Long lastAddressId = null;
    private static Long lastGroupId = null;

    @Override
    public void configure(Type type, Properties params, ServiceRegistry serviceRegistry) throws MappingException {
        target = ConfigurationHelper.getString("target", params);
    }

    @Override
    public Serializable generate(SharedSessionContractImplementor session, Object object) throws HibernateException {

        boolean isGroup = target.equals("group");
        Connection connect = session.connection();
        try {
            Statement state = connect.createStatement();
            if(isGroup) {
                if(lastGroupId==null) {
                    ResultSet rs = state.executeQuery("select max(gid) from log_manager.group_book");
                    if(rs.next()) {
                        long id = rs.getLong(1);
                        if(id<offset) {
                            id += offset;
                        }
                        lastGroupId = new Long(id);
                    }
                }
                lastGroupId = new Long(lastGroupId.longValue()+1);
                return lastGroupId;
            } else {
                if(lastAddressId==null) {
                    ResultSet rs = state.executeQuery("select max(id) from log_manager.address_book");
                    if(rs.next()) {
                        long id = rs.getLong(1);
                        lastAddressId = new Long(id);
                    }
                }
                lastAddressId = new Long(lastAddressId.longValue()+1);
                return lastAddressId;
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return new Long(isGroup?offset:0);
    }
}
