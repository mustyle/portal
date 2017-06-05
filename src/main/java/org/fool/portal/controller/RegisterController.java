package org.fool.portal.controller;

import org.apache.commons.lang3.StringUtils;
import org.fool.portal.common.PortalResult;
import org.fool.portal.model.User;
import org.fool.portal.service.RegisterService;
import org.fool.portal.util.ExceptionUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/user")
public class RegisterController {
    @Autowired
    private RegisterService registerService;

    @GetMapping("/check/{param}/{type}")
    @ResponseBody
    public Object checkData(@PathVariable String param, @PathVariable Integer type, String callback) {
        try {
            PortalResult portalResult = registerService.checkData(param, type);

            if (StringUtils.isNotBlank(callback)) {
                // 需要支持jsonp
                MappingJacksonValue mappingJacksonValue = new MappingJacksonValue(portalResult);
                mappingJacksonValue.setJsonpFunction(callback);

                return mappingJacksonValue;
            }

            return portalResult;
        } catch (Exception e) {
            e.printStackTrace();
            return PortalResult.build(500, ExceptionUtil.getStackTrace(e));
        }
    }

    @PostMapping("/register")
    @ResponseBody
    public PortalResult register(User user) {
        try {
            PortalResult portalResult = registerService.register(user);

            return portalResult;
        } catch (Exception e) {
            e.printStackTrace();
            return PortalResult.build(500, ExceptionUtil.getStackTrace(e));
        }

    }
}
