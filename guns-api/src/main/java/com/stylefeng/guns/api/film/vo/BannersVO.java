package com.stylefeng.guns.api.film.vo;

import lombok.Data;
import lombok.Setter;
import java.io.Serializable;

@Data
public class BannersVO implements Serializable {

    @Setter
    private String bannerId;

    private String bannerAddress;

    private String bannerUrl;

}
