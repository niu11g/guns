package com.stylefeng.guns.rest.modular.film.vo;

import lombok.Data;

@Data
public class FilmRequestVO {

    private int showType = 1;
    private int sortId = 1;
    private int catId = 99;
    private int sourceId = 99;
    private int yearId = 99;
    private int nowPage = 1;
    private int pageSize = 18;

}
