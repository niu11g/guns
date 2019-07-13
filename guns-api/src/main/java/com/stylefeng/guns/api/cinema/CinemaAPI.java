package com.stylefeng.guns.api.cinema;

import com.stylefeng.guns.api.cinema.vo.CinemaVO;
import java.util.List;

public interface CinemaAPI {

    List<CinemaVO> getCinemas(int brandId,int hallType,int districtId,int pageSize,int nowPage);

}
