package com.hdsc.edog.jni;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import com.fvision.camera.App;
import com.fvision.camera.R;
import com.fvision.camera.ui.MainActivity;
import com.hdsc.edog.service.TuzhiService;
import java.io.IOException;
import java.io.InputStream;

public class DataShow {
    private static final String TAG = "DataShow";
    private static Context mContext;
    static int[] resId = {R.mipmap.edog_icon_spd_30, R.mipmap.edog_icon_spd_40, R.mipmap.edog_icon_spd_50, R.mipmap.edog_icon_spd_60, R.mipmap.edog_icon_spd_70, R.mipmap.edog_icon_spd_80, R.mipmap.edog_icon_spd_90, R.mipmap.edog_icon_spd_100, R.mipmap.edog_icon_spd_110, R.mipmap.edog_icon_spd_120, R.mipmap.edog_icon_spd_130, R.mipmap.edog_icon_traffic_light, R.mipmap.edog_icon_ty};
    private boolean BLOCKFlage = false;
    private int count = 0;
    private EdogDataManager edogDataManager;

    public DataShow(Context context) {
        mContext = context;
        if (this.edogDataManager == null) {
            this.edogDataManager = new EdogDataManager(context);
        }
    }

    public void radarDataShow(int radarType) {
        switch (radarType) {
            case 0:
                if (MainActivity.radarImg != null) {
                    MainActivity.radarImg.setImageResource(R.mipmap.edog_rd1_yes);
                    return;
                }
                return;
            case 1:
                if (MainActivity.isMinMode) {
                    if (!this.edogDataManager.GetgetLogDisp() && !TuzhiService.radarfvIsVisible) {
                        App.operateFloatView(mContext, TuzhiService.ACTION_CRREATE_RADAR_FLOATVIEW);
                    }
                    if (TuzhiService.mRadarFloatView != null) {
                        TuzhiService.mRadarFloatView.setImageResource(R.mipmap.edog_cam_la);
                        return;
                    }
                    return;
                } else if (MainActivity.radarTypeImg != null) {
                    MainActivity.radarTypeImg.setImageResource(R.mipmap.edog_cam_la);
                    return;
                } else {
                    return;
                }
            case 2:
                if (MainActivity.isMinMode) {
                    if (!this.edogDataManager.GetgetLogDisp() && !TuzhiService.radarfvIsVisible) {
                        App.operateFloatView(mContext, TuzhiService.ACTION_CRREATE_RADAR_FLOATVIEW);
                    }
                    if (TuzhiService.mRadarFloatView != null) {
                        TuzhiService.mRadarFloatView.setImageResource(R.mipmap.edog_cam_k);
                        return;
                    }
                    return;
                } else if (MainActivity.radarTypeImg != null) {
                    MainActivity.radarTypeImg.setImageResource(R.mipmap.edog_cam_k);
                    return;
                } else {
                    return;
                }
            case 3:
                if (MainActivity.isMinMode) {
                    if (!this.edogDataManager.GetgetLogDisp() && !TuzhiService.radarfvIsVisible) {
                        App.operateFloatView(mContext, TuzhiService.ACTION_CRREATE_RADAR_FLOATVIEW);
                    }
                    if (TuzhiService.mRadarFloatView != null) {
                        TuzhiService.mRadarFloatView.setImageResource(R.mipmap.edog_cam_ka);
                        return;
                    }
                    return;
                } else if (MainActivity.radarTypeImg != null) {
                    MainActivity.radarTypeImg.setImageResource(R.mipmap.edog_cam_ka);
                    return;
                } else {
                    return;
                }
            case 4:
                if (MainActivity.isMinMode) {
                    if (!this.edogDataManager.GetgetLogDisp() && !TuzhiService.radarfvIsVisible) {
                        App.operateFloatView(mContext, TuzhiService.ACTION_CRREATE_RADAR_FLOATVIEW);
                    }
                    if (TuzhiService.mRadarFloatView != null) {
                        TuzhiService.mRadarFloatView.setImageResource(R.mipmap.edog_cam_ku);
                        return;
                    }
                    return;
                } else if (MainActivity.radarTypeImg != null) {
                    MainActivity.radarTypeImg.setImageResource(R.mipmap.edog_cam_ku);
                    return;
                } else {
                    return;
                }
            case 5:
                if (MainActivity.isMinMode) {
                    if (!this.edogDataManager.GetgetLogDisp() && !TuzhiService.radarfvIsVisible) {
                        App.operateFloatView(mContext, TuzhiService.ACTION_CRREATE_RADAR_FLOATVIEW);
                    }
                    if (TuzhiService.mRadarFloatView != null) {
                        TuzhiService.mRadarFloatView.setImageResource(R.mipmap.edog_cam_x);
                        return;
                    }
                    return;
                } else if (MainActivity.radarTypeImg != null) {
                    MainActivity.radarTypeImg.setImageResource(R.mipmap.edog_cam_x);
                    return;
                } else {
                    return;
                }
            case 6:
                if (MainActivity.isMinMode) {
                    if (!this.edogDataManager.GetgetLogDisp()) {
                        App.operateFloatView(mContext, TuzhiService.ACTION_REMOVE_RADAR_FLOATVIEW);
                    }
                    if (TuzhiService.mRadarFloatView != null) {
                        TuzhiService.mRadarFloatView.setImageBitmap((Bitmap) null);
                        return;
                    }
                    return;
                } else if (MainActivity.radarTypeImg != null) {
                    MainActivity.radarTypeImg.setImageBitmap((Bitmap) null);
                    return;
                } else {
                    return;
                }
            case 7:
                if (MainActivity.radarImg != null) {
                    MainActivity.radarImg.setImageResource(0);
                    return;
                }
                return;
            case 8:
                if (MainActivity.radarImg != null) {
                    MainActivity.radarImg.setImageResource(R.mipmap.edog_rd_no);
                    return;
                }
                return;
            case 9:
                if (MainActivity.radarImg != null) {
                    MainActivity.radarImg.setImageResource(R.mipmap.edog_rd_no_err);
                    return;
                }
                return;
            case 10:
                if (MainActivity.radarImg != null) {
                    MainActivity.radarImg.setImageResource(R.mipmap.edog_rd_yes);
                    return;
                }
                return;
            default:
                return;
        }
    }

    public void edogDataShow(int nType, EdogDataInfo edogShowInfo, int BlockSpeedLimit) {
        if (edogShowInfo != null) {
            this.count++;
            if (edogShowInfo.getmBlockSpace() != 0) {
                int TresNo = getSpeedLimitImg(BlockSpeedLimit);
                Log.d(TAG, "(datashow : " + MainActivity.blockLimitSpeedIv);
                if (MainActivity.blockLimitSpeedIv != null) {
                    MainActivity.blockLimitSpeedIv.setImageResource(resId[TresNo - 3]);
                }
                if (MainActivity.blockSpeedLbTv != null) {
                    MainActivity.blockSpeedLbTv.setVisibility(0);
                }
                if (MainActivity.blockSpaceLbTv != null) {
                    MainActivity.blockSpaceLbTv.setVisibility(0);
                }
                if (MainActivity.blockSpeedTv != null) {
                    MainActivity.blockSpeedTv.setText(String.valueOf(edogShowInfo.getmBlockSpeed()));
                }
                if (edogShowInfo.getmBlockSpace() % 1000 == 0) {
                    if (MainActivity.blockSpaceTv != null) {
                        MainActivity.blockSpaceTv.setText(String.valueOf(edogShowInfo.getmBlockSpace() / 1000) + "公里");
                    }
                } else if (MainActivity.blockSpaceTv != null) {
                    MainActivity.blockSpaceTv.setText(String.valueOf(edogShowInfo.getmBlockSpace() / 1000) + "." + String.valueOf((edogShowInfo.getmBlockSpace() % 1000) / 100) + "公里");
                }
                if (edogShowInfo.getmBlockSpeed() < BlockSpeedLimit) {
                    if (MainActivity.pbG != null) {
                        MainActivity.pbG.setVisibility(0);
                    }
                    if (MainActivity.pbR != null) {
                        MainActivity.pbR.setVisibility(4);
                    }
                    if (MainActivity.pbG != null) {
                        MainActivity.pbG.setProgress(edogShowInfo.getmPercent());
                    }
                } else if (this.count % 2 == 0) {
                    if (MainActivity.pbG != null) {
                        MainActivity.pbG.setVisibility(4);
                    }
                    if (MainActivity.pbR != null) {
                        MainActivity.pbR.setVisibility(0);
                    }
                    if (MainActivity.pbR != null) {
                        MainActivity.pbR.setProgress(edogShowInfo.getmPercent());
                    }
                } else {
                    if (MainActivity.pbG != null) {
                        MainActivity.pbG.setVisibility(4);
                    }
                    if (MainActivity.pbR != null) {
                        MainActivity.pbR.setVisibility(4);
                    }
                }
                this.BLOCKFlage = true;
            } else if (this.BLOCKFlage) {
                this.BLOCKFlage = false;
                if (MainActivity.blockSpeedLbTv != null) {
                    MainActivity.blockSpeedLbTv.setVisibility(4);
                }
                if (MainActivity.blockSpaceLbTv != null) {
                    MainActivity.blockSpaceLbTv.setVisibility(4);
                }
                if (MainActivity.pbG != null) {
                    MainActivity.pbG.setVisibility(4);
                }
                if (MainActivity.pbR != null) {
                    MainActivity.pbR.setVisibility(4);
                }
                if (MainActivity.blockLimitSpeedIv != null) {
                    MainActivity.blockLimitSpeedIv.setImageResource(0);
                }
                if (MainActivity.blockSpeedTv != null) {
                    MainActivity.blockSpeedTv.setText("");
                }
                if (MainActivity.blockSpaceTv != null) {
                    MainActivity.blockSpaceTv.setText("");
                }
            }
            if (edogShowInfo.ismIsAlarm() && edogShowInfo.getmAlarmType() != 12) {
                int resNo = getSpeedLimitImg(edogShowInfo.getmSpeedLimit());
                if (resNo > 12 || resNo < 3) {
                    if (MainActivity.isMinMode) {
                        if (!this.edogDataManager.GetgetLogDisp() && !TuzhiService.speedfvIsVisible) {
                            App.operateFloatView(mContext, TuzhiService.ACTION_CRREATE_SPEED_FLOATVIEW);
                        }
                        if (edogShowInfo.getmAlarmType() == 0) {
                            if (TuzhiService.fImage != null) {
                                TuzhiService.fImage.setImageResource(resId[resId.length - 2]);
                            }
                        } else if (TuzhiService.fImage != null) {
                            TuzhiService.fImage.setImageResource(resId[resId.length - 1]);
                        }
                    } else if (MainActivity.wsImg != null) {
                        MainActivity.wsImg.setImageBitmap((Bitmap) null);
                    }
                } else if (MainActivity.isMinMode) {
                    if (!this.edogDataManager.GetgetLogDisp() && !TuzhiService.speedfvIsVisible) {
                        App.operateFloatView(mContext, TuzhiService.ACTION_CRREATE_SPEED_FLOATVIEW);
                    }
                    if (edogShowInfo.getmAlarmType() == 0) {
                        if (TuzhiService.fImage != null) {
                            TuzhiService.fImage.setImageResource(resId[resId.length - 2]);
                        }
                    } else if (TuzhiService.fImage != null) {
                        TuzhiService.fImage.setImageResource(resId[resNo - 3]);
                    }
                } else if (MainActivity.wsImg != null) {
                    MainActivity.wsImg.setImageResource(resId[resNo - 3]);
                }
                if (!MainActivity.isMinMode) {
                    MainActivity.tvDis.setText(String.valueOf(edogShowInfo.getmDistance()) + "m");
                    if (MainActivity.wtImg != null) {
                        MainActivity.wtImg.setImageDrawable(getWarnTypeImg(edogShowInfo.getmAlarmType()));
                    }
                }
            } else if (!MainActivity.isMinMode) {
                if (MainActivity.tvDis != null) {
                    MainActivity.tvDis.setText("");
                }
                if (MainActivity.wtImg != null) {
                    MainActivity.wtImg.setImageDrawable((Drawable) null);
                }
                if (MainActivity.wsImg != null) {
                    MainActivity.wsImg.setImageResource(0);
                }
            } else if (!this.edogDataManager.GetgetLogDisp() && TuzhiService.speedfvIsVisible) {
                App.operateFloatView(mContext, TuzhiService.ACTION_REMOVE_SPEED_FLOATVIEW);
            }
            if (!MainActivity.isMinMode) {
                if (MainActivity.ivGps != null) {
                    if (TuzhiService.gpsInfo.getgpsFixTime() < 1) {
                        MainActivity.ivGps.setImageResource(R.mipmap.edog_no_gps);
                    } else {
                        MainActivity.ivGps.setImageResource(R.mipmap.edog_has_gps);
                    }
                }
                TextView dirTv = MainActivity.directionTv;
                ImageView dirIv = MainActivity.directionIv;
                int dirNo = GetdirNo(TuzhiService.gpsInfo.getBearing());
                if (dirTv != null) {
                    dirTv.setText(GetStrDir(dirNo));
                }
                if (dirIv != null) {
                    dirIv.setImageResource(getDirImg(dirNo));
                }
                int bw = TuzhiService.gpsInfo.getSpeed() / 100;
                int sw = (TuzhiService.gpsInfo.getSpeed() % 100) / 10;
                int gw = TuzhiService.gpsInfo.getSpeed() % 10;
                TextView speed1Tv = MainActivity.speed1;
                TextView speed2Tv = MainActivity.speed2;
                TextView speed3Tv = MainActivity.speed3;
                TextView car_speed = MainActivity.car_speed;
                if (car_speed != null) {
                    car_speed.setText("" + TuzhiService.gpsInfo.getSpeed());
                }
                if (speed1Tv != null) {
                    speed1Tv.setText(String.valueOf(bw));
                    speed1Tv.setTextColor(-1);
                }
                if (speed2Tv != null) {
                    speed2Tv.setText(String.valueOf(sw));
                    speed2Tv.setTextColor(-1);
                }
                if (speed3Tv != null) {
                    speed3Tv.setText(String.valueOf(gw));
                    speed3Tv.setTextColor(-1);
                }
                if (MainActivity.ringView != null) {
                    MainActivity.ringView.restoreRingView(TuzhiService.gpsInfo.getSpeed());
                }
                if (MainActivity.dataVersionTv != null && MainActivity.startMuteTime == 0) {
                    MainActivity.dataVersionTv.setText(String.format(mContext.getResources().getString(R.string.current_dataversion), new Object[]{String.valueOf(TuzhiService.Use_Mapver) + "-" + String.valueOf(TuzhiService.Use_Addver)}));
                }
            }
        }
    }

    public static boolean IsWindowVisible(int winID) {
        if (winID != 1) {
            return TuzhiService.speedfvIsVisible;
        }
        if (App.isCurrent(mContext) || MainActivity.isMinMode) {
            return true;
        }
        return false;
    }

    private Drawable getWarnTypeImg(int warnType) {
        InputStream is = null;
        try {
            is = mContext.getAssets().open("imgs/" + ("cam_" + String.valueOf(warnType) + ".png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (is == null && warnType != 0) {
            try {
                is = mContext.getAssets().open("imgs/cam_all.png");
            } catch (IOException e2) {
                e2.printStackTrace();
            }
        }
        return Drawable.createFromStream(is, (String) null);
    }

    private int getSpeedLimitImg(int speedLimit) {
        if (speedLimit < 30 || speedLimit > 130) {
            return (resId.length - 1) + 3;
        }
        return speedLimit / 10;
    }

    private int GetdirNo(int direction) {
        return ((direction + 22) / 45) % 8;
    }

    private int getDirImg(int dirNo) {
        if (dirNo == 0) {
            return R.mipmap.edog_dir0;
        }
        if (dirNo == 1) {
            return R.mipmap.edog_dir1;
        }
        if (dirNo == 2) {
            return R.mipmap.edog_dir2;
        }
        if (dirNo == 3) {
            return R.mipmap.edog_dir3;
        }
        if (dirNo == 4) {
            return R.mipmap.edog_dir4;
        }
        if (dirNo == 5) {
            return R.mipmap.edog_dir5;
        }
        if (dirNo == 6) {
            return R.mipmap.edog_dir6;
        }
        if (dirNo == 7) {
            return R.mipmap.edog_dir7;
        }
        return R.mipmap.edog_dir0;
    }

    private String GetStrDir(int dirNo) {
        if (dirNo == 0) {
            return mContext.getString(R.string.north);
        }
        if (dirNo == 1) {
            return mContext.getString(R.string.northeast);
        }
        if (dirNo == 2) {
            return mContext.getString(R.string.east);
        }
        if (dirNo == 3) {
            return mContext.getString(R.string.southeast);
        }
        if (dirNo == 4) {
            return mContext.getString(R.string.south);
        }
        if (dirNo == 5) {
            return mContext.getString(R.string.southwest);
        }
        if (dirNo == 6) {
            return mContext.getString(R.string.west);
        }
        if (dirNo == 7) {
            return mContext.getString(R.string.northwest);
        }
        return mContext.getString(R.string.north);
    }
}
