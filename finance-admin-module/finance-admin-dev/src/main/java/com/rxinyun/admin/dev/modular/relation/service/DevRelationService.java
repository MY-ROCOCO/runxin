package com.rxinyun.admin.dev.modular.relation.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.rxinyun.admin.dev.modular.relation.entity.DevRelation;
import java.util.List;

public interface DevRelationService extends IService<DevRelation> {
   void saveRelationWithAppend(String objectId, String targetId, String category);

   void saveRelationWithAppend(String objectId, String targetId, String category, String extJson);

   void saveRelationBatchWithAppend(String objectId, List<String> targetIdList, String category);

   void saveRelationBatchWithAppend(String objectId, List<String> targetIdList, String category, List<String> extJsonList);

   void saveRelationWithClear(String objectId, String targetId, String category);

   void saveRelationWithClear(String objectId, String targetId, String category, String extJson);

   void saveRelationBatchWithClear(String objectId, List<String> targetIdList, String category);

   void saveRelationBatchWithClear(String objectId, List<String> targetIdList, String category, List<String> extJsonList);

   List<DevRelation> getRelationListByObjectId(String objectId);

   List<DevRelation> getRelationListByObjectIdList(List<String> objectIdList);

   List<DevRelation> getRelationListByObjectIdAndCategory(String objectId, String category);

   List<DevRelation> getRelationListByObjectIdListAndCategory(List<String> objectIdList, String category);

   List<DevRelation> getRelationListByTargetId(String targetId);

   List<DevRelation> getRelationListByTargetIdList(List<String> targetIdList);

   List<DevRelation> getRelationListByTargetIdAndCategory(String targetId, String category);

   List<DevRelation> getRelationListByTargetIdListAndCategory(List<String> targetIdList, String category);

   List<String> getRelationTargetIdListByObjectId(String objectId);

   List<String> getRelationTargetIdListByObjectIdList(List<String> objectIdList);

   List<String> getRelationTargetIdListByObjectIdAndCategory(String objectId, String category);

   List<String> getRelationTargetIdListByObjectIdListAndCategory(List<String> objectIdList, String category);

   List<String> getRelationObjectIdListByTargetId(String targetId);

   List<String> getRelationObjectIdListByTargetIdList(List<String> targetIdList);

   List<String> getRelationObjectIdListByTargetIdAndCategory(String targetId, String category);

   List<String> getRelationObjectIdListByTargetIdListAndCategory(List<String> targetIdList, String category);
}
