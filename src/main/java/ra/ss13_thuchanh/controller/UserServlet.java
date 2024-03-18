package ra.ss13_thuchanh.controller;

import ra.ss13_thuchanh.model.User;
import ra.ss13_thuchanh.service.IUserService;
import ra.ss13_thuchanh.service.UserServiceIMPL;

import java.io.*;
import java.sql.SQLException;
import java.util.List;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;

@WebServlet(name = "helloServlet", value = "/users")
public class UserServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private UserServiceIMPL userService;

    // Khởi tạo servlet
    public void init() {
        userService = new UserServiceIMPL();
    }

    // Xử lý yêu cầu GET
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html; charset=UTF-8");
        String action = request.getParameter("action");
        if (action == null) {
            action = "";
        }

        try {
            switch (action) {
                // Hiển thị form thêm mới người dùng
                case "create":
                    showNewForm(request, response);
                    break;
                // Hiển thị form chỉnh sửa thông tin người dùng
                case "edit":
                    showEditForm(request, response);
                    break;
                // Xóa người dùng
                case "delete":
                    deleteUser(request, response);
                    break;
                // Mặc định: Hiển thị danh sách người dùng
                case "searchByCountry":
                    sreachByCountry(request, response);
                    break;
                case "sortByName":
                    sortByName(request, response);
                    break;
                default:
                    listUser(request, response);
                    break;
            }
        } catch (SQLException ex) {
            throw new ServletException(ex);
        }

    }

    // Xử lý yêu cầu POST
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setContentType("text/html; charset=UTF-8");
        String action = req.getParameter("action");
        if (action == null) {
            action = "";
        }
        try {
            switch (action) {
                // Thêm mới người dùng
                case "create":
                    insertUser(req, resp);
                    break;
                // Chỉnh sửa thông tin người dùng
                case "edit":
                    updateUser(req, resp);
                    break;
            }
        } catch (SQLException ex) {
            throw new ServletException(ex);
        }

    }

    // Hiển thị danh sách người dùng
    private void listUser(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, IOException, ServletException {
        List<User> listUser = userService.selectAllUsers();
        request.setAttribute("listUser", listUser);
        RequestDispatcher dispatcher = request.getRequestDispatcher("user/list.jsp");
        dispatcher.forward(request, response);
    }

    // Hiển thị form thêm mới người dùng
    private void showNewForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        RequestDispatcher dispatcher = request.getRequestDispatcher("user/create.jsp");
        dispatcher.forward(request, response);
    }

    // Hiển thị form chỉnh sửa thông tin người dùng
    private void showEditForm(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, ServletException, IOException {
        int id = Integer.parseInt(request.getParameter("id"));
        User existingUser = userService.selectUser(id);
        RequestDispatcher dispatcher = request.getRequestDispatcher("user/edit.jsp");
        request.setAttribute("user", existingUser);
        dispatcher.forward(request, response);
    }

    // Thêm mới người dùng
    private void insertUser(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, IOException, ServletException {
        String name = request.getParameter("name");
        String email = request.getParameter("email");
        String country = request.getParameter("country");
        User newUser = new User(name, email, country);
        userService.insertUser(newUser);
        RequestDispatcher dispatcher = request.getRequestDispatcher("user/create.jsp");
        dispatcher.forward(request, response);
    }

    // Chỉnh sửa thông tin người dùng
    private void updateUser(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, IOException, ServletException {
        int id = Integer.parseInt(request.getParameter("id"));
        String name = request.getParameter("name");
        String email = request.getParameter("email");
        String country = request.getParameter("country");

        User user = new User(id, name, email, country);
        userService.updateUser(user);
        RequestDispatcher dispatcher = request.getRequestDispatcher("user/edit.jsp");
        dispatcher.forward(request, response);
    }

    // Xóa người dùng
    private void deleteUser(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, IOException, ServletException {
        int id = Integer.parseInt(request.getParameter("id"));
        userService.deleteUser(id);

        List<User> listUser = userService.selectAllUsers();
        request.setAttribute("listUser", listUser);
        RequestDispatcher dispatcher = request.getRequestDispatcher("user/list.jsp");
        dispatcher.forward(request, response);
    }

    //tìm kiếm theo country
    private void sreachByCountry(HttpServletRequest request, HttpServletResponse response) throws SQLException, ServletException, IOException {
        String seachKey = request.getParameter("key");
        List<User> result = userService.searchByCountry(seachKey);
        request.setAttribute("listUser", result);
        RequestDispatcher dispatcher = request.getRequestDispatcher("user/list.jsp");
        dispatcher.forward(request, response);
    }

    private void sortByName(HttpServletRequest request,HttpServletResponse response) throws SQLException, ServletException, IOException {
        List<User> result = userService.sortByName();
        request.setAttribute("listUser", result);
        RequestDispatcher dispatcher = request.getRequestDispatcher("user/list.jsp");
        dispatcher.forward(request, response);
    }
    // Hủy và giải phóng tài nguyên khi servlet bị hủy
    public void destroy() {
    }
}
