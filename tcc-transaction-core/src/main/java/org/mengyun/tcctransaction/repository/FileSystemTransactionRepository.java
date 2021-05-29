package org.mengyun.tcctransaction.repository;

import org.apache.commons.lang3.StringUtils;
import org.mengyun.tcctransaction.Transaction;
import org.mengyun.tcctransaction.serializer.KryoTransactionSerializer;
import org.mengyun.tcctransaction.serializer.TransactionSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.transaction.xa.Xid;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class FileSystemTransactionRepository extends AbstractTransactionRepository {

    static final Logger log = LoggerFactory.getLogger(FileSystemTransactionRepository.class.getSimpleName());

    private static final String FILE_NAME_DELIMITER = "&";
    private static final String FILE_NAME_PATTERN = "*-*-*-*-*" + FILE_NAME_DELIMITER + "*-*-*-*-*";

    private String domain = "/var/log/";

    private volatile boolean initialized;

    private TransactionSerializer serializer = new KryoTransactionSerializer();

    @Override
    protected int doCreate(Transaction transaction) {
        writeFile(transaction);
        return 1;
    }

    @Override
    protected int doUpdate(Transaction transaction) {

        Transaction foundTransaction = doFindOne(transaction.getXid());
        if (foundTransaction.getVersion() != transaction.getVersion()) {
            return 0;
        }

        transaction.setVersion(transaction.getVersion() + 1);
        transaction.setLastUpdateTime(new Date());

        writeFile(transaction);
        return 1;
    }

    @Override
    protected int doDelete(Transaction transaction) {
        String fullFileName = getFullFileName(transaction.getXid());
        File file = new File(fullFileName);
        if (file.exists()) {
            return file.delete() ? 1 : 0;
        }
        return 1;
    }

    @Override
    protected Transaction doFindOne(Xid xid) {

        makeDirIfNecessary();

        String fullFileName = getFullFileName(xid);
        File file = new File(fullFileName);

        if (file.exists()) {
            return readTransaction(file);
        }

        return null;
    }

    @Override
    protected Page<Transaction> doFindAllUnmodifiedSince(Date date, String offset, int pageSize) {

        List<Transaction> fetchedTransactions = new ArrayList<>();

        String tryFetchOffset = offset;

        int haveFetchedCount = 0;

        do {

            Page<Transaction> page = doFindAll(tryFetchOffset, pageSize - haveFetchedCount);

            tryFetchOffset = page.getNextOffset();

            for (Transaction transaction : page.getData()) {
                if (transaction.getLastUpdateTime().compareTo(date) < 0) {
                    fetchedTransactions.add(transaction);
                }
            }

            haveFetchedCount += page.getData().size();

            if (page.getData().size() <= 0 || haveFetchedCount >= pageSize) {
                break;
            }
        } while (true);


        return new Page<Transaction>(tryFetchOffset, fetchedTransactions);
    }

    @Override
    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    /*
     * offset: jedisIndex:cursor,eg = 0:0,1:0
     * */
    protected Page<Transaction> doFindAll(String offset, int maxFindCount) {

        makeDirIfNecessary();

        Page<Transaction> page = new Page<Transaction>();

        Path dir = Paths.get(domain);

        int currentOffset = StringUtils.isEmpty(offset) ? 0 : Integer.valueOf(offset);
        int nextOffset = currentOffset;

        int index = 0;

        List<Transaction> transactions = new ArrayList<Transaction>();

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir, FILE_NAME_PATTERN)) {

            for (Path path : stream) {

                if (index < currentOffset) {
                    index++;
                    continue;
                }

                if (index < currentOffset + maxFindCount) {

                    try {

                        Transaction transaction = readTransaction(path.toFile());
                        if (transaction != null) {
                            transactions.add(transaction);
                        }

                    } catch (Exception e) {
                        // ignore the read error.
                    }

                    index++;
                    nextOffset = index;
                } else {
                    break;
                }
            }

            page.setNextOffset(String.valueOf(nextOffset));
            page.setData(transactions);

            return page;

        } catch (IOException e) {
            throw new TransactionIOException(e);
        }
    }


    private void writeFile(Transaction transaction) {
        makeDirIfNecessary();

        String file = getFullFileName(transaction.getXid());

        FileChannel channel = null;
        RandomAccessFile raf = null;

        byte[] content = serializer.serialize(transaction);
        try {
            raf = new RandomAccessFile(file, "rw");
            channel = raf.getChannel();
            ByteBuffer buffer = ByteBuffer.allocate(content.length);
            buffer.put(content);
            buffer.flip();

            while (buffer.hasRemaining()) {
                channel.write(buffer);
            }

            channel.force(true);
        } catch (Exception e) {
            throw new TransactionIOException(e);
        } finally {
            if (channel != null && channel.isOpen()) {
                try {
                    channel.close();
                } catch (IOException e) {
                    throw new TransactionIOException(e);
                }
            }
        }
    }

    private Transaction readTransaction(File file) {

        FileInputStream fis = null;
        try {
            fis = new FileInputStream(file);

            byte[] content = new byte[(int) file.length()];

            fis.read(content);

            if (content != null) {
                return serializer.deserialize(content);
            }

        } catch (Exception e) {
            throw new TransactionIOException(e);
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    throw new TransactionIOException(e);
                }
            }
        }

        return null;
    }


    private void makeDirIfNecessary() {
        if (!initialized) {
            synchronized (FileSystemTransactionRepository.class) {
                if (!initialized) {
                    File rootPathFile = new File(domain);
                    if (!rootPathFile.exists()) {

                        boolean result = rootPathFile.mkdir();

                        if (!result) {
                            throw new TransactionIOException("cannot create root path, the path to create is:" + domain);
                        }

                        initialized = true;
                    } else if (!rootPathFile.isDirectory()) {
                        throw new TransactionIOException("rootPath is not directory");
                    }
                }
            }
        }
    }

    private String getFullFileName(Xid xid) {
        return String.format(domain.endsWith("/") ? "%s%s" + FILE_NAME_DELIMITER + "%s" : "%s/%s" + FILE_NAME_DELIMITER + "%s", domain,
                UUID.nameUUIDFromBytes(xid.getGlobalTransactionId()).toString(),
                UUID.nameUUIDFromBytes(xid.getBranchQualifier()).toString());
    }
}
