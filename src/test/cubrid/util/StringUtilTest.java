package test.cubrid.util;

import com.cubrid.util.StringUtil;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by hun on 19/07/2017.
 */
public class StringUtilTest {
    @Test
    public void countBindingParamsTest() {
        String sql = "SELECT * FROM db WHERE col1 = ? and col2 = ?";
        int count = StringUtil.countBindingParams(sql);
        assertEquals(2, count);

        String sql2 = "INSERT INTO tables VALUES (?, 'abha', '?', '\"?\"', ?)";
        int count2 = StringUtil.countBindingParams(sql2);
        assertEquals(2, count2);
    }
}
