package com.rxinyun.dev.api;

import java.io.File;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

public interface DevEmailApi {
   void sendTextEmailLocal(String tos, String subject, String content, List<File> files);

   void sendHtmlEmailLocal(String tos, String subject, String content, Map<String, InputStream> imageMap, List<File> files);
}
