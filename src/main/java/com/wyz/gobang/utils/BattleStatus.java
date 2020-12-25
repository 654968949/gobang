package com.wyz.gobang.utils;

/**
 * <p>
 *     对战状态枚举类
 * </p>
 *
 * @author wuyuzi
 * @since 2020/12/16
 */
public enum BattleStatus {
   NOTOK("还未开始",-1),ING("暂无胜负",0),WIN("赢",1),LOST("输",2);

   BattleStatus(String status, int value) {
      this.status = status;
      this.value = value;
   }

   private String status;
   private int value;

   public String getStatus() {
      return status;
   }
   public int getValue() {
      return value;
   }

}
