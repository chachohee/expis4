package com.expis.community.dto;

import com.expis.common.CommonConstants;
import com.expis.common.paging.PagingDTO;
import com.expis.manage.dto.SystemOptionDto;
import com.expis.user.dto.UserDetailInfoDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import static com.expis.common.CommonConstants.YN_NO;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CmntViewDTO {
    private int boardMSeq = 1;
    private int nPage = 1;
    private int boardDSeq = 0;
    private int boardCSeq;
    private int boardFSeq = 0;
    private int relSeq;
    private int boardPSeq = 0;
    private int startRow;
    private int recordCnt;
    private int totCnt;
    private String boardTitle;
    private String boardContents;
    private String boardComments;
    private String relTitle;
    private String relUrl;
    private String relContents;
    private String comment;
    private String boardKind = "";
    private String className;
    private String searchCate = "none";
    private String searchValue = "none";
    private String boardClass;
    private String fileTranYn = "N";
    private String boardTopYn = "N";
    private String boardName;
    private String roleCode;
    private String userId;
    private boolean fileType = true;
    private FileMasterDTO file;
    private BoardMasterDTO boardOption;
    private BoardMasterDTO detailData;
    private PagingDTO pagingDTO;
    private RelatedDTO relatedDto;
    private UserDetailInfoDto userDetailInfo;
    private List<RelatedDTO> relatedList;
    private List<BoardMasterDTO> boardList;
    private List<BoardMasterDTO> topList;
    private List<BoardMasterDTO> commentList;
    private List<BoardMasterDTO> boardMList;
    private SystemOptionDto option;

    public static BoardMasterDTO ofBoard(CmntViewDTO cmntViewDTO) {
        return BoardMasterDTO.builder()
                .boardMSeq(cmntViewDTO.getBoardMSeq())
                .boardDSeq(cmntViewDTO.getBoardDSeq())
                .boardFSeq(cmntViewDTO.getBoardFSeq())
                .boardPSeq(cmntViewDTO.getBoardPSeq())
                .searchCate(cmntViewDTO.getSearchCate())
                .searchValue(URLDecoder.decode(cmntViewDTO.getSearchValue(), StandardCharsets.UTF_8))
                .statusKind(CommonConstants.ACTIVE)
                .boardTitle(cmntViewDTO.getBoardTitle())
                .boardContents(cmntViewDTO.getBoardContents())
                .boardFileYn(YN_NO)
                .boardTopYn(cmntViewDTO.getBoardTopYn())
                .boardFSeq(cmntViewDTO.getBoardFSeq())
                .boardTopYn(cmntViewDTO.getBoardTopYn())
                .nPage(cmntViewDTO.getNPage())
                .build();
    }

    public static BoardMasterDTO ofComment(CmntViewDTO cmntViewDTO) {
        return BoardMasterDTO.builder()
                .boardMSeq(cmntViewDTO.getBoardMSeq())
                .boardDSeq(cmntViewDTO.getBoardDSeq())
                .boardCSeq(cmntViewDTO.getBoardCSeq())
                .boardComments(cmntViewDTO.getBoardComments())
                .build();
    }

    public static RelatedDTO ofRelatedSite(CmntViewDTO cmntViewDTO) {
        return RelatedDTO.builder()
                .relSeq(cmntViewDTO.getRelSeq())
                .relTitle(cmntViewDTO.getRelTitle())
                .relUrl(cmntViewDTO.getRelUrl())
                .relContents(cmntViewDTO.getRelContents())
                .nPage(cmntViewDTO.getNPage())
                .build();
    }
}
