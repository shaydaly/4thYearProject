import android.content.Context;
import android.test.mock.MockContext;

import com.TestPackage.MockRequest;
import com.amazonaws.mobile.user.signin.CognitoUserPoolsSignInProvider;
import com.carvis.Journey;
import com.carvis.TrackSpeedActivity;
import com.carvis.UserStat;
import com.carvis.VolleyService;

import org.joda.time.DateTime;
import org.junit.Test;

//import static com.facebook.FacebookSdk.getApplicationContext;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * Created by Seamus on 18/02/2017.
 */


public class UnitTest {

    Context context;
    CognitoUserPoolsSignInProvider provider;
//    @Test
//    public void testQuerySpeed(){
////        com.carvis.TrackSpeedActivity track = new TrackSpeedActivity();
////        Journey j = new Journey();
////        track.getSpeedFromLambda("53.3509124","-6.47792");
////        System.out.println(j.getCurrentSpeed());
////        assertThat(j.getCurrentSpeed(), is("50"));
//
//    }

    @Test
    public void testDayOfWeekMethod(){
        UserStat userStat = new UserStat();
        DateTime dateTime = new DateTime();

       //assertThat(userStat.getDayOfWeek(dateTime).equals("Friday"),is(true));
    }

    @Test
    public void testOverSpeedDay(){
        UserStat userStat = new UserStat();
        DateTime dateTime = new DateTime();

        userStat.addOverSpeedDate(dateTime);
        userStat.addOverSpeedDate(dateTime.plusDays(5));
        userStat.addOverSpeedDate(dateTime.plusDays(7));
        userStat.addOverSpeedDate(dateTime.plusDays(6));

        //assertThat(userStat.getOverSpeedDay().equals("Friday"),is(true));
    }

    @Test
    public void testDaysOverSpeed(){
        provider = new CognitoUserPoolsSignInProvider(context);
        context = new MockContext();
        VolleyService volleyService = new VolleyService(context);
        volleyService.getDaysSinceLastOverSpeed(provider, context);
    }




}
