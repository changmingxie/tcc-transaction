package org.mengyun.tcctransaction.serializer;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.pool.KryoCallback;
import com.esotericsoftware.kryo.pool.KryoFactory;
import com.esotericsoftware.kryo.pool.KryoPool;
import org.objenesis.strategy.StdInstantiatorStrategy;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by changming.xie on 9/18/17.
 */
public class KryoPoolSerializer<T> implements ObjectSerializer<T> {

    public static final int DEFAULT_MAX_POOL_SIZE = 300;

    protected int initPoolSize = DEFAULT_MAX_POOL_SIZE;

    KryoPool pool = null;

    public KryoPoolSerializer() {
        init();
    }

    public KryoPoolSerializer(int initPoolSize) {
        this.initPoolSize = initPoolSize;
        init();
    }


    protected void init() {

        KryoFactory factory = new KryoFactory() {
            public Kryo create() {
                Kryo kryo = new Kryo();
                kryo.setReferences(true);
                kryo.setRegistrationRequired(false);
//            kryo.setDefaultSerializer(CompatibleFieldSerializer.class);
//                kryo.setWarnUnregisteredClasses(true);
                //Fix the NPE bug when deserializing Collections.
                ((Kryo.DefaultInstantiatorStrategy) kryo.getInstantiatorStrategy())
                        .setFallbackInstantiatorStrategy(new StdInstantiatorStrategy());
                initHook(kryo);
                return kryo;
            }
        };

        pool = new KryoPool.Builder(factory).softReferences().build();

        List<Kryo> preCreatedKryos = new ArrayList<>();
        for (int i = 0; i < initPoolSize; i++) {
            preCreatedKryos.add(pool.borrow());
        }

        for (Kryo kryo : preCreatedKryos) {
            pool.release(kryo);
        }
    }

    @Override
    public byte[] serialize(final T object) {

        return pool.run(new KryoCallback<byte[]>() {
            public byte[] execute(Kryo kryo) {
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                Output output = new Output(byteArrayOutputStream);

                kryo.writeClassAndObject(output, object);
                output.flush();

                return byteArrayOutputStream.toByteArray();
            }
        });
    }

    @Override
    public T deserialize(final byte[] bytes) {

        return pool.run(new KryoCallback<T>() {
            public T execute(Kryo kryo) {
                ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
                Input input = new Input(byteArrayInputStream);

                return (T) kryo.readClassAndObject(input);
            }
        });
    }

    @Override
    public T clone(final T object) {
        return pool.run(new KryoCallback<T>() {
            public T execute(Kryo kryo) {
                return kryo.copy(object);
            }
        });
    }

    protected void initHook(Kryo kryo) {

    }
}
