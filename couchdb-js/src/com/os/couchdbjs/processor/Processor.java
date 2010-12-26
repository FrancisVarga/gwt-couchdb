package com.os.couchdbjs.processor;

import java.io.Writer;
import java.lang.annotation.Annotation;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.ElementFilter;
import javax.tools.FileObject;
import javax.tools.JavaFileObject;
import javax.tools.StandardLocation;
import javax.tools.Diagnostic.Kind;

import com.os.couchdbjs.client.annotations.FilterFn;
import com.os.couchdbjs.client.annotations.FulltextFn;
import com.os.couchdbjs.client.annotations.ListFn;
import com.os.couchdbjs.client.annotations.ShowFn;
import com.os.couchdbjs.client.annotations.UpdateHandlerFn;
import com.os.couchdbjs.client.annotations.ValidateDocUpdateFn;
import com.os.couchdbjs.client.annotations.ViewMapFn;
import com.os.couchdbjs.client.annotations.ViewReduceFn;

@SupportedAnnotationTypes( { Processor.ANNO_FILTER_FN, Processor.ANNO_FULLTEXT_FN, Processor.ANNO_LIST_FN,
		Processor.ANNO_SHOW_FN, Processor.ANNO_MAP_FN, Processor.ANNO_REDUCE_FN, Processor.ANNO_VALIDATE_FN, Processor.ANNO_UPDATE_FN })
@SupportedSourceVersion(SourceVersion.RELEASE_6)
public class Processor extends AbstractProcessor {
	public static final String ANNO_FILTER_FN = "com.os.couchdbjs.client.annotations.FilterFn";
	public static final String ANNO_FULLTEXT_FN = "com.os.couchdbjs.client.annotations.FulltextFn";
	public static final String ANNO_LIST_FN = "com.os.couchdbjs.client.annotations.ListFn";
	public static final String ANNO_SHOW_FN = "com.os.couchdbjs.client.annotations.ShowFn";
	public static final String ANNO_MAP_FN = "com.os.couchdbjs.client.annotations.ViewMapFn";
	public static final String ANNO_REDUCE_FN = "com.os.couchdbjs.client.annotations.ViewReduceFn";
	public static final String ANNO_VALIDATE_FN = "com.os.couchdbjs.client.annotations.ValidateDocUpdateFn";
	public static final String ANNO_UPDATE_FN = "com.os.couchdbjs.client.annotations.UpdateHandlerFn";

	private static Set<TypeElement> getMapFnTypes(RoundEnvironment pRoundEnv) {
		return (ElementFilter.typesIn(pRoundEnv.getElementsAnnotatedWith(ViewMapFn.class)));
	}

	private static Set<TypeElement> getReduceFnTypes(RoundEnvironment pRoundEnv) {
		return (ElementFilter.typesIn(pRoundEnv.getElementsAnnotatedWith(ViewReduceFn.class)));
	}

	private static Set<TypeElement> getShowFnTypes(RoundEnvironment pRoundEnv) {
		return (ElementFilter.typesIn(pRoundEnv.getElementsAnnotatedWith(ShowFn.class)));
	}

	private static Set<TypeElement> getListFnTypes(RoundEnvironment pRoundEnv) {
		return (ElementFilter.typesIn(pRoundEnv.getElementsAnnotatedWith(ListFn.class)));
	}

	private static Set<TypeElement> getFilterFnTypes(RoundEnvironment pRoundEnv) {
		return (ElementFilter.typesIn(pRoundEnv.getElementsAnnotatedWith(FilterFn.class)));
	}

	private static Set<TypeElement> getFulltextFnTypes(RoundEnvironment pRoundEnv) {
		return (ElementFilter.typesIn(pRoundEnv.getElementsAnnotatedWith(FulltextFn.class)));
	}
	
	private static Set<TypeElement> getValidateDocUpdateFnTypes(RoundEnvironment pRoundEnv) {
		return (ElementFilter.typesIn(pRoundEnv.getElementsAnnotatedWith(ValidateDocUpdateFn.class)));
	}
	
	private static Set<TypeElement> getUpdateFnTypes(RoundEnvironment pRoundEnv) {
		return (ElementFilter.typesIn(pRoundEnv.getElementsAnnotatedWith(UpdateHandlerFn.class)));
	}
	//////////////////////////////////////////////////////////////////////////////////////////////
	public static AnnotationMirror getAnnotationMirror(Element pElement, Class<? extends Annotation> pAnnotation) {
		for (AnnotationMirror am : pElement.getAnnotationMirrors()) {
			if (am.getAnnotationType().toString().equals(pAnnotation.getName())) {
				return (am);
			}
		}
		return (null);
	}

	public static AnnotationValue getAnnotationValue(AnnotationMirror pAnnotationMirror, String pName) {
		if (pAnnotationMirror == null) {
			return null;
		}
		Map<? extends ExecutableElement, ? extends AnnotationValue> m = pAnnotationMirror.getElementValues();
		for (ExecutableElement ee : m.keySet()) {
			if (ee.getSimpleName().toString().equals(pName)) {
				return (m.get(ee));
			}
		}
		return (null);
	}

	private void debug(String pMsg) {
		processingEnv.getMessager().printMessage(Kind.NOTE, pMsg);
	}

	private TypeMirror m_mapFnType;
	private TypeMirror m_reduceFnType;

	@Override
	public void init(ProcessingEnvironment pEnv) {
		super.init(pEnv);
		m_mapFnType = processingEnv.getElementUtils().getTypeElement("com.os.couchdbjs.client.base.AbstractViewMap")
				.asType();
		m_reduceFnType = processingEnv.getElementUtils().getTypeElement("com.os.couchdbjs.client.base.AbstractViewReduce")
				.asType();
	}

	@Override
	public boolean process(Set<? extends TypeElement> pAnnotations, RoundEnvironment pRoundEnv) {
		if (!pRoundEnv.processingOver()) {
			processFulltextFnTypes(pAnnotations, pRoundEnv);
			processMapFnTypes(pAnnotations, pRoundEnv);
			processReduceFnTypes(pAnnotations, pRoundEnv);
			processShowFnTypes(pAnnotations, pRoundEnv);
			processListFnTypes(pAnnotations, pRoundEnv);
			processFilterFnTypes(pAnnotations, pRoundEnv);
			processValidateDocUpdateFnTypes(pAnnotations, pRoundEnv);
			processUpdateFnTypes(pAnnotations, pRoundEnv);
		} else {
		}
		return false;
	}

	private void processTypeElement(TypeElement pElement, String pJsType) {
		String entryPointClassName = "EntryPoint_" + pElement.getSimpleName();
		debug("process " + entryPointClassName);
		try {
			JavaFileObject jfo = processingEnv.getFiler().createSourceFile("gen.client." + entryPointClassName, pElement);
			Writer wr = jfo.openWriter();
			wr.append("package gen.client;\n");
			wr.append("import com.google.gwt.core.client.EntryPoint;\n");
			wr.append("import com.google.gwt.core.client.GWT;\n");
			wr.append("import " + pElement.getQualifiedName() + ";\n");
			wr.append("public class " + entryPointClassName + " implements EntryPoint {\n");
			wr.append("  public void onModuleLoad() {\n");
			wr.append("     GWT.create(" + pElement.getSimpleName() + ".class);\n");
			wr.append("  }\n");
			wr.append("}\n");
			wr.close();

			FileObject fo = processingEnv.getFiler().createResource(StandardLocation.SOURCE_OUTPUT, "gen",
					"module_" + pElement.getSimpleName() + ".gwt.xml", pElement);
			wr = fo.openWriter();
			wr.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
			wr.append("<module rename-to='" + pElement.getSimpleName() + "'>\n");
			wr.append("  <inherits name='com.os.FnModule'/>\n");
			wr.append("  <entry-point class='gen.client." + entryPointClassName + "'/>\n");
			// wr.append("<define-configuration-property name='js_type' is_multi_valued='false'/>\n");
			wr.append("  <set-configuration-property name='js_type' value='" + pJsType + "'/>\n");

			String docName = null;
			String name = null;
			if ("map".equals(pJsType)) {
				AnnotationValue av = getAnnotationValue(getAnnotationMirror(pElement, ViewMapFn.class), "designDocName");
				if (av != null) {
					docName = av.toString();
				}
				av = getAnnotationValue(getAnnotationMirror(pElement, ViewMapFn.class), "viewName");
				if (av != null) {
					name = av.toString();
				}
			} else if ("reduce".equals(pJsType)) {
				AnnotationValue av = getAnnotationValue(getAnnotationMirror(pElement, ViewReduceFn.class), "designDocName");
				if (av != null) {
					docName = av.toString();
				}
				av = getAnnotationValue(getAnnotationMirror(pElement, ViewReduceFn.class), "viewName");
				if (av != null) {
					name = av.toString();
				}
			} else if ("show".equals(pJsType)) {
				AnnotationValue av = getAnnotationValue(getAnnotationMirror(pElement, ShowFn.class), "designDocName");
				if (av != null) {
					docName = av.toString();
				}
				av = getAnnotationValue(getAnnotationMirror(pElement, ShowFn.class), "showName");
				if (av != null) {
					name = av.toString();
				}
			} else if ("list".equals(pJsType)) {
				AnnotationValue av = getAnnotationValue(getAnnotationMirror(pElement, ListFn.class), "designDocName");
				if (av != null) {
					docName = av.toString();
				}
				av = getAnnotationValue(getAnnotationMirror(pElement, ListFn.class), "listName");
				if (av != null) {
					name = av.toString();
				}
			} else if ("filter".equals(pJsType)) {
				AnnotationValue av = getAnnotationValue(getAnnotationMirror(pElement, FilterFn.class), "designDocName");
				if (av != null) {
					docName = av.toString();
				}
				av = getAnnotationValue(getAnnotationMirror(pElement, FilterFn.class), "filterName");
				if (av != null) {
					name = av.toString();
				}
			} else if ("fulltext".equals(pJsType)) {
				AnnotationValue av = getAnnotationValue(getAnnotationMirror(pElement, FulltextFn.class), "designDocName");
				if (av != null) {
					docName = av.toString();
				}
				av = getAnnotationValue(getAnnotationMirror(pElement, FulltextFn.class), "idxName");
				if (av != null) {
					name = av.toString();
				}
			} else if ("validate".equals(pJsType)) {
				AnnotationValue av = getAnnotationValue(getAnnotationMirror(pElement, ValidateDocUpdateFn.class), "designDocName");
				if (av != null) {
					docName = av.toString();
				}
			} else if ("update".equals(pJsType)) {
				AnnotationValue av = getAnnotationValue(getAnnotationMirror(pElement, UpdateHandlerFn.class), "designDocName");
				if (av != null) {
					docName = av.toString();
				}
				av = getAnnotationValue(getAnnotationMirror(pElement, UpdateHandlerFn.class), "name");
				if (av != null) {
					name = av.toString();
				}
			}
			if (docName != null) {
				// wr.append("<define-configuration-property name='design_name' is_multi_valued='false'/>\n");
				wr.append("  <set-configuration-property name='design_name' value='" + docName + "'/>\n");
			}
			if (name != null) {
				// wr.append("<define-configuration-property name='name' is_multi_valued='false'/>\n");
				wr.append("  <set-configuration-property name='name' value='" + name + "'/>\n");
			}
			// wr.append("  <define-linker name='jsLinker' class='com.os.couchdbjs.linker.JsScriptLinker' />\n");
			wr.append("  <add-linker name='jsLinker' />\n");
			// wr.append("  <define-linker name='cdbLinker' class='com.os.couchdbjs.linker.CouchDBLinker' />\n");
			wr.append("  <add-linker name='cdbLinker' />\n");
			wr.append("</module>\n");
			wr.close();
		} catch (Exception ex) {
			processingEnv.getMessager().printMessage(Kind.WARNING, "Exception generating :" + entryPointClassName);
		}
	}

	private void processMapFnTypes(Set<? extends TypeElement> pAnnotations, RoundEnvironment pRoundEnv) {
		for (TypeElement te : getMapFnTypes(pRoundEnv)) {
			if (te.getKind() == ElementKind.CLASS) {
				processTypeElement(te, "map");
			} else {
				processingEnv.getMessager().printMessage(Kind.WARNING, "Map function not a class :" + te.getQualifiedName());
			}
		}
	}

	private void processReduceFnTypes(Set<? extends TypeElement> pAnnotations, RoundEnvironment pRoundEnv) {
		for (TypeElement te : getReduceFnTypes(pRoundEnv)) {
			if (te.getKind() == ElementKind.CLASS) {
				processTypeElement(te, "reduce");
			} else {
				processingEnv.getMessager().printMessage(Kind.WARNING, "Reduce function not a class :" + te.getQualifiedName());
			}
		}
	}

	private void processShowFnTypes(Set<? extends TypeElement> pAnnotations, RoundEnvironment pRoundEnv) {
		for (TypeElement te : getShowFnTypes(pRoundEnv)) {
			if (te.getKind() == ElementKind.CLASS) {
				processTypeElement(te, "show");
			} else {
				processingEnv.getMessager().printMessage(Kind.WARNING, "Show function not a class :" + te.getQualifiedName());
			}
		}
	}

	private void processListFnTypes(Set<? extends TypeElement> pAnnotations, RoundEnvironment pRoundEnv) {
		for (TypeElement te : getListFnTypes(pRoundEnv)) {
			if (te.getKind() == ElementKind.CLASS) {
				processTypeElement(te, "list");
			} else {
				processingEnv.getMessager().printMessage(Kind.WARNING, "List function not a class :" + te.getQualifiedName());
			}
		}
	}
	
	private void processFilterFnTypes(Set<? extends TypeElement> pAnnotations, RoundEnvironment pRoundEnv) {
		for (TypeElement te : getFilterFnTypes(pRoundEnv)) {
			if (te.getKind() == ElementKind.CLASS) {
				processTypeElement(te, "filter");
			} else {
				processingEnv.getMessager().printMessage(Kind.WARNING, "Filter function not a class :" + te.getQualifiedName());
			}
		}
	}

	private void processFulltextFnTypes(Set<? extends TypeElement> pAnnotations, RoundEnvironment pRoundEnv) {
		for (TypeElement te : getFulltextFnTypes(pRoundEnv)) {
			if (te.getKind() == ElementKind.CLASS) {
				processTypeElement(te, "fulltext");
			} else {
				processingEnv.getMessager().printMessage(Kind.WARNING, "Fulltext function not a class :" + te.getQualifiedName());
			}
		}
	}
	
	private void processValidateDocUpdateFnTypes(Set<? extends TypeElement> pAnnotations, RoundEnvironment pRoundEnv) {
		for (TypeElement te : getValidateDocUpdateFnTypes(pRoundEnv)) {
			if (te.getKind() == ElementKind.CLASS) {
				processTypeElement(te, "validate");
			} else {
				processingEnv.getMessager().printMessage(Kind.WARNING, "ValidateDocUpdate function not a class :" + te.getQualifiedName());
			}
		}
	}

	private void processUpdateFnTypes(Set<? extends TypeElement> pAnnotations, RoundEnvironment pRoundEnv) {
		for (TypeElement te : getUpdateFnTypes(pRoundEnv)) {
			if (te.getKind() == ElementKind.CLASS) {
				processTypeElement(te, "update");
			} else {
				processingEnv.getMessager().printMessage(Kind.WARNING, "Update function not a class :" + te.getQualifiedName());
			}
		}
	}
}