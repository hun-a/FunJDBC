package com.cubrid.core;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by hun on 18/07/2017.
 */
@FunctionalInterface
public interface Extractor<T> {
    T extract(ResultSet rs) throws SQLException;
}
