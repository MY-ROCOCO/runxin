package com.rxinyun.common.listener;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONUtil;
import com.rxinyun.common.exception.CommonException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CommonDataChangeEventCenter {
   private static List<CommonDataChangeListener> listenerList = new ArrayList<>();

   public static List<CommonDataChangeListener> getListenerList() {
      return listenerList;
   }

   public static void setListenerList(List<CommonDataChangeListener> listenerList) {
      if (listenerList == null) {
         throw new CommonException("重置的侦听器集合不可以为空");
      } else {
         CommonDataChangeEventCenter.listenerList = listenerList;
      }
   }

   public static void registerListener(CommonDataChangeListener listener) {
      if (listener == null) {
         throw new CommonException("注册的侦听器不可以为空");
      } else {
         listenerList.add(listener);
      }
   }

   public static void registerListenerList(List<CommonDataChangeListener> listenerList) {
      if (listenerList == null) {
         throw new CommonException("注册的侦听器不可以为空");
      } else {
         for (CommonDataChangeListener listener : listenerList) {
            if (listener == null) {
               throw new CommonException("注册的侦听器不可以为空");
            }
         }

         CommonDataChangeEventCenter.listenerList.addAll(listenerList);
      }
   }

   public static void removeListener(CommonDataChangeListener listener) {
      listenerList.remove(listener);
   }

   public static void removeListener(Class<? extends CommonDataChangeListener> cls) {
      for (CommonDataChangeListener listener : new ArrayList<>(listenerList)) {
         if (cls.isAssignableFrom(listener.getClass())) {
            listenerList.remove(listener);
         }
      }
   }

   public static void clearListener() {
      listenerList.clear();
   }

   public static boolean hasListener(CommonDataChangeListener listener) {
      return listenerList.contains(listener);
   }

   public static boolean hasListener(Class<? extends CommonDataChangeListener> cls) {
      for (CommonDataChangeListener listener : listenerList) {
         if (cls.isAssignableFrom(listener.getClass())) {
            return true;
         }
      }

      return false;
   }

   public static void doAddWithData(String dataType, JSONArray jsonArray) {
      for (CommonDataChangeListener listener : listenerList) {
         listener.doAddWithDataIdList(dataType, jsonArray.stream().map(o -> JSONUtil.parseObj(o).getStr("id")).collect(Collectors.toList()));
         listener.doAddWithDataList(dataType, jsonArray);
      }
   }

   public static void doUpdateWithData(String dataType, JSONArray jsonArray) {
      for (CommonDataChangeListener listener : listenerList) {
         listener.doUpdateWithDataIdList(dataType, jsonArray.stream().map(o -> JSONUtil.parseObj(o).getStr("id")).collect(Collectors.toList()));
         listener.doUpdateWithDataList(dataType, jsonArray);
      }
   }

   public static void doDeleteWithDataId(String dataType, List<String> dataIdList) {
      for (CommonDataChangeListener listener : listenerList) {
         listener.doDeleteWithDataIdList(dataType, dataIdList);
      }
   }
}
