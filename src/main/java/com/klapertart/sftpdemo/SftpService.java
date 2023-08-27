package com.klapertart.sftpdemo;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Vector;

/**
 * @author tritr
 * @since 8/27/2023
 */

@Service
@Slf4j
public class SftpService {
    @Value("${sftp.remote.directory}")
    private String remoteDirectory;
    @Value("${sftp.file.filter}")
    private String filePrefixAndExtension;
    @Value("${sftp.local.directory:${java.io.tmpdir}/localDownload}")
    private String localDirectory;
    @Autowired
    private ChannelSftp channelSftp;
    @Autowired
    private Session session;

    public Vector<ChannelSftp.LsEntry> getFilesNamesFromSFTP(){
        Vector<ChannelSftp.LsEntry> files = new Vector<>();
        if(channelSftp != null && channelSftp.isConnected()){
            try {
                // Change to the remote directory
                channelSftp.cd(remoteDirectory);
                // List files in the remote directory
                files = channelSftp.ls(filePrefixAndExtension);
            } catch (SftpException e) {
                log.error("Could Not Create SFTP Channel, Possible reasons: {}", e.getMessage());
                e.printStackTrace();
            }
        }
        return files;
    }

    public String downloadFileToLocalDirFromSFTP(ChannelSftp.LsEntry file){
        if(channelSftp != null && channelSftp.isConnected()){
            try {
                String localFilePath = localDirectory + File.separator + file.getFilename();
                // Download the file from the remote directory to the local directory
                channelSftp.get(file.getFilename(), localFilePath);
                File downloadedFile = new File(localFilePath);
                Date dateModify = new Date(file.getAttrs().getMTime() * 1000L);
                downloadedFile.setLastModified(dateModify.getTime());
                log.info("File copied from SFTP, Filename: {}", downloadedFile.getName());
                return downloadedFile.getName();
            } catch (SftpException e) {
                log.error("Could Not Download File from SFTP Server, File Name: {}, Possible reasons: {}",file.getFilename(), e.getMessage());
                e.printStackTrace();
            }
        }
        return null;
    }

    public void moveFile(String fileName){
        Vector<ChannelSftp.LsEntry> files = new Vector<>();
        if(channelSftp != null && channelSftp.isConnected()){
            try {
                // Change to the remote directory
                channelSftp.cd(remoteDirectory);
                if (channelSftp.get(fileName) != null){
                    channelSftp.rename(remoteDirectory+"/"+fileName,remoteDirectory+"/tmp/"+fileName);
                }
            } catch (SftpException e) {
                log.error("Could Not Create SFTP Channel, Possible reasons: {}", e.getMessage());
                e.printStackTrace();
            }
        }
    }

    public void disconnectChannelAndSession(){
        // Disconnect the SFTP channel and session
        if (channelSftp != null && channelSftp.isConnected()) {
            channelSftp.disconnect();
            log.info("SFTP Channel Disconnected from SFTP Server");
        }
        if (session != null && session.isConnected()) {
            session.disconnect();
            log.info("SFTP Session Ended from SFTP Server");
        }
    }
}
