package com.expis.ietm.service;

import com.expis.ietm.component.TreeComponent;
import com.expis.manage.dao.TreeXContMapper;
import com.expis.manage.dto.SystemInfoDto;
import com.expis.manage.dto.TreeXContDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.w3c.dom.Node;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TreeService {

    private final TreeComponent treeComponent;
    private final TreeXContMapper treeMapper;

    public StringBuffer getToTree(TreeXContDto treeDto) {
        return treeComponent.getToTree(treeDto);
    }

    public List<SystemInfoDto> getRelatedToList(SystemInfoDto sysDto) {
        return treeComponent.getRelatedToList(sysDto);
    }

    public ArrayList<TreeXContDto> selectSiblingList(String toKey) {
        ArrayList<TreeXContDto> siblingList = new ArrayList<TreeXContDto>();

        siblingList = treeMapper.selectSiblingList(toKey);

        return siblingList;
    }

    public StringBuffer getSubTocoList(TreeXContDto treeDto) {
        return treeComponent.getSubTocoList(treeDto);
    }

    public List<SystemInfoDto> getSystemToList(SystemInfoDto sysDto) {
        return treeComponent.getSystemToList(sysDto);
    }

    public Node getToTreeDom(TreeXContDto treeDto) {
        return treeComponent.getToTreeDom(treeDto);
    }
}