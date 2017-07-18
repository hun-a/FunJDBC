package com.cubrid.core;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Created by hun on 18/07/2017.
 */
public class CUBRIDIterator<T>
        implements Iterator<T>, AutoCloseable {
    private final ResultSet resultSet;
    private final Extractor<T> extractor;

    public CUBRIDIterator(final ResultSet resultSet,
                          final Extractor<T> extractor) {
        this.resultSet = resultSet;
        this.extractor = extractor;
    }

    @Override
    public void close() throws SQLException {
        if (resultSet != null ) {
            resultSet.close();
        }
    }

    @Override
    public boolean hasNext() {
        try {
            return resultSet.next();
        } catch (Exception e) {
            throw new RuntimeException();
        }
    }

    @Override
    public T next() {
        try {
            return extractor.extract(resultSet);
        } catch (Exception e) {
            throw new RuntimeException();
        }
    }

    public static <T> Stream<T> stream(final ResultSet resultSet,
                                       final Extractor<T> extractor) {
        CUBRIDIterator<T> iterator = new CUBRIDIterator(resultSet, extractor);
        return setIterator(iterator);
    }

    private static <T> Stream<T> setIterator(CUBRIDIterator<T> iterator) {
        return StreamSupport.stream(Spliterators.spliteratorUnknownSize(iterator, 0), false)
                .onClose(() -> {
                    try {
                        iterator.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
    }
}
