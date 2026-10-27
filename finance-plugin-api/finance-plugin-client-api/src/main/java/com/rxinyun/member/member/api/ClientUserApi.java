package com.rxinyun.member.member.api;

import java.time.LocalDateTime;
import java.util.HashMap;

public interface ClientUserApi {
   Long getUserCountByCreateTime(HashMap<String, LocalDateTime> hashMap);
}
