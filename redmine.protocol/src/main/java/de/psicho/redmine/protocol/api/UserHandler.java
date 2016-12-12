package de.psicho.redmine.protocol.api;

import org.springframework.stereotype.Component;

import com.taskadapter.redmineapi.RedmineException;
import com.taskadapter.redmineapi.UserManager;
import com.taskadapter.redmineapi.bean.User;

@Component
public class UserHandler {

    UserManager userManager = null;

    public UserHandler(RedmineHandler redmineHandler) {
        userManager = redmineHandler.getRedmineManager().getUserManager();
    }

    public User getUserById(String userId) {
        Integer id = Integer.valueOf(userId);
        try {
            return userManager.getUserById(id);
        } catch (RedmineException ex) {
            ex.printStackTrace();
            return null;
        }
    }
}
