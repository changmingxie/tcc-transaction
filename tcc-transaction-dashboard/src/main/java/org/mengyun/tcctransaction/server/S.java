package org.mengyun.tcctransaction.server;

/*
 * Copyright (C) 2015 The Guava Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you
 * may not use this file except in compliance with the License.  You may
 * obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied.  See the License for the specific language governing
 * permissions and limitations under the License.
 */


import java.util.Iterator;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Static utility methods related to {@code Stream} instances.
 *
 * @since 21.0
 */
public final class S {

    public static <A, B, R> Stream<R> zip(Stream<A> streamA, Stream<B> streamB, BiFunction<? super A, ? super B, R> function) {
        checkNotNull(streamA);
        checkNotNull(streamB);
        checkNotNull(function);
        boolean isParallel = streamA.isParallel() || streamB.isParallel(); // same as Stream.concat
        Spliterator<A> splitrA = streamA.spliterator();
        Spliterator<B> splitrB = streamB.spliterator();
        int characteristics = splitrA.characteristics() & splitrB.characteristics() & (Spliterator.SIZED | Spliterator.ORDERED);
        Iterator<A> itrA = Spliterators.iterator(splitrA);
        Iterator<B> itrB = Spliterators.iterator(splitrB);
        return StreamSupport.stream(
                new Spliterators.AbstractSpliterator<R>(
                        Math.min(splitrA.estimateSize(), splitrB.estimateSize()), characteristics) {
                    @Override
                    public boolean tryAdvance(Consumer<? super R> action) {
                        if (itrA.hasNext() && itrB.hasNext()) {
                            action.accept(function.apply(itrA.next(), itrB.next()));
                            return true;
                        }
                        return false;
                    }
                },
                isParallel).onClose(streamA::close).onClose(streamB::close);
    }

}
