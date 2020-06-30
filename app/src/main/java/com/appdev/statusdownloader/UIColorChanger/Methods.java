package com.appdev.statusdownloader.UIColorChanger;


import com.appdev.statusdownloader.Common.Common;
import com.appdev.statusdownloader.R;

public class Methods {

    public void setColorTheme(){

        switch (Common.color){
            case 0xffF44336:
                Common.theme = R.style.AppTheme_red;
                break;
            case 0xffE91E63:
                Common.theme = R.style.AppTheme_pink;
                break;
            case 0xff9C27B0:
                Common.theme = R.style.AppTheme_darpink;
                break;
            case 0xff673AB7:
                Common.theme = R.style.AppTheme_violet;
                break;
            case 0xff3F51B5:
                Common.theme = R.style.AppTheme_blue;
                break;
            case 0xff03A9F4:
                Common.theme = R.style.AppTheme_skyblue;
                break;
            case 0xff4CAF50:
                Common.theme = R.style.AppTheme_green;
                break;
            case 0xffFF9800:
                Common.theme = R.style.AppTheme_orange;
                break;
            case 0xff9E9E9E:
                Common.theme = R.style.AppTheme_grey;
                break;

            case 0xff795548:
                Common.theme = R.style.AppTheme_brown;
                break;
            case 0xff2196F3:
                Common.theme = R.style.AppTheme_bluee;
                break;
            case 0xff00BCD4:
                Common.theme = R.style.AppTheme_cyan;
                break;
            case 0xff009688:
                Common.theme = R.style.AppTheme_teal;
                break;
            case 0xff8BC34A:
                Common.theme = R.style.AppTheme_lgreen;
                break;
            case 0xffCDDC39:
                Common.theme = R.style.AppTheme_lime;
                break;
            case 0xffFFEB3B:
                Common.theme = R.style.AppTheme_yellow;
                break;
            case 0xffFFC107:
                Common.theme = R.style.AppTheme_amber;
                break;
            case 0xffFF5722:
                Common.theme = R.style.AppTheme_dorange;
                break;
            case 0xff000000:
                Common.theme = R.style.AppTheme_black;
                break;
            case 0xff607D8B:
                Common.theme = R.style.AppTheme_gray;
                break;
            default:
                Common.theme = R.style.AppTheme;
                break;
        }
    }

}
