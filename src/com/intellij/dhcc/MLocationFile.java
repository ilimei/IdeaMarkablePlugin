package com.intellij.dhcc;

import com.intellij.openapi.project.Project;

/**
 * Created by Administrator on 2017/2/24.
 */
public class MLocationFile {

    private Project project;
    private String name;
    private int line;

    public MLocationFile(Project project, String name, int line){
        this.project=project;
        this.name=name;
        this.line=line;
    }

    public String getName(){
        return this.name;
    }

    public Project getProject(){
        return this.project;
    }

    public int getLine() {
        return line;
    }
}
