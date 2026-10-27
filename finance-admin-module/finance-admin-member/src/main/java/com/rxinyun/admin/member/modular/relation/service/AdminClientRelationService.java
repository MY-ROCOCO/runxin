package com.rxinyun.admin.member.modular.relation.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.rxinyun.admin.member.modular.relation.entity.AdminClientRelation;
import java.util.List;

public interface AdminClientRelationService extends IService<AdminClientRelation> {
   void saveRelationWithAppend(String objectId, String targetId, String category);

   void saveRelationWithAppend(String objectId, String targetId, String category, String extJson);

   void saveRelationBatchWithAppend(String objectId, List<String> targetIdList, String category);

   void saveRelationBatchWithAppend(String objectId, List<String> targetIdList, String category, List<String> extJsonList);

   void saveRelationWithClear(String objectId, String targetId, String category);

   void saveRelationWithClear(String objectId, String targetId, String category, String extJson);

   void saveRelationBatchWithClear(String objectId, List<String> targetIdList, String category);

   void saveRelationBatchWithClear(String objectId, List<String> targetIdList, String category, List<String> extJsonList);

   List<AdminClientRelation> getRelationListByObjectId(String objectId);

   List<AdminClientRelation> getRelationListByObjectIdList(List<String> objectIdList);

   List<AdminClientRelation> getRelationListByObjectIdAndCategory(String objectId, String category);

   List<AdminClientRelation> getRelationListByObjectIdListAndCategory(List<String> objectIdList, String category);

   List<AdminClientRelation> getRelationListByTargetId(String targetId);

   List<AdminClientRelation> getRelationListByTargetIdList(List<String> targetIdList);

   List<AdminClientRelation> getRelationListByTargetIdAndCategory(String targetId, String category);

   List<AdminClientRelation> getRelationListByTargetIdListAndCategory(List<String> targetIdList, String category);

   List<String> getRelationTargetIdListByObjectId(String objectId);

   List<String> getRelationTargetIdListByObjectIdList(List<String> objectIdList);

   List<String> getRelationTargetIdListByObjectIdAndCategory(String objectId, String category);

   List<String> getRelationTargetIdListByObjectIdListAndCategory(List<String> objectIdList, String category);

   List<String> getRelationObjectIdListByTargetId(String targetId);

   List<String> getRelationObjectIdListByTargetIdList(List<String> targetIdList);

   List<String> getRelationObjectIdListByTargetIdAndCategory(String targetId, String category);

   List<String> getRelationObjectIdListByTargetIdListAndCategory(List<String> targetIdList, String category);
}
