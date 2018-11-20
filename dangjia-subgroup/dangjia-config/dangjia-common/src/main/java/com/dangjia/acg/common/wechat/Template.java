package com.dangjia.acg.common.wechat;

/**
 * @author Qiyuxiang
 * @date 2018-04-26 22:47:27
 **/
public class Template {

  // 消息接收方
  private String touser;
  // 模板id
  private String template_id;
  private String page;
  private String form_id;
  private String color;
  private String emphasis_keyword;
  // 参数列表
  private TemplateData data;

  public String getTouser() {
    return touser;
  }

  public void setTouser(String touser) {
    this.touser = touser;
  }

  public String getTemplate_id() {
    return template_id;
  }

  public void setTemplate_id(String template_id) {
    this.template_id = template_id;
  }

  public String getPage() {
    return page;
  }

  public void setPage(String page) {
    this.page = page;
  }

  public String getForm_id() {
    return form_id;
  }

  public void setForm_id(String form_id) {
    this.form_id = form_id;
  }

  public String getColor() {
    return color;
  }

  public void setColor(String color) {
    this.color = color;
  }

  public String getEmphasis_keyword() {
    return emphasis_keyword;
  }

  public void setEmphasis_keyword(String emphasis_keyword) {
    this.emphasis_keyword = emphasis_keyword;
  }

  public TemplateData getData() {
    return data;
  }

  public void setData(TemplateData data) {
    this.data = data;
  }
}
