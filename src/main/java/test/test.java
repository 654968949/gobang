package test;

import com.wyz.gobang.dao.RecordDao;
import com.wyz.gobang.entity.Record;
import com.wyz.gobang.utils.DBUtils;

import java.sql.*;

/**
 * <p>
 *     jdbc
 * </p>
 *
 * @author wuyuzi
 * @since 2020/12/22
 */
@SuppressWarnings("SqlNoDataSourceInspection")
public class test {
    public static void main(String[] args) {

        Connection conn = DBUtils.getConnection();
        RecordDao recordDao = new RecordDao();
        Record record = new Record();
        record.setWhite("w");
        record.setBlack("b");
        record.setChessTime("2020-12-24 09:57:15");
        record.setResult(1);
        Record record1 = new Record();
        record1.setWhite("w");
        record1.setBlack("b");
        record1.setChessTime("2020-12-24 09:57:15");
        record1.setResult(100000);

        try {
            conn.setAutoCommit(false);
            recordDao.addOneRecord(record);
            recordDao.addOneRecord(record1);
            conn.commit();
        } catch (SQLException e) {
            try {
                conn.rollback();
            } catch (SQLException sqlException) {
                sqlException.printStackTrace();
            }
            e.printStackTrace();
        } finally {
            try {
                boolean f = conn.getAutoCommit();
                System.out.println(f);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
