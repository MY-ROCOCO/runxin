package com.rxinyun.common.excel;

import com.alibaba.excel.metadata.Head;
import com.alibaba.excel.write.merge.AbstractMergeStrategy;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import java.util.ArrayList;
import java.util.List;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;

public class CommonExcelCustomMergeStrategy extends AbstractMergeStrategy {
   private final List<Integer> exportFieldGroupCountList;
   private final Integer targetColumnIndex;
   private Integer rowIndex;

   public CommonExcelCustomMergeStrategy(List<String> exportDataList, Integer targetColumnIndex) {
      this.exportFieldGroupCountList = this.getGroupCountList(exportDataList);
      this.targetColumnIndex = targetColumnIndex;
   }

   protected void merge(Sheet sheet, Cell cell, Head head, Integer relativeRowIndex) {
      if (null == this.rowIndex) {
         this.rowIndex = cell.getRowIndex();
      }

      if (cell.getRowIndex() == this.rowIndex && cell.getColumnIndex() == this.targetColumnIndex) {
         this.mergeGroupColumn(sheet);
      }
   }

   private void mergeGroupColumn(Sheet sheet) {
      int rowCount = this.rowIndex;

      for (Integer count : this.exportFieldGroupCountList) {
         if (count == 1) {
            rowCount += count;
         } else {
            CellRangeAddress cellRangeAddress = new CellRangeAddress(rowCount, rowCount + count - 1, this.targetColumnIndex, this.targetColumnIndex);
            sheet.addMergedRegionUnsafe(cellRangeAddress);
            rowCount += count;
         }
      }
   }

   private List<Integer> getGroupCountList(List<String> exportDataList) {
      if (CollectionUtils.isEmpty(exportDataList)) {
         return new ArrayList<>();
      } else {
         List<Integer> groupCountList = new ArrayList<>();
         int count = 1;

         for (int i = 1; i < exportDataList.size(); i++) {
            if (exportDataList.get(i).equals(exportDataList.get(i - 1))) {
               count++;
            } else {
               groupCountList.add(count);
               count = 1;
            }
         }

         groupCountList.add(count);
         return groupCountList;
      }
   }
}
