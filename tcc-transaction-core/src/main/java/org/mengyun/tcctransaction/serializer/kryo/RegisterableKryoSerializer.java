package org.mengyun.tcctransaction.serializer.kryo;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.SerializerFactory;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class RegisterableKryoSerializer<T> extends KryoPoolSerializer<T> {

    List<Class> registerClasses = null;
    boolean warnUnregisteredClasses = false;

    public RegisterableKryoSerializer() {
    }

    public RegisterableKryoSerializer(int initPoolSize) {
        super(initPoolSize);
    }

    public RegisterableKryoSerializer(List<Class> registerClasses) {
        this.registerClasses = registerClasses;
        this.init();
    }

    public RegisterableKryoSerializer(int initPoolSize, List<Class> registerClasses) {
        this(initPoolSize, registerClasses, false);
    }

    public RegisterableKryoSerializer(int initPoolSize, List<Class> registerClasses, boolean warnUnregisteredClasses) {
        this.initPoolSize = initPoolSize;
        this.registerClasses = registerClasses;
        this.warnUnregisteredClasses = warnUnregisteredClasses;
        this.init();
    }

    @Override
    protected void initHook(Kryo kryo) {
        kryo.setWarnUnregisteredClasses(this.warnUnregisteredClasses);

        SerializerFactory.CompatibleFieldSerializerFactory factory = new SerializerFactory.CompatibleFieldSerializerFactory();
        factory.getConfig().setReadUnknownFieldData(true);
        factory.getConfig().setChunkedEncoding(true);
        kryo.setDefaultSerializer(factory);

        registerClasses(kryo, this.registerClasses);
    }


    private void registerClasses(Kryo kryo, List<Class> registerClasses) {

        List<Class> allClasses = registerJdkClasses();
        Set<Class> classesSet = new HashSet<>(allClasses);

        List<Class> externalClasses = registerClasses;

        if (externalClasses != null) {
            for (Class clazz : externalClasses) {
                if (clazz != null && !classesSet.contains(clazz)) {
                    allClasses.add(clazz);
                }
            }
        }

        for (Class clazz : allClasses) {
            kryo.register(clazz);
        }
    }

    private List<Class> registerJdkClasses() {
        List<Class> jdkClasses = new ArrayList<>();
        jdkClasses.add(Class.class);
        jdkClasses.add(Class[].class);

        jdkClasses.add(Object.class);
        jdkClasses.add(Object[].class);

        jdkClasses.add(byte[].class);
        jdkClasses.add(char[].class);
        jdkClasses.add(int[].class);
        jdkClasses.add(float[].class);
        jdkClasses.add(double[].class);

        jdkClasses.add(HashMap.class);
        jdkClasses.add(ConcurrentHashMap.class);

        jdkClasses.add(ArrayList.class);
        jdkClasses.add(LinkedList.class);
        jdkClasses.add(HashSet.class);
        jdkClasses.add(TreeSet.class);
        jdkClasses.add(Hashtable.class);
        jdkClasses.add(Vector.class);
        jdkClasses.add(BitSet.class);
        jdkClasses.add(Arrays.asList("").getClass());

        jdkClasses.add(StringBuffer.class);
        jdkClasses.add(StringBuilder.class);
        jdkClasses.add(String[].class);

        jdkClasses.add(Date.class);
        jdkClasses.add(BigDecimal.class);

        jdkClasses.add(UUID.class);
        jdkClasses.add(Calendar.class);
        jdkClasses.add(SimpleDateFormat.class);
        jdkClasses.add(GregorianCalendar.class);

        return jdkClasses;
    }
}