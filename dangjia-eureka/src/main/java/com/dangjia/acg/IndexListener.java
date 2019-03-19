package com.dangjia.acg;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

/**
 * @author: QiYuXiang
 * @date: 2018/3/30
 */
@WebListener
public class IndexListener implements ServletContextListener{
  @Override
  public void contextDestroyed(ServletContextEvent arg0) {
    System.out.println("IndexListener2 contextDestroyed method");
  }
  @Override
  public void contextInitialized(ServletContextEvent arg0) {
    System.out.println("IndexListener2 contextInitialized method");
  }
}
