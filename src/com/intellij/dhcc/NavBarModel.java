package com.intellij.dhcc;

import com.intellij.analysis.AnalysisScopeBundle;
import com.intellij.ide.navigationToolbar.AbstractNavBarModelExtension;
import com.intellij.openapi.editor.CaretModel;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.LogicalPosition;
import com.intellij.openapi.editor.ScrollType;
import com.intellij.openapi.editor.actionSystem.EditorActionManager;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.ex.FileEditorManagerEx;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.JdkOrderEntry;
import com.intellij.openapi.roots.LibraryOrderEntry;
import com.intellij.openapi.roots.ModuleOrderEntry;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.intellij.util.PathUtil;
import com.intellij.util.Processor;
import org.jetbrains.annotations.Nullable;

/**
 * Created by Administrator on 2017/2/24.
 */
public class NavBarModel extends AbstractNavBarModelExtension {

    final static EditorActionManager actionManager = EditorActionManager.getInstance();
    private boolean isShowPop=false;

    @Override
    @Nullable
    public String getPresentableText(final Object object) {
        if (object instanceof Project) {
            return ((Project) object).getName();
        } else if (object instanceof Module) {
            return ((Module) object).getName();
        } else if (object instanceof PsiFile) {
            VirtualFile file = ((PsiFile) object).getVirtualFile();
            return file != null ? file.getPresentableName() : ((PsiFile) object).getName();
        } else if (object instanceof PsiDirectory) {
            return ((PsiDirectory) object).getVirtualFile().getName();
        } else if (object instanceof JdkOrderEntry) {
            return ((JdkOrderEntry) object).getJdkName();
        } else if (object instanceof LibraryOrderEntry) {
            final String libraryName = ((LibraryOrderEntry) object).getLibraryName();
            return libraryName != null ? libraryName : AnalysisScopeBundle.message("package.dependencies.library.node.text");
        } else if (object instanceof ModuleOrderEntry) {
            final ModuleOrderEntry moduleOrderEntry = (ModuleOrderEntry) object;
            return moduleOrderEntry.getModuleName();
        }
        if (object instanceof MLocationFile) {
            return ((MLocationFile) object).getName();
        }
        if (object != null) {
            return object.toString();
        }
        return null;
    }

    @Override
    public PsiElement adjustElement(final PsiElement psiElement) {
        isShowPop=false;
        final PsiFile containingFile = psiElement.getContainingFile();
        if (containingFile != null) return containingFile;
        return psiElement;
    }

    @Override
    public boolean processChildren(final Object object, final Object rootElement, final Processor<Object> processor) {
        LogUtil.logSuccess(object.getClass().getName());
        LogUtil.logSuccess("PsiElement??" + (object instanceof PsiElement));
        LogUtil.logSuccess("PsiFile??" + (object instanceof PsiFile));
        if (object instanceof MLocationFile) {
            isShowPop=true;
            FileEditorManager fileEditorManagerEx = FileEditorManagerEx.getInstance(((MLocationFile) object).getProject());
            Editor editor = fileEditorManagerEx.getSelectedTextEditor();
            CaretModel model = editor.getCaretModel();
            LogUtil.logSuccess("modelIsnull?" + (model == null));
            editor.getScrollingModel().scrollTo(new LogicalPosition(((MLocationFile) object).getLine(), 1), ScrollType.CENTER);
            return false;
        } else if (object instanceof PsiFile) {
                if(isShowPop) {
                    isShowPop=false;
                }else{
                    TextAnalyze.list(((PsiFile) object).getText(), ((PsiFile) object).getProject(), processor);
                }
        }
        return true;
    }

    @Nullable
    @Override
    public PsiElement getParent(PsiElement psiElement) {
        PsiFile containingFile = psiElement.getContainingFile();
        if (containingFile != null) {
            PsiDirectory containingDirectory = containingFile.getContainingDirectory();
            if (containingDirectory != null) {
                return containingDirectory;
            }
        } else if (psiElement instanceof PsiDirectory) {
            PsiDirectory psiDirectory = (PsiDirectory) psiElement;
            Project project = psiElement.getProject();

            PsiDirectory parentDirectory = psiDirectory.getParentDirectory();

            if (parentDirectory == null) {
                VirtualFile jar = PathUtil.getLocalFile(psiDirectory.getVirtualFile());
                if (ProjectRootManager.getInstance(project).getFileIndex().isInContent(jar)) {
                    parentDirectory = PsiManager.getInstance(project).findDirectory(jar.getParent());
                }
            }
            return parentDirectory;
        } else if (psiElement instanceof PsiFileSystemItem) {
            VirtualFile virtualFile = ((PsiFileSystemItem) psiElement).getVirtualFile();
            if (virtualFile == null) return null;
            PsiManager psiManager = psiElement.getManager();
            PsiElement resultElement;
            if (virtualFile.isDirectory()) {
                resultElement = psiManager.findDirectory(virtualFile);
            } else {
                resultElement = psiManager.findFile(virtualFile);
            }
            if (resultElement == null) return null;
            VirtualFile parentVFile = virtualFile.getParent();
            if (parentVFile != null) {
                PsiDirectory parentDirectory = psiManager.findDirectory(parentVFile);
                if (parentDirectory != null) {
                    return parentDirectory;
                }
            }
        }
        return null;
    }
}
