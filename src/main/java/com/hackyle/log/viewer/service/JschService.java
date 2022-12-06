package com.hackyle.log.viewer.service;

import com.jcraft.jsch.Session;

public interface JschService {
    Session buildConnect();

    void destroyConnect(Session sshSession);
}
