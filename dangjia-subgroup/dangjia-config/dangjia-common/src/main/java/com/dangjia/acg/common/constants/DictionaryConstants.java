package com.dangjia.acg.common.constants;

/***
 * 对应字典表 t_dictionary 详解 根据JDK包装原因， 请大家注意 , 凡是Integer数据超过128 之后 请使用Integer.value 进行比较
 *
 * @author Administrator
 */

abstract public class DictionaryConstants {

  /***
   * start 数据状态
   */

  /**
   * 数据状态: 0 可用， 1 锁定
   **/
  public static final Integer USER_STATE_VALID = 0;

  /**
   * 数据状态: 0 可用， 1 锁定
   **/
    public static final Integer USER_STATE_LOCKED = 1;

  /**
   * end
   */

  /***
   * start 数据状态
   */

  /**
   * 数据状态: 0 可用
   **/
  public static final Integer DATA_STATUS_VALID = 0;

  /**
   * 数据状态: 1 锁定
   **/
  public static final Integer DATA_STATUS_LOCKED = 1;

  /**
   * 数据状态: 2 过期
   **/
  public static final Integer DATA_STATUS_OVERDUE = 2;

  /** 数据状态:等待/等待进行 **/
  public static final Integer DATA_STATUS_WAI = 3;


  /***
   * start 答题匹配
   */

  /**
   * 答题匹配: 团战人数
   **/
  public static final Integer GROUP_WAR = 3;

  /** 个人匹配人数 **/
  public static final Integer PERSON_PEOPLE_NUMBER = 2;

  /** 团队匹配人数 **/
  public static final Integer TEAM_PEOPLE_NUMBER = 6;

  /**
   * 答题匹配: 段位匹配漂移量
   **/
  public static final Integer RANK_OFFSET = 1;
  /**
   * 团队数量
   */
  public static final Integer TEAM_SIZE = 2;
  /**
   * 团队最小开战人数
   */
  public static final Integer TEAM_MINIMUM_NUMBER_OF_WAR = 2;
  /**
   * 团队匹配人数
   */
  public static final Integer TEAM_NUMBER = 3;
  /**
   * end
   */


  /***
   * start 城市是否开通
   */

  /**
   * 城市是否开通， 0 未开通， 1 开通
   **/
  public static final Integer CITY_OPENED = 1;

  /**
   * 城市是否开通， 0 未开通， 1 开通
   **/
  public static final Integer CITY_NOT_OPEN = 0;

  /**
   * end
   */

  /**
   * start 公共文件类型
   */

  /**
   * 公共文件类型: 用户头像
   **/
  public static final Integer SOURCE_TYPE_USER_IMG = 0;

  /**
   * 公共文件类型: 机器人头像
   **/
  public static final Integer SOURCE_TYPE_ROBOT_IMG = 1;

  /**
   * 公共文件类型: 答题题目类型
   **/
  public static final Integer SOURCE_TYPE_QUESTIONS_TYPE = 2;

  /**
   * 公共文件类型: 答题场景类型
   **/
  public static final Integer SOURCE_TYPE_MAP_TYPE = 3;

  /**
   * 公共文件类型: 用户弹窗类型
   **/
  public static final Integer SOURCE_TYPE_POPUP_TYPE = 4;


  /**
   * end
   */

  /**
   * start 应用类型
   */

  /**
   * 应用类型: 当家平台
   **/
  public static final Integer APP_TYPE = 0;

  /**
   * 应用类型: 装修
   **/
  public static final Integer APP_TYPE_ZX = 1;
  /**
   * 应用类型: 工匠
   **/
  public static final Integer APP_TYPE_GJ = 2;




  /**
   * end
   */

  /**
   * start 搜索排序规则
   */
  /** asc */
  public static final Integer SCHOOL_ES_SORT_ASC = 0;

  /** desc */
  public static final Integer SCHOOL_ES_SORT_DESC = 1;

}
