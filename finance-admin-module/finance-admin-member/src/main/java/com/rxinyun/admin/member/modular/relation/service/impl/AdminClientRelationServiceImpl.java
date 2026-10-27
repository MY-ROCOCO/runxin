package com.rxinyun.admin.member.modular.relation.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rxinyun.admin.member.modular.relation.entity.AdminClientRelation;
import com.rxinyun.admin.member.modular.relation.mapper.AdminClientRelationMapper;
import com.rxinyun.admin.member.modular.relation.service.AdminClientRelationService;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AdminClientRelationServiceImpl extends ServiceImpl<AdminClientRelationMapper, AdminClientRelation> implements AdminClientRelationService {
   @Transactional(
      rollbackFor = {Exception.class}
   )
   public void saveRelation(String objectId, String targetId, String category, String extJson, boolean clear) {
      if (clear) {
         this.remove(
            (Wrapper<AdminClientRelation>)((LambdaQueryWrapper<AdminClientRelation>)new LambdaQueryWrapper<AdminClientRelation>().eq(AdminClientRelation::getObjectId, objectId))
               .eq(AdminClientRelation::getCategory, category)
         );
      }

      AdminClientRelation clientRelation = new AdminClientRelation();
      clientRelation.setObjectId(objectId);
      clientRelation.setTargetId(targetId);
      clientRelation.setCategory(category);
      clientRelation.setExtJson(extJson);
      this.save(clientRelation);
   }

   @Transactional(
      rollbackFor = {Exception.class}
   )
   public void saveRelationBatch(String objectId, List<String> targetIdList, String category, List<String> extJsonList, boolean clear) {
      if (clear) {
         this.remove(
            (Wrapper<AdminClientRelation>)((LambdaQueryWrapper<AdminClientRelation>)new LambdaQueryWrapper<AdminClientRelation>().eq(AdminClientRelation::getObjectId, objectId))
               .eq(AdminClientRelation::getCategory, category)
         );
      }

      List<AdminClientRelation> clientRelationList = CollectionUtil.newArrayList(new AdminClientRelation[0]);

      for (int i = 0; i < targetIdList.size(); i++) {
         AdminClientRelation clientRelation = new AdminClientRelation();
         clientRelation.setObjectId(objectId);
         clientRelation.setTargetId(targetIdList.get(i));
         clientRelation.setCategory(category);
         if (ObjectUtil.isNotEmpty(extJsonList)) {
            clientRelation.setExtJson(extJsonList.get(i));
         }

         clientRelationList.add(clientRelation);
      }

      if (ObjectUtil.isNotEmpty(clientRelationList)) {
         this.saveBatch(clientRelationList);
      }
   }

   @Override
   public void saveRelationWithAppend(String objectId, String targetId, String category) {
      this.saveRelation(objectId, targetId, category, null, false);
   }

   @Override
   public void saveRelationWithAppend(String objectId, String targetId, String category, String extJson) {
      this.saveRelation(objectId, targetId, category, extJson, false);
   }

   @Override
   public void saveRelationBatchWithAppend(String objectId, List<String> targetIdList, String category) {
      this.saveRelationBatch(objectId, targetIdList, category, null, false);
   }

   @Override
   public void saveRelationBatchWithAppend(String objectId, List<String> targetIdList, String category, List<String> extJsonList) {
      this.saveRelationBatch(objectId, targetIdList, category, extJsonList, false);
   }

   @Override
   public void saveRelationWithClear(String objectId, String targetId, String category) {
      this.saveRelation(objectId, targetId, category, null, true);
   }

   @Override
   public void saveRelationWithClear(String objectId, String targetId, String category, String extJson) {
      this.saveRelation(objectId, targetId, category, extJson, true);
   }

   @Override
   public void saveRelationBatchWithClear(String objectId, List<String> targetIdList, String category) {
      this.saveRelationBatch(objectId, targetIdList, category, null, true);
   }

   @Override
   public void saveRelationBatchWithClear(String objectId, List<String> targetIdList, String category, List<String> extJsonList) {
      this.saveRelationBatch(objectId, targetIdList, category, extJsonList, true);
   }

   @Override
   public List<AdminClientRelation> getRelationListByObjectId(String objectId) {
      return this.getRelationListByObjectIdAndCategory(objectId, null);
   }

   @Override
   public List<AdminClientRelation> getRelationListByObjectIdList(List<String> objectIdList) {
      return this.getRelationListByObjectIdListAndCategory(objectIdList, null);
   }

   @Override
   public List<AdminClientRelation> getRelationListByObjectIdAndCategory(String objectId, String category) {
      LambdaQueryWrapper<AdminClientRelation> lambdaQueryWrapper = new LambdaQueryWrapper();
      lambdaQueryWrapper.eq(AdminClientRelation::getObjectId, objectId);
      if (ObjectUtil.isNotEmpty(category)) {
         lambdaQueryWrapper.eq(AdminClientRelation::getCategory, category);
      }

      return this.list(lambdaQueryWrapper);
   }

   @Override
   public List<AdminClientRelation> getRelationListByObjectIdListAndCategory(List<String> objectIdList, String category) {
      LambdaQueryWrapper<AdminClientRelation> lambdaQueryWrapper = new LambdaQueryWrapper();
      lambdaQueryWrapper.in(AdminClientRelation::getObjectId, objectIdList);
      if (ObjectUtil.isNotEmpty(category)) {
         lambdaQueryWrapper.eq(AdminClientRelation::getCategory, category);
      }

      return this.list(lambdaQueryWrapper);
   }

   @Override
   public List<AdminClientRelation> getRelationListByTargetId(String targetId) {
      return this.getRelationListByTargetIdAndCategory(targetId, null);
   }

   @Override
   public List<AdminClientRelation> getRelationListByTargetIdList(List<String> targetIdList) {
      return this.getRelationListByTargetIdListAndCategory(targetIdList, null);
   }

   @Override
   public List<AdminClientRelation> getRelationListByTargetIdAndCategory(String targetId, String category) {
      LambdaQueryWrapper<AdminClientRelation> lambdaQueryWrapper = new LambdaQueryWrapper();
      lambdaQueryWrapper.eq(AdminClientRelation::getTargetId, targetId);
      if (ObjectUtil.isNotEmpty(category)) {
         lambdaQueryWrapper.eq(AdminClientRelation::getCategory, category);
      }

      return this.list(lambdaQueryWrapper);
   }

   @Override
   public List<AdminClientRelation> getRelationListByTargetIdListAndCategory(List<String> targetIdList, String category) {
      LambdaQueryWrapper<AdminClientRelation> lambdaQueryWrapper = new LambdaQueryWrapper();
      lambdaQueryWrapper.in(AdminClientRelation::getTargetId, targetIdList);
      if (ObjectUtil.isNotEmpty(category)) {
         lambdaQueryWrapper.eq(AdminClientRelation::getCategory, category);
      }

      return this.list(lambdaQueryWrapper);
   }

   @Override
   public List<String> getRelationTargetIdListByObjectId(String objectId) {
      return this.getRelationTargetIdListByObjectIdAndCategory(objectId, null);
   }

   @Override
   public List<String> getRelationTargetIdListByObjectIdList(List<String> objectIdList) {
      return this.getRelationTargetIdListByObjectIdListAndCategory(objectIdList, null);
   }

   @Override
   public List<String> getRelationTargetIdListByObjectIdAndCategory(String objectId, String category) {
      return this.getRelationListByObjectIdAndCategory(objectId, category).stream().map(AdminClientRelation::getTargetId).collect(Collectors.toList());
   }

   @Override
   public List<String> getRelationTargetIdListByObjectIdListAndCategory(List<String> objectIdList, String category) {
      return this.getRelationListByObjectIdListAndCategory(objectIdList, category).stream().map(AdminClientRelation::getTargetId).collect(Collectors.toList());
   }

   @Override
   public List<String> getRelationObjectIdListByTargetId(String targetId) {
      return this.getRelationObjectIdListByTargetIdAndCategory(targetId, null);
   }

   @Override
   public List<String> getRelationObjectIdListByTargetIdList(List<String> targetIdList) {
      return this.getRelationObjectIdListByTargetIdListAndCategory(targetIdList, null);
   }

   @Override
   public List<String> getRelationObjectIdListByTargetIdAndCategory(String targetId, String category) {
      return this.getRelationListByTargetIdAndCategory(targetId, category).stream().map(AdminClientRelation::getObjectId).collect(Collectors.toList());
   }

   @Override
   public List<String> getRelationObjectIdListByTargetIdListAndCategory(List<String> targetIdList, String category) {
      return this.getRelationListByTargetIdListAndCategory(targetIdList, category).stream().map(AdminClientRelation::getObjectId).collect(Collectors.toList());
   }
}
