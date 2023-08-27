package com.klapertart.sftpdemo;

import com.jcraft.jsch.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;
import java.util.Date;
import java.util.Vector;

/**
 * @author tritr
 * @since 8/26/2023
 */

@Configuration
@Slf4j
public class SftpConfig {

    @Value("${sftp.user}")
    private String user;
    @Value("${sftp.password}")
    private String password;
    @Value("${sftp.host}")
    private String host;
    @Value("${sftp.port}")
    private String port;

    @Bean
    public Session session(){
        JSch jsch = new JSch();
        Session session = null;
        try {
            session = jsch.getSession(user, host,Integer.valueOf(port));
            session.setPassword(password);
            session.setConfig("StrictHostKeyChecking", "no");
            session.connect();
            log.info("SFTP Session Created");
        } catch (JSchException e) {
        /* Handle session or channel disconnect exception
        This catch block will handle any exceptions related to session or channel
        disconnects that are thrown by the session.connect() or channelSftp.connect() methods */

            log.error("Could Not Create SFTP Session, Possible reasons: {}", e.getMessage());
            e.printStackTrace();
        }
        return session;
    }

    @Bean
    public ChannelSftp channelSftp (Session session){
        ChannelSftp channelSftp = null;
        if (session != null && session.isConnected()){
            try {
                channelSftp = (ChannelSftp) session.openChannel("sftp");
                channelSftp.connect();
                log.info("SFTP Channel Created");
            } catch (JSchException e) {
            /* Handle SFTP related exception
            This catch block will handle any SFTP-related exceptions that may occur during SFTP
            operations like listing files (channelSftp.ls()) or changing directories (channelSftp.cd()) */

                log.error("Could Not Create SFTP Channel, Possible reasons: {}", e.getMessage());
                e.printStackTrace();
            }
        }
        return channelSftp;
    }

}
