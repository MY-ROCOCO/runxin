package com.rxinyun.admin.system.modular.relation.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.rxinyun.admin.system.modular.relation.entity.SysRelation;
import java.util.List;

public interface SysRelationService extends IService<SysRelation> {
   void saveRelationWithAppend(String objectId, String targetId, String category);

   void saveRelationWithAppend(String objectId, String targetId, String category, String extJson);

   void saveRelationBatchWithAppend(String objectId, List<String> targetIdList, String category);

   void saveRelationBatchWithAppend(String objectId, List<String> targetIdList, String category, List<String> extJsonList);

   void saveRelationWithClear(String objectId, String targetId, String category);

   void saveRelationWithClear(String objectId, String targetId, String category, String extJson);

   void saveRelationBatchWithClear(String objectId, List<String> targetIdList, String category);

   void saveRelationBatchWithClear(String objectId, List<String> targetIdList, String category, List<String> extJsonList);

   List<SysRelation> getRelationListByObjectId(String objectId);

   List<SysRelation> getRelationListByObjectIdList(List<String> objectIdList);

   List<SysRelation> getRelationListByObjectIdAndCategory(String objectId, String category);

   List<SysRelation> getRelationListByObjectIdListAndCategory(List<String> objectIdList, String category);

   List<SysRelation> getRelationListByTargetId(String targetId);

   List<SysRelation> getRelationListByTargetIdList(List<String> targetIdList);

   List<SysRelation> getRelationListByTargetIdAndCategory(String targetId, String category);

   List<SysRelation> getRelationListByTargetIdListAndCategory(List<String> targetIdList, String category);

   List<String> getRelationTargetIdListByObjectId(String objectId);

   List<String> getRelationTargetIdListByObjectIdList(List<String> objectIdList);

   List<String> getRelationTargetIdListByObjectIdAndCategory(String objectId, String category);

   List<String> getRelationTargetIdListByObjectIdListAndCategory(List<String> objectIdList, String category);

   List<String> getRelationObjectIdListByTargetId(String targetId);

   List<String> getRelationObjectIdListByTargetIdList(List<String> targetIdList);

   List<String> getRelationObjectIdListByTargetIdAndCategory(String targetId, String category);

   List<String> getRelationObjectIdListByTargetIdListAndCategory(List<String> targetIdList, String category);
}
