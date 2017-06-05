package org.fool.portal.service;

import org.apache.commons.lang3.StringUtils;
import org.fool.portal.common.PortalResult;
import org.fool.portal.dao.UserMapper;
import org.fool.portal.model.User;
import org.fool.portal.model.UserExample;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.util.Date;
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

    @Override
    public PortalResult register(User user) {
        if (StringUtils.isBlank(user.getUsername()) || StringUtils.isBlank(user.getPassword())) {
            return PortalResult.build(400, "username or password cannot be empty");
        }

        PortalResult result = checkData(user.getUsername(), 1);
        if (!(boolean) result.getData()) {
            return PortalResult.build(400, "username duplicate");
        }

        if (StringUtils.isNotBlank(user.getPhone())) {
            result = checkData(user.getPhone(), 2);

            if (!(boolean) result.getData()) {
                return PortalResult.build(400, "phone duplicate");
            }
        }

        if (StringUtils.isNotBlank(user.getEmail())) {
            result = checkData(user.getEmail(), 3);

            if (!(boolean) result.getData()) {
                return PortalResult.build(400, "email duplicate");
            }
        }

        user.setCreated(new Date());
        user.setUpdated(new Date());

        user.setPassword(DigestUtils.md5DigestAsHex(user.getPassword().getBytes()));

        userMapper.insert(user);

        return PortalResult.ok();
    }
}
