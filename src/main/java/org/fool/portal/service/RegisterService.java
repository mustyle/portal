package org.fool.portal.service;

import org.fool.portal.common.PortalResult;

public interface RegisterService {
    PortalResult checkData(String param, int type);
}
