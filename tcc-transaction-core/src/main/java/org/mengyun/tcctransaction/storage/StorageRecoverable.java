package org.mengyun.tcctransaction.storage;

import org.mengyun.tcctransaction.storage.domain.DomainStore;

import java.util.Date;
import java.util.List;

public interface StorageRecoverable {

    Page<TransactionStore> findAllUnmodifiedSince(String domain, Date date, String offset, int pageSize);

    Page<TransactionStore> findAllDeletedSince(String domain, Date date, String offset, int pageSize);

    int count(String domain, boolean isMarkDeleted);

    void registerDomain(DomainStore domainStore);

    void updateDomain(DomainStore domainStore);

    void removeDomain(String domain);

    DomainStore findDomain(String domain);

    List<DomainStore> getAllDomains();

}
