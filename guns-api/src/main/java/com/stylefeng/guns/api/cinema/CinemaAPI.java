package com.stylefeng.guns.api.cinema;

import com.stylefeng.guns.api.cinema.vo.CinemaRequestVO;
import java.util.List;

public interface CinemaAPI {

    List<CinemaRequestVO> getCinemas(int brandId, int hallType, int districtId, int pageSize, int nowPage);

}
