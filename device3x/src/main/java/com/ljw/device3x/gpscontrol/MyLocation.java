package com.ljw.device3x.gpscontrol;
public class MyLocation {
    public double lat;       // 纬度
    public double lon;       // 经度
    public float speed;     // km/h
    public short course;    // 0-359
    public float alt;       // 海拔高度 altitude
    public long  time;      // 时间戳

    public String toString() {
        if(this != null)
            return "current lat:" + lat + "--" + "current lon" + lon + "--" + "current speed" + speed + "--"
                 + "current direction" + course + "--" + "current alt" + alt + "current time" + time;
        return "null";
    }
}
