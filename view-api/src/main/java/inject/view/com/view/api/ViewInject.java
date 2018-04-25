package inject.view.com.view.api;

/**
 * Created by apple on 18/4/26.
 */

public interface ViewInject<T> {
    void inject(T t,Object source);
}
