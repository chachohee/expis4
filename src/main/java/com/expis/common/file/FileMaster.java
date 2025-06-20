package com.expis.common.file;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import com.expis.common.IConstants;
import com.expis.community.dto.FileMasterDTO;
import com.expis.util.StringUtil;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.MultipartRequest;

@Slf4j
public class FileMaster {

    private static final int COMPRESSION_LEVEL = 8;
    private static final int BUFFER_SIZE = 1024 * 2;

    /**
     * 파일 업로드
     */
    public FileMasterDTO fileUpload(FileMasterDTO fileDto, MultipartHttpServletRequest multi, String filePath, String inputName, int renameType) {

        if (inputName == null || inputName.equals("")) {
            inputName = "pup_imgFile";
        }

        try {
            MultipartFile file = multi.getFile(inputName);
            long fileSize = file.getSize();
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();

            String uId = UUID.randomUUID() + "";
            //String fileNameUpload = uId + multi.getFile("pup_imgFile").getOriginalFilename();

            String orgFileName = multi.getFile(inputName).getOriginalFilename();
            String fileType = orgFileName.substring(orgFileName.lastIndexOf(".") + 1, orgFileName.length());
            String userId = auth.getName();

            fileDto.setFileSize(convertFileSize(fileSize));
            fileDto.setFileOrgNm(orgFileName);
            fileDto.setFileType(fileType);
            fileDto.setFilePath(filePath);
            fileDto.setFileStrNm(uId);
            fileDto.setCreateUserId(userId);

            File dir = new File(filePath);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            String newFileName = "";
            if (renameType == IConstants.FILE_UPLOAD_UUID) {
                newFileName = filePath  + uId + "." + fileType;
                fileDto.setFileStrNm(uId);
            } else if (renameType == IConstants.FILE_UPLOAD_ORG) {
                newFileName = filePath  + orgFileName;
                fileDto.setFileStrNm( StringUtil.replace(orgFileName, "."+fileType, "") );
            } else {
                newFileName = filePath  + uId + "." + fileType;
                fileDto.setFileStrNm(uId);
            }

            Path path = Paths.get(newFileName).toAbsolutePath();
            file.transferTo(path.toFile());

            log.info("//filePath : " + filePath);
            log.info("//newFileName : " + newFileName);
        } catch (IOException e) {
            e.printStackTrace();
            log.debug("FileMaster.fileUpload Exception : " + e.toString());
        }

        return fileDto;
    }


    public FileMasterDTO fileUpload(FileMasterDTO fileDto, MultipartHttpServletRequest multi, String filePath) {
        String inputName = "";
        return fileUpload(fileDto, multi, filePath, inputName, IConstants.FILE_UPLOAD_UUID);

    }

    /**
     * 파일 다운로드
     * 2025.04.16 - osm
     * 기존 파일 다운로드 로직 주석 후 리팩토링
     */
    public void fileDownload(HttpServletResponse response,
                             HttpServletRequest request,
                             FileMasterDTO dto,
                             String filePath) {
        try {
            request.setCharacterEncoding("UTF-8");
            int bufferSize = 4096;
            String fullPath;
            File downloadFile;

            if (dto == null) {
                fullPath = filePath;
                downloadFile = new File(fullPath);
            } else {
                fullPath = filePath + dto.getFileStrNm() + "." + dto.getFileType();
                downloadFile = new File(fullPath);
            }

            if (!downloadFile.exists()) {
                throw new FileNotFoundException("파일이 존재하지 않습니다: " + fullPath);
            }

            // MIME type 설정
            String mimeType = Optional.ofNullable(
                    request.getServletContext().getMimeType(fullPath)
            ).orElse("application/octet-stream");

            response.setContentType(mimeType);
            response.setContentLength((int) downloadFile.length());

            // 파일명 인코딩 처리
            String fileName = dto == null
                    ? new String(downloadFile.getName().getBytes(StandardCharsets.UTF_8), "ISO8859_1")
                    : URLEncoder.encode(dto.getFileOrgNm(), StandardCharsets.UTF_8).replaceAll("\\+", "%20");

            response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");

            // 스트림 처리
            try (FileInputStream in = new FileInputStream(downloadFile);
                 ServletOutputStream out = response.getOutputStream()) {

                byte[] buffer = new byte[bufferSize];
                int bytesRead;
                while ((bytesRead = in.read(buffer)) != -1) {
                    out.write(buffer, 0, bytesRead);
                }
            }

        } catch (FileNotFoundException | UnsupportedEncodingException e) {
            log.error("FileMaster.fileDownload Exception : {}", e.toString(), e);
        } catch (IOException e) {
            log.error("FileMaster.fileDownload IOException : {}", e.toString(), e);
        }
    }

/*
    @SuppressWarnings("unused")
    public int fileDownload(HttpServletResponse response
            , HttpServletRequest request
            , FileMasterDTO dto
            , String filePath) {
        int result = 0;

        try {
            request.setCharacterEncoding("UTF-8");
            String fileReal = "";

            int bufferSize = 4096;

            if(dto == null) {
                ServletContext context = request.getServletContext();
                fileReal = filePath;
                File downloadFile = new File(fileReal);
                FileInputStream inputStream;
                inputStream = new FileInputStream(downloadFile);

                // get MIME type of the file
                String mimeType = context.getMimeType(fileReal);

                if(mimeType == null) {
                    // set to binary type if MIME mapping not found
                    mimeType = "application/octet-stream";
                }
                // set content attributes for the response
                response.setContentType(mimeType);
                response.setContentLength((int) downloadFile.length());

                //20191211 add LYM 한글명 파일 인코딩 추가
                String fileName = new String(downloadFile.getName().getBytes(), "ISO8859_1");

                // set headers for the response
                String headerKey = "Content-Disposition";
                //String headerValue = String.format("attachment; filename=\"%s\"", downloadFile.getName());
                String headerValue = String.format("attachment; filename=\"%s\"", fileName);

                response.setHeader(headerKey, headerValue);

                // get output stream of the response
                OutputStream outStream = response.getOutputStream();
                byte[] buffer = new byte[bufferSize];
                int bytesRead = -1;

                // write bytes read from the input stream into the output stream
                while((bytesRead = inputStream.read(buffer)) != -1){
                    outStream.write(buffer, 0, bytesRead);
                }

                inputStream.close();
                outStream.close();
            } else {
                String fileOrgName = dto.getFileOrgNm();
                String fileUuid = dto.getFileStrNm();
                String fileType = dto.getFileType();
                fileReal = filePath + fileUuid + "." +fileType;
                byte b[] = new byte[bufferSize];
                response.reset();
                response.setContentType("application/octet-stream");

                String Encoding = java.net.URLEncoder.encode(fileOrgName, "UTF-8");
                response.setHeader("Content-Disposition", "attachment; filename = " + Encoding);
                FileInputStream in = new FileInputStream(fileReal);
                ServletOutputStream outs = response.getOutputStream();

                int numRead;
                while ((numRead = in.read(b, 0, b.length)) != -1) {
                    outs.write(b, 0, numRead);
                }

                outs.flush();
                outs.close();
                in.close();
                result = 1;
            }


        } catch (FileNotFoundException | UnsupportedEncodingException e) {
            e.printStackTrace();
            result = 0;
        } catch (IOException e) {
            e.printStackTrace();
            result = 0;
        }

        return result;
    }
*/

    /**
     * 파일 삭제
     */
    public boolean fileDelete(String fileStrNm, String filePath) {
        boolean rtBl = false;

        try {
            File file = new File(filePath + fileStrNm);

            if (file.exists() == true) {
                file.delete();
            }
            rtBl = true;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return rtBl;
    }

    public boolean fileDelete(String filePath) {
        return fileDelete("", filePath);
    }

    public boolean fileDelete(FileMasterDTO fileDto) {
        String filePath = fileDto.getFilePath() + fileDto.getFileStrNm() + "." + fileDto.getFileType();
        return fileDelete("", filePath);
    }


    /**
     * 유효한 파일 확장자인지 검사
     */
    public boolean typeValidate(MultipartRequest multi, String fileExtAll, String maxUploadSize, String inputName) {

        boolean fileResult = false;

        if (inputName == null || inputName.equals("")) {
            inputName = "pup_imgFile";
        }

        try {
            MultipartFile file = multi.getFile(inputName);

            String org_file_nm = file.getOriginalFilename();
            String  fileExt = org_file_nm.substring(org_file_nm.indexOf("."));
            long fileSize = file.getSize();

            String[] fileExtArr = fileExtAll.split(",");

            if (!fileExtAll.equals("")) {
                for(int i=0; i<fileExtArr.length; i++) {
                    String fileExtValue = fileExtArr[i];
                    fileExt = fileExt.toLowerCase();

                    log.info("fileExt : " + fileExt);
                    log.info(fileExt + " = " + "." + fileExtValue);

                    if(fileExt.equals("." + fileExtValue)) {
                        log.info("등록 가능 합니다.");
                        fileResult = true;
                        break;
                    } else {
                        log.info("등록 가능한 File Format이 아닙니다.");
                        fileResult = false;
                    }
                }
            }
            fileResult = true;
            if (fileResult == true) {
                // 파일 사이즈 체크(바이트 단위)
                long maxUploadSizeLong = Long.parseLong(maxUploadSize);

                if(fileSize == 0) {
                    fileResult = false;
                }else if(fileSize > maxUploadSizeLong) {
                    fileResult = false;
                } else {
                    fileResult = true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.debug("FileMaster.typeValidate Exception : " + e.toString());
        }

        return  fileResult;
    }

    public boolean typeValidate(MultipartHttpServletRequest multi, String fileExtAll, String maxUploadSize) {
        String inputName = "";

        return typeValidate(multi, fileExtAll, maxUploadSize, inputName);
    }


    /**
     * 파일 크기(사이즈) 추출 - 정수값으로 리턴
     */
    public int getFileSize(String filePath) {

        int rtInt = 0;

        try {
            File file = new File(filePath);

            if (file.exists() == true) {
                rtInt = (int)file.length();
            }

        } catch (Exception e) {
            e.printStackTrace();
            log.debug("FileMaster.getFileSize Exception : " + e.toString());
        }

        return rtInt;
    }


    /**
     * 파일 크기(사이즈) 추출 - 문자열로 추출
     */
    public String convertFileSize(long filesize)	{

        String rtStr = "";

        try {
            if(filesize <= 1024) {
                rtStr = filesize + " Byte";

            } else if(filesize > 1024 && filesize < (1024 * 1024)) {
                double longtemp = filesize / (double)1024;
                int len = Double.toString(longtemp).indexOf(".");
                rtStr = Double.toString(longtemp).substring(0,len) + "k";

            } else if(filesize >= (1024*1024)) {
                double longtemp = filesize / ((double)1024 * 1024);
                int len = Double.toString(longtemp).indexOf(".");
                rtStr = Double.toString(longtemp).substring(0,len) + "m";
            }

        } catch (Exception e) {
            e.printStackTrace();
            log.debug("FileMaster.convertFileSize Exception : " + e.toString());
        }

        return rtStr;
    }


    /**
     * 백업파일 복사
     */
    public int fileZip(String path, String output, String fileName)throws Exception {
        int rtInt = -1;
        String outputFile = "";

        //압축할 경로를 가져온다
        File sourceFile = new File(path);
        File outputPath = new File(output);

        FileOutputStream fos = null;
        BufferedOutputStream bos = null;
        ZipOutputStream zos = null;

        if(!outputPath.exists()) {
            outputPath.mkdirs();
        }

        try {
            outputFile = outputPath + "/" + fileName + ".zip";
            fos = new FileOutputStream(outputFile);
            bos = new BufferedOutputStream(fos); // BufferedStream
            zos = new ZipOutputStream(bos); // ZipOutputStream
            zos.setLevel(8); // 압축 레벨 - 최대 압축률은 9, 디폴트 8
            zipEntry(sourceFile, path, zos); // Zip 파일 생성
            zos.finish(); // ZipOutputStream finish

        } finally {
            if(zos != null) {
                zos.close();
            }
            if(bos != null) {
                bos.close();
            }
            if(fos != null) {
                fos.close();
            }
        }

        return rtInt;
    }

    private void zipEntry(File sourceFile, String sourcePath, ZipOutputStream zos) throws Exception {
        // sourceFile 이 디렉토리인 경우 하위 파일 리스트 가져와 재귀호출
        if(sourceFile.isDirectory()) {
            if(sourceFile.getName().equalsIgnoreCase(".metadata")) { // .metadata 디렉토리 return
                return;
            }

            File[] fileArray = sourceFile.listFiles(); // sourceFile 의 하위 파일 리스트
            for(int i = 0; i < fileArray.length; i++) {
                zipEntry(fileArray[i], sourcePath, zos); // 재귀 호출
            }

        } else { // sourcehFile 이 디렉토리가 아닌 경우
            BufferedInputStream bis = null;

            try {
                String sFilePath = sourceFile.getPath();
                String zipEntryName = sFilePath.substring(sourcePath.length() + 1, sFilePath.length());

                bis = new BufferedInputStream(new FileInputStream(sourceFile));
                ZipEntry zentry = new ZipEntry(zipEntryName);
                zentry.setTime(sourceFile.lastModified());
                zos.putNextEntry(zentry);

                byte[] buffer = new byte[1024 * 2];
                int cnt = 0;
                while((cnt = bis.read(buffer, 0, 1024 * 2)) != -1) {
                    zos.write(buffer, 0, cnt);
                }
                zos.closeEntry();
            } finally {
                if(bis != null) {
                    bis.close();
                }
            }
        }
    }
    /**
     * Zip 파일의 압축을 푼다.
     *
     * @param zipFile - 압축 풀 Zip 파일
     * @param targetDir - 압축 푼 파일이 들어간 디렉토리
     * @param fileNameToLowerCase - 파일명을 소문자로 바꿀지 여부
     * @throws Exception
     */
    public static void unzip(File zipFile, File targetDir, boolean fileNameToLowerCase) throws Exception {
        FileInputStream fis = null;
        ZipInputStream zis = null;
        ZipEntry zentry = null;

        try {
            fis = new FileInputStream(zipFile); // FileInputStream
            zis = new ZipInputStream(fis); // ZipInputStream

            while ((zentry = zis.getNextEntry()) != null) {
                String fileNameToUnzip = zentry.getName();
                if (fileNameToLowerCase) { // fileName toLowerCase
                    fileNameToUnzip = fileNameToUnzip.toLowerCase();
                }

                File targetFile = new File(targetDir, fileNameToUnzip);

                if (zentry.isDirectory()) {// Directory 인 경우
                    FileUtils.forceMkdir(targetFile);
                } else { // File 인 경우
                    // parent Directory 생성
                    FileUtils.forceMkdir(targetFile.getParentFile());
                    unzipEntry(zis, targetFile);
                }
            }
        } finally {
            if (zis != null) {
                zis.close();
            }
            if (fis != null) {
                fis.close();
            }
        }
    }

    /**
     * Zip 파일의 한 개 엔트리의 압축을 푼다.
     */
    protected static File unzipEntry(ZipInputStream zis, File targetFile) throws Exception {
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(targetFile);

            byte[] buffer = new byte[BUFFER_SIZE];
            int len = 0;
            while ((len = zis.read(buffer)) != -1) {
                fos.write(buffer, 0, len);
            }
        } finally {
            if (fos != null) {
                fos.close();
            }
        }
        return targetFile;
    }

    /**
     * 디렉토리 파일삭제.
     */
    public static  boolean deleteDirectory(File directoryToBeDeleted) {
        File[] allContents = directoryToBeDeleted.listFiles();
        if (allContents != null) {
            for (File file : allContents) {
                deleteDirectory(file);
            }
        }
        return directoryToBeDeleted.delete();
    }

    /**
     * 2022 11 18 Park.J.S. 일반 파일 복사용으로 추가
     */
    public FileMasterDTO fileUpload(FileMasterDTO fileDto, File file, String filePath, String inputName, int renameType) {

        if (inputName == null || inputName.equals("")) {
            inputName = "pup_imgFile";
        }

        try {
            long fileSize = file.getTotalSpace();
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();

            String uId = UUID.randomUUID() + "";
            //String fileNameUpload = uId + multi.getFile("pup_imgFile").getOriginalFilename();

            String orgFileName = file.getName();
            String fileType = orgFileName.substring(orgFileName.lastIndexOf(".") + 1, orgFileName.length());
            String userId = auth.getName();

            fileDto.setFileSize(convertFileSize(fileSize));
            fileDto.setFileOrgNm(orgFileName);
            fileDto.setFileType(fileType);
            fileDto.setFilePath(filePath);
            fileDto.setFileStrNm(uId);
            fileDto.setCreateUserId(userId);

            File dir = new File(filePath);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            String newFileName = "";

            //임시
            newFileName = filePath  + uId + "." + fileType;
            fileDto.setFileStrNm(uId);
//			if (renameType == IConstants.FILE_UPLOAD_UUID) {
//				newFileName = filePath  + uId + "." + fileType;
//				fileDto.setFileStrNm(uId);
//			} else if (renameType == IConstants.FILE_UPLOAD_ORG) {
//				newFileName = filePath  + orgFileName;
//				fileDto.setFileStrNm( StringUtil.replace(orgFileName, "."+fileType, "") );
//			} else {
//				newFileName = filePath  + uId + "." + fileType;
//				fileDto.setFileStrNm(uId);
//			}

            byte fileData[] = Files.readAllBytes(Paths.get(file.getAbsolutePath()));
            FileOutputStream fos = new FileOutputStream(newFileName);

            fos.write(fileData);
            fos.close();
            log.info("//filePath : " + filePath);
            log.info("//newFileName : " + newFileName);
        } catch (IOException e) {
            e.printStackTrace();
            log.debug("FileMaster.fileUpload Exception : " + e.toString());
        }

        return fileDto;
    }
}
