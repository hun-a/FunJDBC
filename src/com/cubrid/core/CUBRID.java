package com.cubrid.core;

import javax.xml.bind.DatatypeConverter;
import java.sql.*;
import java.util.*;
import java.util.Date;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Created by hun on 18/07/2017.
 */
public class CUBRID {
    private Connection conn;
    private PreparedStatement pstmt;
    private ResultSet rs;
    private String url;
    private Properties prop;

    private CUBRID() {}

    private static class THIS {
        private static final CUBRID INSTANCE = new CUBRID();
    }

    static {
        try {
            Class.forName("cubrid.jdbc.driver.CUBRIDDriver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static CUBRID getInstance() {
        return THIS.INSTANCE;
    }

    public void init(String url) {
        this.init(url, "", "");
    }

    public void init(String url, String user, String password) {
        prop = new Properties();
        prop.setProperty("user", user);
        prop.setProperty("password", password);
        init(url, prop);
    }

    public void init(String url, Properties prop) {
        this.url = url;
        this.prop = prop;
    }

    public Connection getConnection() throws SQLException {
        return conn = DriverManager.getConnection(url, prop);
    }

    public void close() throws SQLException {
        if (rs != null) {
            rs.close();
        }
        if (pstmt != null) {
            pstmt.close();
        }
        if (conn != null) {
            conn.close();
        }
    }

    public <T> Stream<T> query(String sql,
                               Extractor<T> extractor) throws SQLException {
       return this.query(sql, Optional.empty(), extractor);
    }

    public <T> Stream<T> query(String sql, Optional<List<?>> params,
                               Extractor<T> extractor) throws SQLException {
        conn = getConnection();
        pstmt = conn.prepareStatement(sql);
        params.ifPresent(param -> IntStream
                .rangeClosed(1, param.size())
                .boxed()
                .forEach(i -> param
                        .forEach(p -> bind(i, p))));
        rs = pstmt.executeQuery();

        return CUBRIDIterator.stream(rs, extractor).onClose(() -> {
            try {
                close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    private void bind(int index, Object value) {
        try {
            if (value instanceof Date) {
                java.sql.Date date = new java.sql.Date(((Date) value).getTime());
                pstmt.setDate(index, date);
            } else if (value instanceof String && value.toString().startsWith("X")) {
                value = ((String) value).replace("X", "").replace("'", "");
                byte[] bytes = DatatypeConverter.parseHexBinary(value.toString());
                pstmt.setBytes(index, bytes);
            } else {
                pstmt.setObject(index, value);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int execute(String sql) throws SQLException {
        return execute(sql, Optional.empty());
    }

    public int execute(String sql, Optional<List<Object>> params)
            throws SQLException {
        conn = getConnection();
        pstmt = conn.prepareStatement(sql);
        params.ifPresent(param -> {
            for (int i = 0; i< param.size(); i++) {
                bind(i + 1, param.get(i));
            }
        });
        int result = pstmt.executeUpdate();
        close();
        return result;
    }
}
