package com.deque.axe.downloaders;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;

/**
 * Cache downloaded external resources.
 */
public class CachedContentDownloader  extends ContentDownloader {
  /**
   * The content downloader.
   */
  private ContentDownloader contentDownloader;

  /**
   * the resource cache.
   */
  private static ConcurrentMap<String, String> resourcesCache = new ConcurrentHashMap<>();

  /**
   * Initialize an instance of the CachedContentDownloader class.
   * @param webClient WebClient instance to use
   */
  public CachedContentDownloader(HttpClient webClient) {
    super(webClient);
    if (webClient == null) {
      throw new NullPointerException("the webClient is null");
    }
    contentDownloader = new CachedContentDownloader(webClient);
  }

  /**
   * Get the content from the cache if exists, otherwise get ir from the resource url.
   * @param resourceUrl Resource url
   * @return Content of the resource
   */
  @Override
  public String getContent(HttpUriRequest resourceUrl) throws IOException {
    if (resourceUrl == null) {
      throw new NullPointerException("the Http URI request was null");
    }

    String content;
    String key = resourceUrl.toString();
    if (!resourcesCache.get(key).isEmpty()) {
      return resourcesCache.get(key);
    }
    content = contentDownloader.getContent(resourceUrl);

    if (content.isEmpty()) {
      return content;
    }
    resourcesCache.put(key, content);

    if (resourcesCache.containsKey(key) && resourcesCache.containsValue(content)) {
      return content;
    }
    return null;
  }
}