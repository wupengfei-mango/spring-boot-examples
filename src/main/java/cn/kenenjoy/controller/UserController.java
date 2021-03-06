package cn.kenenjoy.controller;

import cn.kenenjoy.domain.Result;
import cn.kenenjoy.domain.User;
import cn.kenenjoy.service.UserService;
import cn.kenenjoy.util.UUIDTool;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.List;

/**
 * Created by hefa on 2017/7/28.
 */
@Controller
public class UserController {
    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    @RequestMapping("/")
    public String index(HttpSession session) {
        session.setAttribute("uuid",UUIDTool.getUUID());
        log.debug("进入index");
        return "index";
    }

    @RequestMapping("/toolbar")
    public String toolbar() {
        log.debug("进入toolbar");
        return "toolbar";
    }

    @RequestMapping("/expanded")
    public String expande() {
        log.debug("进入expanded");
        return "expanded";
    }

    @RequestMapping("/show_form")
    public String showForm() {
        log.debug("进入showForm");
        return "show_form";
    }

    @RequestMapping("/rss")
    public String rss() {
        log.debug("进入rss");
        return "rssreader";
    }


    @RequestMapping(value = "get_feed",produces="text/html;charset=UTF-8")
    @ResponseBody
    public String getFeed(@RequestParam(value = "url", required = false) String url) {
        log.debug("进入delete_user");
        Gson gson = new Gson();
        Result result = new Result();
        result.setSuccess("success");
        result.setErrorMsg("未报错");
        return gson.toJson(result);
    }





    /**
     * 查询用户
     *
     * @return
     */
    @RequestMapping(value = "/get_users",produces="text/html;charset=UTF-8")
    @ResponseBody
    public String getUsers() {
        log.debug("进入get_users");
        List<User> users = userService.getUsers();
        Gson gson = new Gson();
        return gson.toJson(users);

    }

    /**
     * 修改用户
     *
     * @param user
     * @return
     */
    @RequestMapping(value = "update_user", method = RequestMethod.POST)
    @ResponseBody
    public String updateUser(@ModelAttribute(value = "user") User user) {
        log.debug("进入update_user");
        Gson gson = new Gson();
        Result result = new Result();
        if (user != null) {
            userService.updateUser(user);
            // 针对修改后需要返回值做修改
            // result.setSuccess("success");
            user = userService.findUserById(user.getId());
            return gson.toJson(user);
        } else {
            result.setErrorMsg("修改用户报错，用户为空！");
        }
        return gson.toJson(result);
    }

    /**
     * 保存用户
     *
     * @param user
     * @return
     */
    @RequestMapping(value = "save_user", method = RequestMethod.POST)
    @ResponseBody
    public String saveUser(@ModelAttribute(value = "user") User user) {
        log.debug("进入save_user");
        Gson gson = new Gson();
        Result result = new Result();
        if (user != null) {
            if (StringUtils.isEmpty(user.getId())) {
                user.setId(UUIDTool.getUUID());
            }
            try {
                userService.saveUser(user);
                result.setSuccess("success");
            } catch (BadSqlGrammarException e){
                result.setErrorMsg("保存用户数据库报错！");
                log.error("保存用户数据库报错！",e);
            }
            // 针对保存后需要返回值做修改
            user = userService.findUserById(user.getId());
            return gson.toJson(user);
        }else{
            result.setErrorMsg("保存用户报错，用户为空！");
        }
        return gson.toJson(result);
    }


    /**
     * 根据用户id删除
     *
     * @param id
     * @return
     */
    @RequestMapping(value = "delete_user", method = RequestMethod.POST)
    @ResponseBody
    public String deleteUser(@RequestParam(value = "id", required = false) String id) {
        log.debug("进入delete_user");
        Gson gson = new Gson();
        Result result = new Result();
        try {
            userService.deleteUser(id);
            result.setSuccess("success");
        } catch (BadSqlGrammarException e) {
            result.setErrorMsg("删除报错");
            log.error("删除用户报错", e);
        }
        return gson.toJson(result);
    }
}
