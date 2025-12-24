package com.laulem.vectopath.business.service;

import java.io.IOException;

public interface ContentDownloaderService {
    String downloadContent(String url) throws IOException;
}
