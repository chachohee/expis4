package com.expis.community.service;

import com.expis.common.file.FileMaster;
import com.expis.community.dao.FileMasterMapper;
import com.expis.community.dto.FileMasterDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import java.util.function.Consumer;

import static com.expis.common.CommonConstants.PUP_IMG_FILE;

@Service
@RequiredArgsConstructor
public class FileMasterService {
	private final FileMasterMapper fileMasterMapper;

	@Value("${app.expis.maxUploadSize}")
	private String maxUploadSize;
	@Value("${app.expis.fileRelPath}")
	private String fileRelPath;
	@Value("${app.expis.fileExt}")
	private String fileExtAll;

	/**
	 * 파일 저장
	 *
	 * @param fileMasterDTO                fileMasterDTO
	 * @return FileMasterDTO
	 */
	public int insertFile(FileMasterDTO fileMasterDTO) {
		fileMasterMapper.insertFile(fileMasterDTO);
		return fileMasterDTO.getFileSeq();
	}

	/**
	 * 파일 선택
	 *
	 * @param fileSeq                파일번호
	 * @return FileMasterDTO
	 */
	public FileMasterDTO fileSelect(int fileSeq) {
		return fileMasterMapper.fileSelect(fileSeq);
	}

	/**
	 * 파일 업로드 및 기존 파일 삭제
	 *
	 * @param mult                Multipart 요청 객체
//	 * @param shouldDeleteOldFile 기존 파일 삭제 여부 (true일 경우 기존 파일 삭제 시도)
	 * @param fileSeqConsumer     업로드된 파일 시퀀스를 설정할 Consumer
	 * @return 업로드된 파일 시퀀스 번호 또는 0
	 */
	public int handleFileUpload(MultipartHttpServletRequest mult, Consumer<Integer> fileSeqConsumer) {

		// 1. 업로드 대상 파일 가져오기
		MultipartFile file = mult.getFile(PUP_IMG_FILE);
		FileMaster fileMaster = new FileMaster();

		int existingFileSeq = 0; //기존 파일 삭제하기위해 만든 변수

		// 2. 파일이 비어 있을 경우
		if(file.getOriginalFilename().isBlank()){
			deleteOldFile(existingFileSeq, fileMaster);
			fileSeqConsumer.accept(0);
			return 0;
        }

		// 3. 파일 유효성 체크
		boolean isValid = fileMaster.typeValidate(mult, fileExtAll, maxUploadSize);
		if(!isValid){
			fileSeqConsumer.accept(existingFileSeq);
			return existingFileSeq;
		}

		// 4. 파일 업로드
		FileMasterDTO fileMasterDTO = fileMaster.fileUpload(new FileMasterDTO(), mult, fileRelPath);
		int fileSeq = insertFile(fileMasterDTO);

		deleteOldFile(existingFileSeq, fileMaster); //기존 파일 있다면 삭제
		fileSeqConsumer.accept(fileSeq); // 새 파일 번호 DTO에 반영
		return fileSeq;
	}

	/**
	 * 기존 파일 삭제
	 *
	 * @param existingFileSeq      	    기존 파일 번호
	 * @param fileMethod                업로드된 파일 시퀀스를 설정할 Consumer
	 */
	private void deleteOldFile(int existingFileSeq, FileMaster fileMethod) {
		if (existingFileSeq != 0) {
			fileMethod.fileDelete(fileSelect(existingFileSeq));
		}
	}
}
