package inject.view.com.compiler;

import com.google.auto.service.AutoService;

import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;

import inject.view.com.anotation.BindView;

@AutoService(Processor.class)
public class CompilerProcessor extends AbstractProcessor {

    private Messager messager;
    private Filer filer;
    private Elements elements;

    private Map<String, AnnotationsInfo> mapClass = new HashMap<>();

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        messager = processingEnvironment.getMessager();
        filer = processingEnvironment.getFiler();
        elements = processingEnvironment.getElementUtils();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        LinkedHashSet<String> linkedHashSet = new LinkedHashSet<>();
        linkedHashSet.add(BindView.class.getCanonicalName());
        return linkedHashSet;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        messager.printMessage(Diagnostic.Kind.NOTE, "process...");
        processorBindView(annotations, roundEnv);
        return true;
    }


    private void processorBindView(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        mapClass.clear();
        for (Element element : roundEnv.getElementsAnnotatedWith(BindView.class)) {
            if (element.getKind() != ElementKind.FIELD) {
                return;
            }
            VariableElement variableElement = (VariableElement) element;
            BindView bindView = variableElement.getAnnotation(BindView.class);
            int id = bindView.value();
            TypeElement typeElement = (TypeElement) element.getEnclosingElement();
            String className = typeElement.getQualifiedName().toString();
            AnnotationsInfo bindViewClass = mapClass.get(className);
            if (bindViewClass == null) {
                bindViewClass = new AnnotationsInfo(elements, typeElement);
                mapClass.put(className, bindViewClass);
            }
            bindViewClass.addVariableElement(variableElement, id);
        }

        for (String classKey : mapClass.keySet()) {
            AnnotationsInfo bindViewClass = mapClass.get(classKey);
            if (bindViewClass != null) {
                bindViewClass.generateJavaCode();

                try {
                    JavaFileObject jfo = filer.createSourceFile(
                            bindViewClass.getProxyClassFullName(),
                            bindViewClass.getTypeElement());
                    Writer writer = jfo.openWriter();
                    writer.write(bindViewClass.generateJavaCode());
                    writer.flush();
                    writer.close();
                } catch (IOException e) {
                    error(bindViewClass.getTypeElement(),
                            "Unable to write injector for type %s: %s",
                            bindViewClass.getTypeElement(), e.getMessage());
                }
            }
        }
    }

    private void error(Element element, String message, Object... args) {
        if (args.length > 0) {
            message = String.format(message, args);
        }
        messager.printMessage(Diagnostic.Kind.NOTE, message, element);
    }


}
