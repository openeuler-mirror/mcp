package com.hnkylin.cloud.core.config;


import com.hnkylin.cloud.core.annotation.FieldCheck;
import com.hnkylin.cloud.core.annotation.ListCheck;
import com.hnkylin.cloud.core.annotation.ModelCheck;
import com.hnkylin.cloud.core.config.exception.KylinParamException;
import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
public class ParamterCheckCompent {
    LocalVariableTableParameterNameDiscoverer disc = new LocalVariableTableParameterNameDiscoverer();

    /**
     * 校验带注解的方法
     *
     * @param methodName
     * @param target
     * @param args
     * @throws KylinParamException
     */
    public void checkAnnoValid(String methodName, Object target, Object[] args) throws KylinParamException {
        String str = "";
        try {
            Method method = getMethodByClassAndName(target.getClass(), methodName, args);
            Annotation[][] annotations = method.getParameterAnnotations();
            String[] paramNames = disc.getParameterNames(method);
            if (annotations != null) {
                for (int i = 0; i < annotations.length; i++) {
                    Annotation[] anno = annotations[i];
                    for (int j = 0; j < anno.length; j++) {
                        if (annotations[i][j].annotationType().equals(ModelCheck.class)) {
                            ModelCheck mcheck = (ModelCheck) annotations[i][j];
                            str = checkModel(args[i], mcheck, paramNames[i]);
                        } else if (annotations[i][j].annotationType().equals(ListCheck.class)) {// List
                            ListCheck lcheck = (ListCheck) annotations[i][j];
                            str = checkListParam(args[i], lcheck, paramNames[i]);
                        } else if (annotations[i][j].annotationType().equals(FieldCheck.class)) {// Field
                            FieldCheck fcheck = (FieldCheck) annotations[i][j];
                            str = checkField(fcheck, args[i], paramNames[i]);
                        }
                        if (StringUtils.hasText(str)) {
                            throw new KylinParamException(str);
                        }
                    }
                }
            }
        } catch (Throwable e) {
//			System.out.println(e.getMessage());
            e.printStackTrace();
            throw new KylinParamException(str);
        }
    }

    public void checkValid(ProceedingJoinPoint joinPoint) throws KylinParamException {
        // TODO Auto-generated method stub
        Object[] args = null;
        Method method = null;
        Object target = null;
        String methodName = null;
        String str = "";
        try {
            methodName = joinPoint.getSignature().getName();
            target = joinPoint.getTarget();
            args = joinPoint.getArgs(); // 方法的参数
            method = getMethodByClassAndName(target.getClass(), methodName, args);
            Annotation[][] annotations = method.getParameterAnnotations();
            String[] paramNames = disc.getParameterNames(method);
            if (annotations != null) {
                for (int i = 0; i < annotations.length; i++) {
                    Annotation[] anno = annotations[i];
                    for (int j = 0; j < anno.length; j++) {
                        if (annotations[i][j].annotationType().equals(ModelCheck.class)) {
                            ModelCheck mcheck = (ModelCheck) annotations[i][j];
                            str = checkModel(args[i], mcheck, paramNames[i]);
                        } else if (annotations[i][j].annotationType().equals(ListCheck.class)) {// List
                            ListCheck lcheck = (ListCheck) annotations[i][j];
                            str = checkListParam(args[i], lcheck, paramNames[i]);
                        } else if (annotations[i][j].annotationType().equals(FieldCheck.class)) {// Field
                            FieldCheck fcheck = (FieldCheck) annotations[i][j];
                            str = checkField(fcheck, args[i], paramNames[i]);
                        }
                        if (StringUtils.hasText(str)) {
                            throw new KylinParamException(str);
                        }
                    }
                }
            }
        } catch (Throwable e) {
//			System.out.println(e.getMessage());
            throw new KylinParamException(str);
        }
    }

    @SuppressWarnings("unchecked")
    private String checkField(FieldCheck check, Object arg, String paramNames) {
        int length = 0;
        if (arg == null) {
            if (check.notNull()) {
                return getNotNullMessage(paramNames, check);
            } else if (check.numeric()) {
                return getNumericMessage(paramNames, check);
            } else if (check.minLen() > 0) {
                return getMinLenMessage(paramNames, check);
            } else if (check.maxLen() > 0) {
                return getMaxLenMessage(paramNames, check);
            } else if (check.minNum() != -999999999) {
                return getMinNumMessage(paramNames, check, false);
            } else if (check.maxNum() != -999999999) {
                return getMaxNumMessage(paramNames, check, false);
            } else {
                return "";
            }
        }
        Class<?> cls = arg.getClass();
        String clname = cls.getName();
//        System.out.println("field-class: " + cls.getName());
        boolean arraybl = false, strbl = false, intbl = false, longbl = false, doublebl = false, floatbl = false,
                blbl = false;
        // 判断是否为数字
        if (clname.equals("java.lang.Integer") || clname.equals("int")) {
            intbl = true;
        } else if (clname.equals("java.lang.Long") || clname.equals("long")) {
            longbl = true;
        } else if (clname.equals("java.lang.Double") || clname.equals("double")) {
            doublebl = true;
        } else if (clname.equals("java.lang.Float") || clname.equals("float")) {
            floatbl = true;
        } else if (clname.equals("java.lang.String")) {// 判断是否为字符串
            strbl = true;
        } else if (clname.equals("java.util.ArrayList")) {
            // 判断是否为List
            arraybl = true;
        } else if (clname.equals("java.util.Boolean")) {// 判断是否为Boolean
            blbl = true;
        }

        boolean numbl = intbl || longbl || floatbl || doublebl;
        boolean lenbl = arraybl || strbl;
        if (arg != null) {
            if (strbl)
                length = (String.valueOf(arg)).length();
            if (arraybl)
                length = ((List<Object>) arg).size();
        }

        if (check.numeric() && arg != null) {
            try {
                new BigDecimal(String.valueOf(arg));
            } catch (Exception e) {
                return getNumericMessage(paramNames, check);
            }
        }
        if (lenbl) {
            if (check.maxLen() > 0 && (length > check.maxLen())) {
                return getMaxLenMessage(paramNames, check);
            }

            if (check.minLen() > 0 && (length < check.minLen())) {
                return getMinLenMessage(paramNames, check);
            }
        }
        if (numbl) {
            if (check.minNum() != -999999999) {
                try {
                    boolean errbl = false;
                    if (longbl || intbl) {
                        long fieldValue = Long.parseLong(String.valueOf(arg));
                        if (fieldValue < check.minNum()) {
                            errbl = true;
                        }
                    }
                    if (floatbl || doublebl) {
                        double fieldValue = Double.parseDouble(String.valueOf(arg));
                        if (fieldValue < check.minNum()) {
                            errbl = true;
                        }
                    }
                    if (errbl) {
                        return getMinNumMessage(paramNames, check, false);
                    }
                } catch (Exception e) {
                    return getMinNumMessage(paramNames, check, true);
                }
            }

            if (check.maxNum() != -999999999) {
                try {
                    boolean errbl = false;
                    if (longbl || intbl) {
                        long fieldValue = Long.parseLong(String.valueOf(arg));
                        if (fieldValue > check.maxNum()) {
                            errbl = true;
                        }
                    } else if (floatbl || doublebl) {
                        double fieldValue = Double.parseDouble(String.valueOf(arg));
                        if (fieldValue > check.maxNum()) {
                            errbl = true;
                        }
                    }
                    if (errbl) {
                        return getMaxNumMessage(paramNames, check, false);
                    }
                } catch (Exception e) {
                    return getMaxNumMessage(paramNames, check, true);
                }
            }
        }

        // if(arraybl){
        // checkListParam(args, lcheck)
        // }
        return "";
    }

    /**
     * 校验List入参
     *
     * @param args
     * @param lcheck
     * @param paramNames
     * @return
     * @throws Exception
     */
    private String checkListParam(Object args, ListCheck lcheck, String paramNames) throws Exception {
        String retStr = "";
        if (args == null) {
            if (lcheck.notNull()) {
                if (lcheck.notNullMessage().equals("")) {
                    return paramNames + "不允许为空";
                } else {
                    return lcheck.defaultMessage();
                }
            } else if (lcheck.maxLen() != -1) {
                if (lcheck.defaultMessage().equals("")) {
                    return paramNames + "不允许为空";
                } else {
                    return lcheck.defaultMessage();
                }
            } else if (lcheck.minLen() != -1) {
                if (lcheck.minLenMessage().equals("")) {
                    return paramNames + "不允许为空";
                } else {
                    return lcheck.defaultMessage();
                }
            }
        }
        if (args != null) {
            String aclz = args.getClass().getName();
//			System.out.println("args-class: " + aclz);
            if (aclz.equals("java.util.ArrayList")) {
                @SuppressWarnings("unchecked")
                List<Object> argList = (List<Object>) args;
                int size = argList.size();
                if (lcheck.minLen() != -1) {
                    if (size < lcheck.minLen()) {
                        retStr = lcheck.minLenMessage();
                        if (retStr.equals("")) {
                            retStr = lcheck.defaultMessage();
                        }
                        if (retStr.equals("")) {
                            retStr = paramNames + "集合大小最小为" + lcheck.minLen();
                        }
                        return retStr;
                    }
                }
                if (lcheck.maxLen() != -1) {
                    if (size > lcheck.maxLen()) {
                        retStr = lcheck.maxLenMessage();
                        if (retStr.equals("")) {
                            retStr = lcheck.defaultMessage();
                        }
                        if (retStr.equals("")) {
                            retStr = paramNames + "集合大小最大为" + lcheck.maxLen();
                        }
                        return retStr;
                    }
                }
                // else
                for (Object arg : argList) {
                    if (arg != null) {
                        retStr = checkModel(arg, null, "");
                    }
                    if (!retStr.equals("")) {
                        break;
                    }
                }
            }
        }

        return retStr;
    }

    /**
     * 校验参数
     *
     * @param args
     * @param mcheck
     * @param paramNames
     * @return
     * @throws Exception
     */
    private String checkModel(Object args, ModelCheck mcheck, String paramNames) throws Exception {
        String retStr = "";
        if (args == null) {
            if (mcheck.notNull()) {
                if (mcheck.notNullMessage().equals("")) {
                    return paramNames + "不允许为空";
                } else {
                    return mcheck.notNullMessage();
                }
            }
            return "";
        }
        Field[] field = getBeanFields(args);// 获取实体及父类的field
        // args.getClass().getDeclaredFields();// 获取方法参数（实体）的field
        for (int j = 0; j < field.length; j++) {
            FieldCheck check = field[j].getAnnotation(FieldCheck.class);// 获取方法参数（实体）的field上的注解Check
            if (check != null) {
                retStr = validateFiled(check, field[j], args);
                if (StringUtils.hasText(retStr)) {
                    return retStr;
                }
            } else {
                ListCheck lcheck = field[j].getAnnotation(ListCheck.class);
                if (lcheck != null) {
                    field[j].setAccessible(true);
                    retStr = checkListParam(field[j].get(args), lcheck, field[j].getName());
                    if (StringUtils.hasText(retStr)) {
                        return retStr;
                    }
                }
            }
        }
        return retStr;
    }

    public Field[] getBeanFields(Object obj) {
        List<Field> fieldList = new ArrayList<>();
        Class<?> tmp = obj.getClass();
        while (tmp != null && tmp instanceof Object) {
            fieldList.addAll(Arrays.asList(tmp.getDeclaredFields()));
            tmp = tmp.getSuperclass();
        }
        return fieldList.toArray(new Field[fieldList.size()]);
    }

    /**
     * 校验参数规则
     *
     * @param check
     * @param field
     * @param args
     * @return
     * @throws Exception
     */
    public String validateFiled(FieldCheck check, Field field, Object args) throws Exception {
        field.setAccessible(true);
        // 获取field长度
        int length = 0;
        Class<?> cls = field.getType();
        String clname = cls.getName();
//		System.out.println("field-class: " + cls.getName());
        boolean arraybl = false, strbl = false, intbl = false, longbl = false, doublebl = false, floatbl = false,
                blbl = false;
        // 判断是否为数字
        if (clname.equals("java.lang.Integer") || clname.equals("int")) {
            intbl = true;
        } else if (clname.equals("java.lang.Long") || clname.equals("long")) {
            longbl = true;
        } else if (clname.equals("java.lang.Double") || clname.equals("double")) {
            doublebl = true;
        } else if (clname.equals("java.lang.Float") || clname.equals("float")) {
            floatbl = true;
        } else if (clname.equals("java.lang.String")) {// 判断是否为字符串
            strbl = true;
        } else if (clname.equals("java.util.ArrayList")) {
            // 判断是否为List
            arraybl = true;
        } else if (clname.equals("java.util.Boolean")) {// 判断是否为Boolean
            blbl = true;
        }

        boolean numbl = intbl || longbl || floatbl || doublebl;
        boolean lenbl = arraybl || strbl;
        if (field.get(args) != null) {
            if (strbl)
                length = (String.valueOf(field.get(args))).length();
            if (arraybl)
                length = ((List<Object>) field.get(args)).size();
        }
        if (check.notNull()) {
            if (field.get(args) == null || "".equals(String.valueOf(field.get(args)))) {
                return getNotNullMessage(field.getName(), check);
            }
        }

        if (check.numeric() && field.get(args) != null) {
            try {
                new BigDecimal(String.valueOf(field.get(args)));
            } catch (Exception e) {
                return getNumericMessage(field.getName(), check);
            }
        }
        // spring mvc 默认会把null值设置成false，json非true | false 请求出错
        // 此处判定无用
        /*
         * if (blbl) { try { Boolean.parseBoolean(String.valueOf(field.get(args))); }
         * catch (Exception e) { if (check.defaultMessage().length() > 0) { return
         * check.defaultMessage(); } else { return field.getName() + "必须为true或者false"; }
         * } }
         */
        if (lenbl) {
            if (check.maxLen() > 0 && (length > check.maxLen())) {
                return getMaxLenMessage(field.getName(), check);
            }

            if (check.minLen() > 0 && (length < check.minLen())) {
                return getMinLenMessage(field.getName(), check);
            }
        }
        if (numbl) {
            if (check.minNum() != -999999999) {
                try {
                    boolean errbl = false;
                    if (longbl || intbl) {
                        long fieldValue = Long.parseLong(String.valueOf(field.get(args)));
                        if (fieldValue < check.minNum()) {
                            errbl = true;
                        }
                    }
                    if (floatbl || doublebl) {
                        double fieldValue = Double.parseDouble(String.valueOf(field.get(args)));
                        if (fieldValue < check.minNum()) {
                            errbl = true;
                        }
                    }
                    if (errbl) {
                        return getMinNumMessage(field.getName(), check, false);
                    }
                } catch (Exception e) {
                    return getMinNumMessage(field.getName(), check, true);
                }
            }

            if (check.maxNum() != -999999999) {
                try {
                    boolean errbl = false;
                    if (longbl || intbl) {
                        long fieldValue = Long.parseLong(String.valueOf(field.get(args)));
                        if (fieldValue > check.maxNum()) {
                            errbl = true;
                        }
                    } else if (floatbl || doublebl) {
                        double fieldValue = Double.parseDouble(String.valueOf(field.get(args)));
                        if (fieldValue > check.maxNum()) {
                            errbl = true;
                        }
                    }
                    if (errbl) {
                        return getMaxNumMessage(field.getName(), check, false);
                    }
                } catch (Exception e) {
                    return getMaxNumMessage(field.getName(), check, true);
                }
            }
        }

        // if(arraybl){
        // checkListParam(args, lcheck)
        // }
        return "";
    }

    private String getMaxNumMessage(String name, FieldCheck check, boolean err) {
        if (check.maxNumMessage().length() > 0) {
            return check.maxNumMessage();
        }
        if (check.defaultMessage().length() > 0) {
            return check.defaultMessage();
        }
        if (!err)
            return name + "必须不大于" + check.maxNum();
        else
            return name + "必须为数值型，且不大于" + check.maxNum();
    }

    private String getMinNumMessage(String name, FieldCheck check, boolean err) {
        if (check.minNumMessage().length() > 0) {
            return check.minNumMessage();
        }
        if (check.defaultMessage().length() > 0) {
            return check.defaultMessage();
        }
        if (!err)
            return name + "必须不小于" + check.minNum();
        else
            return name + "必须为数值型，且不小于" + check.minNum();
    }

    private String getMinLenMessage(String name, FieldCheck check) {
        if (check.minLenMessage().length() > 0) {
            return check.minLenMessage();
        }
        if (check.defaultMessage().length() > 0) {
            return check.defaultMessage();
        }
        return name + "长度不能小于" + check.minLen();
    }

    private String getMaxLenMessage(String name, FieldCheck check) {
        if (check.maxLenMessage().length() > 0) {
            return check.maxLenMessage();
        }
        if (check.defaultMessage().length() > 0) {
            return check.defaultMessage();
        }
        return name + "长度不能大于" + check.maxLen();
    }

    private String getNumericMessage(String name, FieldCheck check) {
        if (check.numericMessage().length() > 0) {
            return check.numericMessage();
        }
        if (check.defaultMessage().length() > 0) {
            return check.defaultMessage();
        }
        return name + "必须为数值型";
    }

    private String getNotNullMessage(String fieldName, FieldCheck check) {
        if (check.notNullMessage().length() > 0) {
            return check.notNullMessage();
        }
        if (check.defaultMessage().length() > 0) {
            return check.defaultMessage();
        }
        return fieldName + "不能为空";
    }

    /**
     * 根据类和方法名得到方法
     *
     * @param args
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public Method getMethodByClassAndName(Class c, String methodName, Object[] args) throws Exception {
        boolean pbl = true;
        if (args != null && args.length > 0) {
            Class<?>[] parameterTypes = new Class<?>[args.length];
            if (args != null && args.length > 0) {
                int i = 0;
                for (Object obj : args) {
                    if (obj != null) {
                        Class<?> clz = obj.getClass();
                        if (clz.getName().equals("java.util.ArrayList")) {
                            clz = List.class;
                        }
                        parameterTypes[i++] = clz;
                    } else {
                        pbl = false;
                        break;
                    }
                }
                if (pbl) {
                    return c.getDeclaredMethod(methodName, parameterTypes);
                }
            }
        }

        Method[] methods = c.getDeclaredMethods();
        for (Method method : methods) {
            if (method.getName().equals(methodName)) {
                if (args != null) {
                    if (method.getParameterCount() == args.length) {
                        return method;
                    }
                } else {
                    return method;
                }
            }
        }
        return null;
    }
}
