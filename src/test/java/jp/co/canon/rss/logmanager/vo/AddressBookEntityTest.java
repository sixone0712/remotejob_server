package jp.co.canon.rss.logmanager.vo;

import jp.co.canon.rss.logmanager.repository.AddressBookRepository;
import jp.co.canon.rss.logmanager.vo.address.AddressBookEntity;
import jp.co.canon.rss.logmanager.vo.address.MailAddress;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class AddressBookEntityTest {

    @Autowired
    private AddressBookRepository repo;

    @Test
    void insertItems() {

        AddressBookEntity addr = new AddressBookEntity("ted", "ted@mail.com");
        repo.save(addr);
        repo.save(new AddressBookEntity("ted1", "ted@mail.com"));
        repo.save(new AddressBookEntity("ted2", "ted@mail.com"));
    }

    @Test
    void getAddressList() {
        List<MailAddress> b = repo.getAddressName();
        assertNotNull(b);
    }


}