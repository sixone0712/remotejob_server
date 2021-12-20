package jp.co.canon.rss.logmanager.service;

import jp.co.canon.rss.logmanager.service.exception.ServiceException;
import jp.co.canon.rss.logmanager.service.exception.ServiceInvalidParameterException;
import jp.co.canon.rss.logmanager.vo.address.AddressBookEntity;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.validation.constraints.AssertFalse;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class AddressBookServiceTest {

    @Autowired
    private AddressBookService service;

    private PrintStream out = System.out;

    @Test
    void getGroupAndAddressList() {
        List<String[]> list = service.getGroupAndAddressList();
        list.forEach(item->out.println(item[0] + ", " + item[1]));
    }

    @Test
    void existAddressAndName() {
        out.println(service.existAddress("11"));
        out.println(service.existAddress("10"));

        out.println(service.existAddressName("ted"));
        out.println(service.existAddressName("111"));
    }

    @Test
    void addAddress() {
        String name = RandomStringUtils.randomAlphanumeric(10);
        String addr = RandomStringUtils.randomAlphanumeric(10);
        out.println(String.format("addAddress name=%s addr=%s", name, addr));
        AddressBookEntity entity = service.addAddress(name, addr);
        assertNotNull(entity);
        out.println(String.format("entity id=%d", entity.getId()));
    }

    @Test
    void getAddress() throws Exception {
        AddressBookEntity entity = service.getAddress(-9999);
        assertNull(entity);

        try {
            service.getAddress("abcdef");
            assertTrue(false);
        } catch (ServiceInvalidParameterException e) {
        }
    }

    @Test
    void getAddressList() {
        List<AddressBookEntity> list = service.getAddressList();
        assertNotNull(list);
    }

    @Test
    void modifyAddress() {
        List<AddressBookEntity> list = service.getAddressList();
        if(list.size()>0) {
            AddressBookEntity entity = list.get(0);
            String name = "___"+entity.getName();
            out.println(String.format("modify id %d name %s -> %s", entity.getId(), entity.getName(), name));
            service.modifyAddress(entity.getId(), name, entity.getEmail());
        }
    }

    @Test
    void scenario1() throws Exception {

        List<Long> addrIds = new ArrayList<>();

        // Add 5 addresses.
        for(int i=0; i<5; ++i) {
            String name = RandomStringUtils.randomAlphanumeric(8);
            String addr = RandomStringUtils.randomAlphanumeric(12);
            addr = addr.substring(0, 8) + "@" + addr.substring(8, addr.length());
            AddressBookEntity entity = service.addAddress(name, addr);
            out.println(String.format("add address %d name=%s addr=%s", entity.getId(), entity.getName(), entity.getEmail()));
            addrIds.add(entity.getId());
        }

        out.println(addrIds.size() + " members are added");

        // Add a group
        String group = "Group-" + RandomStringUtils.randomAlphanumeric(5);
        assertNotNull(service.addGroup(group, addrIds));

        // Get all group members
        List<AddressBookEntity> members = service.getGroupMembers(group);
        assertTrue(members.size()==addrIds.size());

        // Delete 1 address from the address book
        service.deleteAddress(addrIds.get(0));

        // Get group members again and compare the before.
        members = service.getGroupMembers(group);
        assertFalse(members.size()==addrIds.size());
    }

}