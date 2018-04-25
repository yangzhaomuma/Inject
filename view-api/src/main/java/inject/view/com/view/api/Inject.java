package inject.view.com.view.api;

import android.app.Activity;

/**
 * Created by apple on 18/4/26.
 */

public class Inject {
    private static final String SUFFIX = "$$ViewInject";

    public static void bind(Activity activity) {
        Class clazz = activity.getClass();
        try {
            Class injectorClazz = null;
            injectorClazz = Class.forName(clazz.getName() + SUFFIX);
            ((ViewInject<Activity>) injectorClazz.newInstance()).inject(activity,activity);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
