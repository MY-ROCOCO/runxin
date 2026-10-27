package com.rxinyun.member.business.modular.esc.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.rxinyun.common.pojo.CommonResult;
import com.rxinyun.member.business.modular.esc.entity.MemberServer;
import com.rxinyun.member.business.modular.esc.entity.MemberServerBackupRecord;
import com.rxinyun.member.business.modular.esc.entity.MemberServerSnapshotRecord;
import com.rxinyun.member.business.modular.esc.param.MemberRemarkEditParam;
import com.rxinyun.member.business.modular.esc.param.MemberServerAddParam;
import com.rxinyun.member.business.modular.esc.param.MemberServerBackupCreateParam;
import com.rxinyun.member.business.modular.esc.param.MemberServerBackupIdParam;
import com.rxinyun.member.business.modular.esc.param.MemberServerCpuHistoryResult;
import com.rxinyun.member.business.modular.esc.param.MemberServerIdListParam;
import com.rxinyun.member.business.modular.esc.param.MemberServerIdParam;
import com.rxinyun.member.business.modular.esc.param.MemberServerIpParam;
import com.rxinyun.member.business.modular.esc.param.MemberServerNetworkHistoryResult;
import com.rxinyun.member.business.modular.esc.param.MemberServerPageParam;
import com.rxinyun.member.business.modular.esc.param.MemberServerRenewAutoParam;
import com.rxinyun.member.business.modular.esc.param.MemberServerSnapshotCreateParam;
import com.rxinyun.member.business.modular.esc.param.MemberServerSnapshotIdParam;
import com.rxinyun.member.business.modular.esc.param.MemberServerTransferRecordAddParam;
import com.rxinyun.member.business.modular.esc.param.MemberServerTransferRecordIdParam;
import com.rxinyun.member.business.modular.esc.param.PasswordEditParam;
import com.rxinyun.member.business.modular.esc.param.ReinstallEditParam;
import java.math.BigDecimal;
import java.util.List;

public interface MemberServerService extends IService<MemberServer> {
   Page<MemberServer> page(MemberServerPageParam pageParam);

   List<MemberServer> list(MemberServerIdListParam memberServerIdListParam);

   void add(MemberServerAddParam serverAddParam);

   MemberServer detail(MemberServerIdParam serverIdParam);

   MemberServer queryEntity(String id);

   MemberServer getByIdAndMemberNo(String id, String memberNo);

   void editRemark(MemberRemarkEditParam memberRemarkEditParam);

   CommonResult startServer(MemberServerIdParam memberServerIdParam);

   CommonResult closeServer(MemberServerIdParam memberServerIdParam);

   CommonResult monitorServer(MemberServerIdParam memberServerIdParam);

   CommonResult reinstallServer(ReinstallEditParam reinstallEditParam);

   CommonResult lockServer(MemberServerIdParam serverIdParam);

   CommonResult unlockServer(MemberServerIdParam serverIdParam);

   CommonResult rebuildServer(MemberServerIdParam serverIdParam);

   CommonResult updatePasswordServer(PasswordEditParam passwordEditParam);

   CommonResult restartServer(MemberServerIdParam serverIdParam);

   CommonResult powerServer(MemberServerIdParam serverIdParam);

   CommonResult thumbnailServer(MemberServerIdParam serverIdParam);

   CommonResult syncStatusServer(MemberServerIdParam serverIdParam);

   CommonResult disconnectServer(MemberServerIdParam serverIdParam);

   CommonResult reconnectServer(MemberServerIdParam serverIdParam);

   CommonResult vncUrl(MemberServerIdParam serverIdParam);

   CommonResult<MemberServerBackupRecord> createBackup(MemberServerBackupCreateParam memberServerBackupCreateParam);

   CommonResult restoreBackup(MemberServerBackupIdParam memberServerBackupIdParam);

   CommonResult deleteBackup(MemberServerBackupIdParam memberServerBackupIdParam);

   CommonResult<List<MemberServerBackupRecord>> listBackup(MemberServerIdParam memberServerIdParam);

   CommonResult<MemberServerSnapshotRecord> createSnapshot(MemberServerSnapshotCreateParam memberServerSnapshotCreateParam);

   CommonResult restoreSnapshot(MemberServerSnapshotIdParam memberServerSnapshotIdParam);

   CommonResult deleteSnapshot(MemberServerSnapshotIdParam memberServerSnapshotIdParam);

   CommonResult<List<MemberServerSnapshotRecord>> listSnapshot(MemberServerIdParam memberServerIdParam);

   CommonResult addIp(MemberServerIpParam serverIdParam);

   CommonResult upgradeHardDisksServer(String orderNo, String serverId, Integer systemDisk, List<Integer> dataDisks, BigDecimal renewPrice);

   CommonResult upgradeBandwidthServer(String orderNo, String serverId, Integer bandwidth, BigDecimal renewPrice);

   CommonResult upgradeCpuRamServer(String orderNo, String serverId, Integer cpu, Integer ram, BigDecimal renewPrice);

   CommonResult renewServer(String orderNo, String serverId, Integer months);

   CommonResult renewServer(MemberServerIdParam memberServerIdParam);

   CommonResult plus(MemberServerTransferRecordAddParam memberServerTransferRecordAddParam);

   CommonResult updateStatus(MemberServerTransferRecordIdParam memberServerTransferRecordEditParam);

   CommonResult upgradeDefenseServer(String orderNo, String serverId, Integer defense, BigDecimal renewPrice);

   void isRenew(MemberServerRenewAutoParam memberServerRenewAutoParam);

   CommonResult<List<MemberServerNetworkHistoryResult>> getNetworkHistory(MemberServerIdParam memberServerIdParam);

   CommonResult<List<MemberServerCpuHistoryResult>> getCpuHistory(MemberServerIdParam memberServerIdParam);
}
