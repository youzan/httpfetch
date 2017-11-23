package com.github.youzan.httpfetch.resolver;

import com.alibaba.fastjson.annotation.JSONField;

import java.awt.image.BufferedImage;

/**
 * Created by daiqiang on 17/7/24.
 */
public class ImageParam {

    @JSONField(name = "image", serialize = false, deserialize = false)
    private BufferedImage image;

    private String imageName;

    public BufferedImage getImage() {
        return image;
    }

    public void setImage(BufferedImage image) {
        this.image = image;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }
}
