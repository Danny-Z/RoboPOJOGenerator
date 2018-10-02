package com.robohorse.robopojogenerator.generator;

import com.robohorse.robopojogenerator.delegates.FileWriterDelegate;
import com.robohorse.robopojogenerator.generator.common.ClassCreator;
import com.robohorse.robopojogenerator.generator.common.ClassItem;
import com.robohorse.robopojogenerator.generator.common.JsonItem;
import com.robohorse.robopojogenerator.generator.processing.ClassProcessor;
import com.robohorse.robopojogenerator.generator.utils.ClassGenerateHelper;
import com.robohorse.robopojogenerator.models.GenerationModel;
import com.robohorse.robopojogenerator.models.ProjectModel;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import testutils.JsonReader;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by vadim on 22.10.16.
 */
public class ClassCreatorTest {
    @InjectMocks
    ClassCreator classCreator;
    @Mock
    RoboPOJOGenerator roboPOJOGenerator;
    @Mock
    FileWriterDelegate fileWriterDelegate;

    @InjectMocks
    ClassProcessor classProcessor;
    @Mock
    ClassGenerateHelper classGenerateHelper;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void generateFiles() throws Exception {
        JsonReader jsonReader = new JsonReader();
        final JSONObject jsonObject = jsonReader.read("check.json");
        final String name = "Response";

        when(classGenerateHelper.formatClassName(name))
                .thenReturn(name);

        final Map<String, ClassItem> classItemMap = new HashMap<>();
        final JsonItem jsonItem = new JsonItem(jsonObject, name);
        classProcessor.proceed(jsonItem, classItemMap);

        final Set<ClassItem> classItemSet = new HashSet<ClassItem>();
        for (ClassItem classItem :
                classItemMap.values()) {
            classItemSet.add(classItem);
        }
        final GenerationModel generationModel = new GenerationModel
                .Builder()
                .build();
        final ProjectModel projectModel = new ProjectModel
                .Builder()
                .build();
        when(roboPOJOGenerator.generate(generationModel))
                .thenReturn(classItemSet);
        classCreator.generateFiles(generationModel, projectModel);
        for (ClassItem classItem :
                classItemSet) {
           fileWriterDelegate.writeFile2(classItem, generationModel, projectModel);
        }
    }
}