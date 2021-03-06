package reflectiongui.controllers;

import reflectiongui.annotations.Ignored;
import reflectiongui.renderers.ObjectRenderer;
import reflectiongui.renderers.RendererFactory;
import reflectiongui.util.Utils;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Контроллер объекта, отражаемого в графический интерфейс
 * с помощью библиотеки Reflection-GUI.
 */
public class ObjectController implements AnnotatedElement {

    private Object controlledObject;
    private ObjectRenderer renderer;
    private MethodController[] methodControllers;
    private PropertyController[] propertyControllers;
    private String title;

    public ObjectController(Object controlledObject) {
        this.controlledObject = controlledObject;
        Class clazz = controlledObject.getClass();
        Set<MethodController> ms = new LinkedHashSet<MethodController>();
        for (Method m : clazz.getDeclaredMethods()) {
            if (m.getAnnotation(Ignored.class) == null) {
                ms.add(new MethodController(this, m));
            }
        }
        methodControllers = ms.toArray(new MethodController[ms.size()]);
        Set<PropertyController> ps = new LinkedHashSet<PropertyController>();
        for (Field f : clazz.getDeclaredFields()) {
            if (f.getAnnotation(Ignored.class) == null) {
                ps.add(new PropertyController(this, f));
            }
        }
        propertyControllers = ps.toArray(new PropertyController[ps.size()]);
        title = Utils.getTitleFromAnnotations(clazz, clazz.getSimpleName());

        renderer = RendererFactory.getInstance().createObjectRenderer(clazz);
        renderer.initialize(this);
    }

    /**
     * Обновить поля объекта в соответствии с тем,
     * что содержит графический интерфейс.
     */
    public void updateObject() {
        for (VariableController c : propertyControllers) {
            c.updateObject();
        }
    }

    /**
     * Обновить графический интерфейс в соответствии
     * со значениями полей контролируемого объекта.
     */
    public void updateUI() {
        for (VariableController c : propertyControllers) {
            c.updateUI();
        }
    }

    public Object getControlledObject() {
        return controlledObject;
    }

    public ObjectRenderer getRenderer() {
        return renderer;
    }

    public void setRenderer(ObjectRenderer renderer) {
        this.renderer = renderer;
    }

    public String getTitle() {
        return title;
    }

    @Override
    public boolean isAnnotationPresent(Class<? extends Annotation> annotationClass) {
        return controlledObject.getClass().isAnnotationPresent(annotationClass);
    }

    @Override
    public <T extends Annotation> T getAnnotation(Class<T> annotationClass) {
        return controlledObject.getClass().getAnnotation(annotationClass);
    }

    @Override
    public Annotation[] getAnnotations() {
        return controlledObject.getClass().getAnnotations();
    }

    @Override
    public Annotation[] getDeclaredAnnotations() {
        return controlledObject.getClass().getDeclaredAnnotations();
    }

    public MethodController[] getMethodControllers() {
        return methodControllers;
    }

    public PropertyController[] getPropertyControllers() {
        return propertyControllers;
    }
}
