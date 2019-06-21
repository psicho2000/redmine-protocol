package de.psicho.redmine.protocol.api;

import com.taskadapter.redmineapi.RedmineException;
import com.taskadapter.redmineapi.UserManager;
import com.taskadapter.redmineapi.bean.User;

public class UserHandler {

    private UserManager userManager = null;

    public UserHandler(RedmineHandler redmineHandler) {
        userManager = redmineHandler.getRedmineManager().getUserManager();
    }

    public User getUserById(Integer userId) {
        try {
            return userManager.getUserById(userId);
        } catch (RedmineException ex) {
            ex.printStackTrace();
            return null;
        }
    }
}
