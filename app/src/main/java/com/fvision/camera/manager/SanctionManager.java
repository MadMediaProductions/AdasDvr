package com.fvision.camera.manager;

public class SanctionManager {
    public static String getSanctionName(int sanction) {
        switch (sanction) {
            case 616:
                return "不支持厂商号";
            case 233333:
                return "歌诗特";
            case 233408:
                return "沃广视";
            case 233519:
                return "环翔";
            case 233620:
                return "云智驾";
            case 233731:
                return "安智享";
            case 233842:
                return "创鑫博业";
            case 233953:
                return "亿能";
            default:
                return "其它";
        }
    }
}
