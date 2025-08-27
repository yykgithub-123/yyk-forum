package yc.star.forum;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import yc.star.forum.dao.UserMapper;
import yc.star.forum.model.User;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

@SpringBootTest
class ForumSystemSpringApplicationTests {

    @Resource
    private DataSource dataSource;

    @Autowired
    private UserMapper userMapper;

    @Test
    public void testMybatis () {
        User user = userMapper.selectByPrimaryKey(1l);
        System.out.println(user.toString());
        System.out.println(user.getUsername());
    }

    @Test
    public void testConnection () throws SQLException {
        System.out.println("dataSource = " + dataSource.getClass());
        Connection connection = dataSource.getConnection();
        System.out.println("connection = " + connection);
        connection.close();
    }

    @Test
    void contextLoads() {

    }

}
