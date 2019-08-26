package com.yxhuang.clicklimit.aspect;

import android.util.Log;
import android.view.View;

import com.yxhuang.clicklimit.R;
import com.yxhuang.clicklimit.annotation.ClickLimit;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;

import java.lang.reflect.Method;

/**
 * Created by yxhuang
 * Date: 2019/6/21
 * Description:
 */
@Aspect
public class ClickLimitAspect {

    private static final String TAG = "ClickLimitAspect";

    private static final int CHECK_FOR_DEFAULT_TIME = 500;

    private static final String POINTCUT_ON_ANNOTATION =
            "execution(@com.yxhuang.clicklimit.annotation.ClickLimit * *(..))";

    @Pointcut(POINTCUT_ON_ANNOTATION)
    public void onAnnotationClick(){}

    @Around("onAnnotationClick()")
    public void processJoinPoint(ProceedingJoinPoint joinPoint) throws Throwable {
        Log.d(TAG, "-----method is click--- ");
        try {
            Signature signature = joinPoint.getSignature();
            if (!(signature instanceof MethodSignature)){
                Log.d(TAG, "method is no MethodSignature, so proceed it");
                joinPoint.proceed();
                return;

            }

            MethodSignature methodSignature = (MethodSignature) signature;
            Method method = methodSignature.getMethod();
            boolean isHasLimitAnnotation = method.isAnnotationPresent(ClickLimit.class);
            String methodName = method.getName();
            int intervalTime = CHECK_FOR_DEFAULT_TIME;
            if (isHasLimitAnnotation){
                ClickLimit clickLimit = method.getAnnotation(ClickLimit.class);
                int limitTime = clickLimit.value();
                // not limit click
                if (limitTime <= 0){
                    Log.d(TAG, "method: " + methodName + " limitTime is zero, so proceed it");
                    joinPoint.proceed();
                    return;
                }
                intervalTime = limitTime;
                Log.d(TAG, "methodName " +  methodName + " intervalTime is " + intervalTime);
            }


            Object[] args = joinPoint.getArgs();
            View view = getViewFromArgs(args);
            if (view == null) {
                Log.d(TAG, "view is null, proceed");
                joinPoint.proceed();
                return;
            }

            Object viewTimeTag =  view.getTag(R.integer.yxhuang_click_limit_tag_view);
            // first click viewTimeTag is null.
            if (viewTimeTag == null){
                Log.d(TAG, "lastClickTime is zero , proceed");
                proceedAnSetTimeTag(joinPoint, view);
                return;
            }

            long lastClickTime = (long) viewTimeTag;
            if (lastClickTime <= 0){
                Log.d(TAG, "lastClickTime is zero , proceed");
                proceedAnSetTimeTag(joinPoint, view);
                return;
            }

            // in limit time
            if (!canClick(lastClickTime, intervalTime)){
                Log.d(TAG, "is in limit time , return");
                return;

            }
            proceedAnSetTimeTag(joinPoint, view);
            Log.d(TAG, "view proceed.");
        } catch (Throwable e) {
            e.printStackTrace();
            Log.d(TAG, e.getMessage());
            joinPoint.proceed();
        }
    }

    private void proceedAnSetTimeTag(ProceedingJoinPoint joinPoint, View view) throws Throwable {
        view.setTag(R.integer.yxhuang_click_limit_tag_view, System.currentTimeMillis());
        joinPoint.proceed();
    }


    /**
     * 获取 view 参数
     *
     * @param args
     * @return
     */
    private View getViewFromArgs(Object[] args) {
        if (args != null && args.length > 0) {
            Object arg = args[0];
            if (arg instanceof View) {
                return (View) arg;
            }
        }
        return null;
    }

    /**
     * 判断是否达到可以点击的时间间隔
     *
     * @param lastClickTime
     * @return
     */
    private boolean canClick(long lastClickTime, int intervalTime) {
        long currentTime = System.currentTimeMillis();
        long realIntervalTime  = currentTime - lastClickTime;
        Log.d(TAG, "canClick currentTime= " + currentTime + " lastClickTime= " + lastClickTime +
                " realIntervalTime= " + realIntervalTime);
        return realIntervalTime >= intervalTime;
    }


}
