package inject.view.com.compiler;

import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.Elements;

import inject.view.com.anotation.BindView;

/**
 * Created by apple on 18/4/25.
 */

public class AnnotationsInfo {
    private List<BindViewInfo> variableElements;
    private TypeElement typeElement;

    private String packageName;
    private String proxyClassName;
    private final String PROXY_TAG = "ViewInject";

    public AnnotationsInfo(Elements elements, TypeElement typeElement) {
        this.typeElement = typeElement;
        this.packageName = elements.getPackageOf(typeElement).toString();
        this.proxyClassName = typeElement.getSimpleName() + "$$" + PROXY_TAG;
    }

    public void addVariableElement(VariableElement variableElement, int id) {
        if (variableElements == null) {
            variableElements = new ArrayList<>();
        }
        BindViewInfo info = new BindViewInfo();
        info.element = variableElement;
        info.id = id;
        variableElements.add(info);
    }



    public String generateJavaCode()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("// Generated code. Do not modify!\n");
        builder.append("package ").append(packageName).append(";\n\n");
        builder.append("import inject.view.com.view.api.ViewInject;\n");
        builder.append('\n');

        builder.append("public class ").append(proxyClassName).append(" implements " + PROXY_TAG + "<" + typeElement.getQualifiedName() + ">");
        builder.append(" {\n");

        generateMethods(builder);
        builder.append('\n');

        builder.append("}\n");
        return builder.toString();

    }


    private void generateMethods(StringBuilder builder)
    {

        builder.append("@Override\n ");
        builder.append("public void inject(" + typeElement.getQualifiedName() + " host, Object source ) {\n");

        for (BindViewInfo info : variableElements)
        {
            String name = info.element.getSimpleName().toString();
            String type = info.element.asType().toString();
            builder.append(" if(source instanceof android.app.Activity){\n");
            builder.append("host." + name).append(" = ");
            builder.append("(" + type + ")(((android.app.Activity)source).findViewById( " + info.id + "));\n");
            builder.append("\n}else{\n");
            builder.append("host." + name).append(" = ");
            builder.append("(" + type + ")(((android.view.View)source).findViewById( " + info.id + "));\n");
            builder.append("\n};");
        }
        builder.append("  }\n");
    }


    public String getProxyClassFullName()
    {
        return packageName + "." + proxyClassName;
    }

    public TypeElement getTypeElement()
    {
        return typeElement;
    }

}
