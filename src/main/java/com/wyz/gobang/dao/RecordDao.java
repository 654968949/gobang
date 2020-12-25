package com.wyz.gobang.dao;

import com.wyz.gobang.entity.Record;
import com.wyz.gobang.utils.DBUtils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * <p>
 *     对战记录实体Dao
 * </p>
 *
 * @author wuyuzi
 * @since 2020/12/23
 */
public class RecordDao {
    /**
     *  查询所有对战记录
     */
    public List<Record> selectAll() {
        List<Record> records = new ArrayList<>();
        String sql = "select * from chess_record";
        try {
            ResultSet rs = DBUtils.executeQuerySQL(sql);
            if (Objects.isNull(rs)) {
                return null;
            }
            while (rs.next()) {
                Record record = new Record();
                record.setId(rs.getInt(1));
                record.setBlack(rs.getString(2));
                record.setWhite(rs.getString(3));
                record.setChessTime(rs.getString(4));
                record.setResult(rs.getInt(5));
                records.add(record);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return records;
    }

    /**
     * 插入一条对战记录
     */
    public void addOneRecord(Record record) {
        String sql = "INSERT INTO chess_record (black, white, chesstime, result) VALUES (?, ?, now(), ?)";
        List<Object> params = new ArrayList<>();
        params.add(record.getBlack());
        params.add(record.getWhite());
        params.add(record.getResult());
        int count = DBUtils.executeUpdateSQL(sql,params);

    }

}
