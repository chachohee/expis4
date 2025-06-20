package com.expis.ietm.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
public class MyToDto {
    private String dbType;

    private String userId;
    private String toKey;
    private String tocoId;
    @JsonProperty("pTocoId")
    private String pTocoId;
    private long   tocoOrd;
    private long   mytoOrd;
    private String mytoName;
    private String statusKind;
    private String createUserId;
    private Date   createDate;
    private String modifyUserId;
    private Date   modifyDate;

    private long   mytoPSeq;

    //    private long   pMytoSeq;
    private long   mytoDepth;
    private long   mytoSeq;
    private long   mytocoSeq;
    private String mytocoName;
    private String mytocoKind;
    private	long	mytocoOrd;

    private String toName;
    private String tocoName;
    private String mytoKind;

    private int num;

    private String gubun;
    private String pToKey;

    private String paraToKey;
    private long paraMytoSeq;
    private int paraPageIndex;

    //MYTO 파싱 관련
    private String myId;
    private String myItemid;
    private String myName;
    private String myStatus;
    private String myType;
    private String myVehicletype;
    private String myVersion;
    private String childCnt;

    private String treeKind;
    private String treeRef;
    private int treeXth;
    private String treeXcont;

    private String position;
    private String order;
    private String offspring;

    //PagingDto에 포함된 항목
    private int firstRecordIndex;

    private List<String> paramToKey;
    private List<String> paramTocoId;
    private List<String> paramTocoName;
    private List<String> paramParentId;
    private List<String> paramType;
    private List<Long> paramOrd;
}
