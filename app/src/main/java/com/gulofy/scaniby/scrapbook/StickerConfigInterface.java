package com.gulofy.scaniby.scrapbook;

public interface StickerConfigInterface {

    public enum STICKER_TYPE {
        IMAGE,
        TEXT,
        STICKER
    }

    int getStickerId();

    STICKER_TYPE getType();
}
