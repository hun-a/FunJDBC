package test.cubrid.core;

import org.junit.*;

import java.sql.Connection;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Properties;

import static org.junit.Assert.*;

/**
 * Created by hun on 18/07/2017.
 */
public class CUBRIDTest {
    private static com.cubrid.core.CUBRID cubrid;
    private static final String URL = "jdbc:cubrid:218.233.240.77:60100:demodb:dba::";

    @BeforeClass
    public static void beforeTest() throws Exception {
        cubrid = com.cubrid.core.CUBRID.getInstance();
        cubrid.init(URL);
        String createSql = "CREATE TABLE IF NOT EXISTS fapi " +
                "(col1 INT PRIMARY KEY AUTO_INCREMENT," +
                " col2 DATETIME)";
        cubrid.execute(createSql);
    }

    @Test
    public void initOneParamTest() throws Exception {
        cubrid.init(URL);
        Connection conn = cubrid.getConnection();
        assertNotNull(conn);
    }

    @Test
    public void closeTest() throws Exception {
        initOneParamTest();
        cubrid.close();
    }

    @Test
    public void initTwoParamTest() throws Exception {
        cubrid.init(URL, "dba", "");
        Connection conn = cubrid.getConnection();
        assertNotNull(conn);
    }

    @Test
    public void initThreeParamTest() throws Exception {
        Properties prop = new Properties();
        prop.setProperty("user", "dba");
        prop.setProperty("password", "");

        cubrid.init(URL, prop);
        Connection conn = cubrid.getConnection();
        assertNotNull(conn);
    }

    @Test
    public void queryOneParamTest() throws Exception {
        String sql = "select 1";
        cubrid.query(sql, r -> r.getString(1))
                .forEach(r -> assertEquals("1", r));
    }

    @Test
    public void queryTwoParamTest() throws Exception {
        String sql = "select 1 from db_root where 1 = ?";
        List<String> params = Arrays.asList("1");
        cubrid.query(sql, Optional.of(params), r -> r.getString(1))
                .forEach(r -> assertEquals("1", r));
    }

    @Test
    public void insertDataTest() throws Exception {
        String sql = "INSERT INTO fapi (col2) VALUES (SYSDATETIME)";
        assertEquals(1, cubrid.execute(sql));
    }

    @Test
    public void deleteDataTest() throws Exception {
        String sql = "DELETE FROM fapi WHERE col1 = 1";
        assertEquals(1, cubrid.execute(sql));
    }

    @Test
    public void insertDatasTest() throws Exception {
        String sql = "INSERT INTO fapi (col2) VALUES (?)";
        List<String> params = Arrays.asList("sysdatetime");
        assertEquals(1, cubrid.execute(sql, Optional.of(params)));
    }

    @AfterClass
    public static void dropTableTest() throws Exception {
        beforeTest();
        String sql = "DROP TABLE IF EXISTS fapi";
        assertEquals(0, cubrid.execute(sql));
    }
}
