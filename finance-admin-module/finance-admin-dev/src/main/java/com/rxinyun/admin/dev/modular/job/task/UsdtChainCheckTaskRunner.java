package com.rxinyun.admin.dev.modular.job.task;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.rxinyun.common.timer.CommonTimerTaskRunner;
import com.rxinyun.dev.api.DevConfigApi;
import com.rxinyun.payments.api.PaymentsApi;
import com.rxinyun.payments.api.pojo.PendingUsdtPaymentDto;
import jakarta.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpTimeoutException;
import java.net.http.HttpClient.Redirect;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class UsdtChainCheckTaskRunner implements CommonTimerTaskRunner {
   private static final Logger log = LoggerFactory.getLogger(UsdtChainCheckTaskRunner.class);
   private static final String USDT_PAY_WALLET_ADDRESS = "USDT_PAY_WALLET_ADDRESS";
   private static final String USDT_PAY_CHAIN = "USDT_PAY_CHAIN";
   private static final String USDT_CHAIN_API = "USDT_CHAIN_API";
   private static final String TRONGRID_URL = "https://api.trongrid.io";
   private static final String TRONSCAN_URL = "https://apilist.tronscanapi.com";
   private static final String TRONSCAN_URL_ALT = "https://apilist.tronscan.org";
   private static final String USDT_TRC20_CONTRACT = "TR7NHqjeKQxGTCi8q8ZY4pL8otSzgjLj6t";
   private static final int USDT_DECIMALS = 6;
   private static final BigDecimal USDT_DIVISOR = BigDecimal.valueOf(1000000L);
   private static final BigDecimal AMOUNT_TOLERANCE = new BigDecimal("0.0001");
   @Resource
   private DevConfigApi devConfigApi;
   @Resource
   private PaymentsApi paymentsApi;

   public void action() {
      String walletAddress = this.devConfigApi.getValueByKey("USDT_PAY_WALLET_ADDRESS");
      if (StrUtil.isBlank(walletAddress)) {
         log.debug("USDT 收款地址未配置，跳过链上检测");
      } else {
         String chain = this.devConfigApi.getValueByKey("USDT_PAY_CHAIN");
         if (StrUtil.isBlank(chain)) {
            chain = "TRC20";
         }

         if (!"TRC20".equalsIgnoreCase(chain.trim())) {
            log.debug("当前仅支持 TRC20 链，配置为 {}，跳过", chain);
         } else {
            List<PendingUsdtPaymentDto> pendingList = this.paymentsApi.getPendingUsdtPayments();
            if (pendingList != null && !pendingList.isEmpty()) {
               String apiSource = this.devConfigApi.getValueByKey("USDT_CHAIN_API");
               if (StrUtil.isBlank(apiSource)) {
                  apiSource = "TRONSCAN";
               }

               List<UsdtChainCheckTaskRunner.Trc20Transfer> transfers = this.fetchTrc20Transfers(walletAddress.trim(), apiSource.trim().toUpperCase());
               if (!transfers.isEmpty()) {
                  Set<String> processedTxIds = new HashSet<>();

                  for (UsdtChainCheckTaskRunner.Trc20Transfer tx : transfers) {
                     if (tx.txId != null && !processedTxIds.contains(tx.txId)) {
                        BigDecimal usdtAmount = tx.valueUsdt;

                        for (PendingUsdtPaymentDto pending : pendingList) {
                           if (pending.getExpectedAmountUsdt() != null
                              && usdtAmount.subtract(pending.getExpectedAmountUsdt()).abs().compareTo(AMOUNT_TOLERANCE) <= 0) {
                              Date payTime = tx.blockTimestamp != null ? new Date(tx.blockTimestamp) : new Date();
                              this.paymentsApi.confirmUsdtPayment(pending.getOrderNo(), payTime, tx.txId);
                              processedTxIds.add(tx.txId);
                              log.info("USDT 到账已确认 orderNo={} txHash={} amount={}", new Object[]{pending.getOrderNo(), tx.txId, usdtAmount});
                              break;
                           }
                        }
                     }
                  }
               }
            } else {
               log.debug("无待确认 USDT 支付，跳过");
            }
         }
      }
   }

   private List<UsdtChainCheckTaskRunner.Trc20Transfer> fetchTrc20Transfers(String toAddress, String apiSource) {
      return "TRONGRID".equals(apiSource) ? this.fetchFromTronGrid(toAddress) : this.fetchFromTronScan(toAddress);
   }

   private List<UsdtChainCheckTaskRunner.Trc20Transfer> fetchFromTronScan(String toAddress) {
      List<UsdtChainCheckTaskRunner.Trc20Transfer> list = this.fetchFromTronScanWithUrl(toAddress, "https://apilist.tronscanapi.com");
      if (list == null && !"https://apilist.tronscanapi.com".equals("https://apilist.tronscan.org")) {
         list = this.fetchFromTronScanWithUrl(toAddress, "https://apilist.tronscan.org");
         if (list != null) {
            log.debug("TronScan 使用备用域名成功");
         }
      }

      return list != null ? list : Collections.emptyList();
   }

   private List<UsdtChainCheckTaskRunner.Trc20Transfer> fetchFromTronScanWithUrl(String toAddress, String baseUrl) {
      long endTs = System.currentTimeMillis();
      long startTs = endTs - 86400000L;
      String url = baseUrl
         + "/api/token_trc20/transfers?toAddress="
         + URLEncoder.encode(toAddress, StandardCharsets.UTF_8)
         + "&contract_address=TR7NHqjeKQxGTCi8q8ZY4pL8otSzgjLj6t&limit=100&start=0&start_timestamp="
         + startTs
         + "&end_timestamp="
         + endTs
         + "&confirm=0";
      HttpClient client = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(20L)).followRedirects(Redirect.NORMAL).build();
      HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).header("Accept", "application/json").timeout(Duration.ofSeconds(25L)).GET().build();

      try {
         HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
         if (response.statusCode() != 200) {
            log.warn("TronScan 请求失败 status={} baseUrl={} body={}", new Object[]{response.statusCode(), baseUrl, response.body()});
            return null;
         } else {
            JSONObject body = JSON.parseObject(response.body());
            JSONArray tokenTransfers = body.getJSONArray("token_transfers");
            if (tokenTransfers != null && !tokenTransfers.isEmpty()) {
               List<UsdtChainCheckTaskRunner.Trc20Transfer> list = new ArrayList<>();

               for (int i = 0; i < tokenTransfers.size(); i++) {
                  JSONObject item = tokenTransfers.getJSONObject(i);
                  String to = item.getString("to_address");
                  if (to != null && to.equalsIgnoreCase(toAddress) && Boolean.TRUE.equals(item.getBoolean("confirmed"))) {
                     String quant = item.getString("quant");
                     if (quant != null) {
                        BigDecimal valueUsdt = new BigDecimal(quant).divide(USDT_DIVISOR, 6, RoundingMode.HALF_UP);
                        UsdtChainCheckTaskRunner.Trc20Transfer t = new UsdtChainCheckTaskRunner.Trc20Transfer();
                        t.txId = item.getString("transaction_id");
                        t.valueUsdt = valueUsdt;
                        Long blockTs = item.getLong("block_ts");
                        t.blockTimestamp = blockTs != null ? blockTs : null;
                        list.add(t);
                     }
                  }
               }

               return list;
            } else {
               return Collections.emptyList();
            }
         }
      } catch (HttpTimeoutException var21) {
         log.warn("TronScan 请求超时: {}", var21.getMessage());
         return null;
      } catch (Exception var22) {
         log.warn("TronScan 请求异常: {}", var22.getMessage());
         return null;
      }
   }

   private List<UsdtChainCheckTaskRunner.Trc20Transfer> fetchFromTronGrid(String toAddress) {
      String url = "https://api.trongrid.io/v1/accounts/"
         + toAddress
         + "/transactions/trc20?contract_address=TR7NHqjeKQxGTCi8q8ZY4pL8otSzgjLj6t&only_confirmed=true&limit=100&min_timestamp="
         + (System.currentTimeMillis() - 86400000L);
      HttpClient client = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(45L)).build();
      HttpRequest request = HttpRequest.newBuilder()
         .uri(URI.create(url))
         .header("Accept", "application/json")
         .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 Chrome/120.0")
         .timeout(Duration.ofSeconds(60L))
         .GET()
         .build();

      try {
         HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
         if (response.statusCode() != 200) {
            log.warn("TronGrid 请求失败 status={} body={}", response.statusCode(), response.body());
            return Collections.emptyList();
         } else {
            JSONObject body = JSON.parseObject(response.body());
            JSONArray data = body.getJSONArray("data");
            if (data != null && !data.isEmpty()) {
               List<UsdtChainCheckTaskRunner.Trc20Transfer> list = new ArrayList<>();

               for (int i = 0; i < data.size(); i++) {
                  JSONObject item = data.getJSONObject(i);
                  if ("Transfer".equalsIgnoreCase(item.getString("type"))) {
                     String to = item.getString("to");
                     if (toAddress.equalsIgnoreCase(to)) {
                        String value = item.getString("value");
                        if (value != null) {
                           BigDecimal valueUsdt = new BigDecimal(value).divide(USDT_DIVISOR, 6, RoundingMode.HALF_UP);
                           UsdtChainCheckTaskRunner.Trc20Transfer t = new UsdtChainCheckTaskRunner.Trc20Transfer();
                           t.txId = item.getString("transaction_id");
                           t.valueUsdt = valueUsdt;
                           t.blockTimestamp = item.getLong("block_timestamp");
                           list.add(t);
                        }
                     }
                  }
               }

               return list;
            } else {
               return Collections.emptyList();
            }
         }
      } catch (HttpTimeoutException var15) {
         log.warn("TronGrid 请求超时: {}，可浏览器直接测: {}", var15.getMessage(), url);
         return Collections.emptyList();
      } catch (Exception var16) {
         log.warn("TronGrid 请求异常: {}，可浏览器直接测: {}", var16.getMessage(), url);
         return Collections.emptyList();
      }
   }

   private static class Trc20Transfer {
      String txId;
      BigDecimal valueUsdt;
      Long blockTimestamp;
   }
}
