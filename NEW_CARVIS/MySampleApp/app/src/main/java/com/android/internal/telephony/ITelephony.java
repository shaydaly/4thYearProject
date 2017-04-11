package com.android.internal.telephony;

/**
 * Created by Seamus on 11/04/2017.
 */

public interface ITelephony {
    boolean endCall();
    void answerRingingCall();
    void silenceRinger();
}
