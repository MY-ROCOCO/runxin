package com.rxinyun.member.member.api;

import java.time.LocalDateTime;
import java.util.HashMap;

public interface PersonalRealApi {
   Long getPersonalRealCount(HashMap<String, LocalDateTime> map);
}
