package com.expis.user.component;

import com.expis.ietm.dto.MainDto;
import com.expis.user.dao.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserComponent {

    private final UserMapper userMapper;

    public MainDto getDisConnDate(MainDto dto) {

        MainDto mainDto = new MainDto();
        MainDto resultDto = new MainDto();
        List<MainDto> mainList = null;
        int num = 0;
        try {

            String connDate = "";
            String dissConnDate = "";

            mainDto = userMapper.getSLoginIngoTest(dto);
            if(mainDto != null) {
                if(mainDto.getConnDate().isEmpty()) {
                    connDate = "-";
                    dissConnDate = "-";
                } else {
                    connDate = mainDto.getConnDate();
                    log.info("mainDto.getDisconnDate() : " + mainDto.getDisconnDate());
                    dissConnDate = mainDto.getDisconnDate();
                }
            } else {
                connDate = "-";
                dissConnDate = "-";
            }

            log.info("dissConnDate : " + dissConnDate);

            resultDto.setConnDate(connDate);

            if(dissConnDate == null) {
                resultDto.setDisconnDate("");
            } else {
                resultDto.setDisconnDate(dissConnDate);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return resultDto;
    }
}
