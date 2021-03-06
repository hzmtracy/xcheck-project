package com.cmy.xcheck.util.handler.impl;

import com.cmy.xcheck.support.XBean;
import com.cmy.xcheck.support.XResult;
import com.cmy.xcheck.util.Validator;
import com.cmy.xcheck.util.XMessageBuilder;
import com.cmy.xcheck.util.handler.ValidationHandler;
import com.cmy.xcheck.util.item.XCheckItem;
import com.cmy.xcheck.util.item.impl.XCheckItemSimple;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class SimpleValidationHandlerImpl implements ValidationHandler {

    @Autowired
    private XMessageBuilder xMessageBuilder;

    @Override
    public void validate(XBean xBean, XCheckItem checkItem, XResult xResult, Map<String, String[]> requestParam) {
        XCheckItemSimple checkItemSimple = (XCheckItemSimple) checkItem;
        List<XCheckItemSimple.FormulaItem> formulaItems = checkItemSimple.getFormulaItems();

        assertNull(xBean,xResult, requestParam, checkItemSimple, formulaItems);

        // 遍历公式
        for (XCheckItemSimple.FormulaItem formulaItem : formulaItems) {
            // 遍历校验字段
            for (String field : checkItemSimple.getFields()) {
                String[] values = requestParam.get(field);

                if (values == null) {
                    // 允许为空结束当前校验
                    if (checkItemSimple.isNullable()) {
                        continue;
                    } else {
                        String message = xMessageBuilder.buildMsg(field, "CanNotBeNull", xBean, checkItemSimple);
                        xResult.failure(message);
                        return;
                    }
                }

                // 遍历校验字段值
                for (String value : values) {
                    if (checkItemSimple.isNullable() && Validator.isEmpty(value)) {
                        // 允许空,字段值为空跳过当前校验
                        continue;
                    }
                    Boolean calculate = formulaItem.calculate(value);
                    if (!calculate) {
                        String message = xMessageBuilder.buildMsg(field, xBean, formulaItem, checkItemSimple);
                        xResult.failure(message);
                        return;
                    }
                }
            }
        }
    }

    private void assertNull(XBean xBean, XResult xResult, Map<String, String[]> requestParam,
                               XCheckItemSimple checkItemSimple, List<XCheckItemSimple.FormulaItem> formulaItems) {
        // 无校验公式，直接判断值不为空
        if (formulaItems.size() > 0) {
            return;
        }
        for (String field : checkItemSimple.getFields()) {
            String[] values = requestParam.get(field);

            if (values == null) {
                String message = xMessageBuilder.buildMsg(field, "CanNotBeNull", xBean, checkItemSimple);
                xResult.failure(message);
                return;
            }

            // 遍历校验字段值
            for (String value : values) {
                if (checkItemSimple.isNullable() && Validator.isEmpty(value)) {
                    String message = xMessageBuilder.buildMsg(field, "CanNotBeNull", xBean, checkItemSimple);
                    xResult.failure(message);
                    return;
                }
            }
        }
    }
//    public void validate(Map<String, String[]> requestParam, XBean xBean, XResult xResult) {
//        XCheckItemSimple[] checkItems = (XCheckItemSimple[]) xBean.getCheckItems();
//        String[] fields;
//        for (XCheckItemSimple item : checkItems) {
//            fields = item.getFields();
//            if (item.canNotBeNull()) {
//                validateFields(fields, requestParam, xBean, item, xResult);
//            }
//            if (xResult.isNotPass()) {
//                return;
//            }
//
//        }
//    }

//    private void validate0(String formulas, ArrayList<FieldMap> fmArray,
//                           String prompt, XResult result) {
//        Matcher formulaMatcher = FORMULA_PARSING_PATT.matcher(formulas);
//        while (formulaMatcher.find()) {
//            if (result.isNotPass()) {
//                return;
//            }
//            validateField(formulaMatcher, fmArray, prompt, result);
//        }
//    }

//    /**
//     * 校验null值
//     * @param fields
//     * @param requestParam
//     * @param xBean
//     * @param checkItem
//     * @param xResult
//     */
//    private void validateFields(String[] fields, Map<String, String[]> requestParam, XBean xBean, XCheckItemSimple checkItem, XResult xResult) {
//        for (String field : fields) {
//            String[] values = requestParam.get(field);
//            for (String val : values) {
//                if (Validator.isEmpty(val)) {
//                    String message = xMessageBuilder.buildMsg(field, "CanNotBeNull", xBean, checkItem);
//                    xResult.failure(message);
//                    return;
//                }
//            }
//        }
//    }

//    public void validate(Map<String, String> requestParam, String expression, XResult cr) {
//
//        String field;
//        ArrayList<FieldMap> fmArray; // 字段名与val值
//        String formulas;
//        String prompt = null; // 错误提示
//
//        int fieldBehindIndex;
//        int atIndex = expression.indexOf("@");
//        int numberSignIndex = expression.indexOf("#");
//        // 错误信息提示分隔符
//        int tildeIndex = expression.indexOf(":");
//
//        if (atIndex == -1 && numberSignIndex == -1) {
//            // 无校验方法，只要字段不为空则校验通过
//            if (tildeIndex == -1) {
//                field = expression;
//            } else {
//                field = expression.substring(0, tildeIndex);
//            }
//            // 同时处理单字段与多字段[]校验
//            fmArray = getVal(field, requestParam);
//            for (FieldMap fm : fmArray) {
//                if (Validator.isEmpty(fm.getVal())) {
//                    if (tildeIndex == -1) {
//                        cr.failure(fm.getField() + " can not be null");
//                    } else {
//                        cr.failure(
//                                expression.substring(tildeIndex+1, expression.length()));
//                    }
//                    // 如果校验不通过退出方法
//                    return;
//                }
//            }
//            // 字段不为空校验结束
//            return;
//        } else if (atIndex != -1) {
//            fieldBehindIndex = atIndex;
//        } else if (numberSignIndex != -1) {
//            fieldBehindIndex = numberSignIndex;
//        } else {
//            fieldBehindIndex = atIndex > numberSignIndex ? numberSignIndex : atIndex;
//        }
//
//        field = expression.substring(0, fieldBehindIndex);
//
//        fmArray = getVal(field, requestParam);
//        // check配置的校验提示
//        if (tildeIndex == -1) {
//            formulas = expression.substring(fieldBehindIndex, expression.length());
//        } else {
//            formulas = expression.substring(fieldBehindIndex, tildeIndex);
//            prompt = expression.substring(tildeIndex+1, expression.length());
//        }
//
//        // 开始公式校验
//        validate0(formulas, fmArray, prompt, cr);
//    }

//    /**
//     * 获取单字段或多字段[]字段名与值
//     * @param field
//     * @param requestParam
//     * @return
//     */
//    private ArrayList<FieldMap> getVal(String field, Map<String, String> requestParam) {
//        ArrayList<FieldMap> array = new ArrayList<FieldMap>();
//
//        if (field.startsWith("[")) {
//            String[] split = field.substring(1, field.length()-1).split(",");
//            for (String f : split) {
//                array.add(new FieldMap(f, requestParam.get(f)));
//            }
//        } else {
//            array.add(new FieldMap(field, requestParam.get(field)));
//        }
//        return array;
//    }

//    private void validate0(String formulas, ArrayList<FieldMap> fmArray,
//            String prompt, XResult result) {
//        Matcher formulaMatcher = FORMULA_PARSING_PATT.matcher(formulas);
//        while (formulaMatcher.find()) {
//            if (result.isNotPass()) {
//                return;
//            }
//            validateField(formulaMatcher, fmArray, prompt, result);
//        }
//    }

//    /**
//     * 字段校验
//     * @param formulaMatcher
//     * @param fmArray
//     * @param prompt
//     * @param result
//     */
//    private void validateField(Matcher formulaMatcher, ArrayList<FieldMap> fmArray,
//            String prompt, XResult result) {
//
//        String formula = formulaMatcher.group();
//        String invokeType = formulaMatcher.group(1);
//        String methodAbbr = formulaMatcher.group(2);
//        String arguments  = formulaMatcher.group(3);
//        for (FieldMap fm : fmArray) {
//            if ("#".equals(invokeType) &&
//                    Validator.isEmpty(fm.getVal())) {
//                // # 允许字段为null
//                continue;
//            } else {
//                try {
//                    // @ 校验首先判断字段是否为空
//                    String val = fm.getVal();
//                    if (Validator.isEmpty(val)) {
//                        result.failure(fm.getField() + " can not be null");
//                        return;
//                    }
//                    Boolean calculate = Validator.calculate(methodAbbr, fm.getVal(), arguments);
//                    if (!calculate) {
//                        String buildMsg = xMessageBuilder.buildMsg(
//                                methodAbbr, fm.getField(), arguments, prompt);
//                        result.failure(buildMsg);
//                        return;
//                    }
//                } catch (Exception e) {
//                    throw new ExpressionDefineException(formula, e);
//                }
//            }
//        }
//    }
    // 表达式：字段@或#（方法名（参数）？）多组（&公式&） 可有可无
//    private static final Pattern EXPRESS_PARSING_PATT = Pattern.compile(
//            "([a-zA-Z0-9]+)([@#a-zA-Z0-9$]+\\(.*\\))+(&(.*?)&)?");
//    private static final Pattern FORMULA_PARSING_PATT = Pattern.compile(
//            "(@|#)([a-zA-Z0-9$]*)(?:\\((.*?)\\))?");

//    private class FieldMap {
//        private String field;
//        private String val;
//        public FieldMap(String field, String val) {
//            super();
//            this.field = field;
//            this.val = val;
//        }
//        public String getField() {
//            return field;
//        }
//        public String getVal() {
//            return val;
//        }
//    }

}

