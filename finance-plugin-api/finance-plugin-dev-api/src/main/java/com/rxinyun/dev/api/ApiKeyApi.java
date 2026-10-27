package com.rxinyun.dev.api;

import com.rxinyun.dev.core.pojo.BaseApiKey;

public interface ApiKeyApi {
   BaseApiKey getByApiKey(String apiKey);
}
