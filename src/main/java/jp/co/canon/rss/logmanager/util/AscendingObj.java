package jp.co.canon.rss.logmanager.util;

import jp.co.canon.rss.logmanager.dto.address.AddressBookDTO;

import java.util.Comparator;

public class AscendingObj implements Comparator<AddressBookDTO> {
    @Override
    public int compare(AddressBookDTO o1, AddressBookDTO o2) {
        return o1.getName().compareTo(o2.getName());
    }
}
