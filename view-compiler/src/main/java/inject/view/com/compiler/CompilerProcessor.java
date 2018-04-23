package inject.view.com.compiler;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.util.LinkedHashSet;
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

import inject.view.com.anotation.BindView;

@AutoService(Processor.class)
public class CompilerProcessor extends AbstractProcessor {

    private Messager messager;
    private Filer filer;
    private Elements elements;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        messager=processingEnvironment.getMessager();
        filer=processingEnvironment.getFiler();
        elements=processingEnvironment.getElementUtils();
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
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        System.out.println("------ process -----");


       Set<? extends Element> elements= roundEnvironment.getElementsAnnotatedWith(BindView.class);
       for(Element element:elements){
           if(element.getKind()!= ElementKind.FIELD || element.getKind()!=ElementKind.METHOD){
               return false;
           }
           if(element.getKind()==ElementKind.FIELD){
               VariableElement variableElement=(VariableElement) element;
               BindView bindView=variableElement.getAnnotation(BindView.class);
               bindView.value();
           }
       }

        MethodSpec methodSpec = MethodSpec.methodBuilder("main")
                .returns(TypeName.VOID)
                .addParameter(String[].class, "args")
                .addStatement("$T.out.println($S)", System.class, "Hello, JavaPoet!")
                .build();

        TypeSpec typeSpec = TypeSpec.classBuilder("Hello")
                .addMethod(methodSpec)
                .build();

        JavaFile javaFile = JavaFile.builder("inject.view.com.compiler", typeSpec).build();

        try {
            javaFile.writeTo(System.out);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return true;
    }
}
