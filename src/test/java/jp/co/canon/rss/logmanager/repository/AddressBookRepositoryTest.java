package jp.co.canon.rss.logmanager.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class AddressBookRepositoryTest {

    @Autowired
    private AddressBookRepository repository;

    @Test
    void getAddressName() {
    }

    @Test
    void findByIdIn() {
        List<Long> idList = new ArrayList<>();
        idList.add(new Long(1));
        idList.add(new Long(2));
        idList.add(new Long(3));
        List list = repository.findByIdIn(idList);
        assertNotNull(list);
    }

    @Test
    void findFirstByName() {
        repository.findFirstByName("ted");
    }

    @Test
    void findFirstByEmail() {
    }
}