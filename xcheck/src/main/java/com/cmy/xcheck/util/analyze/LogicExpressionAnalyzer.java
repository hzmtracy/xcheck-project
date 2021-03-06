package com.cmy.xcheck.util.analyze;

import com.cmy.xcheck.exception.ExpressionDefineException;
import com.cmy.xcheck.util.item.XCheckItem;
import com.cmy.xcheck.util.item.impl.XCheckItemLogic;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 解析逻辑表达式
 * Created by Kevin72c on 2016/5/2.
 */
public class LogicExpressionAnalyzer {

    private static final Pattern COMPARISON_OPERATOR_PATTERN =
            Pattern.compile("(.*?)(<=|<|>=|>|==|!=)(.*)");

    public static XCheckItem analyze(String expression) {
        // 提示信息分隔符
        String[] split = expression.split(":");
        String message = split.length > 1 ? split[1] : null;

        Matcher matcher = COMPARISON_OPERATOR_PATTERN.matcher(split[0]);
        if (!matcher.find()) {
            throw new ExpressionDefineException("公式定义不正确：" + expression);
        }
        // 解析公式
        String leftField = matcher.group(1);
        String comparisonOperator = matcher.group(2);
        String rightField = matcher.group(3);

        return new XCheckItemLogic(leftField, rightField, comparisonOperator, message);
    }

}
