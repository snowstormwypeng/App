package Entity;

/**
 * Created by 王彦鹏 on 2017-12-12.
 */

public class RequestPermissionsResult {
    public int getRequestRes() {
        return requestRes;
    }

    public void setRequestRes(int requestRes) {
        this.requestRes = requestRes;
    }

    public String getPermissionsName() {
        return permissionsName;
    }

    public void setPermissionsName(String permissionsName) {
        this.permissionsName = permissionsName;
    }

    private int requestRes=0;
    private String permissionsName="";
}
