package com.dangjia.acg.common.util;

import com.dangjia.acg.common.annotation.Desc;
import com.dangjia.acg.common.model.BaseEntity;
import org.apache.log4j.Logger;
import org.springframework.util.StringUtils;

import javax.persistence.Column;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.lang.reflect.Field;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;


/**
 * 实体校验器 需要集成BaseEntity
 * 根据 注解 校验实体
 *
 * @author Qiyuxiang
 */
abstract public class EntityValidator {

    /***
     * 待替换名
     */
    public static final String SIGN_FIELDNAME = "[name]";

    /****
     * 待替换描述
     */
    public static final String SIGN_FIELDDESC = "[desc]";

    /***
     * log
     */
    private static Logger logger = Logger.getLogger(EntityValidator.class);

    /***
     * 线程安全的校验器
     */
    private static Validator validator;

    static {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    /**
     * 实体校验,返回不通过的信息列表
     *
     * @param entity 校验
     * @return map
     */
    public static Map<String, String> validate(BaseEntity entity) {
        Map<String, String> map = new LinkedHashMap<String, String>();
        Set<ConstraintViolation<BaseEntity>> cvs = validator.validate(entity);
        validateLength(map, entity);
        validateEnumCode(map, entity);
        for (ConstraintViolation<BaseEntity> cv : cvs) {
            String fieldName = cv.getPropertyPath().toString();
            // 根据Column注解的长度更新message
            String message = refreshMessageByColumnLen(fieldName, entity, cv.getMessage());
            map.put(fieldName, message);
        }

        for (Map.Entry<String, String> entry : map.entrySet()) {

            map.put(entry.getKey(), decorateMessage(entry.getKey(), entity, entry.getValue()));
        }


        return map;
    }

    /***
     * 校验长度
     *
     * @param map    异常信息
     * @param entity 实体
     */
    private static void validateLength(Map<String, String> map, BaseEntity entity) {
        if (null == entity) {
            return;
        }
        Field[] fields = entity.getClass().getDeclaredFields();
        for (Field field : fields) {
            try {
                if (String.class != field.getType()) {
                    continue;
                }
                Column column = field.getAnnotation(Column.class);
                if (null == column) {
                    continue;
                }
                field.setAccessible(true);
                String fieldName = field.getName();
                String fieldvalue = (String) field.get(entity);
                int annoLength = column.length();

                if (null != fieldvalue && fieldvalue.length() > annoLength) {
                    logger.info("字段长度过长.[entity=" + entity.getClass().getName() + ",field=" + field.getName() + ",value=" + fieldvalue + "]");
                    map.put(fieldName, SIGN_FIELDDESC + "的长度超过最大允许长度" + annoLength + ".");
                }
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        }
    }


    /***
     * 根据desc注解校验枚举成员
     *
     * @param map    exception数据
     * @param entity 实体信息
     */
    private static void validateEnumCode(Map<String, String> map, BaseEntity entity) {
        if (null == entity) {
            return;
        }
        Field[] fields = entity.getClass().getDeclaredFields();
        for (Field field : fields) {
            try {
                Desc desc = field.getAnnotation(Desc.class);
                if (StringUtils.isEmpty(desc.enumName())) {
                    continue;
                }
                field.setAccessible(true);
                String fieldName = field.getName();
                Class<?> enumClazz = null;
                try {
                    enumClazz = Class.forName("com.dangjia.acg.push.enums." + desc.enumName());
                } catch (ClassNotFoundException ce) {
                    logger.error("枚举名称配置错误.[entity=" + entity.getClass().getName() + ",field=" + field.getName() + ",enumName=" + desc.enumName() + "]");
                    map.put(fieldName, SIGN_FIELDDESC + "枚举名称配置错误.");
                    continue;
                }
                Object fieldvalue = field.get(entity);
                if (null == fieldvalue) {
                    map.put(fieldName, SIGN_FIELDDESC + "不能为空.");
                    continue;
                }
                if (int.class != fieldvalue.getClass() && Integer.class != fieldvalue.getClass()) {
                    logger.error("返回值类型错误.[entity=" + entity.getClass().getName() + ",field=" + field.getName() + ",enumName=" + desc.enumName() + "]");
                    map.put(fieldName, SIGN_FIELDDESC + "值类型错误.");
                    continue;
                }
                Object result = ClazzUtil.invokeStatic(enumClazz, "getInstance", new Class[] {int.class}, new Object[] {fieldvalue});
                if (null == result) {
                    map.put(fieldName, SIGN_FIELDDESC + "的值不在可选范围内.");
                }
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        }
    }

    /***
     * 替换message信息
     *
     * @param fieldName 字段名
     * @param entity    实体
     * @param message   消息
     * @return 被处理过的异常字段
     */
    private static String refreshMessageByColumnLen(String fieldName, BaseEntity entity, String message) {
        if (StringUtils.hasText(message)) {
            return message;
        }
        try {
            Column column = entity.getClass().getField(fieldName).getAnnotation(Column.class);
            return SIGN_FIELDDESC + "的长度不能超过" + column.length() + ".";
        } catch (Exception e) {
            return message;
        }
    }

    /***
     * 消息包装
     *
     * @param fieldName 字段名
     * @param entity    实体
     * @param message   消息体
     * @return
     */
    private static String decorateMessage(String fieldName, BaseEntity entity, String message) {
        if (null == message) {
            return "";
        }
        String res = message.replace(SIGN_FIELDNAME, fieldName);
        try {
            Desc desc = entity.getClass().getDeclaredField(fieldName).getAnnotation(Desc.class);
            res = res.replace(SIGN_FIELDDESC, desc.value());
        } catch (Exception e) {
            res = res.replace(SIGN_FIELDDESC, "[未知字段]");
        }
        return res;
    }

}