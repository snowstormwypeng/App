package enjoy.Device;

import com.smdt.facesdk.mipsFaceInfoTrack;

import Entity.BaseEnity;

/**
 * 作者：王彦鹏 on 2019-08-24 11:01
 * QQ:5284328
 * 邮箱：snowstorm_wypeng@qq.com
 * 公司：盈加电子科技有限公司
 */
public class FaceEntity extends BaseEnity {
    private String cardNo;
    private String imgPath;
    private mipsFaceInfoTrack faceInfo;
    private boolean isVip=false;

    public boolean isVip() {
        return isVip;
    }

    public void setVip(boolean vip) {
        isVip = vip;
    }



    public String getCardNo() {
        return cardNo;
    }

    public void setCardNo(String cardNo) {
        this.cardNo = cardNo;
    }

    public String getImgPath() {
        return imgPath;
    }

    public void setImgPath(String imgPath) {
        this.imgPath = imgPath;
    }

    public mipsFaceInfoTrack getFaceInfo() {
        return faceInfo;
    }

    public void setFaceInfo(mipsFaceInfoTrack faceInfo) {
        this.faceInfo = faceInfo;
    }
}
