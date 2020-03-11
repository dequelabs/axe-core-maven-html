/*
 * Copyright 2020 (C) Magenic, All rights Reserved
 */

package com.magenic.jmaqs.accessibility.providers;

import com.magenic.jmaqs.accessibility.downloaders.CachedContentDownloader;
import com.magenic.jmaqs.accessibility.downloaders.ContentDownloader;
import java.io.IOException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;

public class ExternalAxeScriptProvider implements IAxeScriptProvider {
  private HttpUriRequest scriptUri;

  private ContentDownloader contentDownloader;

  public HttpUriRequest getScriptUri() {
    return scriptUri;
  }

  public void setScriptUri(HttpUriRequest scriptUri) {
    this.scriptUri = scriptUri;
  }

  public ContentDownloader getContentDownloader() {
    return contentDownloader;
  }

  public void setContentDownloader(ContentDownloader contentDownloader) {
    this.contentDownloader = contentDownloader;
  }

  public ExternalAxeScriptProvider(HttpClient webClient, HttpUriRequest newScriptUri) {
    if (webClient == null) {
      throw new NullPointerException("the web client is null");
    }

    if (newScriptUri == null) {
      throw new NullPointerException("the HTTP Uri Request is ");
    }
    scriptUri = newScriptUri;
    contentDownloader = new CachedContentDownloader(webClient);
  }

  @Override
  public String getScript() throws IOException {
    return contentDownloader.getContent(scriptUri);
  }
}