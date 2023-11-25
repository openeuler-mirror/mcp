package com.hnkylin.cloud.manage.entity.req.role;


import lombok.Data;

@Data
public class AllocateRolePermissionParam {

    private Integer roleId;

    private String permissionIds;

    public static void main(String[] args) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < 63; i++) {
            stringBuilder.append(i).append(",");
        }
        System.out.println(stringBuilder.toString());
    }


}
