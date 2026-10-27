package com.rxinyun.member.auth.modular.relation.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.rxinyun.member.auth.modular.relation.entity.ClientRelation;
import java.util.List;

public interface ClientRelationService extends IService<ClientRelation> {
   void saveRelationWithAppend(String objectId, String targetId, String category);

   void saveRelationWithAppend(String objectId, String targetId, String category, String extJson);

   void saveRelationBatchWithAppend(String objectId, List<String> targetIdList, String category);

   void saveRelationBatchWithAppend(String objectId, List<String> targetIdList, String category, List<String> extJsonList);

   void saveRelationWithClear(String objectId, String targetId, String category);

   void saveRelationWithClear(String objectId, String targetId, String category, String extJson);

   void saveRelationBatchWithClear(String objectId, List<String> targetIdList, String category);

   void saveRelationBatchWithClear(String objectId, List<String> targetIdList, String category, List<String> extJsonList);

   List<ClientRelation> getRelationListByObjectId(String objectId);

   List<ClientRelation> getRelationListByObjectIdList(List<String> objectIdList);

   List<ClientRelation> getRelationListByObjectIdAndCategory(String objectId, String category);

   List<ClientRelation> getRelationListByObjectIdListAndCategory(List<String> objectIdList, String category);

   List<ClientRelation> getRelationListByTargetId(String targetId);

   List<ClientRelation> getRelationListByTargetIdList(List<String> targetIdList);

   List<ClientRelation> getRelationListByTargetIdAndCategory(String targetId, String category);

   List<ClientRelation> getRelationListByTargetIdListAndCategory(List<String> targetIdList, String category);

   List<String> getRelationTargetIdListByObjectId(String objectId);

   List<String> getRelationTargetIdListByObjectIdList(List<String> objectIdList);

   List<String> getRelationTargetIdListByObjectIdAndCategory(String objectId, String category);

   List<String> getRelationTargetIdListByObjectIdListAndCategory(List<String> objectIdList, String category);

   List<String> getRelationObjectIdListByTargetId(String targetId);

   List<String> getRelationObjectIdListByTargetIdList(List<String> targetIdList);

   List<String> getRelationObjectIdListByTargetIdAndCategory(String targetId, String category);

   List<String> getRelationObjectIdListByTargetIdListAndCategory(List<String> targetIdList, String category);
}
