package com.robohorse.robopojogenerator.generator.processing;

import com.robohorse.robopojogenerator.generator.common.ClassField;
import com.robohorse.robopojogenerator.generator.common.ClassItem;
import com.robohorse.robopojogenerator.generator.common.JsonItem;
import com.robohorse.robopojogenerator.generator.common.JsonItemArray;
import com.robohorse.robopojogenerator.generator.consts.ClassEnum;
import com.robohorse.robopojogenerator.generator.consts.templates.ImportsTemplate;
import com.robohorse.robopojogenerator.generator.utils.ClassGenerateHelper;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by vadim on 23.09.16.
 */
public class ClassProcessor {
    @Inject
    ClassGenerateHelper classGenerateHelper;

    @Inject
    public ClassProcessor() {
    }

    public void proceed(JsonItem jsonItem, final Map<String, ClassItem> itemMap) {
        final ClassItem classItem = new ClassItem(classGenerateHelper.formatClassName(jsonItem.getKey()));
        classItem.json = jsonItem.getJsonObject().toString();
        for (final String jsonObjectKey : jsonItem.getJsonObject().keySet()) {
            final Object object = jsonItem.getJsonObject().get(jsonObjectKey);
            final String key = jsonObjectKey;
            final InnerObjectResolver innerObjectResolver = new InnerObjectResolver() {

                @Override
                public void onInnerObjectIdentified(String json, ClassEnum classType) {
                    ClassField classField = new ClassField(classType);
                    classField.json = json;
                    classItem.addClassField(jsonObjectKey, classField);
                }

                @Override
                public void onJsonObjectIdentified(String json) {
                    final String className = classGenerateHelper.formatClassName(jsonObjectKey);
                    final ClassField classField = new ClassField(className);
                    classField.json = json;
                    final JsonItem jsonItem = new JsonItem((JSONObject) object, jsonObjectKey);

                    classItem.addClassField(jsonObjectKey, classField);
                    proceed(jsonItem, itemMap);
                }

                @Override
                public void onJsonArrayIdentified(String json) {
                    final JSONArray jsonArray = (JSONArray) object;
                    classItem.addClassImport(ImportsTemplate.LIST);

                    final ClassField classField = new ClassField();
                    classField.json = json;
                    if (jsonArray.length() == 0) {
                        classField.setClassField(new ClassField(ClassEnum.OBJECT));
                        classItem.addClassField(jsonObjectKey, classField);

                    } else {
                        final JsonItemArray jsonItemArray = new JsonItemArray((JSONArray) object, jsonObjectKey);
                        proceedArray(jsonItemArray, classField, itemMap);
                        classItem.addClassField(jsonObjectKey, classField);
                    }
                }
            };
            innerObjectResolver.resolveClassType(key, object);
        }
        appendItemsMap(itemMap, classItem);
    }

    private void appendItemsMap(Map<String, ClassItem> itemMap, ClassItem classItem) {
        final String className = classItem.getClassName();
        if (itemMap.containsKey(className)) {
            final ClassItem storedClassItem = itemMap.get(className);
            classItem.getClassFields().putAll(storedClassItem.getClassFields());
        }
        itemMap.put(className, classItem);
    }

    private void proceedArray(final JsonItemArray jsonItemArray,
                              final ClassField classField,
                              final Map<String, ClassItem> itemMap) {
        final String itemName = classGenerateHelper.getClassNameWithItemPostfix(jsonItemArray.getKey());
        if (jsonItemArray.getJsonArray().length() != 0) {
            final Object object = jsonItemArray.getJsonArray().get(0);
            final InnerObjectResolver innerObjectResolver = new InnerObjectResolver() {

                @Override
                public void onInnerObjectIdentified(String json, ClassEnum classType) {
                    classField.setClassField(new ClassField(classType));
                }

                @Override
                public void onJsonObjectIdentified(String json) {
                    final int size = jsonItemArray.getJsonArray().length();
                    final Map<String, ClassItem> innerItemsMap = new HashMap<String, ClassItem>();
                    for (int index = 0; index < size; index++) {
                        final JSONObject jsonObject = (JSONObject) jsonItemArray.getJsonArray().get(index);
                        final JsonItem jsonItem = new JsonItem(jsonObject, itemName);
                        proceed(jsonItem, innerItemsMap);
                    }
                    classField.setClassField(new ClassField(itemName));
                    itemMap.putAll(innerItemsMap);
                }

                @Override
                public void onJsonArrayIdentified(String json) {
                    classField.setClassField(new ClassField());
                    final JsonItemArray jsonItemArray = new JsonItemArray((JSONArray) object, itemName);
                    proceedArray(jsonItemArray, classField, itemMap);
                }
            };
            innerObjectResolver.resolveClassType("", object);

        } else {
            classField.setClassField(new ClassField(ClassEnum.OBJECT));
        }
    }
}
