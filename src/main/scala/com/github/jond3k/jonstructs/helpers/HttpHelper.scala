package com.github.jond3k.jonstructs.helpers

import java.net.{URI, URL}
import org.apache.http.impl.client.DefaultHttpClient
import org.apache.http.client.methods.HttpGet
import org.apache.http.util.EntityUtils

/**
 *
 * @author Jon Davey <jond3k@gmail.com>
 */
trait HttpHelper {

  /**
   *
   * @param uri
   * @return
   */
  def request(uri: String): (Int, String) = request(new URI(uri))

  /**
   *
   * @param url
   * @return
   */
  def request(url: URL): (Int, String) = request(url.toURI)

  /**
   *
   * @param uri
   * @return
   */
  def request(uri: URI): (Int, String) = {
    val client = new DefaultHttpClient
    try {
      val get      = new HttpGet(uri)
      val response = client.execute(get)

      val body     = Option(response.getEntity) match {
        case Some(e) => EntityUtils.toString(e)
        case None => ""
      }

      (response.getStatusLine.getStatusCode, body)
    } finally {
      client.getConnectionManager.shutdown()
    }
  }
}
