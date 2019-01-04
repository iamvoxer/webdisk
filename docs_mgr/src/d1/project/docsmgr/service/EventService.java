package d1.project.docsmgr.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EventService {
    private static EventService instance;
    private Map<Integer, List<ICallbackObject>> callbacks = new HashMap<Integer, List<ICallbackObject>>();

    private EventService() {

    }

    public static EventService getInstance() {
        if (instance == null)
            instance = new EventService();
        return instance;
    }

    public void on(Integer event, ICallbackObject callbackObject) {
        List<ICallbackObject> list;
        if (!callbacks.containsKey(event)) {
            list = new ArrayList<ICallbackObject>();
            callbacks.put(event, list);
        } else
            list = callbacks.get(event);
        list.add(callbackObject);
    }

    public void fire(Integer event, Object obj) {
        List<ICallbackObject> list = callbacks.get(event);
        if (list != null) {
            for (ICallbackObject callback : list) {
                callback.invoke(event, obj);
            }
        }
    }
}
