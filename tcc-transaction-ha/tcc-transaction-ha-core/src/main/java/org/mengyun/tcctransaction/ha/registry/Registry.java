package org.mengyun.tcctransaction.ha.registry;


/**
 * 注册器
 */
public interface Registry extends AutoCloseable {

    /**
     * 注册
     *
     * @throws Exception
     */
    default void register(Registration registration) throws RegistryException {
    }

    /**
     * 撤销
     *
     * @throws Exception
     */
    default void deregister(Registration registration) throws RegistryException {
    }


    @Override
    default void close() throws Exception {
    }
}
