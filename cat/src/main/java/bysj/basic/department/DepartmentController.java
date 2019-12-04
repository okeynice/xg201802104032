package bysj.basic.department;

import bysj.domain.Department;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import service.DepartmentService;
import util.JSONUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Collection;

/**
 * 将所有方法组织在一个Controller(Servlet)中
 */
@WebServlet("/department.ctl")
public class DepartmentController extends HttpServlet {
    /**
     * POST, http://49.234.223.174:8080/department.ctl, 增加院系
     * 增加一个院系对象：将来自前端请求的JSON对象，增加到数据库表中
     * @param request 请求对象
     * @param response 响应对象
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        //设置请求字符编码为UTF-8
        request.setCharacterEncoding("UTF-8");
        //根据request对象，获得代表参数的JSON字串
        String department_json = JSONUtil.getJSON(request);
        //将JSON字串解析为Department对象
        Department departmentToAdd = JSON.parseObject(department_json, Department.class);
        //设置响应字符编码为UTF-8
        response.setContentType("text/html;charset=UTF-8");
        //创建JSON对象message，以便往前端响应信息
        JSONObject message = new JSONObject();
        //在数据库表中增加Department对象
        try {
            DepartmentService.getInstance().add(departmentToAdd);
            message.put("message", "增加成功");
        }catch (SQLException e){
            message.put("message", "数据库操作异常");
        }catch(Exception e){
            message.put("message", "网络异常");
        }
        //响应message到前端
        response.getWriter().println(message);
    }

    /**
     * DELETE, http://49.234.223.174:8080/department.ctl?id=1, 删除id=1的院系
     * 删除一个院系对象：根据来自前端请求的id，删除数据库表中id的对应记录
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //读取参数id
        String id_str = request.getParameter("id");
        int id = Integer.parseInt(id_str);
        //设置响应字符编码为UTF-8
        response.setContentType("text/html;charset=UTF-8");
        //创建JSON对象message，以便往前端响应信息
        JSONObject message = new JSONObject();

        //到数据库表中删除对应的院系
        try {
            DepartmentService.getInstance().delete(id);
            message.put("message", "删除成功");
        }catch (SQLException e){
            message.put("message", "数据库操作异常");
        }catch(Exception e){
            message.put("message", "网络异常");
        }
        //响应message到前端
        response.getWriter().println(message);
    }


    /**
     * PUT, http://49.234.223.174:8080/department.ctl, 修改院系
     *
     * 修改一个院系对象：将来自前端请求的JSON对象，更新数据库表中相同id的记录
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        //设置请求字符编码为UTF-8
        request.setCharacterEncoding("UTF-8");
        String department_json = JSONUtil.getJSON(request);
        //将JSON字串解析为department对象
        Department departmentToAdd = JSON.parseObject(department_json, Department.class);
        //设置响应字符编码为UTF-8
        response.setContentType("text/html;charset=UTF-8");
        //创建JSON对象message，以便往前端响应信息
        JSONObject message = new JSONObject();
        //到数据库表修改department对象对应的记录
        try {
            DepartmentService.getInstance().update(departmentToAdd);
            message.put("message", "修改成功");
        }catch (SQLException e){
            message.put("message", "数据库操作异常");
        }catch(Exception e){
            e.printStackTrace();
            message.put("message", "网络异常");
        }
        //响应message到前端
        response.getWriter().println(message);
    }

    /**
     * GET, http://49.234.223.174:8080/department.ctl?id=1, 查询id=1的院系
     * GET, http://49.234.223.174:8080/department.ctl, 查询所有的院系
     * 把一个或所有学院对象响应到前端
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        //设置响应字符编码为UTF-8
        response.setContentType("text/html;charset=UTF-8");
        //读取参数id
        String id_str = request.getParameter("id");
        String paraType = request.getParameter("paraType");
        //创建JSON对象message，以便往前端响应信息
        JSONObject message = new JSONObject();
        try {
            //如果id = null, 表示响应所有院系对象，否则响应id指定的院系对象
            if (id_str == null) {
                responseDepartments(response);
            } else  if(paraType == null){
                int id = Integer.parseInt(id_str);
                responseDepartment(id, response);
            }else{
            int id = Integer.parseInt(id_str);
            responseDepartmentSchool(id, response);
        }

        }catch (SQLException e){
            message.put("message", "数据库操作异常");
            //响应message到前端
            response.getWriter().println(message);
        }catch(Exception e){
            message.put("message", "网络异常");
            //响应message到前端
            response.getWriter().println(message);
        }
    }
    //响应一个院系对象
    private void responseDepartment(int id, HttpServletResponse response)
            throws ServletException, IOException, SQLException {
        //根据id查找院系
        Department department = DepartmentService.getInstance().find(id);
        String department_json = JSON.toJSONString(department);

        //响应Department_json到前端
        response.getWriter().println(department_json);
    }
    //响应所有院系对象
    private void responseDepartments(HttpServletResponse response)
            throws ServletException, IOException, SQLException {
        //获得所有院系
        Collection<Department> departments = DepartmentService.getInstance().findAll();
        String departments_json = JSON.toJSONString(departments, SerializerFeature.DisableCircularReferenceDetect);
        //响应Departments_json到前端
        response.getWriter().println(departments_json);
    }
    private void responseDepartmentSchool(int id, HttpServletResponse response)
            throws ServletException, IOException, SQLException {
        //创建JSON对象message，以便往前端响应信息
        JSONObject message = new JSONObject();
        Collection<Department> departments = DepartmentService.getInstance().findAllBySchool(id);
        //根据school的id查找所属的系

        String department_json = JSON.toJSONString(departments, SerializerFeature.DisableCircularReferenceDetect);
        //响应

        //加入数据信息
        message.put("statusCode", "200");
        message.put("message", "查找成功");
        message.put("data", department_json);

        //响应message到前端
        response.getWriter().println(department_json);
    }


}