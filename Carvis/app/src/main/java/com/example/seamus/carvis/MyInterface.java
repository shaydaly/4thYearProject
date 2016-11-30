package com.example.seamus.carvis;

import com.amazonaws.mobileconnectors.lambdainvoker.LambdaFunction;

/**
 * Created by Seamus on 22/11/2016.
 */

public interface MyInterface {
    @LambdaFunction
    String SpeedCheckLambda(double speed);
}
