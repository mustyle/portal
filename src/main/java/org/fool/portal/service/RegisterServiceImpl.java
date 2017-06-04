package org.fool.portal.service;

import org.fool.portal.common.PortalResult;
import org.fool.portal.dao.UserMapper;
import org.fool.portal.model.User;
import org.fool.portal.model.UserExample;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RegisterServiceImpl implements RegisterService {
    @Autowired
    private UserMapper userMapper;

    @Override
    public PortalResult checkData(String param, int type) {
        UserExample userExample = new UserExample();

        UserExample.Criteria criteria = userExample.createCriteria();

        // 1 - username, 2 - phone, 3 - email
        if (1 == type) {
            criteria.andUsernameEqualTo(param);
        } else if (2 == type) {
            criteria.andPhoneEqualTo(param);
        } else if (3 == type) {
            criteria.andEmailEqualTo(param);
        }

        List<User> userList = userMapper.selectByExample(userExample);

        if (userList == null || userList.isEmpty()) {
            return PortalResult.ok(true);
        }

        return PortalResult.ok(false);
    }
}
