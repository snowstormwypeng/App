package Entity;

import android.content.Intent;

/**
 * Created by 王彦鹏 on 2017-12-28.
 */

public class ActivityResult extends BaseEnity {
    private int requestCode=0;

    public int getRequestCode() {
        return requestCode;
    }

    public void setRequestCode(int requestCode) {
        this.requestCode = requestCode;
    }

    public int getResultCode() {
        return resultCode;
    }

    public void setResultCode(int resultCode) {
        this.resultCode = resultCode;
    }

    public Intent getData() {
        return data;
    }

    public void setData(Intent data) {
        this.data = data;
    }

    private  int resultCode=0;
    private Intent data=null;

}
