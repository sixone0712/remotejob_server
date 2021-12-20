package jp.co.canon.rss.logmanager.repository;

import jp.co.canon.rss.logmanager.vo.address.AddressBookEntity;
import jp.co.canon.rss.logmanager.vo.address.GroupBookEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class GroupBookRepositoryTest {

    @Autowired
    private GroupBookRepository repository;

    private PrintStream out = System.out;

    @Test
    void getAddressList() {
        List addr = repository.getAddressList("g1");
        assertNotNull(addr);
    }

    @Test
    void countAllByName() {
        out.println(repository.countAllByName("g1"));
        out.println(repository.countAllByName("g2"));
    }

    @Test
    void findGroup() {
        List<String> groups = repository.getGroupList();
        out.println(groups.size() + " groups found");
        if(groups.size()>0) {
            groups.forEach(g -> out.println("--" + g));
            List<GroupBookEntity> entities = repository.findByName(groups.get(0));
            out.println(entities.size() + " members found");
            for(GroupBookEntity ent: entities) {
                out.println(String.format("----%s %s", ent.getAddress().getName(), ent.getAddress().getEmail()));
            }

            // Delete members from the group.
            List<AddressBookEntity> addresses = new ArrayList<>();
            addresses.add(entities.get(0).getAddress());
            out.println("delete address " +  addresses.get(0).getId() + " from " + groups.get(0));
            repository.deleteGroupMember(groups.get(0), addresses.get(0).getId());
            assertNotNull(entities);
        }
    }
}