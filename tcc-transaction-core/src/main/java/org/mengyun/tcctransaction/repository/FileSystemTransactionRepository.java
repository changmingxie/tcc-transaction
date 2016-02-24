package org.mengyun.tcctransaction.repository;

import org.mengyun.tcctransaction.Transaction;
import org.mengyun.tcctransaction.utils.SerializationUtils;

import javax.transaction.xa.Xid;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by changming.xie on 2/24/16.
 */
public class FileSystemTransactionRepository extends CachableTransactionRepository {

    private String rootPath = "/tcc";

    private volatile boolean initialized;

    public void setRootPath(String rootPath) {
        this.rootPath = rootPath;
    }

    @Override
    protected void doCreate(Transaction transaction) {

        writeFile(transaction);
    }

    @Override
    protected void doUpdate(Transaction transaction) {
        writeFile(transaction);
    }

    @Override
    protected void doDelete(Transaction transaction) {

        String fullFileName = getFullFileName(transaction.getXid());
        File file = new File(fullFileName);
        if (file.exists()) {
            file.delete();
        }
    }

    @Override
    protected Transaction doFindOne(Xid xid) {

        String fullFileName = getFullFileName(xid);
        File file = new File(fullFileName);

        if (file.exists()) {
            return readTransaction(file);
        }

        return null;
    }

    @Override
    protected List<Transaction> doFindAll() {

        List<Transaction> transactions = new ArrayList<Transaction>();
        File path = new File(rootPath);
        File[] files = path.listFiles();

        for (File file : files) {
            Transaction transaction = readTransaction(file);
            transactions.add(transaction);
        }

        return transactions;
    }

    private String getFullFileName(Xid xid) {
        return String.format("%s/%s", rootPath, xid);
    }

    private void makeDirIfNecessory() {
        if (!initialized) {
            synchronized (FileSystemTransactionRepository.class) {
                if (!initialized) {
                    File rootPathFile = new File(rootPath);
                    if (!rootPathFile.exists()) {

                        boolean result = rootPathFile.mkdir();

                        if (!result) {
                            throw new TransactionIOException("cannot create root path, the path to create is:" + rootPath);
                        }

                        initialized = true;
                    } else if (!rootPathFile.isDirectory()) {
                        throw new TransactionIOException("rootPath is not directory");
                    }
                }
            }
        }
    }

    private void writeFile(Transaction transaction) {
        makeDirIfNecessory();

        String file = getFullFileName(transaction.getXid());

        FileChannel channel = null;
        RandomAccessFile raf = null;

        byte[] content = SerializationUtils.serialize(transaction);
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
                return (Transaction) SerializationUtils.deserialize(content);
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
}
