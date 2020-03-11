/*
 * Copyright 2020 (C) Magenic, All rights Reserved
 */

package com.magenic.jmaqs.accessibility.downloaders;

import java.io.IOException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;

/**
 * Get resources content from URLs.
 */
public class ContentDownloader {
  private HttpClient webClient;

  /**
   * Initialize an instance of com.magenic.jmaqs.accessiblity.downloaders.ContentDownloader.
   * @param webClient WebClient instance to use
   */
  public ContentDownloader(HttpClient webClient) {
    if (webClient == null) {
      throw new NullPointerException("the webClient is null");
    }
    this.webClient = webClient;
  }

  /**
   * Get the resource's content.
   * @param resourceUrl Resource url
   * @return Content of the resource
   */
  public String getContent(HttpUriRequest resourceUrl) throws IOException {
    return webClient.execute(resourceUrl).toString();
  }
}