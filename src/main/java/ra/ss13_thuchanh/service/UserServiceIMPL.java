package ra.ss13_thuchanh.service;

import ra.ss13_thuchanh.model.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserServiceIMPL {
    private String jdbcURL = "jdbc:mysql://localhost:3306/demo";
    private String jdbcUsername = "root";
    private String jdbcPassword = "admin";

    private static final String INSERT_USERS_SQL = "INSERT INTO user" + "  (name, email, country) VALUES " +
            " (?, ?, ?);";
    private static final String SELECT_USER_BY_ID = "select id,name,email,country from user where id =?";
    private static final String SELECT_ALL_USERS = "select * from user";
    private static final String DELETE_USERS_SQL = "delete from user where id = ?;";
    private static final String UPDATE_USERS_SQL = "update user set name = ?,email= ?, country =? where id = ?;";

    private static final String FIND_USERS_BY_COUNTRY = "select * from user where country=?";
    private static final String SORTBYNAME = "select * from user order by name desc";

    public UserServiceIMPL() {
    }

    // Thiết lập kết nối đến cơ sở dữ liệu MySQL
    protected Connection getConnection() {
        Connection connection = null;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(jdbcURL, jdbcUsername, jdbcPassword);
        } catch (SQLException e) {
            // In lỗi ra console nếu có lỗi kết nối cơ sở dữ liệu
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            // In lỗi ra console nếu không tìm thấy driver JDBC
            e.printStackTrace();
        }
        return connection;
    }

    // Phương thức này thêm một User mới vào cơ sở dữ liệu
    public void insertUser(User user) throws SQLException {
        System.out.println(INSERT_USERS_SQL);
        // Sử dụng try-with-resource để tự đóng kết nối sau khi sử dụng
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(INSERT_USERS_SQL)) {
            preparedStatement.setString(1, user.getName());
            preparedStatement.setString(2, user.getEmail());
            preparedStatement.setString(3, user.getCountry());
            System.out.println(preparedStatement);
            // Thực hiện câu lệnh SQL để thêm dữ liệu vào cơ sở dữ liệu
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            // In lỗi ra console nếu có lỗi thêm dữ liệu
            e.printStackTrace();
        }

    }

    // Phương thức này lấy thông tin của một User dựa trên ID
    public User selectUser(int id) {
        User user = null;
        // Thiết lập kết nối đến cơ sở dữ liệu
        try (Connection connection = getConnection();
             // Tạo statement sử dụng connection
             PreparedStatement preparedStatement = connection.prepareStatement(SELECT_USER_BY_ID);) {
            preparedStatement.setInt(1, id);
            System.out.println(preparedStatement);
            // Thực thi câu lệnh SQL và lấy kết quả trả về
            ResultSet rs = preparedStatement.executeQuery();
            // Xử lý kết quả ResultSet
            while (rs.next()) {
                String name = rs.getString("name");
                String email = rs.getString("email");
                String country = rs.getString("country");
                user = new User(id, name, email, country);
            }
        } catch (SQLException e) {
            // Gọi phương thức printSQLException để in ra lỗi nếu có
            printSQLException(e);
        }
        return user;
    }

    public List<User> searchByCountry(String searchKey) throws SQLException {
        List<User> list = new ArrayList<>();
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(FIND_USERS_BY_COUNTRY);) {
            preparedStatement.setString(1, searchKey);
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                String email = rs.getString("email");
                String country = rs.getString("country");
                list.add(new User(id, name, email, country));
            }
        } catch (SQLException e) {
            // Gọi phương thức printSQLException để in ra lỗi nếu có
            printSQLException(e);
        }
        return list;
    }

    public List<User> sortByName() throws SQLException {
        List<User> list = new ArrayList<>();
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SORTBYNAME);) {
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                String email = rs.getString("email");
                String country = rs.getString("country");
                list.add(new User(id, name, email, country));
            }
        } catch (SQLException e) {
            // Gọi phương thức printSQLException để in ra lỗi nếu có
            printSQLException(e);
        }
        return list;
    }


    // Phương thức này lấy danh sách tất cả User trong cơ sở dữ liệu
    public List<User> selectAllUsers() {
        List<User> users = new ArrayList<>();
        // Thiết lập kết nối đến cơ sở dữ liệu
        try (Connection connection = getConnection();
             // Tạo statement sử dụng connection
             PreparedStatement preparedStatement = connection.prepareStatement(SELECT_ALL_USERS);) {
            System.out.println(preparedStatement);
            // Thực thi câu lệnh SQL và lấy kết quả trả về
            ResultSet rs = preparedStatement.executeQuery();
            // Xử lý kết quả ResultSet
            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                String email = rs.getString("email");
                String country = rs.getString("country");
                users.add(new User(id, name, email, country));
            }
        } catch (SQLException e) {
            // Gọi phương thức printSQLException để in ra lỗi nếu có
            printSQLException(e);
        }
        return users;
    }

    // Phương thức này xóa một User dựa trên ID
    public boolean deleteUser(int id) throws SQLException {
        boolean rowDeleted;
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(DELETE_USERS_SQL);) {
            statement.setInt(1, id);
            rowDeleted = statement.executeUpdate() > 0;
        }
        return rowDeleted;
    }


    public boolean updateUser(User user) throws SQLException {
        boolean rowUpdated;
        try (Connection connection = getConnection(); PreparedStatement statement = connection.prepareStatement(UPDATE_USERS_SQL);) {
            statement.setString(1, user.getName());
            statement.setString(2, user.getEmail());
            statement.setString(3, user.getCountry());
            statement.setInt(4, user.getId());

            rowUpdated = statement.executeUpdate() > 0;
        }
        return rowUpdated;
    }


    private void printSQLException(SQLException ex) {
        for (Throwable e : ex) {
            if (e instanceof SQLException) {
                e.printStackTrace(System.err);
                System.err.println("SQLState: " + ((SQLException) e).getSQLState());
                System.err.println("Error Code: " + ((SQLException) e).getErrorCode());
                System.err.println("Message: " + e.getMessage());
                Throwable t = ex.getCause();
                while (t != null) {
                    System.out.println("Cause: " + t);
                    t = t.getCause();
                }
            }
        }
    }

}
