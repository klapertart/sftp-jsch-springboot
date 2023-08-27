package com.klapertart.sftpdemo;

import com.jcraft.jsch.ChannelSftp;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author tritr
 * @since 8/27/2023
 */

@Slf4j
@SpringBootTest
class SftpServiceTest {
    @Autowired
    private SftpService sftpService;

    @Test

    public void copySftpPdfFiles(){

        List<String> pdfFileList = new ArrayList<>();
        Vector<ChannelSftp.LsEntry> files = sftpService.getFilesNamesFromSFTP();

        if(files == null || files.isEmpty()){
            return;
        }
        for (ChannelSftp.LsEntry file : files) {
            if (file == null || file.getAttrs().isDir() || file.getAttrs().getSize() <= 0 ) {
                continue;
            }
            String downloadedFileName = sftpService.downloadFileToLocalDirFromSFTP(file);
            // ** when copy complete, add the name to list
            if(downloadedFileName == null){
                continue;
            }

            pdfFileList.add(downloadedFileName);
        }
        sftpService.disconnectChannelAndSession();
        // ** Add list of newly copied files ** //
        log.info("Number of New PDF Files Copied From SFTP: {}",pdfFileList.size());
        pdfFileList.forEach(fileName -> log.info("FILE NAME: {}", fileName));
    }

    @Test
    void getFilesNamesFromSFTP() {
        Vector<ChannelSftp.LsEntry> filesNamesFromSFTP = sftpService.getFilesNamesFromSFTP();
        filesNamesFromSFTP.forEach(lsEntry -> {
            log.info("NAMA FILE: {}", lsEntry.getFilename());
        });

        sftpService.disconnectChannelAndSession();
    }

    @Test
    void moveFile() {
        sftpService.moveFile("satu.txt");
    }
}