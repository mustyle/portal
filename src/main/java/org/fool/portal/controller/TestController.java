package org.fool.portal.controller;

import org.fool.portal.common.PortalResult;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

@Controller
public class TestController {

    @PostMapping("posttest")
    @ResponseBody
    public PortalResult postTest(@RequestBody Map map) {
        System.out.println(map.get("username"));
        System.out.println(map.get("password"));

        return PortalResult.ok();
    }
}
