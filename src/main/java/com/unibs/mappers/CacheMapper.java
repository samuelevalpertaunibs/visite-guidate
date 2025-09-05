package com.unibs.mappers;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

public interface CacheMapper<T, K, V> {
        T map(ResultSet rs, Map<K, V> cache) throws SQLException;
}
