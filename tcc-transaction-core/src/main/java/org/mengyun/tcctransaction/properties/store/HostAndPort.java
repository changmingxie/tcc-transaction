package org.mengyun.tcctransaction.properties.store;

import java.io.Serializable;
import java.util.Objects;

/**
 * @author Nervose.Wu
 * @date 2022/5/24 17:44
 */

public class HostAndPort implements Serializable {

    private String host;
    private int port;
    private int database = 0;

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getDatabase() {
        return database;
    }

    public void setDatabase(int database) {
        this.database = database;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        HostAndPort that = (HostAndPort) o;
        return port == that.port &&
                database == that.database &&
                Objects.equals(host, that.host);
    }

    @Override
    public int hashCode() {
        return Objects.hash(host, port, database);
    }
}
