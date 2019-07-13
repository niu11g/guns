package com.stylefeng.guns.rest.modular.film.vo;

import com.stylefeng.guns.api.film.vo.CatInfoVO;
import com.stylefeng.guns.api.film.vo.SourceInfoVO;
import com.stylefeng.guns.api.film.vo.YearInfoVO;
import lombok.Data;

import java.util.List;

@Data
public class FilmConditionList {
    private List<CatInfoVO> catInfo;
    private List<SourceInfoVO> sourceInfo;
    private List<YearInfoVO> yearInfo;
}
