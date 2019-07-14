package com.stylefeng.guns.api.cinema.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class CinemaVO implements Serializable {
   private int brandId;
   private int hallType;
   private int districtId;
   private int pageSize;
   private int nowPage;
}
