package com.robohorse.robopojogenerator.generator.processing;

import com.robohorse.robopojogenerator.generator.consts.ClassEnum;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by vadim on 23.09.16.
 */
public abstract class InnerObjectResolver {

    public void resolveClassType(String key, Object object) {
        if (object instanceof JSONObject) {
            onJsonObjectIdentified(key + ":" + object.toString());

        } else if (object instanceof JSONArray) {
            onJsonArrayIdentified(key + ":" + object.toString());

        } else if (object instanceof String) {
            onInnerObjectIdentified(key + ":" + object.toString(), ClassEnum.STRING);

        } else if (object instanceof Integer) {
            onInnerObjectIdentified(key + ":" + object.toString(), ClassEnum.INTEGER);

        } else if (object instanceof Double) {
            onInnerObjectIdentified(key + ":" + object.toString(), ClassEnum.DOUBLE);

        } else if (object instanceof Float) {
            onInnerObjectIdentified(key + ":" + object.toString(), ClassEnum.FLOAT);

        } else if (object instanceof Long) {
            onInnerObjectIdentified(key + ":" + object.toString(), ClassEnum.LONG);

        } else if (object instanceof Boolean) {
            onInnerObjectIdentified(key + ":" + object.toString(), ClassEnum.BOOLEAN);

        } else {
            onInnerObjectIdentified(key + ":" + object.toString(), ClassEnum.OBJECT);
        }
    }

    public abstract void onInnerObjectIdentified(String json, ClassEnum classEnum);

    public abstract void onJsonObjectIdentified(String json);

    public abstract void onJsonArrayIdentified(String json);
}
