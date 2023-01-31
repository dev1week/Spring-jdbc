package com.example.jdbc.repository;

import com.example.jdbc.connection.DBConnectionUtil;
import com.example.jdbc.domain.Member;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.support.JdbcUtils;

import javax.sql.DataSource;
import java.sql.*;
import java.util.NoSuchElementException;

/**
 * JDBC - DataSource, JdbcUtils
 */
@Slf4j
public class MemberRepositoryV1 {

    private final DataSource dataSource;

    public MemberRepositoryV1(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public Member save(Member member) throws SQLException {
        //sql 쿼리 정의
        String sql = "insert into member(member_id, money) values(?, ?)";
        Connection con = null;
        PreparedStatement pstmt = null;
        try {
            //DBConnectionUtil을 통해 커넥션 획득
            con = getConnection();
            //DB에 전달할 sql과 파라미터 값을 준비한다.
            pstmt = con.prepareStatement(sql);
            //sql 변수 첫번째 ? 에 값을 지정함(String)
            pstmt.setString(1, member.getMemberId());
            //sql 변수 두번째 ? 에 값을 지정함(Integer)
            pstmt.setInt(2, member.getMoney());
            //Statement를 통해 준비된 SQL 커넥션을 통해 DB에 전달함
            //int를 반환 = 업데이트된 DB row 수
            pstmt.executeUpdate();
            return member;
        } catch (SQLException e) {             log.error("db error", e);
            throw e;
        } finally {
            //항상 종료되도록 finally에서 호출해야한다.
            close(con, pstmt, null);
        }
    }

    public void update(String memberId, int money) throws SQLException {
        String sql = "update member set money=? where member_id=?";

        Connection con = null;
        PreparedStatement pstmt = null;

        try{
            con = getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setInt(1, money);
            pstmt.setString(2, memberId);
            int resultSize = pstmt.executeUpdate();
            log.info("resultSize={}", resultSize);
        }catch(SQLException e){
            log.error("db error",e);
            throw e;
        }finally{
            close(con, pstmt, null);
        }
    }

    public Member findById(String memberId) throws SQLException {
        String sql = "select * from member where member_id=?";
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            con = getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setString(1, memberId);

            rs = pstmt.executeQuery();

            if (rs.next()) {
                Member member = new Member();
                member.setMemberId(rs.getString("member_id"));
                member.setMoney(rs.getInt("money"));
                return member;
            } else {
                throw new NoSuchElementException("member not found memberId = " + memberId);
            }

        } catch (SQLException e) {
            log.error("db error", e);
            throw e;
        } finally {
            close(con, pstmt, rs);
        }
    }
    // Statement -> 완성된 SQL 구문
    private void close(Connection con, Statement stmt, ResultSet rs) {
        //jdbc utils는 원래의 try catch 구문을 미리 작성해 놓은 것이라 생각하면 됨
        JdbcUtils.closeResultSet(rs);
        JdbcUtils.closeStatement(stmt);
        JdbcUtils.closeConnection(con);
        }
    private Connection getConnection() throws SQLException {
        Connection con = dataSource.getConnection();
        log.info("get connection = {}, get class ={}");
        return con;
    } }