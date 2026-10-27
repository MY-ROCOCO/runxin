package com.rxinyun.common.qzsystem.common;

import lombok.Data;

public class Response {
   private int code;
   private Data data;
   private String msg;

   public int getCode() {
      return this.code;
   }

   public Data getData() {
      return this.data;
   }

   public String getMsg() {
      return this.msg;
   }

   public void setCode(final int code) {
      this.code = code;
   }

   public void setData(final Data data) {
      this.data = data;
   }

   public void setMsg(final String msg) {
      this.msg = msg;
   }

   @Override
   public boolean equals(final Object o) {
      if (o == this) {
         return true;
      } else if (!(o instanceof Response other)) {
         return false;
      } else if (!other.canEqual(this)) {
         return false;
      } else if (this.getCode() != other.getCode()) {
         return false;
      } else {
         Object this$data = this.getData();
         Object other$data = other.getData();
         if (this$data == null ? other$data == null : this$data.equals(other$data)) {
            Object this$msg = this.getMsg();
            Object other$msg = other.getMsg();
            return this$msg == null ? other$msg == null : this$msg.equals(other$msg);
         } else {
            return false;
         }
      }
   }

   protected boolean canEqual(final Object other) {
      return other instanceof Response;
   }

   @Override
   public int hashCode() {
      int PRIME = 59;
      int result = 1;
      result = result * 59 + this.getCode();
      Object $data = this.getData();
      result = result * 59 + ($data == null ? 43 : $data.hashCode());
      Object $msg = this.getMsg();
      return result * 59 + ($msg == null ? 43 : $msg.hashCode());
   }

   @Override
   public String toString() {
      return "Response(code=" + this.getCode() + ", data=" + this.getData() + ", msg=" + this.getMsg() + ")";
   }
}
