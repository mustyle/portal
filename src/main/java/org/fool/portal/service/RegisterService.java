package org.fool.portal.service;

import org.fool.portal.common.PortalResult;
import org.fool.portal.model.User;

public interface RegisterService {
    PortalResult checkData(String param, int type);

    PortalResult register(User user);
}
