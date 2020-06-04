/*
 * Copyright (C) 2020 Deque Systems Inc.,
 *
 * Your use of this Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * This entire copyright notice must appear in every copy of this file you
 * distribute or in any file that contains substantial portions of this source
 * code.
 */

package results;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;

/**
 * the Axe Result Related Node class object.
 */
public class AxeResultRelatedNode {
  /**
   * the html of the Result related node.
   */
  private String html;

  /**
   * the target of the result related node.
   */
  private List<String> target;

  /**
   * gets the html string.
   * @return the html string
   */
  @JsonProperty("html")
  public String getHtml() {
    return html;
  }

  /**
   * sets the html string.
   * @param value the html string value to be set
   */
  @JsonProperty("html")
  public void setHtml(final String value) {
    this.html = value;
  }

  /**
   * gets the target list of string,.
   * @return a list of target strings
   */
  @JsonProperty("target")
  public List<String> getTarget() {
    return target;
  }

  /**
   * sets the target list of strings.
   * @param value the JSAONArray that contains the list of Target strings
   */
  @JsonProperty("target")
  public void setTarget(final JSONArray value) {
    List<String> list = new ArrayList<>();
    for (int i = 0; i < value.length(); i++) {
      list.add(value.get(i).toString());
    }
    this.target = list;
  }
}
